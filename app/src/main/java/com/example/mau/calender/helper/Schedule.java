package com.example.mau.calender.helper;

/**
 * Created by mau on 10/17/2015.
 */
public class Schedule {
    private long id;
    public String subjectName;
    public String roomNumber;
    public int slot;
    public String day;


    public Schedule(){

    }

    public Schedule(String subjectName, String roomNumber, int slot, String day){
        this.subjectName = subjectName;
        this.roomNumber = roomNumber;
        this.slot = slot;
        this.day = day;
    }

    //Setters and Getters
    public long getId(){
        return id;
    }                                                                   //id

    public void setId(long id){
        this.id = id;
    }
                                                                        //subjectName
    public String getSubjectName(){
        return subjectName;
    }

    public void setSubjectName(String subjectName){
        this.subjectName = subjectName;
    }
                                                                        //roomNumber
    public String getRoomNo(){
        return roomNumber;
    }

    public void setRoomNo(String roomNumber){
        this.roomNumber = roomNumber;
    }
                                                                        //slot
    public int getSlot(){
        return slot;
    }

    public void setSlot(int slot){
        this.slot = slot;
    }
                                                                        //day
    public String getDay(){
        return day;
    }

    public void setDay(String day){
        this.day = day;
    }

    // Will be used by Arrayadapter in the listview
    @Override
    public String toString(){
        //return comment;
        return "";
    }
}
