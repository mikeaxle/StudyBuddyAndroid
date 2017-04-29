/**
 * Created by michaellungu on 4/20/17.
 * Section Realm object
 * Can be for nested question as well e.g Section B, Question 3.1
 */

package com.tecknologick.studybuddy.RealmClasses;


import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Section extends RealmObject {

    @PrimaryKey
    public int id;
    public String name; //can be section name or question number
    public int allocatedMarks;

    public RealmList<Section> nestedQuestions; //nested sections are will be labelled as questions instead

    public RealmList<Question> questions; //questions



}
