package io.razem.bittydream;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.service.dreams.DreamService;
import android.util.Log;
import android.widget.TextView;

import io.razem.bittydream.xchange.ExchangeHelper;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.service.polling.marketdata.PollingMarketDataService;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by julianliebl on 25.02.14.
 */
public class BittyDaydream extends DreamService {
    private SharedPreferences prefs;

    private int currentSelectedExchange;
    private int currentSelectedPair;


    TextView pairTextView, exchangeRateTextView, lastUpdateTextView;
    Exchange exchange;
    PollingMarketDataService pollingService;
    DownloadFilesTask currentTask;
    CurrencyPair currencyPair;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setFullscreen(true);
        setContentView(R.layout.main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //check if there was already an exchange selected before
        currentSelectedExchange = prefs.getInt(Constants.PREF_MARKETS, 0);

        //check if there was already a pair selected before
        currentSelectedPair = prefs.getInt(Constants.PREF_PAIRS, 0);

        pairTextView = (TextView) findViewById(R.id.pair);
        exchangeRateTextView = (TextView) findViewById(R.id.exchange_value);
        lastUpdateTextView = (TextView) findViewById(R.id.last_updated);


        init();
    }

    private void init(){
        exchange = Constants.getKnownExchanges().get(currentSelectedExchange);
        pollingService = exchange.getPollingMarketDataService();

        new ExchangeHelper().requestSupportedCurrencyPairsForExchange(exchange, new ExchangeHelper.SupportedCurrencyPairsCallbackListeners() {
            @Override
            public void onSupportedCurrencyPairsCallback(ArrayList<CurrencyPair> currencyPairs) {
                if (currencyPairs.isEmpty()) {
                    exchangeRateTextView.setText(getResources().getString(R.string.network_error));
                    //TODO: IMPLEMENT RETRY AFTER A CERTAIN TIMEOUT
                    return;
                }
                currencyPair = currencyPairs.get(currentSelectedPair);
                startTickerUpdate();
            }
        });
    }

    private void startTickerUpdate(){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(currentTask != null){
                    currentTask.cancel(true);
                }
                currentTask = new DownloadFilesTask();
                currentTask.execute();
            }
        }, 0, 30000);
    }

    private class DownloadFilesTask extends AsyncTask<Object, Integer, Ticker> {
        protected Ticker doInBackground(Object... urls) {
            Ticker ticker = null;
            try {
                ticker = pollingService.getTicker(currencyPair);
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), e.toString());
            }
            return ticker;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Ticker result) {
            if(result != null){
                pairTextView.setText(result.getCurrencyPair().toString().toUpperCase());

                String currencyText = "";
                try {
                    java.text.NumberFormat f = java.text.NumberFormat.getCurrencyInstance(Locale.getDefault());
                    Currency currency = result.getCurrencyPair().counter;
                    f.setCurrency(java.util.Currency.getInstance(currency.getCurrencyCode()));
                    currencyText = f.format(result.getLast().floatValue());
                }catch(Exception e){
                    currencyText = result.getLast().toPlainString();
                }

                exchangeRateTextView.setText(currencyText);

                DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.getDefault());
                if(result.getTimestamp() == null){
                    lastUpdateTextView.setText(dateFormat.format(new Date()));
                }else{
                    lastUpdateTextView.setText(dateFormat.format(result.getTimestamp()));
                }
            }else{
                exchangeRateTextView.setText(getResources().getString(R.string.network_error));
            }
        }
    }
}
