package com.codepath.johnroyal.thegram.models;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_IMAGE = "image";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_CAPTION = "description";
    public static final String KEY_USER = "user";

    public Post () {}

    public Post (ParseFile image, String caption, ParseUser user) {
        setImage(image);
        setCaption(caption);
        setUser(user);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public String getCaption() {
        return getString(KEY_CAPTION);
    }

    public void setCaption(String caption) {
        put(KEY_CAPTION, caption);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public static void queryPosts(FindCallback<Post> callback) {
        ParseQuery.getQuery(Post.class)
                .include(Post.KEY_USER)
                .addDescendingOrder(Post.KEY_CREATED_AT)
                .setLimit(20)
                .findInBackground(callback);
    }

    public static void queryPostsByUser(ParseUser user, FindCallback<Post> callback) {
        ParseQuery.getQuery(Post.class)
                .include(Post.KEY_USER)
                .addDescendingOrder(Post.KEY_CREATED_AT)
                .whereEqualTo(KEY_USER, user)
                .findInBackground(callback);
    }
}
