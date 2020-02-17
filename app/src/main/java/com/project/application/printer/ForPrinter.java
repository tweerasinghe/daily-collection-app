package com.project.application.printer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.BitSet;

public class ForPrinter {
    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setColor(textColor);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent();
        int width = (int) (paint.measureText(text) + 0.5f);
        int height = (int) (baseline + paint.descent() + 0.5f);

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawColor(Color.WHITE);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }



    public static BitSet convertArgbToGrayscale(Bitmap bmpOriginal, int width, int height) {
        int pixel;
        int k = 0;
        int B = 0, G = 0, R = 0;
        BitSet dots = new BitSet();
        Bitmap bmp = null;
        try {
            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width; y++) {

                    pixel = bmpOriginal.getPixel(y, x);

                    R = Color.red(pixel);
                    G = Color.green(pixel);
                    B = Color.blue(pixel);

                    R = G = B = (int) (0.299 * R + 0.587 * G + 0.114 * B);

                    if (R < 55) {
                        dots.set(k);
                    }
                    k++;
                }
            }
            return dots;
        } catch (Exception e) {

        }
        return null;
    }
}
