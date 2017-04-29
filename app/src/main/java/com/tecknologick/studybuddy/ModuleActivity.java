package com.tecknologick.studybuddy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class ModuleActivity extends AppCompatActivity {

    Toolbar toolbar;

    //TODO: change bottom navigation to tabs if necessary

    //get bottom navigation
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {


        //override selected item listener
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            //create null fragment
            Fragment selectedFragment = null;

            //switch...case to determine selected item
            switch (item.getItemId()) {

                //case of modules/subjects
                case R.id.navigation_subjects:

                    //selected fragment is assigned as module fragment instance
                    selectedFragment = ModuleFragment.newInstance();
                    break;

                //case of marks/progress
                case R.id.navigation_marks:

                    //selected fragment is assigned as module fragment instance
                    selectedFragment = MarksFragment.newInstance();
                    break;

                //case of more papers aka store
                case R.id.navigation_more_papers:
                    selectedFragment = StoreFragment.newInstance();
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
        toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v){
                finish();
            }
        });

    }

    //set change grade button
    public void onClick(View view){

        //send back to course page, clear stack
        finish();
    }
}
