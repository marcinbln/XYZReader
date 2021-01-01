package com.example.xyzreader;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class Constants {

    static final String EXTRA_RECIPE_ID = "OPEN_BOOK";
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");

    public static SimpleDateFormat outputFormat = new SimpleDateFormat();
    public static GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);


}
