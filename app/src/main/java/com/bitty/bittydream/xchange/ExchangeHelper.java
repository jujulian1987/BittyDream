package com.bitty.bittydream.xchange;

import android.os.AsyncTask;
import android.util.Log;

import com.bitty.bittydream.Constants;
import com.bitty.bittydream.R;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeException;
import com.xeiam.xchange.currency.CurrencyPair;

import java.util.ArrayList;

/**
 * Created by julianliebl on 09.03.14.
 */
public class ExchangeHelper {

    public interface SupportedCurrencyPairsCallbackListeners{
        public void onSupportedCurrencyPairsCallback(ArrayList<CurrencyPair> currencyPairs);
    }

    public void requestSupportedCurrencyPairsForExchange(Exchange exchange, SupportedCurrencyPairsCallbackListeners callback){
        new GetCurrencyPairTask().execute(exchange, callback);
    }

    private class GetCurrencyPairTask extends AsyncTask<Object, Integer, ArrayList<CurrencyPair>> {
        SupportedCurrencyPairsCallbackListeners callbackListener;

        @Override
        protected ArrayList<CurrencyPair> doInBackground(Object... objects) {
            ArrayList<CurrencyPair> newList = new ArrayList<CurrencyPair>();

            if(objects == null || objects.length < 2){
                return newList;
            }

            callbackListener = (SupportedCurrencyPairsCallbackListeners) objects[1];

            int currentSelectedExchange = -1;

            int count = 0;
            for(Exchange exchange : Constants.getKnownExchanges()){
                if(exchange.equals(objects[0])){
                    currentSelectedExchange = count;
                }
                count++;
            }

            if(count == -1){
                return newList;
            }


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
            callbackListener.onSupportedCurrencyPairsCallback(newList);
        }
    }
}
