package com.mindcoders.phial.sample;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.Phial;
import com.mindcoders.phial.internal.keyvalue.KeyValueView;
import com.mindcoders.phial.internal.share.ShareView;

import java.io.File;
import java.util.Collections;

import android.app.Application;
import android.content.Context;
import android.view.View;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Phial.builder(this)
             .addPage(new Page(
                     R.drawable.ic_keyvalue,
                     new Page.PageViewFactory() {
                         @Override
                         public View createPageView(Context context) {
                             return new KeyValueView(context);
                         }
                     }
             ))
             .addPage(new Page(
                     R.drawable.ic_share,
                     new Page.PageViewFactory() {
                         @Override
                         public View createPageView(Context context) {
                             ShareView shareView = new ShareView(context);
                             shareView.setFiles(Collections.<File>emptyList());
                             return shareView;
                         }
                     }
             ))
             .initPhial();
    }

}
