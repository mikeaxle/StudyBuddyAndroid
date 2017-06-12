package com.tecknologick.studybuddy;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;


public class EssayAnswerFragment extends Fragment {

    // Hold a reference to the current animator, so that it can be canceled mid-way.
    private Animator mCurrentAnimator;

    //The system "short" animation time duration, in milliseconds. This duration is ideal for subtle animations or animations that occur very frequently.
    private int mShortAnimationDuration;

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
    View view;


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
        view = inflater.inflate(R.layout.fragment_essay_answer, container, false);

        //get text views
        essayQuestionAnswerLabel = (TextView) view.findViewById(R.id.essayQuestionAnswerLabel);
        readAloudAnswserLabel = (TextView) view.findViewById(R.id.readAloudAnswserLabel);

        //set text view
        essayQuestionAnswerLabel.setText(answer);

        //check if question has an image
        if(answerImage != null && !answerImage.equals("")){
            //get image view
            essayAnswerImageView = (ImageView) view.findViewById(R.id.EssayAnswerImageView);

            //convert image to bitmap
            final Bitmap bitmap = MyApplication.Base64ToBitmap(answerImage);

            //set image
            essayAnswerImageView.setImageBitmap(bitmap);

            //make visible
            essayAnswerImageView.setVisibility(view.VISIBLE);

            //hook up click listener
            essayAnswerImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //get expanded image view ID
                    int expandedImageId = R.id.expandedEssayAnswerImage;

                    //get container ID
                    int containerID = R.id.essayAnswerContainer;

                    //call zoom image function
                    zoomImageFromThumb(essayAnswerImageView, bitmap, expandedImageId, containerID);
                }
            });

            // Retrieve and cache the system's default "short" animation time.
            mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

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


    //function to zoom image
    private void zoomImageFromThumb(final View thumbView, Bitmap imageResId, int expandedImageId, int containerID) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        //final ImageView expandedImageView = (ImageView) view.findViewById(R.id.expandedEssayQuestionImage);
        final ImageView expandedImageView = (ImageView) view.findViewById(expandedImageId);
        expandedImageView.setImageBitmap(imageResId);

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
        view.findViewById(containerID).getGlobalVisibleRect(finalBounds, globalOffset);
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
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        super.onDestroy();
    }
}
