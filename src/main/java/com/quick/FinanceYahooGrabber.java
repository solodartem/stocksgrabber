package com.quick;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class FinanceYahooGrabber {

    public static final String OUT_PUT_FOLDER_KEY = "output.folder";
    public static final String TEMP_FOLDER_KEY = "temp.folder";
    public static final String DOWNLOAD_FOLDER_KEY = "downloads.folder";
    public static final String SETTINGS_FILE_KEY = "settings.file";

    public static final String newLine = System.getProperty("line.separator");

    public static String outputFolder;
    public static String tempFolder;
    public static String settingsFile;
    public static String downloadFolder;
    private static ChromeDriver WEB_DRIVER;

    static private class Interval {

        private FormattedDate start;
        private FormattedDate end;

        public Interval(String start, String end) {
            this.start = new FormattedDate(start);
            this.end = new FormattedDate(end);
        }

        @Override
        public String toString() {
            return "Interval{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FinanceYahooGrabber.class);

    private static Random random = new Random();

    static public int gerRandomInterval() {
        int min = 4;
        int max = 15;
        return random.nextInt(max - min + 1) + min;
    }

    static private String URL_PATTERN = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1d&filter=history&frequency=1d";

    private String provider = null;

    private File outputFile = null;

    private int currentInterVal = 0;

    private List<Interval> itervals = new LinkedList<Interval>() {{
        add(new Interval("2015-01-01", "2015-12-31"));
        add(new Interval("2016-01-01", "2016-12-31"));
        add(new Interval("2017-01-01", "2017-12-31"));
        add(new Interval("2018-01-01", "2018-12-31"));
        add(new Interval("2019-01-01", new FormattedDate(DateUtils.addDays(new Date(), -1)).toString()));
    }};

    public boolean hasNext() {
        return currentInterVal < this.itervals.size();
    }

    public int loadAndParseCurrentInterval() {
        try {
            Interval interval = this.itervals.get(currentInterVal);
            LOGGER.debug("Start downloading and parsing interval {} for provider {}", interval, this.provider);
            String pageURL = getURL(interval);
            LOGGER.debug("Going to download page {}", pageURL);

            try {
                WEB_DRIVER.get(pageURL);
            } catch (WebDriverException e) {
                LOGGER.debug("Unable to load page", e);
                return -1;
            }
            //

            List<WebElement> elements = null;
            int attemptsCount = 0;
            do {
                LOGGER.debug("Wait attempt {} while page is loading", attemptsCount);
                Thread.sleep(1 * 1000);
                attemptsCount++;
                elements = WEB_DRIVER.findElements(By.cssSelector("a > span:not([data-reactid]"));
            } while (elements.size() == 0 && attemptsCount < 5);

            if (elements.size() != 1) {
                LOGGER.error("Unable to get download URL from page: {}", pageURL);
                return -1;
            }

            elements.get(0).click();
            Thread.sleep(2 * 1000);
            File file = GrabberFileUtils.moveDownloadedFile(downloadFolder, Paths.get(tempFolder, this.provider + interval.start + "_" + interval.end + ".csv").toFile());
            int result = mergeData(this.itervals.get(currentInterVal), file);
            currentInterVal++;
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private int mergeData(Interval interval, File csvDownloadedFile) {
        LOGGER.debug("Merging data from file {}", csvDownloadedFile.getAbsoluteFile());
        try {
            List<String> lines = FileUtils.readLines(csvDownloadedFile, "UTF-8");
            LOGGER.debug("Merged {} lines from file {} to output file {}", lines.size(), csvDownloadedFile.getAbsoluteFile(), outputFile.getAbsoluteFile());
            int mergedLines = 0;
            for (String csvLine : lines.subList(1, lines.size())) {
                String[] fields = csvLine.split(",");
                FormattedDate date = new FormattedDate(fields[0]);
                if (interval.start.addDays(-1).before(date.getDate()) && interval.end.addDays(1).after(date.getDate())) {
                    FileUtils.writeStringToFile(outputFile, String.join(",", this.provider, fields[0], fields[5] + newLine), "UTF-8", true);
                    mergedLines++;
                }
            }
            return mergedLines;
        } catch (IOException e) {
            return -1;
        }
    }

    public FinanceYahooGrabber(String provider) {
        this.provider = provider;
        this.outputFile = Paths.get(outputFolder, provider + ".csv").toFile();
        if (outputFile.exists()) {
            outputFile.delete();
        }
    }

    private String getURL(Interval interval) {
        return String.format(URL_PATTERN, this.provider, interval.start.toJSInt(), interval.end.toJSInt());
    }

    private static void loadSettings() {
        outputFolder = System.getProperty(OUT_PUT_FOLDER_KEY);
        LOGGER.debug("Using output folder: {}", outputFolder);
        tempFolder = System.getProperty(TEMP_FOLDER_KEY);
        LOGGER.debug("Using temp folder: {}", tempFolder);
        settingsFile = System.getProperty(SETTINGS_FILE_KEY);
        LOGGER.debug("Using output settings file: {}", settingsFile);
        downloadFolder = System.getProperty(DOWNLOAD_FOLDER_KEY);
        LOGGER.debug("Using download folder: {}", downloadFolder);

        Map<String, String> prefs = new Hashtable<>();
        prefs.put("download.prompt_for_download", "false");
        prefs.put("download.default_directory", downloadFolder);
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);

        File driverFile = new File("src/resources/chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", driverFile.getAbsolutePath());
        LOGGER.debug("Loaded chrome WEB_DRIVER {}", driverFile.getAbsoluteFile());

        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);

        WEB_DRIVER = new ChromeDriver(capabilities);
    }

    static private int grabProvider(String provider) {
        FinanceYahooGrabber grabber = new FinanceYahooGrabber(provider);
        int totallyRecordsMerged = 0;
        while (grabber.hasNext()) {
            int sleepInterval = gerRandomInterval();
            LOGGER.debug("Going to sleep on time: {} secs", sleepInterval);
            try {
                Thread.sleep(sleepInterval * 1000);
            } catch (InterruptedException e) {
                LOGGER.error("Exception during sleep", e);
            }
            LOGGER.debug("Wake up from sleeping thread!!");

            int result = grabber.loadAndParseCurrentInterval();

            totallyRecordsMerged += result;
            if (result == -1) {
                grabber.outputFile.delete();
                LOGGER.error("Exception during processing provider: {}", grabber.provider);
                break;
            }
        }
        LOGGER.debug("For provider {} merged total records: {}", grabber.provider, totallyRecordsMerged);
        return totallyRecordsMerged;
    }

    public static void main(String[] args) {
        loadSettings();
        File reportFile = Paths.get(outputFolder, "report.txt").toFile();
        if (reportFile.exists()) {
            reportFile.delete();
        }

        Map<String, Integer> providerResults = new HashMap<>();
        try {
            List<String> providers = FileUtils.readLines(Paths.get(settingsFile).toFile(), "UTF-8");
            for (String provider : providers) {
                int providerMergedFiles = grabProvider(provider);
                providerResults.put(provider, providerMergedFiles);
                FileUtils.writeStringToFile(reportFile, String.join(",", provider, providerMergedFiles + newLine), "UTF-8", true);
            }
        } catch (IOException e) {
            LOGGER.debug("Settings file {} not found.", settingsFile);
        }
        System.out.println(Arrays.toString(providerResults.entrySet().toArray()));
    }

}
