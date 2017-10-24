package com.mindcoders.phial.internal;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.keyvalue.KeyValueView;
import com.mindcoders.phial.internal.share.ShareView;

import android.content.Context;
import android.view.View;

class DefaultPages {

    static final Page KEY_VALUE_PAGE = new Page(
            R.drawable.ic_keyvalue,
            "System info", new Page.PageViewFactory() {
        @Override
        public View createPageView(Context context) {
            return new KeyValueView(context);
        }
    }
    );

    static final Page SHARE_PAGE = new Page(
            R.drawable.ic_share,
            "Share", new Page.PageViewFactory() {
        @Override
        public View createPageView(Context context) {
            return new ShareView(context);
        }
    }
    );

}
