package com.mindcoders.phial.internal.overlay;

import android.content.Context;
import android.view.ContextThemeWrapper;

import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.PhialCore;

/**
 * Created by rost on 11/27/17.
 */

public class OverlayFactory {
    private static OverlayView createView(Context baseContext, PhialCore phialCore) {
        final ContextThemeWrapper wrappedContext = new ContextThemeWrapper(baseContext, R.style.Theme_Phial);
        final PositionStorage storage = new PositionStorage(phialCore.getSharedPreferences());

        return new OverlayView(
                wrappedContext,
                new DragHelper(storage, R.dimen.phial_content_padding),
                new ExpandedView(wrappedContext)
        );
    }

    public static OverlayPresenter createPresenter(Context context, PhialCore phialCore) {
        final OverlayView view = createView(context, phialCore);

        final OverlayPresenter presenter = new OverlayPresenter(
                view,
                phialCore.getPages(),
                new SelectedPageStorage(phialCore.getSharedPreferences()),
                phialCore.getScreenTracker(),
                phialCore.getNotifier()
        );
        view.setPresenter(presenter);

        return presenter;
    }
}
