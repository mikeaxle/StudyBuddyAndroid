package com.tecknologick.studybuddy;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MultipleChoiceAnswerFragment extends Fragment {

    int fragNum;
    boolean correct;
    String selectedAnswer;
    String answer;
    String question;
    String explanation;
    TextView statusAnswerLabel;
    TextView questionAnswerLabel;
    TextView answerAnswerLabel;
    TextView selectedAnswerLabel;
    TextView explanationAnswerLabel;

    public MultipleChoiceAnswerFragment() {
        // Required empty public constructor
    }

    //create new instance of fragment using num as argument
    public static MultipleChoiceAnswerFragment newInstance(int num, boolean correct, String selectedAnswer, String answer, String question, String explanation){
        //instantiate fragment
        MultipleChoiceAnswerFragment mcaf = new MultipleChoiceAnswerFragment();

        // Supply num input as a bundle argument
        Bundle args = new Bundle();
        args.putString("answer", answer);
        args.putString("selectedAnswer", selectedAnswer);
        args.putString("question", question);
        args.putString("explanation", explanation);
        args.putInt("num", num);
        args.putBoolean("correct", correct);
        mcaf.setArguments(args);

        //return fragment
        return mcaf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        fragNum =  getArguments() != null ? getArguments().getInt("num") : 1;
        correct =  getArguments() != null ? getArguments().getBoolean("correct") : true;
        selectedAnswer = getArguments() != null ? getArguments().getString("selectedAnswer") : "";
        answer = getArguments() != null ? getArguments().getString("answer") : "";
        question = getArguments() != null ? getArguments().getString("question") : "";
        explanation = getArguments() != null ? getArguments().getString("explanation") : "";
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_multiple_choice_answer, container, false);

        //get text views
        statusAnswerLabel = (TextView) view.findViewById(R.id.statusAnswerLabel);
        questionAnswerLabel = (TextView) view.findViewById(R.id.questionAnswerLabel);
        answerAnswerLabel = (TextView) view.findViewById(R.id.answerAnswerLabel);
        selectedAnswerLabel = (TextView) view.findViewById(R.id.selectedAnswerLabel);
        explanationAnswerLabel = (TextView) view.findViewById(R.id.explanationAnswerLabel);

        //set text views
        questionAnswerLabel.setText(question);
        selectedAnswerLabel.setText("Your answer: " + selectedAnswer);
        answerAnswerLabel.setText("Correct answer: " + answer);
        explanationAnswerLabel.setText("Explanation: " + explanation);

        //set layout background color & status label based on value of correct
        if(correct){
            statusAnswerLabel.setText("Correct");
            statusAnswerLabel.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.answer_background));
        } else {
            statusAnswerLabel.setText("Wrong");
            statusAnswerLabel.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.answer_wrong_background));
        }

        return view;
    }
}
