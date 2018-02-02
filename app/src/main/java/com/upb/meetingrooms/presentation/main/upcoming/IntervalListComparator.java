package com.upb.meetingrooms.presentation.main.upcoming;


import com.upb.meetingrooms.data.model.Interval;

import java.util.Calendar;
import java.util.Comparator;

public class IntervalListComparator implements Comparator<Interval> {

    @Override
    public int compare(Interval o1, Interval o2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(o1.getDateInMillis());
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(o2.getDateInMillis());

        int yearDif = c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
        int monthDif = c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
        int dayDif = c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);

        if(yearDif == 0) {
            if(monthDif == 0) {
                if(dayDif == 0){
                    double v = o1.getStartPeriod() - o2.getStartPeriod();
                    return (int) v;
                } else {
                    return dayDif;
                }
            } else {
                return monthDif;
            }
        } else {
            return yearDif;
        }
    }

}
