/**
 * Created by michaellungu on 4/20/17.
 * Question RealmObject
 */

package com.tecknologick.studybuddy.RealmClasses;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Question extends RealmObject {

    //properties
    @PrimaryKey
    public int id; //set number of question if 1, a or whatever
    public String name;
    public String type;
    public int allocatedMarks;

    public String question;
    public String question2;
    public String questionImage;

    public String answer;
    public String answerImage;

    public String explanation;
    public String explanationImage;

    /*  multiple choice & true/false questions
     *  if array has two option is true/false if has more than 2 is multiple choice
     */
    public String a;
    public String aImage;

    public String b;
    public String bImage;

    public String c;
    public String cImage;

    public String d;
    public String dImage;

}
