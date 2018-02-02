package com.upb.meetingrooms.utils;

public class StringUtils {

    public static String getHourString(double theHour) {
        String hourString = "";
        int theHourInt = (int) theHour;

        if(theHour != theHourInt) {
            hourString = theHourInt +":"+"30";
        } else {
            hourString = theHourInt +":"+"00";
        }
        return hourString;
    }
}
