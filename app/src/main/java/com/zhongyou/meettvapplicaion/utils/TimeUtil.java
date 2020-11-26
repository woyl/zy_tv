package com.zhongyou.meettvapplicaion.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by whatisjava on 17-2-9.
 */

public class TimeUtil {
    public static final String TAG = "TimeUtil";

    public static String timeFormat(String timestr) {
        SimpleDateFormat sdfTemp = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS", Locale.getDefault());
        long timestamp = 0;
        try {
            timestamp = sdfTemp.parse(timestr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = (System.currentTimeMillis() - timestamp) / 1000;
        long monthSecond = 86400 * 30;
        long daySecond = 86400;
        long hourSecond = 3600;
        long minuteSecond = 60;
        if (time / monthSecond > 0) {
            return timestr.substring(0, 10);
        } else if (time / daySecond > 0) {
            return time / daySecond + "天前";
        } else if (time / hourSecond > 0) {
            return time / hourSecond + "小时前";
        } else if (time / minuteSecond > 0) {
            return time / minuteSecond + "分钟前";
        } else {
            return "刚刚";
        }
    }

    public static boolean compare(String time1, String time2) {
        //如果想比较日期则写成"yyyy-MM-dd"就可以了
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS", Locale.getDefault());
        //将字符串形式的时间转化为Date类型的时间
        long timestamp1 = 0;
        long timestamp2 = 0;
        try {
            timestamp1 = sdf.parse(time1).getTime();
            timestamp2 = sdf.parse(time2).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (timestamp1 - timestamp2 < 0)
            return true;
        else
            return false;
    }

    public static boolean isEnd(String time1) {
        {
            Logger.d(TAG,"time1 "+time1);
            //如果想比较日期则写成"yyyy-MM-dd"就可以了
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS" ,Locale.getDefault());
            //将字符串形式的时间转化为Date类型的时间
            long timestamp1 = 0;
            long timestamp2 = System.currentTimeMillis();

            try {
                timestamp1 = sdf.parse(time1).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Logger.d(TAG,"timestamp1 "+timestamp1);
            Logger.d(TAG,"timestamp2 "+timestamp2);

            if (timestamp1  < timestamp2)
                return true;
            else
                return false;
        }

    }

    public static boolean isEnd(String time1,String time2) {
        {
            Logger.d(TAG,"time1 "+time1);
            //如果想比较日期则写成"yyyy-MM-dd"就可以了
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS" ,Locale.getDefault());
            //将字符串形式的时间转化为Date类型的时间
            long timestamp1 = 0;
            long timestamp2 = 0;

            try {
                timestamp1 = sdf.parse(time1).getTime();
                timestamp2 =  sdf.parse(time2).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Logger.d(TAG,"timestamp1 "+timestamp1);
            Logger.d(TAG,"timestamp2 "+timestamp2);

            if (timestamp1  < timestamp2)
                return true;
            else
                return false;
        }

    }

    /**
     * 返回精确到天数时间 2017-04-01
     *
     * @param time
     * @return
     */
    public static String getDayTime(String time) {
        Logger.d(TAG,"time "+time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd" ,Locale.getDefault());
        try {
            Date date=sdf.parse(time);
            time =sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Logger.d(TAG,"time day "+time);
        return time;
    }

    /**
     * 根据时间戳，获得[小时:分钟]的显示格式
     * @param time
     * @return
     */
    public static String getHourAndMinute(String time) {
        String regex = "(\\d\\d:\\d\\d)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(time);

        while (matcher.find()){
            return matcher.group();
        }
        return "";
    }

    public static int diffDays(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentStr = sdf.format(new Date());
        int days=1;
        try {
            Date currentDate = sdf.parse(currentStr); //把时间1和时间2的时分秒毫秒全置为0
            Date diffDate = sdf.parse(time);
            days = differentDaysByMillisecond(diffDate, currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG,"diffDays "+e.getMessage());
        }
//        LogUtils.d(TAG,"time "+time+" days "+days);
        return days;
    }

    public static String getDayStr(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

     /**
     * 通过时间秒毫秒数判断两个时间的间隔,单纯需要天数,需要传入yyyy-MM-dd格式
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(Date date1, Date date2) {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
        return days;
    }

    public static boolean isToday(String time){
        return  diffDays(time) == 0;
    }

//    public static String getMonth(String time){
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        // 创建 Calendar 对象
//        Calendar calendar = Calendar.getInstance();
//        try {
//            Date date = sdf.parse(time);
//            calendar.setTime(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//    }


    public static String getMonth(String time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM", Locale.getDefault());
        String str="";
        try {
            Date date = sdf.parse(time);
            str = sdf2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getDay(String time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd", Locale.getDefault());
        String str="";
        try {
            Date date = sdf.parse(time);
            str = sdf2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getyyyyMMddHHmm(String time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String str="";
        try {
            Date date = sdf.parse(time);
            str = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }


    public static String getMonthDayHM(String time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        String str="";
        try {
            Date date = sdf.parse(time);
            str = sdf2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getHM(String time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String str="";
        try {
            Date date = sdf.parse(time);
            str = sdf2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 是否同一天
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isDayEqual(String time1,String time2){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String timeDay1="";
        String timeDay2="";
        try {
            Date date1 = sdf.parse(time1);
            Date date2 = sdf.parse(time2);
            timeDay1 = sdf2.format(date1);
            timeDay2 = sdf2.format(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeDay1.equals(timeDay2);

    }

    /**
     *
     * 将字符串转为时间戳
     * */
    public static long getStringToDate(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try{
            date = dateFormat.parse(dateString);
        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }
}