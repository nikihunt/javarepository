package com.date;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.log.LoggerFactory;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.util.Print.*;

/**
 * Created by dell on 2015/11/24.
 */
public class DateUtil {
    private static final Logger logger = LoggerFactory.getInstance().getLogger(DateUtil.class);

    public static String getFormatDate(long millisecond,String dateFormat){
        DateFormat format1 = new SimpleDateFormat(dateFormat);
        return format1.format(new Date(millisecond));
    }

    public static long getMillionSeconds(String date,String dateFormat){
        long millionS = 0;
        if(checkDate(date,dateFormat)){
            DateFormat format1 = new SimpleDateFormat(dateFormat);
            try {
                Date d = format1.parse(date);
                millionS = d.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return millionS;
    }

    public static boolean checkDate(String date,String dateFormat){
        if(Strings.isNullOrEmpty(date)){
            logger.error("date "+date+" is empty");
            return false;
        }
        else if(Strings.isNullOrEmpty(dateFormat)){
            logger.error("dateformat "+dateFormat+" is empty");
            return false;
        }
        else{
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            sdf.setLenient(false);
            try {
                sdf.parse(date);
                return true;
            } catch (ParseException e) {
                logger.error("date "+date+" is invalid with format"+dateFormat);
                return false;
            }
        }
    }

    public static List<String> splitDateByDay(String start,String end,String dateFormat){
        List<String> lst = Lists.newArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try{
            Date sdate = sdf.parse(start);
            Calendar scr = Calendar.getInstance();
            scr.setTime(sdate);
            Date edate = sdf.parse(end);
            Calendar ecr = Calendar.getInstance();
            ecr.setTime(edate);

            if(scr.get(1) == ecr.get(1) && scr.get(2) == ecr.get(2) && scr.get(5) == ecr.get(5)){
                lst.add(start);
                lst.add(end);
            }
            else{
                long stime = sdate.getTime();
                long etime = edate.getTime();
                while(stime < etime){
                    lst.add(sdf.format(scr.getTime()));
                    scr.set(scr.get(1),scr.get(2),scr.get(5),scr.get(10),scr.get(12),scr.get(13));
                    if(scr.get(2)+1 == 12 && scr.get(5) == 31){
                        scr.set(scr.get(1)+1,scr.get(2),scr.get(5),scr.get(10),scr.get(12),scr.get(13));
                    }
                    scr.roll(Calendar.DAY_OF_YEAR,true);
                    stime = scr.getTime().getTime();
                }
                lst.add(end);
            }
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return lst;
    }

    public static List<String> splitDate(String start,String end,String dateFormat,long gap){
        List<String> result = Lists.newArrayList();

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            Date sdate = sdf.parse(start);
            Date edate = sdf.parse(end);
            long stime = sdate.getTime();
            long etime = edate.getTime();
            if(stime == etime){
                result.add(start);
                result.add(end);
            }
            else if(stime < etime) {
                Calendar cr = Calendar.getInstance();
                long tmp = stime;
                for (; tmp <= etime; tmp += gap) {
                    cr.setTimeInMillis(tmp);
                    result.add(sdf.format(cr.getTime()));
                }
                if (tmp - gap < etime) {
                    result.add(end);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return result;
    }

    public static void main(String[] args){
//        String dateFormat = "yyyy-MM-dd HH:mm:ss";
//        long millisecond = 1445297723000L;
//        println(getFormatDate(millisecond,dateFormat));
//        millisecond = 1445602071000L;
//        println(getFormatDate(millisecond,dateFormat));
//
//        millisecond = 1445303028000L;
//        println(getFormatDate(millisecond,dateFormat));
//
//        millisecond = 1447084892508L;
//        println(getFormatDate(millisecond,dateFormat));
        //splitDate("2015-05-01 00:00:00","2017-01-10 00:00:00","yyyy-MM-dd HH:mm:ss",60*60*24*1000);
//        checkDate("2015-05-01 00:00","yyyy-MM-dd HH:mm:ss");
        print(getMillionSeconds("2016-01-20 16:55:20","yyyy-MM-dd HH:mm:ss"));
    }
}
