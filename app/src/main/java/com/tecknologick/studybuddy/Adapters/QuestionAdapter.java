/**
 * Created by michaellungu on 4/23/17.
 */

package com.tecknologick.studybuddy.Adapters;

import android.app.Fragment;
import android.app.FragmentManager;

import androidx.legacy.app.FragmentStatePagerAdapter;

import com.tecknologick.studybuddy.QuestionContainerFragment;


public class QuestionAdapter extends FragmentStatePagerAdapter {

    private  int NUM_ITEMS;
    private  String questionType;

    public QuestionAdapter(FragmentManager fragmentManager, int num_items, String questionType){
        super(fragmentManager);

        NUM_ITEMS = num_items;
        this.questionType = questionType;

    }

    @Override
    public int getCount(){
        return NUM_ITEMS;
    }



    @Override
    public Fragment getItem(int position){

        return QuestionContainerFragment.newInstance(position, questionType);
    }

}
