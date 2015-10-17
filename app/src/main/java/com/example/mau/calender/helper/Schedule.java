package com.example.mau.calender.helper;

/**
 * Created by mau on 10/17/2015.
 */
public class Schedule {
    public String roomNumber;
    public String slot;
    public String subjectName;

    public Schedule(){

    }

    public Schedule(String subjectName, String roomNumber, String slot){
        this.subjectName = subjectName;
        this.roomNumber = roomNumber;
        this.slot = slot;
    }
    
}
