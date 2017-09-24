package com.example.maola.degradotourmap.Model;

/**
 * Created by Maola on 29/08/2017.
 */

import com.google.firebase.database.IgnoreExtraProperties;

// [START comment_class]
@IgnoreExtraProperties
public class Comment {

    public String uid;
    public String author;
    public String text;
    public String reportingDate;

    public Comment() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Comment(String uid, String author, String text, String reportingDate) {
        this.uid = uid;
        this.author = author;
        this.text = text;
        this.reportingDate = reportingDate;
    }

}
// [END comment_class]