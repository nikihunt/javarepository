package com.date;

import com.log.LoggerFactory;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.util.Print.println;

/**
 * Created by dell on 2015/11/24.
 */
public class DateUtil {
    private static final String dateFormat = "^(19|20)\\d\\d-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01]) [0-2][0-3]:[0-5][0-9]:[0-5][0-9]$";
    private static final Logger logger = LoggerFactory.getInstance().getLogger(DateUtil.class);

    public static String getFormatDate(long millisecond){
        java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format1.format(new Date(millisecond));
    }

    public static boolean checkDate(String date){
        Pattern pattern = Pattern.compile(dateFormat);
        Matcher matcher = pattern.matcher(date);
        if(matcher.matches())
            return true;
        else {
            logger.error("date "+date+" is invalid");
            return false;
        }
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
