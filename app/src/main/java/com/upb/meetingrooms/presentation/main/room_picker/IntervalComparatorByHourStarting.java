package com.upb.meetingrooms.presentation.main.room_picker;

import com.upb.meetingrooms.data.model.Interval;

import java.util.Comparator;

public class IntervalComparatorByHourStarting implements Comparator<Interval> {
    @Override
    public int compare(Interval interval, Interval t1) {
        double v = interval.getStartPeriod() - t1.getStartPeriod();
        if (v == 0) return 0;
        return v > 0 ? 1 : -1;
    }
}
