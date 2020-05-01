package com.tecknologick.studybuddy;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.model.value.ServerTimestampValue;
import com.tecknologick.studybuddy.Adapters.UlTagHandler;
import com.tecknologick.studybuddy.RealmClasses.Question;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class EssayQuestionFragment extends Fragment {

    private final String TAG = "Essay Question";
    // Hold a reference to the current animator, so that it can be canceled mid-way.
    private Animator mCurrentAnimator;

    // The system "short" animation time duration, in milliseconds.
    // This duration is ideal for subtle animations or animations that occur very frequently.
    private int mShortAnimationDuration;

    // firebase authentication
    private FirebaseAuth firebaseAuth;

    // firestore instance
    private FirebaseFirestore firestore;

    //variable to store fragment value
    int fragNum;
    TextView essayQuestionNumberLabel;
    TextView essayAllocatedMarksLabel;
    TextView essayQuestionLabel;
    TextView essayQuestionBottomLabel;
    TextView readAloudQuestionLabel;
    Question question;
    ImageButton essayQuestionImageView;
    ImageButton seeAnswerButton;
    ImageButton readAloudButton;
    ImageButton requestTutor;
    TextToSpeech textToSpeech;
    boolean readingFlag = false;
    View view;
    Bitmap bitmap = null;
    Bitmap bitmapTemp = null;

    // create new instance of fragment using num as argument
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
        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        // init firestore
        firestore = FirebaseFirestore.getInstance();

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_essay_question, container, false);

        //get current activity
        final QuestionActivity questionActivity = (QuestionActivity) getActivity();

        //get current question
        question = questionActivity.questions.get(fragNum);

        //get text view
        essayQuestionNumberLabel = (TextView) view.findViewById(R.id.essayQuestionNumberLabel);
        essayAllocatedMarksLabel = (TextView) view.findViewById(R.id.essayAllocatedMarksLabel);

        readAloudQuestionLabel = (TextView) view.findViewById(R.id.readAloudQuestionLabel);

        //set text views
        essayQuestionNumberLabel.setText(question.name);
        essayAllocatedMarksLabel.setText("(" + question.allocatedMarks + " marks)");

        //check if question is set
        if(question.question != null && !question.question.equals("")){

            //assign question from realm to question before image
            essayQuestionLabel = (TextView) view.findViewById(R.id.essayQuestionLabel);
            essayQuestionLabel.setText(Html.fromHtml(question.question.trim(), null, new UlTagHandler()));
            essayQuestionLabel.setVisibility(View.VISIBLE);

        }

        //check if there is a question after the image
        if(question.question2 != null && !question.question2.equals("")){

            //assign question2 from realm to question after image
            essayQuestionBottomLabel = (TextView) view.findViewById(R.id.essayQuestionBottomLabel);
            essayQuestionBottomLabel.setText(Html.fromHtml(question.question2.trim(), null, new UlTagHandler() ));
            essayQuestionBottomLabel.setVisibility(View.VISIBLE);

        }

        //check if question has an image
        if(question.questionImage != null && !question.questionImage.equals("")){
            //get image view
            essayQuestionImageView = (ImageButton) view.findViewById(R.id.essayQuestionImageView);



//            // check if questionImage is url, starts with 'http'
            String compare = question.questionImage.toString().substring(0, 4);
            if (compare.equals("http")) {
                // convert url to bitmap
                String questionImageString = question.questionImage + "";
                // TODO: put in async thread
                new GetImageFromUrl(essayQuestionImageView).execute(questionImageString);
//                try {
//                    new Thread(new Thread(() -> {
//                        bitmapTemp = imageLoader.loadImageSync(questionImageString);
//                    })).start();
//                } catch (Exception e) {
//                    Log.e(TAG, e.getMessage());
//                }



            } else {
                //convert base64 string to bitmap
                Log.d(MyApplication.TAG, "image is BASE64");
                bitmap = MyApplication.Base64ToBitmap(question.questionImage);
                essayQuestionImageView.setImageBitmap(bitmap);
            }

//            bitmap = bitmapTemp;

            //set image


            //make visible
            essayQuestionImageView.setVisibility(View.VISIBLE);

            //hook up click listener
            essayQuestionImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //get expanded image view ID
                    int expandedImageId = R.id.expandedEssayQuestionImage;

                    //get container ID
                    //int containerID = R.id.essayQuestionContainer;

                    //call zoom image function
                    zoomImageFromThumb(essayQuestionImageView, bitmap, expandedImageId);
                }
            });

            // Retrieve and cache the system's default "short" animation time.
            mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        }

        //get image buttons
        seeAnswerButton = (ImageButton) view.findViewById(R.id.seeAnswerImageButton);
        readAloudButton = (ImageButton) view.findViewById(R.id.readAloudQuestionImageButton);
        requestTutor = view.findViewById(R.id.questionETutorImageButton);

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
                            getActivity().runOnUiThread(() -> {
                                //change button & text color
                                readAloudButton.setColorFilter(getResources().getColor(R.color.colorBgGray), PorterDuff.Mode.SRC_IN);
                                readAloudQuestionLabel.setTextColor(getResources().getColor(R.color.colorBgGray));
                            });
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            //change read aloud button image
                            getActivity().runOnUiThread(() -> {
                                //change button & text color
                                readAloudButton.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                                readAloudQuestionLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
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
        readAloudButton.setOnClickListener(v -> {
            //read question aloud

            if(readingFlag){

                textToSpeech.stop();
                readingFlag = false;

            } else {

                //remove html tags from text
                String readableText = Html.fromHtml(question.question).toString();

                //check if question 2 is set
                if(question.question2 != null && !question.question2.equals("")){

                    //concat with question 1
                    readableText += Html.fromHtml("." + question.question2).toString();
                }

                //read aloud
                textToSpeech.speak(readableText, TextToSpeech.QUEUE_FLUSH, params);


                readingFlag = true;

            }

        });

        seeAnswerButton.setOnClickListener(v -> {
            // Perform action on click
            QuestionContainerFragment questionFragment = (QuestionContainerFragment)getParentFragment();            //get instance of parent fragment

            //call flipCard function of parent fragment
            if (question.answerImage != null){

                //check if answer has an image
                questionFragment.flipCard(question.answer, question.question, question.answerImage);

            } else {
                questionFragment.flipCard(question.answer, question.question, null);
            }
        });

        // set request tutor click listener
        requestTutor.setOnClickListener(v -> {
//            Toast.makeText(questionActivity, "Request tutor", Toast.LENGTH_SHORT).show();

            // if question has image
            if (question.questionImage != null && !question.questionImage.equals("")) {

            }

            // create question payload
            Map<String, Object> tutorQuestion = new HashMap<>();
            tutorQuestion.put("caption", question.question + "\n" + question.question2);
            tutorQuestion.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp());
            tutorQuestion.put("grade", 12);
            tutorQuestion.put("inSession", false);
            tutorQuestion.put("payload", null);
            tutorQuestion.put("subject", questionActivity.module.name);
            tutorQuestion.put("userId", firebaseAuth.getUid());

            Log.d(TAG, tutorQuestion.toString());

            // write to firestore
            firestore.collection("Questions").add(tutorQuestion)
                    .addOnCompleteListener(task -> {
                       if (task.isSuccessful()) {
                           Toast.makeText(questionActivity, "Question has been added to brainstein papers", Toast.LENGTH_SHORT).show();
                           Log.d(TAG, "Question has been added to brainstein papers");
                       } else {
                           Toast.makeText(questionActivity, "Something went wrong: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                           Log.d(TAG, "Something went wrong: " + task.getException().getMessage());
                       }
                    });

        });

        return view;
    }

    //function to zoom image
    private void zoomImageFromThumb(final View thumbView, Bitmap bitmap, int expandedImageId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        //final ImageView expandedImageView = (ImageView) view.findViewById(R.id.expandedEssayQuestionImage);
        final PhotoView expandedImageView = (PhotoView) view.findViewById(expandedImageId);
        expandedImageView.setImageBitmap(bitmap);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        view.findViewById(R.id.essayQuestionContainer).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
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

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }

        super.onDestroy();
    }

    // temp inner class to load images from url
    public class GetImageFromUrl extends AsyncTask<String, Void, Bitmap> {
        // internal image view
        ImageView imageView;

        // constructor
        GetImageFromUrl(ImageView img) {
            this.imageView = img;
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            String stringUrl = url[0];
            bitmap = null;
            InputStream inputStream;
            try {
                inputStream = new URL(stringUrl).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap){
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }

}
