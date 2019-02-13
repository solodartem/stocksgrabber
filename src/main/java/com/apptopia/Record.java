package com.apptopia;

import static com.apptopia.App.CSV_DELIMITER;

public class Record {

    private String ticker;
    private String date;
    private Float closePrice;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Float getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(Float closePrice) {
        this.closePrice = closePrice;
    }

    @Override
    public String toString() {
        return "Record{" +
                "ticker='" + ticker + '\'' +
                ", date='" + date + '\'' +
                ", closePrice=" + closePrice +
                '}';
    }

    public String toCSV() {
        return ticker + CSV_DELIMITER + new FormattedDate(this.date).toString() + CSV_DELIMITER + closePrice;
    }

}
