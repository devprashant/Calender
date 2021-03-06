package com.example.mau.calender.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mau.calender.R;
import com.example.mau.calender.app.MyApplication;
import com.example.mau.calender.helper.Schedule;
import com.example.mau.calender.helper.SwipeListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    final String url = "http://personal-maucalender.rhcloud.com/g0";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        scheduleList = new ArrayList<>();
        adapter = new SwipeListAdapter(this, scheduleList);
        listView.setAdapter(adapter);

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
                            String slot = scheduleObj.getString("slot");

                            Schedule s = new Schedule(subjectName, roomNumber, slot);

                            scheduleList.add(s);


                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                        }
                    }

                    adapter.notifyDataSetChanged();
                }
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
/*
        JsonArrayRequest req = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());

                if (response.length() > 0) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject scheduleObj = response.getJSONObject(i);

                            //int rank = movieObj.getInt("semester");
                            //String title = movieObj.getString("subject_name");

                            String subjectName = scheduleObj.getString("subject_name");
                            String roomNumber = scheduleObj.getString("room_no");
                            String slot = scheduleObj.getString("slot");

                            Schedule s = new Schedule(subjectName, roomNumber, slot);

                            scheduleList.add(0, s);


                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                        }
                    }

                    adapter.notifyDataSetChanged();
                }
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

        MyApplication.getmInstance().addToRequestQueue(req);
    }

    private void showCrouton(int result) {
        if(result == 1)   Crouton.makeText(this, "Done that!!", Style.CONFIRM).show();
        else   Crouton.makeText(this, "OOPS Retry Later", Style.ALERT).show();
    }
}
