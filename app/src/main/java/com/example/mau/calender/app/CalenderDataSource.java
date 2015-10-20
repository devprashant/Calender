package com.example.mau.calender.app;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.mau.calender.helper.MySQLiteHelper;
import com.example.mau.calender.helper.Schedule;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mau on 10/19/2015.
 */
public class CalenderDataSource {

    int count =1;
    //Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_SUBJECT_NAME,
            MySQLiteHelper.COLUMN_ROOM_NO,
            MySQLiteHelper.COLUMN_SLOT,
            MySQLiteHelper.COLUMN_DAY };

    public CalenderDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createSchedule(String subject_name, String room_no, int slot, String day) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_SUBJECT_NAME, subject_name);
        values.put(MySQLiteHelper.COLUMN_ROOM_NO, room_no);
        values.put(MySQLiteHelper.COLUMN_SLOT, slot);
        values.put(MySQLiteHelper.COLUMN_DAY, day);

        long insertId = database.insert(MySQLiteHelper.TABLE_CALENDER, null, values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_CALENDER, allColumns,
                MySQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Schedule newSchedule = cursorToComment(cursor);
        cursor.close();

    }

    public void deleteFullSchedule() {
        System.out.println("Deleting all previous schedule");
        database.execSQL("DELETE FROM " + MySQLiteHelper.TABLE_CALENDER);
    }

    public List<Schedule> getFullSchedule(){
        List<Schedule> fullSchedule = new ArrayList<Schedule>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_CALENDER, allColumns, null, null, null, null, null);
           count = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Schedule singleSchedule = cursorToComment(cursor);
            fullSchedule.add(singleSchedule);
            cursor.moveToNext();
        }

        //Log.i("Full schedule", fullSchedule.get(0).toString());
        //make sure to close the cursor
        cursor.close();
        return fullSchedule;
    }

    private Schedule cursorToComment(Cursor cursor) {
        Schedule singleSchedule = new Schedule();
        singleSchedule.setId(cursor.getLong(0));
        singleSchedule.setSubjectName(cursor.getString(1));
        singleSchedule.setRoomNo(cursor.getString(2));
        singleSchedule.setSlot(cursor.getInt(3));
        singleSchedule.setDay(cursor.getString(4));
        if (count == 1)Log.e("schedule", singleSchedule.getDay().toString());
        if (count == 0)Log.e("schedule listing", singleSchedule.getDay().toString());
        return singleSchedule;
    }

}