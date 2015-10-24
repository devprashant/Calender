package com.example.mau.calender.activity;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.mau.calender.R;
import com.example.mau.calender.app.CalenderDataSource;
import com.example.mau.calender.app.MyApplication;
import com.example.mau.calender.helper.Schedule;
import com.example.mau.calender.helper.SwipeListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener{

    private String TAG = MainActivity.class.getSimpleName();

    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Schedule> scheduleList;
    private SwipeListAdapter adapter;
    private final String url = "https://nodejst-maucalender.rhcloud.com/schedule/all";
    public CalenderDataSource dataSource = new CalenderDataSource(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        scheduleList = new ArrayList<>();


        swipeRefreshLayout.setOnRefreshListener(this);

        Crouton.makeText(this, "Welcome", Style.INFO).show();

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);

                fetchMovie();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        fetchMovie();
    }

    private void fetchMovie() {
        swipeRefreshLayout.setRefreshing(true);
        //final String url = "http://personal-maucalender.rhcloud.com/g0";

/*
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject responseObj) {

                JSONArray response = null;
                try {
                    response = responseObj.getJSONArray("schedule");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, response.toString());

                if (response.length() > 0) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject scheduleObj = response.getJSONObject(i);

                            //int rank = movieObj.getInt("semester");
                            //String title = movieObj.getString("subject_name");

                            String subjectName = scheduleObj.getString("subject_name");
                            String roomNumber = scheduleObj.getString("room_no");
                            int slot = scheduleObj.getInt("slot");
                            String day = scheduleObj.getString("day");

                            Schedule s = new Schedule(subjectName, roomNumber, slot,day);

                            scheduleList.add(s);


                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                        }
                    }

                    adapter.notifyDataSetChanged();
                }
                //addToDatabase();
                //Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
                showCrouton(1);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "Server Error: " + volleyError.getMessage());
                //Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
                showCrouton(0);
            }
        }); */

        JsonArrayRequest req = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());
                    scheduleList.clear();
                if (response.length() > 0) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject scheduleObj = response.getJSONObject(i);

                            //int rank = movieObj.getInt("semester");
                            //String title = movieObj.getString("subject_name");

                            String subjectName = scheduleObj.getString("subject_name");
                            //String roomNumber = scheduleObj.getString("room_no");
                            String roomNumber = "A-110";
                            int slot = scheduleObj.getInt("slot");
                            String day = scheduleObj.getString("day");

                            Schedule s = new Schedule(subjectName, roomNumber, slot,day);
                              //  if (!(s.roomNumber).isEmpty()) s.roomNumber = "A-110";
                               // if (!(s.day).isEmpty()) s.day="monday";
                            scheduleList.add(s);



                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                        }
                    }

                    //adapter.notifyDataSetChanged();
                }
                //insert data in database
                new dbInOut().execute();
                /*AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        addToDatabase();
                    }
                });*/
                //Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
                showCrouton(1);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "Server Error: " + volleyError.getMessage());
                //Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
                showCrouton(0);
            }
        });

        MyApplication.getmInstance().addToRequestQueue(req);
    }

    private class dbInOut extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                dataSource.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dataSource.deleteFullSchedule();
        }

        @Override
        protected Void doInBackground(Void... params) {
            addToDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            showFullSchedule();
        }
    }
    private void addToDatabase() {

        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String[] value = new String[3];
        int slot = 0;

        for (Schedule map: scheduleList){
            value[0] = map.subjectName;
            value[1] = map.roomNumber;
            slot = map.slot;
            value[2] = map.day;
            dataSource.createSchedule(value[0], value[1],slot, value[2]);
        }
    }

    private void showFullSchedule() {
        scheduleList = null;
        scheduleList = dataSource.getFullSchedule();
        adapter = new SwipeListAdapter(this, scheduleList);
        listView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
        Log.i("show now", String.valueOf(scheduleList.size()));
    }

    private void showCrouton(int result) {
        //addToDatabase();
        if(result == 1)  {
            Crouton.makeText(this, "Done that!!", Style.CONFIRM).show();
            Log.i("oh that ","done!");
        }
        else   Crouton.makeText(this, "OOPS Retry Later", Style.ALERT).show();
    }
}
