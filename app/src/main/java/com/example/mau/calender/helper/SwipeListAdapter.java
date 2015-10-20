package com.example.mau.calender.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mau.calender.R;

import java.util.List;

/**
 * Created by mau on 10/17/2015.
 */
public class SwipeListAdapter extends BaseAdapter{


    private String[] bgColors;
    private List<Schedule> scheduleList;
    private Activity activity;
    private LayoutInflater inflater;

    public SwipeListAdapter(Activity activity, List<Schedule> scheduleList){
        this.activity = activity;
        this.scheduleList = scheduleList;
        bgColors = activity.getApplicationContext().getResources().getStringArray(R.array.movie_serial_bg);
    }
    @Override
    public int getCount() {
        return scheduleList.size();
    }

    @Override
    public Object getItem(int location) {
        return scheduleList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null){
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_row, null);
        }

        TextView room = (TextView) convertView.findViewById(R.id.room);
        TextView subjectName = (TextView) convertView.findViewById(R.id.subjectName);
        TextView slot = (TextView) convertView.findViewById(R.id.slot);

        room.setText(String.valueOf(scheduleList.get(position).roomNumber));
        subjectName.setText(scheduleList.get(position).subjectName);
        slot.setText(String.valueOf(scheduleList.get(position).slot));

        String color;

        switch(scheduleList.get(position).roomNumber){
            case "A-212": color = bgColors[2];
                break;
            case "A-110": color = bgColors[3];
                break;
            default:
                color = bgColors[4]; //default color
        }

        room.setBackgroundColor(Color.parseColor(color));
        return convertView;
    }
}
