package com.mindcoders.phial.internal.overlay;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.util.support.ContextCompat;

public class PhialButton extends View {
    private static final float SHADOW_DY = 0.75f;
    private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int shadowSize;
    private int suggestedSize;
    private ColorStateList backgroundColor;
    private ColorStateList iconColor;
    private ColorStateList shadowColor;

    private Drawable icon;

    public PhialButton(Context context) {
        this(context, null);
    }

    public PhialButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.phial_button_style);
    }

    public PhialButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.PhialButton, defStyleAttr,
                R.style.Widget_Phial_Button);

        try {
            backgroundColor = array.getColorStateList(R.styleable.PhialButton_phial_color_background);
            if (backgroundColor == null) {
                throw new IllegalStateException(array.toString());
            }
            iconColor = array.getColorStateList(R.styleable.PhialButton_phial_color_icon);
            shadowColor = array.getColorStateList(R.styleable.PhialButton_phial_color_shadow);

            icon = array.getDrawable(R.styleable.PhialButton_phial_icon);
            if (icon != null) {
                icon = icon.mutate();
            }

            int defaultButtonSize = context.getResources().getDimensionPixelSize(R.dimen.phial_button_size);
            suggestedSize = array.getDimensionPixelSize(R.styleable.PhialButton_phial_size, defaultButtonSize);

            int defaultShadowSize = context.getResources().getDimensionPixelSize(R.dimen.phial_button_shadow_size);
            shadowSize = array.getDimensionPixelSize(R.styleable.PhialButton_phial_size_shadow, defaultShadowSize);
        } finally {
            array.recycle();
        }

        if (!isInEditMode()) {
            setLayerType(LAYER_TYPE_SOFTWARE, bgPaint);
        }
        setupColors();
    }

    public void setIcon(Drawable drawable) {
        if (drawable != null) {
            icon = drawable.mutate();
        }
        refreshDrawableState();
        invalidate();
    }

    public void setIcon(Bitmap bitmap) {
        if (bitmap != null) {
            setIcon(new BitmapDrawable(getResources(), bitmap));
        } else {
            setIcon((Drawable) null);
        }
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(@DrawableRes int iconId) {
        setIcon(ContextCompat.getDrawable(getContext(), iconId));
    }

    public void setSuggestedSize(int size) {
        suggestedSize = size;
        requestLayout();
    }

    public void setShadowSize(int size) {
        shadowSize = size;
        refreshDrawableState();
        requestLayout();
    }

    @ColorInt
    public int getBackgroundColor() {
        return getColor(backgroundColor, getDrawableState());
    }

    @ColorInt
    public int getIconColor() {
        return getColor(iconColor, getDrawableState());
    }

    public int getShadowColor() {
        return getColor(shadowColor, getDrawableState());
    }

    public int getShadowSize() {
        return shadowSize;
    }

    public int getSuggestedSize() {
        return suggestedSize;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        setupColors();
        invalidate();
    }

    private void setupColors() {
        bgPaint.setColor(getBackgroundColor());
        if (!isInEditMode()) {
            bgPaint.setShadowLayer(shadowSize, 0f, getShadowDy(), getShadowColor());
        }

        if (icon != null) {
            icon.setColorFilter(getIconColor(), PorterDuff.Mode.SRC_ATOP);
        }
    }

    private float getShadowDy() {
        return shadowSize * SHADOW_DY;
    }

    @Override

    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();

        int cx = w / 2;
        int cy = (int) (h / 2f - getShadowDy());

        int radius = (int) (Math.min(w, h) / 2f - shadowSize);

        canvas.drawCircle(cx, cy, radius, bgPaint);

        if (icon != null) {
            final int targetW = getIconSize(2 * radius, icon.getIntrinsicWidth());
            final int targetH = getIconSize(2 * radius, icon.getIntrinsicHeight());
            icon.setBounds(cx - targetW / 2, cy - targetH / 2, cx + targetW / 2, cy + targetH / 2);
            icon.draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                getSize(suggestedSize + shadowSize, widthMeasureSpec),
                getSize(suggestedSize + shadowSize, heightMeasureSpec)
        );
    }

    private static int getSize(int suggestedSize, int measureSpec) {
        final int specMode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                return suggestedSize;
            case MeasureSpec.AT_MOST:
                return Math.min(suggestedSize, specSize);
            case MeasureSpec.EXACTLY:
                return specSize;
            default:
                throw new IllegalArgumentException("bad specMode! mode=" + specMode + ";spec = " + measureSpec);
        }
    }

    private int getIconSize(int maxSize, int intrinsicSize) {
        if (intrinsicSize == -1 || intrinsicSize > maxSize) {
            return maxSize;
        }
        return intrinsicSize;
    }

    @ColorInt
    private static int getColor(ColorStateList colorStateList, int[] drawableState) {
        return colorStateList.getColorForState(drawableState, colorStateList.getDefaultColor());
    }
}
