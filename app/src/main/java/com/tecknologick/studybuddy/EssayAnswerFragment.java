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

import java.util.HashMap;
import java.util.Locale;


public class EssayAnswerFragment extends Fragment {

    int fragNum;
    String answer;
    String question;
    String answerImage;
    //TextView essayQuestionNumberAnswerLabel;
    TextView essayQuestionAnswerLabel;
    TextView readAloudAnswserLabel;
    ImageView essayAnswerImageView;
    ImageButton seeQuestionButton;
    ImageButton readAloudAnswerImageButton;
    TextToSpeech textToSpeech;
    boolean readingFlag = false;


    public EssayAnswerFragment() {
        // Required empty public constructor
    }

    //create new instance of fragment using num as argument
    public static EssayAnswerFragment newInstance(int num, String answer, String question, String answerImage){

        //instantiate fragment
        EssayAnswerFragment eaf = new EssayAnswerFragment();

        // Supply num input as a bundle argument
        Bundle args = new Bundle();
        args.putString("answer", answer);
        args.putString("question", question);
        args.putString("answerImage", answerImage);
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
        answerImage = getArguments() != null ? getArguments().getString("answerImage") : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_essay_answer, container, false);

        //get text views
        essayQuestionAnswerLabel = (TextView) view.findViewById(R.id.essayQuestionAnswerLabel);
        readAloudAnswserLabel = (TextView) view.findViewById(R.id.readAloudAnswserLabel);

        //set text view
        essayQuestionAnswerLabel.setText(answer);

        //check if question has an image
        if(answerImage != null){
            //get image view
            essayAnswerImageView = (ImageView) view.findViewById(R.id.EssayAnswerImageView);

            //set image
            essayAnswerImageView.setImageBitmap(MyApplication.Base64ToBitmap(answerImage));

            //make visible
            essayAnswerImageView.setVisibility(view.VISIBLE);
        }

        //get image buttons
        seeQuestionButton = (ImageButton) view.findViewById(R.id.seeQuestionImageButton);
        readAloudAnswerImageButton = (ImageButton) view.findViewById(R.id.readAloudAnswerImageButton);

        //set button click listeners
        seeQuestionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                QuestionContainerFragment questionFragment = (QuestionContainerFragment)getParentFragment();            //get instance of parent fragment

                //call flipCard function of parent fragment
                questionFragment.flipCard(answer, question, null);
            }
        });

        //utterance id string hash map, required for utterance listeners to work
        final HashMap<String, String> params= new HashMap<String,String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"messageID");

        readAloudAnswerImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //read question aloud

                if(readingFlag){

                    textToSpeech.stop();
                    readingFlag = false;

                } else {

                    textToSpeech.speak(answer, TextToSpeech.QUEUE_FLUSH, params);
                    readingFlag = true;

                }

            }
        });

        //init text to speech
        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
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
                                    readAloudAnswerImageButton.setColorFilter(getResources().getColor(R.color.colorBgGray), PorterDuff.Mode.SRC_IN);
                                    readAloudAnswserLabel.setTextColor(getResources().getColor(R.color.colorBgGray));

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
                                    readAloudAnswerImageButton.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                                    readAloudAnswserLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
                            }
                            });
                        }

                        @Override
                        public void onError(String utteranceId) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "Sorry, Study Buddy cannot read aloud right now", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
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
