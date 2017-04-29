package com.tecknologick.studybuddy.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tecknologick.studybuddy.MyApplication;
import com.tecknologick.studybuddy.R;
import com.tecknologick.studybuddy.RealmClasses.Course;
import com.tecknologick.studybuddy.RealmClasses.Module;
import com.tecknologick.studybuddy.RealmClasses.Paper;
import com.tecknologick.studybuddy.RealmClasses.Section;

/**
 * Created by michaellungu on 4/11/17.
 */

public class CustomListAdapter extends ArrayAdapter {

    private int layoutResourceID;                                                       //resource ID
    private String[] colors;                                                            //array to store icon colors
    //Random random;
    ColorFilter colorFilter;                                                            //color filter
    String currentActivity;                                                             //current activity name
    String additionalInfo;                                                              //additonal info to pass into list items

    //constructor
    public CustomListAdapter(@NonNull Context context, @LayoutRes int resource, String currentActivity) {
        super(context, resource);

        layoutResourceID = resource;                                                    //assign resource ID
        colors = new String[]{"#7ED321", "#A229B8", "#50E3C2", "#FF0000"};              //colors array
        this.currentActivity = currentActivity;                                         //assign current activity name
        //random = new Random();
    }

    //overload constructor
    public CustomListAdapter(@NonNull Context context, @LayoutRes int resource, String currentActivity, String additionalInfo) {
        super(context, resource);

        layoutResourceID = resource;                                                    //assign resource ID
        colors = new String[]{"#7ED321", "#A229B8", "#50E3C2", "#FF0000"};              //colors array
        this.currentActivity = currentActivity;                                         //assign current activity name
        this.additionalInfo = additionalInfo;                                           //assign additional info
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {

            View v = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                v = inflater.inflate(layoutResourceID, null);

            } else {
                v = convertView;
            }

            switch (currentActivity){

                //if on course activity
                case "course":
                    //get current course name
                    final Course course = (Course) getItem(position);

                    //get text view
                    TextView courseName = (TextView) v.findViewById(R.id.courseNameLabel);

                    //get icon and set color
                    ImageButton icon = (ImageButton) v.findViewById(R.id.courseImageButton);
                    colorFilter = new PorterDuffColorFilter(Color.parseColor(colors[position]), PorterDuff.Mode.SRC_IN);
                    //rgb(random.nextInt(200),random.nextInt(256),random.nextInt(256) *all random colors
                    icon.setColorFilter(colorFilter);

                    //assign with values from current course
                    courseName.setText(course.name);

                    break;

                //if on module activity
                case "module":
                    //get current module name
                    final Module module = (Module) getItem(position);

                    //get module text view and set module name
                    TextView moduleName = (TextView) v.findViewById(R.id.moduleRowLabel);
                    moduleName.setText(module.name);

                    // TODO: add module images

                    break;

                case "paper":
                    //get current paper name
                    final Paper paper = (Paper) getItem(position);

                    //get paper text view and set paper name
                    TextView paperName = (TextView) v.findViewById(R.id.paperRowLabel);
                    paperName.setText(additionalInfo + " " + paper.name + " (" + paper.year + ")");

                    break;

                case "section":
                    //get current section
                    final Section section = (Section) getItem(position);

                    //get and set text view
                    TextView sectionName = (TextView) v.findViewById(R.id.sectionNameLabel);
                    sectionName.setText(section.name);

                    break;

                case "multiple choice":
                    //get current question
                    final String answer = (String) getItem(position);



                    //set answer letter
                    String letter = "";

                    switch(position){
                            case 0:
                                letter = "A";
                                break;
                            case 1:
                                letter = "B";
                                break;
                            case 2:
                                letter = "C";
                                break;
                            case 3:
                                letter = "D";
                                break;
                            default:
                                break;
                    }


                    //get question row
                    TextView answerLabel = (TextView) v.findViewById(R.id.multipleChoiceQuestionRowQuestionLabel);
                    answerLabel.setText(answer);

                    //get answer letter label
                    TextView answerLetter = (TextView) v.findViewById(R.id.multipleChoiceQuestionRowNumberLabel);
                    //set answer letter
                    answerLetter.setText(letter);

                default:
                    break;
            }

            return v;
        } catch (Exception ex) {

            Log.e(MyApplication.TAG, "error", ex);
            return null;

        }
    }
}
