package com.bitty.bittydream;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.service.dreams.DreamService;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeException;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

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
public class ExampleDaydream extends DreamService {
    private SharedPreferences prefs;

    private int currentSelectedExchange;
    private int currentSelectedPair;


    TextView textView, textUpdatedView;
    Exchange exchange;
    PollingMarketDataService pollingService;
    DownloadFilesTask currentTask;
    CurrencyPair currencyPair;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setContentView(R.layout.main);
        setFullscreen(true);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //check if there was already an exchange selected before
        currentSelectedExchange = prefs.getInt(Constants.PREF_MARKETS, 0);

        //check if there was already a pair selected before
        currentSelectedPair = prefs.getInt(Constants.PREF_PAIRS, 0);

        textView = (TextView) findViewById(R.id.textView);
        textUpdatedView = (TextView) findViewById(R.id.last_updated);


        init();
    }

    private void init(){
        exchange = Constants.getKnownExchanges().get(currentSelectedExchange);
        pollingService = exchange.getPollingMarketDataService();

        new GetCurrencyPairTask().execute();
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

    private class GetCurrencyPairTask extends AsyncTask<Object, Integer, ArrayList<CurrencyPair>> {

        @Override
        protected ArrayList<CurrencyPair> doInBackground(Object... objects) {
            ArrayList<CurrencyPair> newList = new ArrayList<CurrencyPair>();
            try{
                for (CurrencyPair pair : Constants.getKnownExchanges().get(currentSelectedExchange).getPollingMarketDataService().getExchangeSymbols()) {
                    if(pair.counterCurrency != null && pair.baseCurrency != null){
                        newList.add(pair);
                    }else{
                        Log.d("GetCurrencyPairTask", pair.toString());
                    }
                }
            }catch (ExchangeException e){
                //WE DONT DO ANYTHING HERE. Instead we handle the empty list in onPostExecute
            }
            return newList;
        }

        protected void onPostExecute(ArrayList<CurrencyPair> newList) {
            if(newList.isEmpty()){
                textView.setText(getResources().getString(R.string.network_error));
                //TODO: IMPLEMENT RETRY AFTER A CERTAIN TIMEOUT
                return;
            }
            currencyPair = newList.get(currentSelectedPair);
            startTickerUpdate();
        }
    }

    private class DownloadFilesTask extends AsyncTask<Object, Integer, Ticker> {
        protected Ticker doInBackground(Object... urls) {
            Ticker btceTicker = null;
            try {
                btceTicker = pollingService.getTicker(currencyPair.baseCurrency, currencyPair.counterCurrency);
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), e.toString());
            }
            return btceTicker;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Ticker result) {
            if(result != null){
                java.text.NumberFormat f = java.text.NumberFormat.getCurrencyInstance(Locale.getDefault());
                java.util.Currency currency = java.util.Currency.getInstance(result.getLast().getCurrencyUnit().toCurrency().getCurrencyCode());
                f.setCurrency(currency);

                textView.setText(f.format(result.getLast().getAmount().floatValue()));

                DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.getDefault());
                if(result.getTimestamp() == null){
                    textUpdatedView.setText(dateFormat.format(new Date()));
                }else{
                    textUpdatedView.setText(dateFormat.format(result.getTimestamp()));
                }
            }else{
                textView.setText(getResources().getString(R.string.network_error));
            }
        }
    }
}
