package com.mindcoders.phial.internal.overlay2;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mindcoders.phial.OverlayCallback;
import com.mindcoders.phial.Page;
import com.mindcoders.phial.PageView;
import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.util.AnimatorFactory;
import com.mindcoders.phial.internal.util.ArgbEvaluator;
import com.mindcoders.phial.internal.util.ObjectUtil;
import com.mindcoders.phial.internal.util.SimpleAnimatorListener;
import com.mindcoders.phial.internal.util.support.ResourcesCompat;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by rost on 11/21/17.
 */

public class ExpandedView extends FrameLayout {
    private static final int ANIMATION_DURATION = 400;
    private static final int BACKGROUND_COLOR_RES = R.color.phial_palette_gray_darkest_transparent;
    public static final int BACGROUND_TRANSPARENT_COLOR_RES = R.color.phial_palette_gray_darkest_full_transparent;
    private View settingsButton;

    interface ExpandedViewCallback extends OverlayCallback {
        void onPageSelected(Page page);
    }

    private final LinearLayout iconsHolder;
    private final View arrow;
    private final TextView title;
    private final FrameLayout contentContainer;

    private Animator animator = ValueAnimator.ofInt();
    private View content;
    private ExpandedViewCallback callback;
    private LayoutHelper.Disposable disposable = LayoutHelper.Disposable.EMPTY;

    public ExpandedView(@NonNull Context context) {
        this(context, (AttributeSet) null);
    }

    public ExpandedView(@NonNull Context context, ExpandedViewCallback callback) {
        this(context);
        setCallback(callback);
    }

    public ExpandedView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandedView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_expanded, this, true);
        iconsHolder = findViewById(R.id.tab_icons_holder);
        arrow = findViewById(R.id.arrow);
        title = findViewById(R.id.title);
        contentContainer = findViewById(R.id.content);

        settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(v -> callback.finish());

        setBackgroundResource(BACKGROUND_COLOR_RES);
        final int padding = getResources().getDimensionPixelSize(R.dimen.phial_content_padding);
        setPadding(padding, padding, padding, padding);
    }

    /**
     * Shows pages as tabIcons. Highlights selectedTabIcon and shows content for selected Page.
     * <p>
     * Important: It has optimization: if selected page is same we don't recreate content.
     * Important: OverlayPresenter expects the optimization.
     *
     * @param pages    list of pages to display
     * @param selected selected page
     */
    public void displayPages(List<Page> pages, Page selected, boolean animated) {
        setVisibility(VISIBLE);
        setupIcons(pages, selected, animated);
        setupPage(selected);
        title.setText(selected.getTitle());
    }

    public void setCallback(ExpandedViewCallback callback) {
        this.callback = callback;
    }

    public void destroyContent(Runnable runnable) {
        animator.cancel();
        animator = AnimatorFactory
                .createFactory(settingsButton)
                .createDisappearAnimator(this)
                .setDuration(ANIMATION_DURATION);
        animator.addListener(SimpleAnimatorListener.createEndListener(() -> {
            setVisibility(INVISIBLE);
            content = null;
            contentContainer.removeAllViews();
            disposable.dispose();
            runnable.run();
        }));
        animator.start();
    }

    private void setupIcons(List<Page> pages, Page selected, boolean animated) {
        // - 1 in order to not remove settings button
        iconsHolder.removeViews(0, iconsHolder.getChildCount() - 1);
        PhialButton selectedButton = null;
        for (int i = 0; i < pages.size(); i++) {
            final Page page = pages.get(i);

            final PhialButton button = new PhialButton(getContext());
            button.setIcon(page.getIconResourceId());
            final boolean isSelected = ObjectUtil.equals(selected, page);
            button.setSelected(isSelected);
            button.setOnClickListener(v -> onTabClicked(button, page));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            iconsHolder.addView(button, 0, lp);

            if (isSelected) {
                selectedButton = button;
            }
        }

        if (selectedButton != null) {
            final PhialButton finalSelectedButton = selectedButton;
            disposable.dispose();
            disposable = LayoutHelper.onLayout(() -> {
                setupArrowPosition(finalSelectedButton);
                if (animated) {
                    animateAppear();
                }
            }, finalSelectedButton, arrow);
        }
    }

    private void setupPage(Page selectedPage) {
        if (content == null || !ObjectUtil.equals(selectedPage.getId(), content.getTag())) {
            contentContainer.removeAllViews();

            final View view = selectedPage.getPageViewFactory().createPageView(getContext(), callback);
            content = view;
            content.setTag(selectedPage.getId());

            LayoutParams lp = new LayoutParams(MATCH_PARENT, MATCH_PARENT, Gravity.CENTER);
            contentContainer.addView(view, lp);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (!isBackClicked(event)) {
            return super.dispatchKeyEvent(event);
        }

        if (content instanceof PageView) {
            final PageView pageView = (PageView) content;
            if (pageView.onBackPressed()) {
                return true;
            }
        }

        callback.finish();
        return true;
    }

    private void setupArrowPosition(PhialButton target) {
        int cX = (target.getLeft() + target.getRight()) / 2;
        arrow.setX(cX - arrow.getWidth() / 2);
    }

    private void onTabClicked(PhialButton button, Page page) {
        if (button.isSelected()) {
            callback.finish();
        } else {
            callback.onPageSelected(page);
        }
    }

    private boolean isBackClicked(KeyEvent event) {
        return event.getAction() == KeyEvent.ACTION_UP
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK;
    }

    private void animateAppear() {
        setBackgroundResource(BACGROUND_TRANSPARENT_COLOR_RES);

        final AnimatorSet animator = new AnimatorSet();
        animator.playTogether(
                createRevelAnimator(),
                createAppearBackgroundAnimator()
        );

        animator.start();
    }

    private Animator createRevelAnimator() {
        return AnimatorFactory
                .createFactory(settingsButton)
                .createAppearAnimator(this)
                .setDuration(ANIMATION_DURATION);
    }

    private Animator createAppearBackgroundAnimator() {
        int startColor = ResourcesCompat.getColor(getResources(),
                BACGROUND_TRANSPARENT_COLOR_RES,
                getContext().getTheme()
        );
        int finColor = ResourcesCompat.getColor(getResources(),
                BACKGROUND_COLOR_RES,
                getContext().getTheme()
        );
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(startColor, finColor);
        anim.setEvaluator(ArgbEvaluator.getInstance());
        anim.setStartDelay(ANIMATION_DURATION / 2);
        anim.setDuration(ANIMATION_DURATION / 2);
        anim.addUpdateListener(animation -> {
            int color = (int) animation.getAnimatedValue();
            setBackgroundColor(color);
        });
        return anim;
    }
}
