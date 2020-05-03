package com.tecknologick.studybuddy;

import android.animation.Animator;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by michaellungu on 4/20/17.
 * Application level class
 */

public class MyApplication extends Application {

    public static String TAG = "studybuddy";                           //Log tag
    private boolean hasNextSection;                             //bool to check if paper has an additional section
    private int currentSection;                                 //index of current section being viewed in paper

    public static String BRAINSTEIN_STUDENT_APP = "co.brainstein.student";          // package name for brainstein student
    public static String BRAINSTEIN_PWA = "https://brainstein-temp.web.app/";           // url for brainstein PWA


    // Hold a reference to the current animator, so that it can be canceled mid-way.
    private Animator mCurrentAnimator;

    //The system "short" animation time duration, in milliseconds. This duration is ideal for subtle animations or animations that occur very frequently.
    private int mShortAnimationDuration;


    //setters
    public void setHasNextSection(boolean hasNextSection){
        this.hasNextSection = hasNextSection;
    }

    public void setCurrentSection(int currentSection){
        this.currentSection = currentSection;
    }

    //getters
    public boolean getHasNextSection(){
        return hasNextSection;
    }

    public int getCurrentSection(){
        return currentSection;
    }

    @Override
    public void onCreate() {

        super.onCreate();


        //Set true to overwrite database - Optional
        boolean overwriteDatabase = true;

        //copy bundled realm database to app
        if (overwriteDatabase){

            copyBundledRealmFile(this.getResources().openRawResource(R.raw.studybuddy), "studybuddy.realm");

        } else {

            //check if the db is already in place
            if (!fileFound("testdb.realm", this.getFilesDir())){
                copyBundledRealmFile(this.getResources().openRawResource(R.raw.studybuddy), "studybuddy.realm");
            }

        }

        //initialize realm
        Realm.init(this);

        //realm migration to add fields
        RealmMigration migration = new RealmMigration() {
            @Override
            public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                RealmSchema schema = realm.getSchema();

                if(oldVersion == 2){
                    //add fields
                    schema.get("Question")
                            .addField("question2", String.class);

                    //increment
                    oldVersion++;
                }
            }
        };

        //create realm configuration
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("studybuddy.realm")
                .schemaVersion(3)
                .migration(migration)
                .build();

        //set default realm configuration
        Realm.setDefaultConfiguration(config);

    }

    //shit i copied from some site: copies realm file to app
    private String copyBundledRealmFile(InputStream inputStream, String outFileName) {
        try {
            File file = new File(this.getFilesDir(), outFileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean fileFound(String name, File file) {
        File[] list = file.listFiles();
        if (list != null)
            for (File fil : list) {
                if (fil.isDirectory()) {
                    fileFound(name, fil);
                } else if (name.equalsIgnoreCase(fil.getName())) {
                    return true;
                }
            }
        return false;
    }

    //function to convert the String representation of an image to a bitmap
    public static Bitmap Base64ToBitmap(String myImageData)
    {
        byte[] imageAsBytes = Base64.decode(myImageData.getBytes(),Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    /**
     *  function to check if package / app is installed and enable it if it is
     * @param packageName
     * @param packageManager
     * @return
     */
    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            return packageManager.getApplicationInfo(packageName, 0).enabled;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * function to open brainstein tutor app or PWA
     * @param context
     */
    public static void gotoBrainsteinApp (Context context) {
        if (isPackageInstalled(BRAINSTEIN_STUDENT_APP,context.getPackageManager())) {
            context.startActivity(new Intent(context.getPackageManager().getLaunchIntentForPackage(BRAINSTEIN_STUDENT_APP)));
        } else {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BRAINSTEIN_PWA)));
        }
    }


}
