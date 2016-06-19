package io.razem.bittydream;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import io.razem.bittydream.xchange.ExchangeHelper;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;

import java.util.ArrayList;

/**
 * Created by julianliebl on 25.02.14.
 */
public class SettingsActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("what", "yes");
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(android.R.id.content, new SettingsFragment());
        tx.commit();
    }


    public static class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener{
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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.settings, container, false);

            Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);


            prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

            for (Exchange exchange : Constants.getKnownExchanges()) {
                String exchangeName = exchange.getExchangeSpecification().getExchangeName();
                exchangeName = Character.toUpperCase(exchangeName.charAt(0)) + exchangeName.substring(1);
                marketList.add(exchangeName);
            }

            marketAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, marketList);
            marketAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spMarkets = (Spinner)view.findViewById(R.id.markets);
            spMarkets.setAdapter(marketAdapter);


            symbolAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, symbolList);
            symbolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spSymbols = (Spinner)view.findViewById(R.id.symbols);
            spSymbols.setAdapter(symbolAdapter);

            updatePairList();

            btDayDreamSettings = (Button) view.findViewById(R.id.daydream_settings);

            btDayDreamSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Settings.ACTION_DREAM_SETTINGS));
                }
            });

            return view;
        }

        @Override
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);


            marketList = new ArrayList<String>();
            symbolList = new ArrayList<String>();
        }

        @Override
        public void onResume() {
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

        private void updatePairList(){
            symbolList.clear();
            symbolList.add(getResources().getString(R.string.loading));
            symbolAdapter.notifyDataSetChanged();
            new ExchangeHelper().requestSupportedCurrencyPairsForExchange(Constants.getKnownExchanges().get(currentSelectedExchange), new ExchangeHelper.SupportedCurrencyPairsCallbackListeners() {
                @Override
                public void onSupportedCurrencyPairsCallback(ArrayList<CurrencyPair> currencyPairs) {
                    ArrayList<String> newList = new ArrayList<String>();
                    symbolList.clear();
                    for(CurrencyPair pair : currencyPairs){
                        newList.add(pair.toString().toUpperCase());
                    }
                    symbolList.addAll(newList);
                    symbolAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if(spSymbols.equals(adapterView)){
                currentSelectedPair = spSymbols.getSelectedItemPosition();

                final SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(Constants.PREF_PAIRS, currentSelectedPair);
                editor.apply();
            }else if(spMarkets.equals(adapterView)){
                currentSelectedExchange = spMarkets.getSelectedItemPosition();
                final SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(Constants.PREF_MARKETS, currentSelectedExchange);
                editor.apply();

                updatePairList();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }




}
