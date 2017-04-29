/**
 * Created by michaellungu on 4/20/17.
 * Paper Realm Object
 */

package com.tecknologick.studybuddy.RealmClasses;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Paper extends RealmObject {

    //properties
    @PrimaryKey
    public int id;
    public String name;
    public String year;
    public String description;
    public String image;


    //related to papers
    public RealmList<Section> sections;
}
