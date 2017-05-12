package com.tecknologick.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tecknologick.studybuddy.Adapters.CustomListAdapter;
import com.tecknologick.studybuddy.RealmClasses.Course;
import com.tecknologick.studybuddy.RealmClasses.Institution;
import com.tecknologick.studybuddy.SharedPref.TinyDB;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.exceptions.RealmException;

public class CourseActivity extends AppCompatActivity {

    // declare variables
    ListView listView;
    RealmList<Course> courses;
    CustomListAdapter adapter;
    private Realm realm;
    int institutionID;
    TinyDB tinyDB;
    Toolbar toolbar;
    Intent i;

    // TODO: change to recycler view


    //onCreate override, Instantiate objects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        //create tiny db instance for access to shared preferences
        tinyDB = new TinyDB(getApplicationContext());
        institutionID = tinyDB.getInt("institutionID");

        //if course ID is set, go to module's activity
        if(institutionID == 0){

            i = new Intent(getApplicationContext(), InstitutionActivity.class);
            startActivity(i);
        }

        //get toolbar
        toolbar =  (Toolbar) findViewById(R.id.courseToolBar);

        //set up button
        toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v){

                //zero Institution id
                tinyDB.putInt("institutionID", 0);

                //send institution activity
                i = new Intent(getApplicationContext(), InstitutionActivity.class);
                startActivity(i);
                finish();
            }
        });

        //get all course from realm
        try {

            //get realm instance
            realm = Realm.getDefaultInstance();

            //get all course
            courses = realm.where(Institution.class)
                    .equalTo("id", institutionID)
                    .findFirst()
                    .courses;

        } catch(RealmException re) {

            //log realm error
            Log.d(MyApplication.TAG,"Realm error: " + re.getMessage());

        }

        //init adapter
        adapter = new CustomListAdapter(this, R.layout.row_course_list,"course");
        adapter.addAll(courses);

        //init list view and set adapter
        listView = (ListView) findViewById(R.id.courseListView);
        listView.setAdapter(adapter);

        //set list view item click listener
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //create intent and send to paper class
                        i = new Intent(getApplicationContext(), ModuleActivity.class);

                        //put course ID as extra
                        i.putExtra("courseID", courses.get(position).id);

                        //go to activity
                        startActivity(i);
                    }
                }
        );
    }

    //set change grade button
    public void onClick(View view){

        //send back to course page, clear stack
        i = new Intent(this, InstitutionActivity.class);

        //zero Institution id
        tinyDB.putInt("institutionID", 0);

        //start activity
        startActivity(i);
        finish();
    }


    // onDestroy
    @Override
    public void onDestroy(){
        super.onDestroy();

        //close realm object
        realm.close();

    }

}
