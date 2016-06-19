package io.razem.bittydream;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bitcurex.BitcurexExchange;
import org.knowm.xchange.bitstamp.BitstampExchange;
import org.knowm.xchange.btce.v3.BTCEExchange;
import org.knowm.xchange.bter.BTERExchange;
import org.knowm.xchange.campbx.CampBXExchange;
import org.knowm.xchange.coinbase.CoinbaseExchange;
import org.knowm.xchange.kraken.KrakenExchange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by julianliebl on 03.03.14.
 */
public class Constants {
    private static final ArrayList<Exchange> knownExchanges;

    public static final String PREF_PAIRS = "pairs";
    public static final String PREF_MARKETS = "markets";

    static{
        knownExchanges = new ArrayList<Exchange>();

        //BTCE
        knownExchanges.add(ExchangeFactory.INSTANCE.createExchange(BTCEExchange.class.getName()));

        //BTER
        knownExchanges.add(ExchangeFactory.INSTANCE.createExchange(BTERExchange.class.getName()));

        //BITSTAMP
        knownExchanges.add(ExchangeFactory.INSTANCE.createExchange(BitstampExchange.class.getName()));

        //KRAKEN
        knownExchanges.add(ExchangeFactory.INSTANCE.createExchange(KrakenExchange.class.getName()));

        //BITCOIN CHARTS
        //TODO: IMPLEMENT
        //knownExchanges.add((ExchangeFactory.INSTANCE.createExchange(BitcoinChartsExchange.class.getName())));

        //BITCUREX
        knownExchanges.add(ExchangeFactory.INSTANCE.createExchange(BitcurexExchange.class.getName()));

        //CAMPBX
        knownExchanges.add(ExchangeFactory.INSTANCE.createExchange(CampBXExchange.class.getName()));

        //COINBASE
        knownExchanges.add(ExchangeFactory.INSTANCE.createExchange(CoinbaseExchange.class.getName()));

        Collections.sort(knownExchanges, new Comparator<Exchange>() {
            @Override
            public int compare(Exchange exchange, Exchange exchange2) {
                return exchange.getClass().getName().compareTo(exchange2.getClass().getName());
            }
        });

    }

    public static ArrayList<Exchange> getKnownExchanges(){
        return knownExchanges;
    }
}
