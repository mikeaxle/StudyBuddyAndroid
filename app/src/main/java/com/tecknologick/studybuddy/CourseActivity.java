package com.tecknologick.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tecknologick.studybuddy.Adapters.CustomListAdapter;
import com.tecknologick.studybuddy.RealmClasses.Course;
import com.tecknologick.studybuddy.SharedPref.TinyDB;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

public class CourseActivity extends AppCompatActivity {

    /*
    *   declare variables
    *
    */
    ListView listView;
    RealmResults<Course> courses;
    CustomListAdapter adapter;
    private Realm realm;
    int courseID;
    TinyDB tinyDB;
    Intent i;

    // TODO: change to recycler view


    /*
    *   onCreate override
    *   Instantiate objects
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        //create tinydb instance for access to shared preferences
        tinyDB = new TinyDB(getApplicationContext());
        courseID = tinyDB.getInt("courseID");

        //if course ID is set, go to module's actity
        if(courseID != 0){

            i = new Intent(getApplicationContext(), ModuleActivity.class);
            startActivity(i);
        }

        //get all course from realm
        try {

            //get realm instance
            realm = Realm.getDefaultInstance();

            //get all course
            courses = realm.where(Course.class)
                    .findAll();

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

                        //get selected course id
                        courseID = courses.get(position).id;

                        // write course id to shared preferences
                        tinyDB.putInt("courseID", courseID);

                        //go to activity
                        startActivity(i);
                    }
                }
        );
    }


    // onDestroy
    @Override
    public void onDestroy(){
        super.onDestroy();

        //close realm object
        realm.close();

    }

}
