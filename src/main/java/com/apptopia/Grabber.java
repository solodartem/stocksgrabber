package com.apptopia;

import java.text.ParseException;

public abstract class Grabber {

    protected final FormattedDate grabStartDate;
    protected final FormattedDate grabEndDate;

    public Grabber(String grabStartedDate, String grabEndDate, String outputFile) throws ParseException {
        this.grabStartDate = new FormattedDate(grabStartedDate);
        this.grabEndDate = new FormattedDate(grabEndDate);
    }

    public abstract int readNextBatch();

    public abstract boolean hasNext();
}
