package com.tecknologick.studybuddy;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class ModuleActivity extends AppCompatActivity {

    Toolbar toolbar;
    private Intent i;
    AlertDialog.Builder alert;

    //TODO: change bottom navigation to tabs if necessary

    // set bottom navigation
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {


        //override selected item listener
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            //create null fragment
            Fragment selectedFragment = null;

            //switch...case to determine selected item
            switch (item.getItemId()) {

                //case of past papers
                case R.id.navigation_subjects:
                    selectedFragment = ModuleFragment.newInstance();
                    break;

                //case of progress
//                case R.id.navigation_progress:
//                    selectedFragment = MarksFragment.newInstance();
//                    break;

                //case of settings
//                case R.id.navigation_settings:
//                    selectedFragment = StoreFragment.newInstance();
//                    break;

                // case of help
                case R.id.navigation_help:
                    selectedFragment = HelpFragment.newInstance();
                    break;
            }

            //display selected fragment on activity
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.contentFrameLayout, selectedFragment);
            transaction.commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.contentFrameLayout, ModuleFragment.newInstance());
        transaction.commit();


        //set toolbar
        toolbar = (Toolbar) findViewById(R.id.moduleToolBar);

        //set up button
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(getApplicationContext(), CourseActivity.class);
                startActivity(i);
                finish();
            }
        });

        //init dialog box
        alert = new AlertDialog.Builder(this);

    }


    //override hardware back button
    @Override
    public void onBackPressed() {
        //show alert dialog
        alert.setTitle("Exit App");
        alert.setMessage("Would you like to exit Study Buddy?")
                //continue paper
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                //exit paper
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //destroy current activity
                        finish();
                    }
                });
        alert.create();
        alert.show();
    }

    // go to brainstein papers
    public void openBrainsteinStudent(View view) {
        MyApplication.gotoBrainsteinApp(this);
    }
}
