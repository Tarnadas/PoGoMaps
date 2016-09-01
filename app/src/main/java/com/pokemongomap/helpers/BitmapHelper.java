package com.pokemongomap.helpers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.HashMap;
import java.util.Map;

public abstract class BitmapHelper {

    private static final float TEXT_SIZE = 18.f;

    private static Map<Integer, Bitmap> mBitmaps;

    public static void init() {
        mBitmaps = new HashMap<>();
        mBitmaps.put(-1, textAsBitmap(">15:00"));
        for (int i = 0; i < 15*60; i++) {
            int min = i / 60;
            int sec = i % 60;
            String mins = "", secs = "";
            if (min < 10) {
                mins = "0" + min;
            } else {
                mins = "" + min;
            }
            if (sec < 10) {
                secs = "0" + sec;
            } else {
                secs = "" + sec;
            }
            String text = mins + ":" + secs;
            mBitmaps.put(i, textAsBitmap(text));
        }
    }

    public static Bitmap getBitmap(int i) {
        return mBitmaps.get(i);
    }

    private static Bitmap textAsBitmap(String text) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(TEXT_SIZE);
        paint.setARGB(255, 0, 0, 0);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

}
