package com.example.mau.calender.app;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.mau.calender.R;
import com.example.mau.calender.activity.MainActivity;

/**
 * Created by mau on 11/5/2015.
 */
public class FirstStart extends ActionBarActivity {


    public static final String BRANCH_PREF = "branch";
    public static final String SEMESTER_PREF = "semester";
    public static final String CLASS_GROUP_PREF = "class_group";
    public static final String FIRST_START_CHECK_PREF = "first_start_check";
    public static final String MEMBER_TYPE_PREF = "member_type";

    private Spinner sBranch;
    private Spinner sSemester;
    private Spinner sGroup;

    private Button bSave;

    private static final String branch = "branch";
    private static final String semester = "semester";
    private static final String classGroup = "classGroup";
    private static final String firstStartCheck = "done";

    private SharedPreferences prefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.first_start_activity);

        sBranch = (Spinner) findViewById(R.id.spinnerBranch);
        sSemester = (Spinner) findViewById(R.id.spinnerSemester);
        sGroup = (Spinner) findViewById(R.id.spinnerGroup);

        //button to show saved data and finish activity.
        bSave = (Button) findViewById(R.id.btnSave);

        System.out.println("Value of this: " + this);
        System.out.println("Value of sBranch" + sBranch);
        System.out.println("Value of R.array.branch_arrays" + R.array.branch_arrays);
        System.out.println("Value of branch" +  branch);
                //sets spinner with data.
                setSpinner(this, sBranch, R.array.branch_arrays, branch);
        setSpinner(this,sSemester,R.array.semester_arrays, semester);
        setSpinner(this,sGroup,R.array.classGroup_arrays, classGroup);

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString(MEMBER_TYPE_PREF, "student");
                edit.putString(FIRST_START_CHECK_PREF, firstStartCheck);
                edit.apply();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private int numberTextToInt(String stringTONumber) {
        int convertedString = 0;
        switch (stringTONumber){
            case "ONE": convertedString = 1; break;
            case "TWO": convertedString = 2; break;
            case "THREE": convertedString = 3; break;
            case "FOUR": convertedString = 4; break;
            case "FIVE": convertedString = 5; break;
            case "SIX" : convertedString = 6; break;
            case "SEVEN": convertedString = 7; break;
            case "EIGHT": convertedString = 8; break;
        }
        return convertedString;
    }

    public void setSpinner(Context context, Spinner spinnerToSpin, int dataResId, final String prefDataHead){
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,dataResId, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerToSpin.setAdapter(adapter);

        spinnerToSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor edit = prefs.edit();

                switch (prefDataHead){
                    case branch: edit.putString(BRANCH_PREF, parent.getItemAtPosition(position).toString());break;
                    case semester: edit.putInt(SEMESTER_PREF, numberTextToInt(parent.getItemAtPosition(position).toString())); break;
                    case classGroup: edit.putInt(CLASS_GROUP_PREF, numberTextToInt(parent.getItemAtPosition(position).toString())); break;
                }

                edit.apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
}

