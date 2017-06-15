package com.tecknologick.studybuddy;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class QuestionContainerFragment extends Fragment {

    int fragNum;
    String questionType;
    Fragment questionFragment;

    //bool to check if card is flipped
    private boolean cardFlipped = false;

    public QuestionContainerFragment() {
        // Required empty public constructor
    }

    /**
     * create new instance of fragment using num as argument
     * @param num
     * @return MultipleChoiceQuestionFragment
     */

    public static QuestionContainerFragment newInstance(int num, String questionType){

        //instantiate fragment
        QuestionContainerFragment mcqf = new QuestionContainerFragment();

        // Supply num input as a bundle argument
        Bundle args = new Bundle();
        args.putInt("num", num);
        args.putString("questionType", questionType);
        mcqf.setArguments(args);

        //return fragment
        return mcqf;

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        fragNum =  getArguments() != null ? getArguments().getInt("num") : 1;
        questionType = getArguments() != null ? getArguments().getString("questionType") : "";
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question_container, container, false);

        //check question type and assigned value to questionFragment
        if(questionType.equals("multiple choice")){

            questionFragment = MultipleChoiceQuestionFragment.newInstance(fragNum);

        }else if(questionType.equals("essay")){

            questionFragment = EssayQuestionFragment.newInstance(fragNum);
        }

        //set initial question fragment
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.questionContainer,  questionFragment)
                .commit();


        //return view
        return  view;
    }

    //Function to flip question and answer cards
    public void flipCard(boolean correct, String selectedAnswer, String answer, String question, String explanation, String answerImage) {

        //create blank fragment
        Fragment newFragment;

        //check condition of card
        if (cardFlipped) {
            newFragment = MultipleChoiceQuestionFragment.newInstance(fragNum);
        } else {
            newFragment = MultipleChoiceAnswerFragment.newInstance(fragNum, correct, selectedAnswer, answer, question, explanation, answerImage);
        }

        //assign animations
        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.card_flip_right_in,
                        R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out
                )
                //replace fragment
                .replace(R.id.questionContainer, newFragment)
                .commit();

        //toggle boolean
        cardFlipped = !cardFlipped;
    }

    //overload flipCard function
    public void flipCard(String answer, String question, String answerImage) {

        //create blank fragment
        Fragment newFragment;

        //check condition of card
        if (cardFlipped) {
            newFragment = EssayQuestionFragment.newInstance(fragNum);
        } else {
            newFragment = EssayAnswerFragment.newInstance(fragNum, answer, question, answerImage);
        }

        //assign animations
        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.card_flip_top_in,
                        R.animator.card_flip_top_out,
                        R.animator.card_flip_bottom_in,
                        R.animator.card_flip_bottom_out
                )
                //replace fragment
                .replace(R.id.questionContainer, newFragment)
                .commit();

        //toggle boolean
        cardFlipped = !cardFlipped;
    }

}
