package com.tecknologick.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tecknologick.studybuddy.SharedPref.TinyDB;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    TextView resultTitle;
    TextView resultPercentage;
    PieChart resultChart;
    Button exitPaperNextButton;
    boolean hasNextSection;
    int currentSection;
    TinyDB tinyDB;
    Toolbar toolbar;
    Intent i;
    int[] courseID_moduleID_paperID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //set details array to use to go back to previous
        courseID_moduleID_paperID = (int[]) getIntent().getExtras().get("courseID_moduleID_paperID");

        //set up navigation button
        toolbar = (Toolbar) findViewById(R.id.ResultToolBar);
        toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v){

                //create intent to go to previous page
                i = new Intent(getApplicationContext(), PaperActivity.class);

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

        //get exit paper/ next section button
        exitPaperNextButton = (Button) findViewById(R.id.exitPaperButton);

        //get global variables
        hasNextSection = ((MyApplication)getApplicationContext()).getHasNextSection();
        currentSection = ((MyApplication)getApplicationContext()).getCurrentSection();

        //check if paper has next section and set button text
        if(hasNextSection){
            exitPaperNextButton.setText("Next Section");
        } else {
            exitPaperNextButton.setText("End Paper");
        }


        //get text views
        resultTitle = (TextView) findViewById(R.id.resultTitleLabel);
        resultPercentage = (TextView) findViewById(R.id.resultPercentageLabel);

        //get pie chart
        resultChart = (PieChart) findViewById(R.id.resultsChart);
        resultChart.setNoDataText("Essay questions are not marked");


        //set title
        String title = (String) getIntent().getExtras().get("title");
        resultTitle.setText(title);

        String previousActivity = (String) getIntent().getExtras().get("fromActivity");

        if(previousActivity.equals("multiple choice")){

            //add percentage and grading
            int total = (int) getIntent().getExtras().get("total");
            int correct = (int) getIntent().getExtras().get("correct");
            float percentage = (float) correct / total * 100;
            String grading = "";
            if(percentage >= 75 ){
                grading = "Distinction";
            } else if (percentage < 75 && percentage >= 60 ){
                grading = "Merit";
            } else if(percentage < 60 && percentage >= 50){
                grading = "Merit";
            } else {
                grading = "Failed";
            }

            //set result percentage and grading text view
            resultPercentage.setText(Math.floor(percentage) + "% - " + grading);

            //create pie chart dataset using results
            List<PieEntry> entries = new ArrayList<>();

            entries.add(new PieEntry(percentage, "correct"));
            entries.add(new PieEntry(100 - percentage, "wrong"));

            PieDataSet set = new PieDataSet(entries, "");
            set.setColors(ColorTemplate.VORDIPLOM_COLORS);
            PieData data = new PieData(set);

            Description d = new Description();
            d.setText("");
            resultChart.setDescription(d);
            resultChart.setData(data);
            resultChart.setUsePercentValues(true);
            resultChart.setDrawSlicesUnderHole(true);
            resultChart.setCenterText(correct + "/" + total);
            resultChart.setCenterTextColor(R.color.colorStudyBlue);
            resultChart.setCenterTextSize(30);
            resultChart.setHoleRadius(70);
            resultChart.animateY(3000, Easing.EasingOption.EaseOutBack);
            resultChart.invalidate(); // refresh

        } else if (previousActivity.equals("essay")){

            resultPercentage.setText("You have completed this section");

            TextView resultLabel = (TextView) findViewById(R.id.resultLabel);
            resultLabel.setText("Section Complete");

        }

        //TODO: write test details to shared preferences
        //tinyDB = new TinyDB(getApplicationContext());
        //write to shared storage

    }

    //set rewrite button and exit button listeners
    public void onClick(View view){




        switch (view.getId()){

            case R.id.rewritePaperButton:

                //get paper details from intent


                //create new intent
                i = new Intent(this, PaperSummaryActivity.class);

                //put paper details as extra
                i.putExtra("courseID_moduleID_paperID", courseID_moduleID_paperID);

                //go back to paper
                startActivity(i);

                finish();

                break;

            case R.id.exitPaperButton:

                if(hasNextSection){

                    //create intent
                    i = new Intent(getApplicationContext(), QuestionActivity.class);

                    //put paper details as extra
                    i.putExtra("courseID_moduleID_paperID", courseID_moduleID_paperID);

                    ((MyApplication)getApplicationContext()).setCurrentSection(currentSection + 1);

                    //to next section of paper
                    startActivity(i);

                    finish();


                } else {


                    //create intent to go to previous page
                    i = new Intent(getApplicationContext(), PaperActivity.class);

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

                break;

            default:

                break;
        }

    }

    //override hardware back button
    @Override
    public void onBackPressed(){


    }
}
