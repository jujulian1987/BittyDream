package com.bitty.bittydream;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeException;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.bitstamp.Bitstamp;
import com.xeiam.xchange.bitstamp.BitstampExchange;
import com.xeiam.xchange.btce.BTCEExchange;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

import java.util.ArrayList;

/**
 * Created by julianliebl on 25.02.14.
 */
public class SettingsActivity extends Activity implements AdapterView.OnItemSelectedListener{
    private SharedPreferences prefs;

    private int currentSelectedExchange;
    private int currentSelectedPair;

    private Spinner spMarkets;
    private Spinner spSymbols;
    Button btDayDreamSettings;

    private ArrayList<String> marketList;
    private ArrayList<String> symbolList;

    ArrayAdapter<String> marketAdapter;
    ArrayAdapter<String> symbolAdapter;

    public SettingsActivity(){
        marketList = new ArrayList<String>();
        symbolList = new ArrayList<String>();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        for (Exchange exchange : Constants.getKnownExchanges()) {
            marketList.add(exchange.getExchangeSpecification().getExchangeName());
        }

        marketAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, marketList);
        marketAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMarkets = (Spinner)findViewById(R.id.markets);
        spMarkets.setAdapter(marketAdapter);


        symbolAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, symbolList);
        symbolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSymbols = (Spinner) findViewById(R.id.symbols);
        spSymbols.setAdapter(symbolAdapter);

        updatePairList();

        btDayDreamSettings = (Button) findViewById(R.id.daydream_settings);

        btDayDreamSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("WHY", "Clicked this shit!");
                startActivity(new Intent(Settings.ACTION_DREAM_SETTINGS));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check if there was already an exchange selected before
        currentSelectedExchange = prefs.getInt(Constants.PREF_MARKETS, 0);

        //check if there was already a pair selected before
        currentSelectedPair = prefs.getInt(Constants.PREF_PAIRS, 0);

        spMarkets.setSelection(currentSelectedExchange);
        spSymbols.setSelection(currentSelectedPair);

        spMarkets.setOnItemSelectedListener(this);
        spSymbols.setOnItemSelectedListener(this);
    }

    private class UpdatePairListTask extends AsyncTask<Object, Integer, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Object... objects) {
            ArrayList<String> newList = new ArrayList<String>();
            try{
            for (CurrencyPair pair : Constants.getKnownExchanges().get(currentSelectedExchange).getPollingMarketDataService().getExchangeSymbols()) {
                if(pair.counterCurrency != null && pair.baseCurrency != null){
                    newList.add(pair.toString());
                }
            }
            }catch (ExchangeException e){
                newList.add(getResources().getString(R.string.network_error));
            }
            return newList;
        }

        protected void onPostExecute(ArrayList<String> newList) {
            symbolList.clear();
            symbolList.addAll(newList);
            symbolAdapter.notifyDataSetChanged();
        }
    }

    private void updatePairList(){
        symbolList.clear();
        symbolList.add(getResources().getString(R.string.loading));
        symbolAdapter.notifyDataSetChanged();
        new UpdatePairListTask().execute();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(spSymbols.equals(adapterView)){
            currentSelectedPair = spSymbols.getSelectedItemPosition();

            final SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(Constants.PREF_PAIRS, currentSelectedPair);
            editor.commit();
        }else if(spMarkets.equals(adapterView)){
            currentSelectedExchange = spMarkets.getSelectedItemPosition();
            final SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(Constants.PREF_MARKETS, currentSelectedExchange);
            editor.commit();

            updatePairList();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}
