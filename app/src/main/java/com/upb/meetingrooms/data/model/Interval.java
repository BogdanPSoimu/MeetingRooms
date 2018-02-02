package com.upb.meetingrooms.data.model;

public class Interval {

    private long dateInMillis;
    private String userName;
    private double startPeriod;
    private int nrPeriods;

    private long roomId;

    public Interval() {
    }

    public Interval(String userName, double startPeriod, int nrPeriods,long dateKey,long roomId) {
        this.userName = userName;
        this.startPeriod = startPeriod;
        this.nrPeriods = nrPeriods;
        this.dateInMillis = dateKey;
        this.roomId = roomId;
    }

    public String getUserName() {
        return userName;
    }

    public double getStartPeriod() {
        return startPeriod;
    }

    public int getNrPeriods() {
        return nrPeriods;
    }

    public void setDateInMillis(long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setStartPeriod(double startPeriod) {
        this.startPeriod = startPeriod;
    }

    public void setNrPeriods(int nrPeriods) {
        this.nrPeriods = nrPeriods;
    }

    public long getDateInMillis() {
        return dateInMillis;
    }

    public long getRoomId() {
        return roomId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Interval interval = (Interval) o;

        if (dateInMillis != interval.dateInMillis) return false;
        if (Double.compare(interval.startPeriod, startPeriod) != 0) return false;
        if (nrPeriods != interval.nrPeriods) return false;
        if (roomId != interval.roomId) return false;
        return userName != null ? userName.equals(interval.userName) : interval.userName == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (dateInMillis ^ (dateInMillis >>> 32));
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        temp = Double.doubleToLongBits(startPeriod);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + nrPeriods;
        result = 31 * result + (int) (roomId ^ (roomId >>> 32));
        return result;
    }
}
