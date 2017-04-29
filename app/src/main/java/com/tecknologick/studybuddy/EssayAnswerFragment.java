package com.tecknologick.studybuddy;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class EssayAnswerFragment extends Fragment {

    int fragNum;
    String answer;
    String question;
    //TextView essayQuestionNumberAnswerLabel;
    TextView essayQuestionAnswerLabel;
    ImageButton seeQuestionButton;
    ImageButton readAloudAnswerImageButton;


    public EssayAnswerFragment() {
        // Required empty public constructor
    }

    //create new instance of fragment using num as argument
    public static EssayAnswerFragment newInstance(int num, String answer, String question){
        //instantiate fragment
        EssayAnswerFragment eaf = new EssayAnswerFragment();

        // Supply num input as a bundle argument
        Bundle args = new Bundle();
        args.putString("answer", answer);
        args.putString("question", question);
        args.putInt("num", num);
        eaf.setArguments(args);

        //return fragment
        return eaf;
    }

    //get arguments
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        fragNum =  getArguments() != null ? getArguments().getInt("num") : 1;
        answer = getArguments() != null ? getArguments().getString("answer") : "";
        question = getArguments() != null ? getArguments().getString("question") : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_essay_answer, container, false);

        //get text views
        essayQuestionAnswerLabel = (TextView) view.findViewById(R.id.essayQuestionAnswerLabel);
        //essayQuestionNumberAnswerLabel = (TextView) view.findViewById(R.id.essayQuestionNumberAnswerLabel);

        //set text view
        essayQuestionAnswerLabel.setText(answer);

        //get image buttons
        seeQuestionButton = (ImageButton) view.findViewById(R.id.seeQuestionImageButton);
        readAloudAnswerImageButton = (ImageButton) view.findViewById(R.id.readAloudAnswerImageButton);

        //set button click listeners
        seeQuestionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                QuestionContainerFragment questionFragment = (QuestionContainerFragment)getParentFragment();            //get instance of parent fragment

                //call flipCard function of parent fragment
                questionFragment.flipCard(answer, question);
            }
        });


        //check if is last page
        /*final QuestionActivity questionActivity = (QuestionActivity) getActivity();                                     //get current activity
        if(questionActivity.pager.getCurrentItem() == questionActivity.questions.size() - 1){
            Toast.makeText(questionActivity, "this is the last item", Toast.LENGTH_SHORT).show();
        }*/


        return view;
    }

    // TODO: read aloud functionality

}
