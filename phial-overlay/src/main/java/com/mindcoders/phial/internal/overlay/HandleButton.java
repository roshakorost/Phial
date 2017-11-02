package com.mindcoders.phial.internal.overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.annotation.ColorInt;
import android.support.annotation.VisibleForTesting;
import android.view.View;

import com.mindcoders.phial.internal.util.Precondition;

class HandleButton extends View {
    private static final int SHADOW_SIZE = 14;
    private static final int SHADOW_COLOR = Color.argb(60, 0, 0, 0);
    private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int bgColor;
    private int fgColor;

    private Bitmap iconBitmap;

    @VisibleForTesting
    HandleButton(Context context) {
        super(context);
        Precondition.calledFromTools(this);
    }

    HandleButton(Context context, int iconResource, @ColorInt int bgColor, @ColorInt int fgColor) {
        super(context);
        this.bgColor = bgColor;
        this.fgColor = fgColor;

        bgPaint.setShadowLayer(SHADOW_SIZE, 0, SHADOW_SIZE / 2f, SHADOW_COLOR);

        iconBitmap = BitmapFactory.decodeResource(context.getResources(), iconResource);
        setLayerType(LAYER_TYPE_SOFTWARE, bgPaint);

        applyColors();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();

        float cx = w / 2;
        float cy = h / 2;

        float radius = (w / 2f) - SHADOW_SIZE * 1.2f;

        canvas.drawCircle(cx, cy, radius, bgPaint);

        float left = cx - (iconBitmap.getWidth() / 2);
        float top = cy - (iconBitmap.getHeight() / 2);

        canvas.drawBitmap(iconBitmap, left, top, fgPaint);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        applyColors();
    }

    void applyColors() {
        bgPaint.setColor(isSelected() ? fgColor : bgColor);
        fgPaint.setColorFilter(new PorterDuffColorFilter(isSelected() ? bgColor : fgColor, PorterDuff.Mode.SRC_ATOP));
        invalidate();
    }
}
