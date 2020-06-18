package com.wgu.c196.services;

import android.content.Context;
import com.wgu.c196.utilities.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeFormatService {
    public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT, Locale.US);
    public static SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);

    public static Date getFormattedDate(String dateString, String errorMessage, Context context) {
        try {
            return TimeFormatService.dateTimeFormat.parse(dateString);
        } catch (Exception e) {
            AlertDialogService.showAlert(errorMessage, context);
        }
        return null;
    }

    public static String getDateString(Date date) { return dateFormat.format(date); }
}
