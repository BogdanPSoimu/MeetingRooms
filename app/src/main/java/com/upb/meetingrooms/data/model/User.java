package com.upb.meetingrooms.data.model;

import java.util.ArrayList;


public class User {

    private String name;
    private ArrayList<Room> rooms;

    public User(String name, ArrayList<Room> rooms) {
        this.name = name;
        this.rooms = rooms;
    }

    public User() {

    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    public String getName() {
        return name;
    }
}
