package com.tecknologick.studybuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tecknologick.studybuddy.Adapters.QuestionAdapter;
import com.tecknologick.studybuddy.Adapters.QuestionViewPager;
import com.tecknologick.studybuddy.RealmClasses.Course;
import com.tecknologick.studybuddy.RealmClasses.Paper;
import com.tecknologick.studybuddy.RealmClasses.Question;
import com.tecknologick.studybuddy.Transitions.DepthPageTransformer;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

public class QuestionActivity extends AppCompatActivity implements QuestionViewPager.OnSwipeOutListener {

    TextView titleText;
    AlertDialog.Builder alert;
    RealmResults<Course> course;
    Realm realm;
    Paper paper;
    RealmList<Question> questions;
    int[] courseID_moduleID_paperID;
    int correct = 0;
    QuestionAdapter adapter;
    QuestionViewPager pager;
    int currentSection;
    String questionType;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        

        //init dialog box
        alert = new AlertDialog.Builder(this);

        //get courseID, moduleID & paperID from intent
        courseID_moduleID_paperID = (int[]) getIntent().getExtras().get("courseID_moduleID_paperID");
        //courseID_moduleID_paperID = new int[]{225,101,14};

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

            //get questions using global currentSection object
            currentSection = ((MyApplication) getApplicationContext()).getCurrentSection();
            questions = paper.sections.get(currentSection).questions;

        } catch(RealmException re) {

            Log.d(MyApplication.TAG,"Realm error: " + re.getMessage());

        }

        //check if current section is the last one
        if(paper.sections.size() == currentSection + 1){

            //set has next section global variable  to false
            ((MyApplication) getApplicationContext()).setHasNextSection(false);

        }

        //init fragment adapter, amount of questions and type of questions as parameters
        adapter = new QuestionAdapter(getFragmentManager(), questions.size(), questions.first().type);

        //init pager & set adapter
        pager = (QuestionViewPager) findViewById(R.id.questionPager);
        pager.setAdapter(adapter);
        pager.setPageMargin(20);
        pager.setPageTransformer(true, new DepthPageTransformer());
        pager.setOnSwipeOutListener(this);

        //set paper title
        titleText = (TextView) findViewById(R.id.questionTitleLabel);
        titleText.setText(course.get(0).modules.where().equalTo("id",courseID_moduleID_paperID[1]).findFirst().name +  " " + paper.name + " (" + paper.year + ")");
    }

    //set end paper button click listener
    public void onClick (View view){

        //TODO: style dialog box

        //check which button was clicked
        switch (view.getId()){

            //exit paper button
            case R.id.endPaperButton:
                //show alert dialog
                alert.setTitle("End Exam");
                alert.setMessage("Would you quit this paper?")
                        //continue paper
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        //exit paper
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //create intent to go to previous page
                                Intent i = new Intent(getApplicationContext(), PaperActivity.class);

                                //get course id and module id from courseID_moduleID_paperID array
                                int[] courseID_moduleID = new int[]{courseID_moduleID_paperID[0],courseID_moduleID_paperID[1]};

                                //add as intent extra
                                i.putExtra("courseID_moduleID", courseID_moduleID);

                                //destroy stack
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                //start activity
                                startActivity(i);

                                //destroy current activity
                                finish();
                            }
                        });
                alert.create();
                alert.show();
                break;

            //next question button (in multiple choice answer)
            case R.id.nextAnswerButton:

                //if current item is not last item
                if (pager.getCurrentItem() < questions.size() - 1){

                    //go to next question
                    pager.setCurrentItem(pager.getCurrentItem() + 1);

                } else {

                    //go to result activity
                    Intent i = new Intent(this, ResultActivity.class);

                    if(questions.first().type.equals("multiple choice")){

                        //add which activity the intent is coming from
                        i.putExtra("fromActivity", "multiple choice");

                    } else {

                        //add which activity the intent is coming from
                        i.putExtra("fromActivity", "essay");

                    }

                    //add number of correct answers as extra
                    i.putExtra("correct", correct);

                    //add total questions as extra
                    i.putExtra("total",  questions.size());

                    //put paper title as extra
                    i.putExtra("title", course.get(0).modules.where().equalTo("id",courseID_moduleID_paperID[1]).findFirst().name +  " " + paper.name + " (" + paper.year + ")");

                    //put courseID_moduleID_paperID  as extra in case a student wants to retry the paper
                    i.putExtra("courseID_moduleID_paperID", courseID_moduleID_paperID );

                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    //start activity
                    startActivity(i);

                    //destroy activity
                    finish();

                }
                break;

            default:
                break;
        }

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

        //end paper button on click even handler
       onClick(findViewById(R.id.endPaperButton));

    }

    //override swipe out at start
    @Override
    public void onSwipeOutAtStart() {
        
    }

    //override swipe out at end
    @Override
    public void onSwipeOutAtEnd() {

        //go to result activity
        Intent i = new Intent(this, ResultActivity.class);

        if(questions.first().type.equals("multiple choice")){

            //add which activity the intent is coming from
            i.putExtra("fromActivity", "multiple choice");

        } else {

            //add which activity the intent is coming from
            i.putExtra("fromActivity", "essay");

        }

        //add number of correct answers as extra
        i.putExtra("correct", correct);

        //add total questions as extra
        i.putExtra("total",  questions.size());

        //put paper title as extra
        i.putExtra("title", course.get(0).modules.where().equalTo("id",courseID_moduleID_paperID[1]).findFirst().name +  " " + paper.name + " (" + paper.year + ")");

        //put courseID_moduleID_paperID  as extra in case a student wants to retry the paper
        i.putExtra("courseID_moduleID_paperID", courseID_moduleID_paperID );

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //start activity
        startActivity(i);

        //destroy activity
        finish();
    }
}
