/**
 * Created by michaellungu on 4/20/17.
 * Module Realm Object
 */


package com.tecknologick.studybuddy.RealmClasses;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Module extends RealmObject {

    //properties
    @PrimaryKey
    public int id;
    public String name;
    public String description;
    public String image;


    //related to papers
    public RealmList<Paper> papers;

}
