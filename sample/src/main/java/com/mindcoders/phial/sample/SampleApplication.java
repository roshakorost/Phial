package com.mindcoders.phial.sample;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.Phial;
import com.mindcoders.phial.internal.PhialErrorPlugins;
import com.mindcoders.phial.internal.keyvalue.KeyValueView;
import com.mindcoders.phial.internal.share.ShareView;


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
                                return new ShareView(context);
                            }
                        }
                ))
                .initPhial();

        PhialErrorPlugins.setHandler(new PhialErrorPlugins.ErrorHandler() {
            @Override
            public void onError(Throwable throwable) {
                Log.w("Phial", throwable);
            }
        });
    }

}
