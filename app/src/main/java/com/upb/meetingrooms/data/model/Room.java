package com.upb.meetingrooms.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room {

    private static double PERIOD_DURATION = 0.5; // aka 30 minutes

    private long id;
    private String name;

    private Map<String, Boolean> ocupationMap;
    private ArrayList<Interval> intervals;


    public Room(long id, String name) {
        this.id = id;
        this.name = name;
        ocupationMap = new HashMap<>();
        for (double i = 0; i < 24; i+=PERIOD_DURATION) {
            int key = (int) (i*10);
            ocupationMap.put(key+"",false);
        }

        intervals = new ArrayList<>();
    }

    public Room() {
        intervals = new ArrayList<>();
        ocupationMap = new HashMap<>();
        for (double i = 0; i < 24; i+=PERIOD_DURATION) {
            int key = (int) (i*10);
            ocupationMap.put(key+"",false);
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Map<String, Boolean> getOcupationMap() {
        return ocupationMap;
    }

    public ArrayList<Interval> getIntervals() {
        return intervals;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param timeStart      the time when the reservation starts
     *                       can take values like (12, 12.5, 13, 13.5 etc.)
     *                       <p>
     *                       1 Period = 30 minutes
     * @param nrPeriods      the number of periods you want to reserve the room for
     *                       for example if you give the timeStart = 12 and nrPeriods = 3
     *                       you will reserve the room from 12:00 until 13:30
     * @param userDispalyName the user identifier from firebase. We use this to know which user reserves the room.
     *
     * @param ddateInMillis used for when we need to cancel a room, so we know on which date the interval is reserverd
     *
     * @return the interval we just created for the period we reserved
     */
    public Interval reserverRoom(double timeStart, int nrPeriods, String userDispalyName,long ddateInMillis) {

        for (double i = timeStart; i < timeStart + (PERIOD_DURATION * nrPeriods); i += PERIOD_DURATION) {
            int key = (int) (i*10);

            ocupationMap.put(key+"", true);
        }
        Interval interval = new Interval(userDispalyName, timeStart, nrPeriods, ddateInMillis, this.getId());
        intervals.add(interval);
        return interval;
    }

    /**
     * @param timeStart the time from which we start to see how manny ocupationMap we have
     * @return the nr of free ocupationMap. This way we can see for how much we can reserve the room given a start time.
     */
    public int getNrOfFreeIntervals(double timeStart) {
        int count = 0;

        for (double i = timeStart; i < 24; i += PERIOD_DURATION) {
            String mapKey = ((int)(i * 10))+"";
            if (!ocupationMap.get(mapKey)) {
                count++;
            } else {
                break;
            }
        }

        return count;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ocupationMap=" + ocupationMap +
                ", intervals=" + intervals +
                '}';
    }
}
