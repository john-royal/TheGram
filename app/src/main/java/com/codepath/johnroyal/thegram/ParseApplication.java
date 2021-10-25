package com.codepath.johnroyal.thegram;

import android.app.Application;

import com.codepath.johnroyal.thegram.models.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Post.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("A0KIN2Nrb2XF3cx0jF1oO36EFloEpEmSymsIYQLP")
                .clientKey("doAoGmwBZRkgPyyf81fYsPKzDWbzU1CWVMlYbTYY")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
