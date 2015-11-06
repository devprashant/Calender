package com.example.mau.calender.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.mau.calender.R;
import com.example.mau.calender.app.CalenderDataSource;
import com.example.mau.calender.app.FirstStart;
import com.example.mau.calender.app.GCMMessageSource;
import com.example.mau.calender.app.MyApplication;
import com.example.mau.calender.helper.ConnectionDetector;
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
    private String url = "https://nodejst-maucalender.rhcloud.com/schedule/";
    public CalenderDataSource dataSource = new CalenderDataSource(this);
    public GCMMessageSource messageSource;

    private ConnectionDetector cd;
    private boolean isInternetPresent;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_main);
        System.out.println("First check: " + prefs.getString(FirstStart.FIRST_START_CHECK_PREF, "repeat").equals("done"));
        if(!prefs.getString(FirstStart.FIRST_START_CHECK_PREF, "repeat").equals("done")){
            //start first_start activity
            Intent intent = new Intent(this, FirstStart.class);
            startActivity(intent);
            finish();

        }else {
            //user has already entered there query string now build url
           if (prefs.getString(FirstStart.MEMBER_TYPE_PREF, "haga").equals("student")){
               String branch = prefs.getString(FirstStart.BRANCH_PREF, "CSE");
               int semester = prefs.getInt(FirstStart.SEMESTER_PREF, 1);
               int group = prefs.getInt(FirstStart.CLASS_GROUP_PREF,1);
               url = buildURL(branch, semester, group );
           } else {
               if (prefs.getString(FirstStart.MEMBER_TYPE_PREF,null).equals("faculty")){
                    url = buildURL(prefs.getString("FACULTY NAME", null));

               } else{
                   if (prefs.getString(FirstStart.MEMBER_TYPE_PREF,null).equals("me")){
                       url = "https://nodejst-maucalender.rhcloud.com/schedule/me";
                   } else Toast.makeText(this, "Problem with stored values", Toast.LENGTH_SHORT).show();
                 }
           }
        }
        System.out.println("URL to request: " + url);
        messageSource = new GCMMessageSource(this);
        messageSource.getFirebaseMessage();

        listView = (ListView) findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        scheduleList = new ArrayList<>();


        swipeRefreshLayout.setOnRefreshListener(this);

        Crouton.makeText(this, "Welcome", Style.INFO).show();

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            swipeRefreshLayout.setRefreshing(false);
            Crouton.makeText(this, "Please Connect to a working internet connection", Style.ALERT).show();
            showFullSchedule();
        } else {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                    fetchMovie();
                }
            });
        }

    }

    @Override
    public void onRefresh() {
        fetchMovie();
    }

    public String buildURL(String personName){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("nodejst-maucalender.rhcloud.com")
                .appendPath("schedule")
                .appendPath("today")
                .appendQueryParameter("lecturer", personName);
        String myUrl = builder.build().toString();
        return myUrl;
    }

    public String buildURL(String branch, int semester, int group){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("nodejst-maucalender.rhcloud.com")
                .appendPath("schedule")
                .appendPath("class")
                .appendQueryParameter("branch", branch)
                .appendQueryParameter("semester", Integer.toString(semester))
                .appendQueryParameter("group", Integer.toString(group));
        String myUrl = builder.build().toString();
        return myUrl;
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
                            String roomNumber = scheduleObj.getString("room_no");
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
            dataSource.close();
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
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        scheduleList = null;
        scheduleList = dataSource.getFullSchedule();
        adapter = new SwipeListAdapter(this, scheduleList);
        listView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
        Log.i("show now", String.valueOf(scheduleList.size()));
        dataSource.close();
    }

    private void showCrouton(int result) {
        //addToDatabase();
        if(result == 1)  {
            Crouton.makeText(this, "Have A Nice Day", Style.CONFIRM).show();
            Log.i("oh that ","done!");
        }
        else   Crouton.makeText(this, "OOPS Retry Later", Style.ALERT).show();
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

        switch (id){
            case R.id.action_settings: return true;
            case R.id.first_start: //start first_start activity
                            Intent intent = new Intent(this, FirstStart.class);
                            startActivity(intent);
                            finish();
                break;
            case R.id.faculty: createDialog(); break;
            default: return true;
        }
       

        return super.onOptionsItemSelected(item);
    }

    public void createDialog() {
        AlertDialog alertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.enter_name, null))
                .setTitle("Please Enter Your Name")
                // Add action buttons
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        Dialog nameDialog = (Dialog) dialog;
                        EditText fName = ((EditText) nameDialog.findViewById(R.id.facultyName));
                        String facultyName = fName.getText().toString();
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(FirstStart.MEMBER_TYPE_PREF, "faculty");
                        editor.putString("FACULTY NAME", facultyName);
                        editor.apply();
                        recreate();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }


}
