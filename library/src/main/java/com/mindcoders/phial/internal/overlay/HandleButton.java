package com.mindcoders.phial.internal.overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;

class HandleButton extends View {

    private final Paint backgroundPaint = new Paint();

    private Bitmap iconBitmap;

    public HandleButton(Context context, int backgroundColorResource, int iconResource) {
        super(context);

        backgroundPaint.setColor(ResourcesCompat.getColor(context.getResources(), backgroundColorResource, context.getTheme()));
        backgroundPaint.setShadowLayer(12, 0, 0, Color.LTGRAY);

        iconBitmap = BitmapFactory.decodeResource(context.getResources(), iconResource);
        setLayerType(LAYER_TYPE_SOFTWARE, backgroundPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();

        float cx = w / 2;
        float cy = h / 2;

        float radius = (w / 2) - 10;

        canvas.drawCircle(cx, cy, radius, backgroundPaint);

        float left = cx - (iconBitmap.getWidth() / 2);
        float top = cy - (iconBitmap.getHeight() / 2);

        canvas.drawBitmap(iconBitmap, left, top, null);
    }

}
