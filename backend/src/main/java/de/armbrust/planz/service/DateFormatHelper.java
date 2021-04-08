package de.armbrust.planz.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        String stringDate = outputFormat.format(tempDate);

        return stringDate;
    }

}
