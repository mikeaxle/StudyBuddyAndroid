package com.tecknologick.studybuddy;


import android.app.Fragment;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tecknologick.studybuddy.RealmClasses.Question;

import java.util.HashMap;
import java.util.Locale;


public class EssayQuestionFragment extends Fragment {

    //variable to store fragment value
    int fragNum;
    TextView essayQuestionNumberLabel;
    TextView essayAllocatedMarksLabel;
    TextView essayQuestionLabel;
    TextView readAloudQuestionLabel;
    Question question;
    ImageView essayQuestionImageView;
    ImageButton seeAnswerButton;
    ImageButton readAloudButton;
    TextToSpeech textToSpeech;
    boolean readingFlag = false;

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

    //override on init text to speech listener


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
        readAloudQuestionLabel = (TextView) view.findViewById(R.id.readAloudQuestionLabel);

        //set text views
        essayQuestionNumberLabel.setText(question.name);
        essayAllocatedMarksLabel.setText("(" + question.allocatedMarks + " marks)");
        essayQuestionLabel.setText(question.question);                                              //display question

        //check if question has an image
        if(question.questionImage != null){
            //get image view
            essayQuestionImageView = (ImageView) view.findViewById(R.id.essayQuestionImageView);

            //set image
            essayQuestionImageView.setImageBitmap(MyApplication.Base64ToBitmap(question.questionImage));

            //make visible
            essayQuestionImageView.setVisibility(view.VISIBLE);
        }

        //get image buttons
        seeAnswerButton = (ImageButton) view.findViewById(R.id.seeAnswerImageButton);
        readAloudButton = (ImageButton) view.findViewById(R.id.readAloudQuestionImageButton);

        //utterance id string hash map, required for utterance listeners to work
        final HashMap<String, String> params= new HashMap<String,String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"messageID");

        //init text to speech
        textToSpeech = new TextToSpeech(questionActivity, new TextToSpeech.OnInitListener() {
            //override initialization method
            @Override
            public void onInit(int status) {

                //if speech engine is not in an error state
                if(status != TextToSpeech.ERROR){

                    //set locale
                    textToSpeech.setLanguage(Locale.ENGLISH);

                    //set audio progress listener
                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                        @Override
                        public void onStart(String utteranceId) {
                            //change read aloud button image
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //change button & text color
                                    readAloudButton.setColorFilter(getResources().getColor(R.color.colorBgGray), PorterDuff.Mode.SRC_IN);
                                    readAloudQuestionLabel.setTextColor(getResources().getColor(R.color.colorBgGray));
                                }
                            });
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            //change read aloud button image
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //change button & text color
                                    readAloudButton.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                                    readAloudQuestionLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
                                }
                            });
                        }

                        @Override
                        public void onError(String utteranceId) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(questionActivity, "Sorry, Study Buddy cannot read aloud right now", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });

        //set button click listeners
        readAloudButton.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {
                //read question aloud

                if(readingFlag){

                    textToSpeech.stop();
                    readingFlag = false;

                } else {

                    textToSpeech.speak(question.question, TextToSpeech.QUEUE_FLUSH, params);
                    readingFlag = true;

                }

            }
        });

        seeAnswerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                QuestionContainerFragment questionFragment = (QuestionContainerFragment)getParentFragment();            //get instance of parent fragment

                //call flipCard function of parent fragment
                if (question.answerImage != null){

                    //check if answer has an image
                    questionFragment.flipCard(question.answer, question.question, question.answerImage);

                } else {
                    questionFragment.flipCard(question.answer, question.question, null);
                }
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        super.onDestroy();
    }

}
