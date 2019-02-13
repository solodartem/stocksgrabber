package com.apptopia;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

public class FinanceYahooGrabber extends Grabber {

    private static final Logger LOGGER = LoggerFactory.getLogger(FinanceYahooGrabber.class);
    private String URL_PATTERN = "https://finance.yahoo.com/quote/AAPL/history?period1=%d&period2=%d&interval=1d&filter=history&frequency=1d";

    private FormattedDate batchStartDate;
    private FormattedDate batchEndDate;


    public FinanceYahooGrabber(String grabStartedDate, String grabEndDate, String outputFile) throws ParseException {
        super(grabStartedDate, grabEndDate, outputFile);
        rollBackBatchInterval();
    }

    protected void rollBackBatchInterval() {
        int rollOnDuration = 10;
        FormattedDate endDate = this.batchEndDate == null ? this.grabEndDate.addDays(1) : this.batchEndDate;
        this.batchStartDate = endDate.minusDays(rollOnDuration + 1).butAfter(this.grabStartDate);
        this.batchEndDate = endDate.minusDays(1).butAfter(this.grabStartDate);
    }

/*
    protected String genURL(Date start, Date end) {
        return String.format(URL_PATTERN, DateUtilsToRemove.toJSInt(start), DateUtilsToRemove.toJSInt(end));
    }
*/

    @Override
    public int readNextBatch() {
        this.LOGGER.debug("Current batch time slot from {} till {}", this.batchStartDate, this.batchEndDate);


        return 0;
    }

    @Override
    public boolean hasNext() {
        boolean result = DateUtils.isSameDay(this.grabStartDate, this.batchStartDate);
        this.LOGGER.debug("Has next returns {} because grabStartDate is {} and batchStartDate date is {}", result, this.grabStartDate, this.batchStartDate);
        return result;
    }

    public static void main(String[] args) throws ParseException {
        // LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        // print logback's internal status
        //    StatusPrinter.print(lc);

        FinanceYahooGrabber grabber = new FinanceYahooGrabber("2015-01-01", "2019-02-12", "sd");

        // while (grabber.hasNext())
        {
            grabber.readNextBatch();
        }
    }

}
