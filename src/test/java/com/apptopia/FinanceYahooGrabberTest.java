package com.apptopia;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;

import static junit.framework.TestCase.assertEquals;

public class FinanceYahooGrabberTest {

    private FinanceYahooGrabber grabber;

    @Before
    public void createGrabber() throws ParseException {
        this.grabber =
                new FinanceYahooGrabber("2015-01-01", "2019-02-01", "output.csv");
    }


    @Test
    public void genURL() {
        FormattedDate start = new FormattedDate("2018-02-12");
        FormattedDate end = new FormattedDate("2019-02-12");
      //  assertEquals("https://finance.yahoo.com/quote/AAPL/history?period1=1518386400&period2=1549922400&interval=1d&filter=history&frequency=1d", this.grabber.genURL(start, end));
    }
}