package com.mindcoders.phial.internal.overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;

import com.mindcoders.phial.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static android.view.View.MeasureSpec.UNSPECIFIED;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


@RunWith(RobolectricTestRunner.class)
public class PhialButtonTest {

    private Context context;

    @Before
    public void setUp() throws Exception {
        context = RuntimeEnvironment.application;
    }

    @Test
    public void init_fromAttributes() {
        final int bgColor = Color.RED;
        final int iconColor = Color.YELLOW;
        final int shadowColor = Color.BLUE;

        final AttributeSet attrs = Robolectric.buildAttributeSet()
                .addAttribute(getButtonStyleable(R.styleable.PhialButton_phial_color_background),
                        hexColor(bgColor))

                .addAttribute(getButtonStyleable(R.styleable.PhialButton_phial_color_icon),
                        hexColor(iconColor))

                .addAttribute(getButtonStyleable(R.styleable.PhialButton_phial_color_shadow),
                        hexColor(shadowColor))

                .addAttribute(getButtonStyleable(R.styleable.PhialButton_phial_icon),
                        "@drawable/ic_handle")

                .addAttribute(getButtonStyleable(R.styleable.PhialButton_phial_size),
                        "20px")

                .addAttribute(getButtonStyleable(R.styleable.PhialButton_phial_size_shadow),
                        "5px")

                .build();

        final PhialButton button = new PhialButton(context, attrs);

        assertEquals(bgColor, button.getBackgroundColor());
        assertEquals(iconColor, button.getIconColor());
        assertEquals(shadowColor, button.getShadowColor());
        assertEquals(context.getDrawable(R.drawable.ic_handle), button.getIcon());
        assertEquals(20, button.getSuggestedSize());
        assertEquals(5, button.getShadowSize());
    }

    @Test
    public void setIcon_null_returns_null_drawable() {
        final PhialButton button = new PhialButton(context);
        button.setIcon((Bitmap) null);
        assertNull(button.getIcon());
    }

    @Test
    public void setIcon_bitmap_returns_bitmapDrawable() {
        final PhialButton button = new PhialButton(context);
        final Bitmap bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        button.setIcon(bitmap);
        assertEquals(new BitmapDrawable(context.getResources(), bitmap), button.getIcon());
    }

    @Test
    public void measure_honors_measure_specs() {
        final PhialButton button = new PhialButton(context);
        button.setSuggestedSize(10);
        button.setShadowSize(2);

        button.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        assertEquals(12, button.getMeasuredWidth());
        assertEquals(12, button.getMeasuredHeight());

        final int specs = makeMeasureSpec(15, MeasureSpec.AT_MOST);
        button.measure(specs, specs);
        assertEquals(12, button.getMeasuredWidth());
        assertEquals(12, button.getMeasuredHeight());

        final int specsReduced = makeMeasureSpec(10, MeasureSpec.AT_MOST);
        button.measure(specsReduced, specsReduced);
        assertEquals(10, button.getMeasuredWidth());
        assertEquals(10, button.getMeasuredHeight());

        final int specsExactly = makeMeasureSpec(15, MeasureSpec.EXACTLY);
        button.measure(specsExactly, specsExactly);
        assertEquals(15, button.getMeasuredWidth());
        assertEquals(15, button.getMeasuredHeight());
    }

    //test is stupid it only can check rather on draw will crash or not.
    @Test
    public void check_draw_crash() {
        final PhialButton button = new PhialButton(context);
        button.setSuggestedSize(10);
        button.setShadowSize(1);
        button.measure(UNSPECIFIED, UNSPECIFIED);
        button.setIcon((Bitmap) null);

        final Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        button.onDraw(canvas);

        button.setIcon(Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_4444));
        button.onDraw(canvas);
    }

    @Test
    public void getIconSize_returns_correct_size() {
        assertEquals(10, PhialButton.getIconSize(10, -1));
        assertEquals(10, PhialButton.getIconSize(10, 20));
    }

    private static int getButtonStyleable(int styleable) {
        return R.styleable.PhialButton[styleable];
    }

    private String hexColor(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }
}