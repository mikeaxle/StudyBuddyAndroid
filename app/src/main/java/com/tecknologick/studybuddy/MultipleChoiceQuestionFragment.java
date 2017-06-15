package com.tecknologick.studybuddy;


import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tecknologick.studybuddy.Adapters.CustomListAdapter;
import com.tecknologick.studybuddy.Adapters.ExpandableHeightListView;
import com.tecknologick.studybuddy.RealmClasses.Question;


public class MultipleChoiceQuestionFragment extends Fragment {

    //variable to store fragment value
    int fragNum;
    TextView questionNumberLabel;
    TextView allocatedMarksLabel;
    TextView questionLabel;
    TextView questionBottomLabel;
    ImageView questionImageView;
    ExpandableHeightListView multipleChoiceQuestionsListView;
    CustomListAdapter adapter;
    Question question;
    String[] answers;
    String[] answerImages;
    boolean correct;
    String questionType;


    //create new instance of fragment using num as argument
    public static MultipleChoiceQuestionFragment newInstance(int num){

        //instantiate fragment
        MultipleChoiceQuestionFragment mcqf = new MultipleChoiceQuestionFragment();

        // Supply num input as a bundle argument
        Bundle args = new Bundle();
        args.putInt("num", num);
        mcqf.setArguments(args);

        //return fragment
        return mcqf;

    }

    //get arguments on create
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        fragNum =  getArguments() != null ? getArguments().getInt("num") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_multiple_choice_question, container, false);

        //get current activity
        final QuestionActivity questionActivity = (QuestionActivity) getActivity();

        //get current question
        question = questionActivity.questions.get(fragNum);

        //check if question has an image
        if(question.questionImage != null && !question.questionImage.equals("")){

            //check if there is a question after the image
            if(question.question2 != null && !question.question2.equals("")){

                //assign question2 from realm to question after image
                questionBottomLabel = (TextView) view.findViewById(R.id.questionBottomLabel);
                questionBottomLabel.setText(Html.fromHtml(question.question2.trim()));
                questionBottomLabel.setVisibility(View.VISIBLE);

            }
            
            //get image view
            questionImageView = (ImageView) view.findViewById(R.id.questionImageView);

            //set image
            questionImageView.setImageBitmap(MyApplication.Base64ToBitmap(question.questionImage));

            //make visible
            questionImageView.setVisibility(View.VISIBLE);

        }

        //get text views
        questionNumberLabel = (TextView) view.findViewById(R.id.questionNumberLabel);
        allocatedMarksLabel = (TextView) view.findViewById(R.id.allocatedMarksLabel);
        questionLabel = (TextView) view.findViewById(R.id.questionLabel);

        //set text views
        questionNumberLabel.setText(question.name.trim());

        //check if allocated marks are more than 1
        if(question.allocatedMarks == 1){

            allocatedMarksLabel.setText("(" + question.allocatedMarks + " mark)");
        } else {

            allocatedMarksLabel.setText("(" + question.allocatedMarks + " marks)");
        }

        questionLabel.setText(Html.fromHtml(question.question.trim()));

        //check if options c and d are set: this checks if the question is a true/false question
        if(question.c.equals("") && question.d.equals("")){

            //store answers into string array
            answers = new String[]{question.a, question.b};

        } else {
            //store answers into string array
            answers = new String[]{question.a, question.b, question.c, question.d};

            //check answers have images and add to array
            if(question.aImage != null){

                answerImages = new String[]{question.aImage, question.bImage, question.cImage, question.dImage};

            } else {

                answerImages = null;
            }
        }

        //init adapter
        adapter = new CustomListAdapter(getActivity().getApplicationContext(), R.layout.row_multiple_choice_question, "multiple choice", answerImages);
        adapter.addAll(answers);

        //init list view and set adapter
        multipleChoiceQuestionsListView = (ExpandableHeightListView) view.findViewById(R.id.multipleChoiceQuestionsListView);
        multipleChoiceQuestionsListView.setAdapter(adapter);
        multipleChoiceQuestionsListView.setExpanded(true);


        //set list view item click listener
        multipleChoiceQuestionsListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        //check answer, go to appropriate answer activity
                        correct = false;

                        //condition to check if answers are images and how to handle in flip card

                        if(question.answer.trim().equals(answers[position].trim())){

                            //increment number of correct items on parent activity
                            questionActivity.correct++;

                            //log number of correct items
                            Log.d(MyApplication.TAG, "number of correct answers: " + questionActivity.correct);

                            //set local correct boolean to true
                            correct = true;

                        }

                        //get instance of parent fragment
                        QuestionContainerFragment questionFragment = (QuestionContainerFragment)getParentFragment();

                        //call flipCard function of parent fragment
                        questionFragment.flipCard(correct, answers[position], question.answer, question.question, question.explanation, question.answerImage);

                    }
                }
        );

        return  view;
    }

}
