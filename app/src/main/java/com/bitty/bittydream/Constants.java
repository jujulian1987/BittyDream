package com.bitty.bittydream;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.bitcoincharts.BitcoinChartsExchange;
import com.xeiam.xchange.bitcurex.BitcurexExchange;
import com.xeiam.xchange.bitstamp.Bitstamp;
import com.xeiam.xchange.bitstamp.BitstampExchange;
import com.xeiam.xchange.btcchina.BTCChina;
import com.xeiam.xchange.btcchina.BTCChinaExchange;
import com.xeiam.xchange.btce.BTCEExchange;
import com.xeiam.xchange.campbx.CampBXExchange;
import com.xeiam.xchange.kraken.KrakenExchange;
import com.xeiam.xchange.virtex.VirtExExchange;

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
        knownExchanges.add(ExchangeFactory.INSTANCE.createExchange(BTCEExchange.class.getName()));

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

    }

    public static ArrayList<Exchange> getKnownExchanges(){
        return knownExchanges;
    }
}
