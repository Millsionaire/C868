package com.wgu.c196.services;

import com.wgu.c196.utilities.Constants;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TimeFormatService {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
}
