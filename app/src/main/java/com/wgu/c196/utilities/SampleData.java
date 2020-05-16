package com.wgu.c196.utilities;

import com.wgu.c196.model.TermEntity;

import java.util.*;

public class SampleData {
    private static final String TITLE_1 = "TERM 1";
    private static final String TITLE_2 = "TERM 2";
    private static final String TITLE_3 = "TERM 3";
    private static final String TITLE_4 = "TERM 4";
    private static final String TITLE_5 = "TERM 5";

    private static Date getDate(int diff) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.DATE, diff);
        return cal.getTime();
    }

    public static List<TermEntity> getTerms() {
        List<TermEntity> terms = new ArrayList<>();

        terms.add(new TermEntity(1, TITLE_1, getDate(0), getDate(1)));
        terms.add(new TermEntity(1, TITLE_2, getDate(2), getDate(3)));
        terms.add(new TermEntity(1, TITLE_3, getDate(4), getDate(5)));
        terms.add(new TermEntity(1, TITLE_4, getDate(5), getDate(6)));
        terms.add(new TermEntity(1, TITLE_5, getDate(7), getDate(8)));

        return terms;
    }

}
