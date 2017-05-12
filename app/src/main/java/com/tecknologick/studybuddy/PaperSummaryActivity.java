package com.tecknologick.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tecknologick.studybuddy.Adapters.CustomListAdapter;
import com.tecknologick.studybuddy.RealmClasses.Course;
import com.tecknologick.studybuddy.RealmClasses.Paper;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

public class PaperSummaryActivity extends AppCompatActivity {

    TextView titleText;
    TextView details;
    Toolbar toolbar;
    ListView sectionListView;
    CustomListAdapter adapter;
    RealmResults<Course> course;
    Realm realm;
    Paper paper;
    int[] courseID_moduleID_paperID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_summary);

        //get courseID, moduleID & paperID from intent
        courseID_moduleID_paperID = (int[]) getIntent().getExtras().get("courseID_moduleID_paperID");
        //courseID_moduleID_paperID = new int[]{225,101,14};

        //set back button
        toolbar = (Toolbar) findViewById(R.id.paperSummaryToolBar);
        toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //create intent to go to previous page
                Intent back = new Intent(getApplicationContext(), PaperActivity.class);

                //get course id and module id from courseID_moduleID_paperID array
                int[] courseID_moduleID = new int[]{courseID_moduleID_paperID[0],courseID_moduleID_paperID[1]};

                //add as intent extra
                back.putExtra("courseID_moduleID", courseID_moduleID);

                //destroy stack
                back.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                //start activity
                startActivity(back);

                //destroy current activity
                finish();
            }
        });


        //get paper from realm
        try {

            //get realm instance
            realm = Realm.getDefaultInstance();

            //get course where id = selected Course
            course = realm.where(Course.class)
                    .equalTo("id", courseID_moduleID_paperID[0])
                    .findAll();

            //get current paper
            paper  =  course.get(0).modules.where().equalTo("id",courseID_moduleID_paperID[1]).findFirst().papers.where().equalTo("id", courseID_moduleID_paperID[2]).findFirst();

        } catch(RealmException re) {

            Log.d(MyApplication.TAG,"Realm error: " + re.getMessage());

        }

        //set paper title
        titleText = (TextView) findViewById(R.id.paperSummaryTitleLabel);
        titleText.setText(course.get(0).modules.where().equalTo("id",courseID_moduleID_paperID[1]).findFirst().name +  " " + paper.name + " (" + paper.year + ")");

        //set paper summary
        details = (TextView) findViewById(R.id.paperDetailsLabel);
        details.setText(paper.description);

        //set paper title
        titleText = (TextView) findViewById(R.id.paperSummaryTitleLabel);
        titleText.setText(course.get(0).modules.where().equalTo("id",courseID_moduleID_paperID[1]).findFirst().name +  " " + paper.name + " (" + paper.year + ")");

        //set paper summary
        details = (TextView) findViewById(R.id.paperDetailsLabel);
        details.setText(paper.description);

        //set section list view
        adapter = new CustomListAdapter(this, R.layout.row_section_list, "section");                    //create adapter
        adapter.addAll(paper.sections);                                                                 //add all paper sections
        sectionListView = (ListView) findViewById(R.id.sectionListView);                                //get list view
        sectionListView.setAdapter(adapter);                                                            //set list view adapter

        //set list view click listener
        sectionListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        //check if clicked item is last item
                        if(paper.sections.size() == position + 1){

                            //set has next section global variable  to false
                            ((MyApplication) getApplicationContext()).setHasNextSection(false);

                        } else {

                            //set has next section global variable to true
                            ((MyApplication) getApplicationContext()).setHasNextSection(true);
                        }

                        //set current section global variable to index position
                        ((MyApplication) getApplicationContext()).setCurrentSection(position);

                        //create intent send to question activity
                        Intent i = new Intent(getApplicationContext(), QuestionActivity.class);

                        //put extra
                        i.putExtra("courseID_moduleID_paperID", courseID_moduleID_paperID);

                        //start activity
                        startActivity(i);

                    }
                }
        );
    }

    //begin exam button click event
    public void onClick(View view){

        //check if clicked item is last item
        if(paper.sections.size() == 1){

            //set has next section global variable  to false
            ((MyApplication) getApplicationContext()).setHasNextSection(false);

        } else {

            //set has next section global variable to true
            ((MyApplication) getApplicationContext()).setHasNextSection(true);
        }

        //set current section global variable to index position
        ((MyApplication) getApplicationContext()).setCurrentSection(0);

        //create intent send to question activity
        Intent i = new Intent(getApplicationContext(), QuestionActivity.class);

        //put extra
        i.putExtra("courseID_moduleID_paperID", courseID_moduleID_paperID);

        //start activity
        startActivity(i);
    }

    // onDestroy
    @Override
    public void onDestroy(){
        super.onDestroy();

        //close realm object
        realm.close();
    }

    //override hardware back button
    @Override
    public void onBackPressed(){


    }
}
