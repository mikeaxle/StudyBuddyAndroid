/**
 * Created by michaellungu on 4/20/17.
 * Institution Realm Object
 */

package com.tecknologick.studybuddy.RealmClasses;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Institution extends RealmObject {

    //properties
    @PrimaryKey
    public int id;
    public String name;
    public String description;
    public String image;
    public String url;

    //related to courses
    public RealmList<Course> courses;
}
