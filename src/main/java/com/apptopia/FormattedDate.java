package com.apptopia;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class FormattedDate extends Date {

    static public final String DATE_PATTERN = "yyyy-MM-dd";

    private static final Logger LOGGER = LoggerFactory.getLogger(FormattedDate.class);

    private Date date;

    public FormattedDate(Date date) {
        this.date = date;
    }

    public FormattedDate(String dateAsString) {
        try {
            this.date = DateUtils.truncate(DateUtils.parseDate(dateAsString, DATE_PATTERN), Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            LOGGER.error("Unable to parse date from string {}", dateAsString);
            throw new RuntimeException(e);
        }
    }

    public FormattedDate minusDays(int daysAmount) {
        return new FormattedDate(DateUtils.addDays(this, -1*daysAmount));
    }

    public FormattedDate addDays(int daysAmount) {
        return new FormattedDate(DateUtils.truncate(DateUtils.addDays(this, daysAmount)));
    }

    public FormattedDate butAfter(FormattedDate formattedDate) {
        return this.after(formattedDate)?this:formattedDate;
    }

    public long toJSInt() {
        return this.date.getTime() / 1000;
    }

    @Override
    public String toString() {
        return DateFormatUtils.format(this.date, DATE_PATTERN);
    }
}
