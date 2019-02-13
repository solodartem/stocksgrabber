package com.quick;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormattedDate {

    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    private static final Logger LOGGER = LoggerFactory.getLogger(FormattedDate.class);

    private Date date;

    public FormattedDate(Date date) {
        this.date = date;
    }

    public FormattedDate(String dateAsString) {
        try {
            this.date = DATE_FORMATTER.parse(dateAsString);
        } catch (ParseException e) {
            LOGGER.error("Unable to parse date from string {}", dateAsString);
            throw new RuntimeException(e);
        }
    }

    public long toJSInt() {
        return this.date.getTime() / 1000;
    }

    @Override
    public String toString() {
        return DATE_FORMATTER.format(this.date);
    }

    public Date addDays(int daysAmount) {
        return DateUtils.addDays(this.date, daysAmount);

    }

    public Date getDate() {
        return date;
    }
}
