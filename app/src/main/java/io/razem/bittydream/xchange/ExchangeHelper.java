package io.razem.bittydream.xchange;

import android.os.AsyncTask;
import android.util.Log;

import io.razem.bittydream.Constants;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by julianliebl on 09.03.14.
 */
public class ExchangeHelper {

    public interface SupportedCurrencyPairsCallbackListeners{
        void onSupportedCurrencyPairsCallback(ArrayList<CurrencyPair> currencyPairs);
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
                Exchange exchange = Constants.getKnownExchanges().get(currentSelectedExchange);
                exchange.remoteInit();
                for (CurrencyPair pair : exchange.getMetaData().getMarketMetaDataMap().keySet()) {
                    if(pair.counter != null && pair.base != null){
                        newList.add(pair);
                    }else{
                        Log.d("GetCurrencyPairTask", pair.toString());
                    }
                }
            }catch (Exception e){
                //WE DONT DO ANYTHING HERE. Instead we handle the empty list in onPostExecute
            }
            return newList;
        }

        protected void onPostExecute(ArrayList<CurrencyPair> newList) {
            Collections.sort(newList, new Comparator<CurrencyPair>() {
                @Override
                public int compare(CurrencyPair currencyPair, CurrencyPair currencyPair2) {
                    int result = currencyPair.base.compareTo(currencyPair2.base);
                    if(result == 0){
                        result = currencyPair.counter.compareTo(currencyPair2.counter);
                    }
                    return result;
                }
            });
            callbackListener.onSupportedCurrencyPairsCallback(newList);
        }
    }
}
