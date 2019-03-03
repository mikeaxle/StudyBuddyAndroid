package com.tecknologick.studybuddy;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tecknologick.studybuddy.Adapters.ItemAdapter;
import com.tecknologick.studybuddy.RealmClasses.Course;
import com.tecknologick.studybuddy.RealmClasses.Paper;

import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;


public class PaperActivity extends AppCompatActivity {

    /*
    *   declare variables
    *
    */

    RecyclerView recyclerView;
    TextView titleText;
    RealmList<Paper> papers;
    RealmResults<Course> course;
    ItemAdapter adapter;
    String courseName;
    int[] courseID_moduleID;
    private Realm realm;
    Toolbar toolbar;

    // TODO: change to recycler view

    public PaperActivity() {
        // Required empty public constructor
    }

    /*
    *   onCreateView override
    *   Instantiate objects
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper);

        //get courseID & moduleID from intent
        courseID_moduleID = (int[]) getIntent().getExtras().get("courseID_moduleID");

        //set back button
        toolbar = (Toolbar) findViewById(R.id.paperToolBar);
        toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v){

                //create intent to go to previous page
                Intent back = new Intent(getApplicationContext(), ModuleActivity.class);

                //get course id from courseID_moduleID array
                int courseID = courseID_moduleID[0];

                //add as intent extra
                back.putExtra("courseID", courseID);

                //destroy stack
                back.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                //start activity
                startActivity(back);

                //destroy current activity
                finish();
            }
        });

        //get papers from realm
        try {

            //get realm instance
            realm = Realm.getDefaultInstance();

            //get course where id = selected Course
            course = realm.where(Course.class)
                    .equalTo("id", courseID_moduleID[0])
                    .findAll();

            //get papers
            papers  =  course.get(0).modules.where().equalTo("id",courseID_moduleID[1]).findFirst().papers;


        } catch(RealmException re) {

            Log.d(MyApplication.TAG,"Realm error: " + re.getMessage());

        }

        // get course name
        courseName = course.get(0).modules.where().equalTo("id",courseID_moduleID[1]).findFirst().name;

        //set module title
        titleText = findViewById(R.id.paperTitleLabel);
        titleText.setText(courseName);


        //init adapter and course name as an additional argument
        adapter = new ItemAdapter("papers", papers, courseName);

        // create new layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        //init list view and set adapter
        recyclerView =  findViewById(R.id.paperRecyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);



        //set list view item click listener
        adapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object item, int position) {
                //create intent and send to paper activity
                Intent i = new Intent(getApplicationContext(), PaperSummaryActivity.class);

                //copy array + add selected paper id
                int[] tmp = Arrays.copyOf(courseID_moduleID, 3);
                tmp[2] = papers.get(position).id;

                //add data to intent
                i.putExtra("courseID_moduleID_paperID", tmp);

                //start activity
                startActivity(i);
            }
        });

    }

    // onDestroy
    @Override
    public void onDestroy(){
        super.onDestroy();
        //close realm object
        realm.close();
    }
}
