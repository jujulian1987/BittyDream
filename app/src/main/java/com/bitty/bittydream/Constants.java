package com.bitty.bittydream;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.bitstamp.BitstampExchange;
import com.xeiam.xchange.btce.BTCEExchange;

import java.util.ArrayList;

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
        ExchangeSpecification exSpec = new ExchangeSpecification(BTCEExchange.class);
        exSpec.setSslUri("https://btc-e.com");
        knownExchanges.add(ExchangeFactory.INSTANCE.createExchange(exSpec));

        //BITSTAMP
        ExchangeSpecification exSpecBistamp = new ExchangeSpecification(BitstampExchange.class);
        knownExchanges.add(ExchangeFactory.INSTANCE.createExchange(exSpecBistamp));
    }

    public static ArrayList<Exchange> getKnownExchanges(){
        return knownExchanges;
    }
}
