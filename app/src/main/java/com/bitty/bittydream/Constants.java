package com.bitty.bittydream;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.bitcurex.BitcurexExchange;
import com.xeiam.xchange.bitstamp.BitstampExchange;
import com.xeiam.xchange.btcchina.BTCChinaExchange;
import com.xeiam.xchange.btce.v3.BTCEExchange;
import com.xeiam.xchange.bter.BTERExchange;
import com.xeiam.xchange.campbx.CampBXExchange;
import com.xeiam.xchange.coinbase.CoinbaseExchange;
import com.xeiam.xchange.cryptsy.CryptsyExchange;
import com.xeiam.xchange.kraken.KrakenExchange;
import com.xeiam.xchange.virtex.v2.VirtExExchange;

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

        //BTC CHINA
        knownExchanges.add(ExchangeFactory.INSTANCE.createExchange(BTCChinaExchange.class.getName()));

        //BITCUREX
        knownExchanges.add(ExchangeFactory.INSTANCE.createExchange(BitcurexExchange.class.getName()));

        //VIRTEX
        knownExchanges.add(ExchangeFactory.INSTANCE.createExchange(VirtExExchange.class.getName()));

        //CAMPBX
        knownExchanges.add(ExchangeFactory.INSTANCE.createExchange(CampBXExchange.class.getName()));

        //CRYPTSY
        knownExchanges.add(ExchangeFactory.INSTANCE.createExchange(CryptsyExchange.class.getName()));

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
