package com.tecknologick.studybuddy.Adapters;

import android.text.Editable;
import android.text.Html;

import org.xml.sax.XMLReader;

/**
 * Created by michaellungu on 6/23/17.
 */

public class UlTagHandler implements Html.TagHandler{

    boolean first = true;

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {

        if (tag.equals("li")) {
            char lastChar = 0;
            if (output.length() > 0)
                lastChar = output.charAt(output.length() - 1);
            if (first) {
                if (lastChar == '\n')
                    output.append("\t•  ");
                else
                    output.append("\n\t•  ");
                first = false;
            } else {
                first = true;
            }
        }
    }
}