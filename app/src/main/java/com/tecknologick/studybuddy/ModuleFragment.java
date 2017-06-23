package com.tecknologick.studybuddy;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tecknologick.studybuddy.Adapters.CustomListAdapter;
import com.tecknologick.studybuddy.RealmClasses.Course;
import com.tecknologick.studybuddy.RealmClasses.Module;
import com.tecknologick.studybuddy.SharedPref.TinyDB;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;


public class ModuleFragment extends Fragment {

    // declare variables
    ListView listView;
    TextView titleText;
    CustomListAdapter adapter;
    RealmResults<Course> courses;
    RealmList<Module> modules;
    private Realm realm;
    int courseID;
    int moduleID;
    TinyDB tinyDB;

    // TODO: change to recycler view

    //method to instantiate fragment
    public static ModuleFragment newInstance() {
        return new ModuleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Instantiate objects
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //inflate view
        View view =  inflater.inflate(R.layout.fragment_module, container, false);

        //set realm ID of grade 12
        courseID = 233;

        //get modules from realm
        try {

            //get realm instance
            realm = Realm.getDefaultInstance();

            //assign modules Realm List
            modules = realm.where(Course.class)
                    .equalTo("id", courseID)
                    .findFirst()
                    .modules;


        } catch(RealmException re) {

            Log.d(MyApplication.TAG,"Realm error: " + re.getMessage());

        }


        //set title
        titleText = (TextView) getActivity().findViewById(R.id.moduleTitleLabel);
        titleText.setText(realm.where(Course.class).equalTo("id", courseID).findFirst().name);


        //init adapter
        adapter = new CustomListAdapter(getActivity(), R.layout.row_module_list,"module");
        adapter.addAll(modules);

        //init list view and set adapter
        listView = (ListView) view.findViewById(R.id.moduleListView);
        listView.setAdapter(adapter);

        //set list view item click listener
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        //create intent and send to paper class
                        Intent i = new Intent(getActivity(), PaperActivity.class);

                        //get module id
                        moduleID = modules.get(position).id;

                        //load module id and course id into int array
                        int[] tmp = {courseID, moduleID};

                        //add data to intent
                        i.putExtra("courseID_moduleID", tmp);

                        startActivity(i);
                    }
                }
        );

        //return view
        return view;

    }


    /* onDestroyView
     */
    @Override
     public void onDestroyView(){
        super.onDestroyView();

        //close realm object
        realm.close();

    }

}
