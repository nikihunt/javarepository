package com.Date;

import java.util.Date;

import static com.util.Print.println;

/**
 * Created by dell on 2015/11/24.
 */
public class date {
    public static String getFormatDate(long millisecond){
        java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format1.format(new Date(millisecond));
    }

    public static void main(String[] args){
        long millisecond = 1445297723000L;
        println(getFormatDate(millisecond));
        millisecond = 1445602071000L;
        println(getFormatDate(millisecond));

        millisecond = 1445303028000L;
        println(getFormatDate(millisecond));

        millisecond = 1447084892508L;
        println(getFormatDate(millisecond));
    }
}
