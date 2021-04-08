package de.armbrust.planz.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class DateFormatHelper {


    public String parseDateToSimpleStringDate(String dateToParse) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXXXX");
        OffsetDateTime parsedDate = OffsetDateTime.parse(dateToParse, dateTimeFormatter);
        DateTimeFormatter dateTimeFormatterRequired = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String stringDate = parsedDate.format(dateTimeFormatterRequired);

        return stringDate;
    }

    public String parseDateFromDDMMYYYYtoUTC(String dateToParse) throws ParseException {

        SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date tempDate = inputFormat.parse(dateToParse);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String stringDate = sdf.format(tempDate);

        return stringDate;
    }

}
