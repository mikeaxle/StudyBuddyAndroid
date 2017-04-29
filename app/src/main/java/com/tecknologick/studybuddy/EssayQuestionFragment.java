package com.tecknologick.studybuddy;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tecknologick.studybuddy.RealmClasses.Question;


public class EssayQuestionFragment extends Fragment {

    //variable to store fragment value
    int fragNum;
    TextView essayQuestionNumberLabel;
    TextView essayAllocatedMarksLabel;
    TextView essayQuestionLabel;
    Question question;
    ImageButton seeAnswerButton;
    ImageButton readAloudButton;

    //create new instance of fragment using num as argument
    public static EssayQuestionFragment newInstance(int num){

        //instantiate fragment
        EssayQuestionFragment eqf = new EssayQuestionFragment();

        // Supply num input as a bundle argument
        Bundle args = new Bundle();
        args.putInt("num", num);
        eqf.setArguments(args);

        //return fragment
        return eqf;

    }

    //get arguments on create
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        fragNum =  getArguments() != null ? getArguments().getInt("num") : 1;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_essay_question, container, false);

        //get current activity
        final QuestionActivity questionActivity = (QuestionActivity) getActivity();

        //get current question
        question = questionActivity.questions.get(fragNum);

        //get text view
        essayQuestionNumberLabel = (TextView) view.findViewById(R.id.essayQuestionNumberLabel);
        essayAllocatedMarksLabel = (TextView) view.findViewById(R.id.essayAllocatedMarksLabel);
        essayQuestionLabel = (TextView) view.findViewById(R.id.essayQuestionLabel);

        //set text views
        essayQuestionNumberLabel.setText(question.name);
        essayAllocatedMarksLabel.setText("(" + question.allocatedMarks + " marks)");
        essayQuestionLabel.setText(question.question);                                              //display question


        //get image buttons
        seeAnswerButton = (ImageButton) view.findViewById(R.id.seeAnswerImageButton);
        readAloudButton = (ImageButton) view.findViewById(R.id.readAloudQuestionImageButton);

        //set button click listeners
        seeAnswerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                QuestionContainerFragment questionFragment = (QuestionContainerFragment)getParentFragment();            //get instance of parent fragment

                //call flipCard function of parent fragment
                questionFragment.flipCard(question.answer, question.question);
            }
        });

        return view;
    }
    // TODO: read aloud functionality

}
