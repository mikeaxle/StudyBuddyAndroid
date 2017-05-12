package com.tecknologick.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tecknologick.studybuddy.Adapters.CustomListAdapter;
import com.tecknologick.studybuddy.RealmClasses.Institution;
import com.tecknologick.studybuddy.SharedPref.TinyDB;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

public class InstitutionActivity extends AppCompatActivity {

    //declare variables
    ListView listView;
    CustomListAdapter adapter;
    TextView institutionTitle;
    RealmResults<Institution> institutions;
    private Realm realm;
    int institutionID;
    TinyDB tinyDB;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institution);

        //check if institution id is set
        tinyDB =  new TinyDB(getApplicationContext());
        institutionID = tinyDB.getInt("institutionID");

        //if institution id is set, go to course activity
        if(institutionID != 0) {
            i = new Intent(getApplicationContext(), CourseActivity.class);
            startActivity(i);
        }

        //get all institutions from realm
        try {

            //get realm instance
            realm = Realm.getDefaultInstance();

            //query all institutions
            institutions = realm.where(Institution.class)
                    .findAll();


        } catch (RealmException re){

            //log realm error
            Log.d(MyApplication.TAG,"Realm error: " + re.getMessage());

        }

        //init adapter
        adapter = new CustomListAdapter(this, R.layout.row_institution_list, "institution");
        adapter.addAll(institutions);
        listView = (ListView) findViewById(R.id.institutionListView);
        listView.setAdapter(adapter);

        //set list view item click listener
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //create intent and send to course activity
                        i = new Intent(getApplicationContext(), CourseActivity.class);

                        //get selected institution id
                        institutionID = institutions.get(position).id;

                        // write institution id to shared preferences
                        tinyDB.putInt("institutionID", institutionID);

                        //go to activity
                        startActivity(i);
                    }
                }
        );
    }

    // onDestroy
    @Override
    public void onDestroy(){
        super.onDestroy();

        //close realm object
        realm.close();

    }
}
