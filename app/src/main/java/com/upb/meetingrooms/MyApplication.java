package com.upb.meetingrooms;

import android.app.Application;
import android.util.SparseArray;

import com.upb.meetingrooms.data.model.Room;

public class MyApplication extends Application {

    private static SparseArray<Room> roomSparseArray;

    @Override
    public void onCreate() {
        super.onCreate();

        roomSparseArray = new SparseArray<>();
        roomSparseArray.append(0,new Room(0, "Mont Blanc Meeting Room"));
        roomSparseArray.append(1,new Room(1, "Omu Meeting Room"));
        roomSparseArray.append(2,new Room(2, "Everest Meeting Room"));
        roomSparseArray.append(3,new Room(3, "Vesuvius Meeting Room"));
        roomSparseArray.append(4,new Room(4, "Fuji Meeting Room"));
        roomSparseArray.append(5,new Room(5, "Negoiu Meeting Room"));

    }

    public static SparseArray<Room> getRoomSparseArray() {
        return roomSparseArray;
    }
}
