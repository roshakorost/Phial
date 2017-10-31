package com.mindcoders.phial.internal.share;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

import com.mindcoders.phial.OverlayCallback;
import com.mindcoders.phial.PageView;
import com.mindcoders.phial.R;
import com.mindcoders.phial.ShareContext;
import com.mindcoders.phial.internal.PhialErrorPlugins;
import com.mindcoders.phial.internal.share.attachment.AttachmentManager;
import com.mindcoders.phial.internal.util.AnimatorFactory;
import com.mindcoders.phial.internal.util.Precondition;
import com.mindcoders.phial.internal.util.UiUtils;

import java.io.File;
import java.util.List;

/**
 * Created by rost on 10/22/17.
 */
public class ShareView extends FrameLayout implements PageView {
    private final GridView contentGV;
    private final EditText messageTV;
    private final ShareManager shareManager;
    private final AttachmentManager attachmentManager;
    private final OverlayCallback overlayCallback;
    private final View sharePickerView;
    private final View progressBar;
    private ViewShareContext shareContext;

    @VisibleForTesting
    public ShareView(@NonNull Context context, OverlayCallback overlayCallback) {
        this(context, null, null, overlayCallback);
        Precondition.calledFromTools(this);
    }

    public ShareView(@NonNull Context context, ShareManager shareManager, AttachmentManager attachmentManager,
                     OverlayCallback overlayCallback) {
        super(context);
        this.shareManager = shareManager;
        this.attachmentManager = attachmentManager;
        this.overlayCallback = overlayCallback;

        LayoutInflater.from(context).inflate(R.layout.view_share, this, true);
        LayoutInflater.from(context).inflate(R.layout.progressbar_horizontal, this, true);

        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(GONE);

        sharePickerView = findViewById(R.id.share_picker);
        contentGV = findViewById(R.id.content);
        messageTV = findViewById(R.id.message);

        if (!isInEditMode()) {
            setupAdapter(context);
        }
    }

    private void setupAdapter(@NonNull Context context) {
        final List<ShareItem> shareables = this.shareManager.getShareables();
        final ShareAdapter adapter = new ShareAdapter(context, shareables);
        contentGV.setAdapter(adapter);
        contentGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ShareItem item = adapter.getItem(position);
                shareItem(item, view);
            }
        });
    }


    @Override
    public boolean onBackPressed() {
        if (shareContext != null) {
            shareContext.showSharePicker();
            return true;
        }

        return false;
    }

    private void shareItem(ShareItem shareItem, View view) {
        try {
            final File attachment = attachmentManager.createAttachment();
            final String message = messageTV.getText().toString();
            final AnimatorFactory factory = AnimatorFactory.createFactory(view);

            shareContext = new ViewShareContext(factory);
            shareManager.share(shareItem, shareContext, attachment, message);
        } catch (Exception e) {
            PhialErrorPlugins.onError(e);
            Toast.makeText(getContext(), R.string.share_error_attachment, Toast.LENGTH_SHORT).show();
        }
    }

    private void close() {
        overlayCallback.finish();
        shareContext = null;
    }

    private class ViewShareContext implements ShareContext {
        private View presentView;
        private final AnimatorFactory animatorFactory;

        private ViewShareContext(AnimatorFactory animatorFactory) {
            this.animatorFactory = animatorFactory;
        }

        @Override
        public Context getAndroidContext() {
            return getContext();
        }

        @Override
        public void onSuccess() {
            close();
        }

        @Override
        public void onFailed(String message) {
            showSharePicker();
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            showSharePicker();
        }

        @Override
        public void presentView(final View view) {
            boolean isFirstSubView = presentView == null; //only share view
            if (!isFirstSubView) {
                removeSubViews();
            }

            final View focusedChild = ShareView.this.getFocusedChild();
            UiUtils.hideKeyBoard(focusedChild);

            final int insertPosition = getChildCount() - 1; // we would like to keep progressBar at the end.
            addView(view, insertPosition, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            presentView = view;

            if (isFirstSubView) {
                animateViewAfterMeasured(view);
            }
        }

        private void animateViewAfterMeasured(final View view) {
            final ViewTreeObserver vto = view.getViewTreeObserver();
            if (!vto.isAlive()) {
                sharePickerView.setVisibility(GONE);
                return;
            }

            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (view.getHeight() > 0 || view.getWidth() > 0) {
                        if (vto.isAlive()) {
                            vto.removeGlobalOnLayoutListener(this);
                            animateAppear();
                        }
                    }
                }
            });
        }

        private void animateAppear() {
            final Animator appearAnimator = animatorFactory.createAppearAnimator(presentView);
            appearAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    sharePickerView.setVisibility(GONE);
                }
            });
            appearAnimator.start();
        }


        @Override
        public void setProgressBarVisibility(boolean isVisible) {
            progressBar.setVisibility(isVisible ? VISIBLE : GONE);
        }

        @Override
        public View inflate(@LayoutRes int layoutId) {
            final LayoutInflater inflater = LayoutInflater.from(getContext());
            return inflater.inflate(layoutId, ShareView.this, false);
        }

        private void showSharePicker() {
            final boolean hasSubView = presentView != null;
            if (hasSubView) {
                final View focusedChild = ShareView.this.getFocusedChild();
                UiUtils.hideKeyBoard(focusedChild);

                final Animator appearAnimator = animatorFactory.createDisappearAnimator(presentView);
                appearAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        removeSubViews();
                        sharePickerView.setVisibility(VISIBLE);
                    }
                });
                appearAnimator.start();
            }
            shareContext = null;
        }

        private void removeSubViews() {
            // fist view is sharePicker and last is progress bar that we don't wan't to remove
            removeViews(1, getChildCount() - 2);
        }
    }
}
