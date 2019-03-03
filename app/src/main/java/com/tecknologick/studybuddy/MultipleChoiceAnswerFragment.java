package com.tecknologick.studybuddy;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tecknologick.studybuddy.Adapters.UlTagHandler;


public class MultipleChoiceAnswerFragment extends Fragment {

    int fragNum;
    boolean correct;
    String selectedAnswer;
    String answer;
    String question;
    String question2;
    String explanation;
    String questionImage;
    String answerImage;
    TextView statusAnswerLabel;
    TextView questionAnswerLabel;
    TextView questionAnswerBelowLabel;
    TextView answerAnswerLabel;
    TextView selectedAnswerLabel;
    TextView explanationAnswerLabel;
    ImageButton questionImageButton;
    ImageButton answerImageButton;

    public MultipleChoiceAnswerFragment() {
        // Required empty public constructor
    }

    //create new instance of fragment using num as argument
    public static MultipleChoiceAnswerFragment newInstance(int num, boolean correct, String selectedAnswer, String answer, String question, String question2, String explanation, String questionImage, String answerImage){
        //instantiate fragment
        MultipleChoiceAnswerFragment mcaf = new MultipleChoiceAnswerFragment();

        // Supply num input as a bundle argument
        Bundle args = new Bundle();
        args.putString("answer", answer);
        args.putString("selectedAnswer", selectedAnswer);
        args.putString("question", question);
        args.putString("question2", question2);
        args.putString("explanation", explanation);
        args.putString("questionImage", questionImage);
        args.putString("answerImage", answerImage);
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
        correct = getArguments() == null || getArguments().getBoolean("correct");
        selectedAnswer = getArguments() != null ? getArguments().getString("selectedAnswer") : "";
        answer = getArguments() != null ? getArguments().getString("answer") : "";
        question = getArguments() != null ? getArguments().getString("question") : "";
        question2 = getArguments() != null ? getArguments().getString("question2") : "";
        explanation = getArguments() != null ? getArguments().getString("explanation") : "";
        questionImage = getArguments() != null ? getArguments().getString("questionImage") : "";
        answerImage = getArguments() != null ? getArguments().getString("answerImage") : "";
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_multiple_choice_answer, container, false);

        //get text views
        statusAnswerLabel = view.findViewById(R.id.statusAnswerLabel);
        answerAnswerLabel = view.findViewById(R.id.testy).findViewById(R.id.multipleChoiceQuestionRowQuestionLabel);
        selectedAnswerLabel = view.findViewById(R.id.testy2).findViewById(R.id.multipleChoiceQuestionRowQuestionLabel);

        //set text views
        selectedAnswerLabel.setText(Html.fromHtml(selectedAnswer.trim()));
        answerAnswerLabel.setText(Html.fromHtml(answer.trim()));

        //check if there is a question before the image
        if(question != null && !question.equals("")){

            //get and set text view
            questionAnswerLabel = view.findViewById(R.id.questionAnswerLabel);
            questionAnswerLabel.setText(Html.fromHtml(question.trim(), null, new UlTagHandler()));

            //make visible
            questionAnswerLabel.setVisibility(View.VISIBLE);
        }

        //check if there is a question after the image
        if(question2 != null && !question2.equals("")){

            //get and set text view
            questionAnswerBelowLabel = view.findViewById(R.id.questionAnswerBelowLabel);
            questionAnswerBelowLabel.setText(Html.fromHtml(question2.trim(), null, new UlTagHandler()));

            //make visible
            questionAnswerBelowLabel.setVisibility(View.VISIBLE);
        }

        //check if there is a question image
        if(questionImage != null && !questionImage.equals("")){

            //get answer image button
            questionImageButton = view.findViewById(R.id.questionAnswerImageButton);

            //convert base64 string to image
            final Bitmap bitmap = MyApplication.Base64ToBitmap(questionImage);

            //set image
            questionImageButton.setImageBitmap(bitmap);

            //make visible
            questionImageButton.setVisibility(View.VISIBLE);

        }

        //set layout background color & status label based on value of correct
        if(correct){
            statusAnswerLabel.setText(getResources().getString(R.string.correct));
            statusAnswerLabel.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.answer_correct_background));
        } else {
            statusAnswerLabel.setText(getResources().getString(R.string.wrong));
            statusAnswerLabel.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.answer_wrong_background));
        }

        //check if an explanation is available
        if(explanation != null && !explanation.equals("")){

            //get and set explanation textview
            explanationAnswerLabel = view.findViewById(R.id.explanationAnswerLabel);
            explanationAnswerLabel.setText(Html.fromHtml("<b>Explanation:</b> " + explanation.trim()));

            //show textview
            explanationAnswerLabel.setVisibility(View.VISIBLE);

        }

        //check if answer has an image
        if(answerImage != null && !answerImage.equals("")){

            //get answer image button
            answerImageButton = view.findViewById(R.id.multipleChoiceAnswerImageButton);

            //convert base64 string to image
            final Bitmap bitmap = MyApplication.Base64ToBitmap(answerImage);

            //set image
            answerImageButton.setImageBitmap(bitmap);

            //make visible
            answerImageButton.setVisibility(View.VISIBLE);

        }

        return view;
    }
}
