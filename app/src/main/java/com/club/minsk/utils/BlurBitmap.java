package com.club.minsk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class BlurBitmap {

    static BlurBitmap instance = new BlurBitmap();

    public static BlurBitmap getInstance() {
        return instance;
    }

    public Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);
        return mutableBitmap;
    }

    Drawable blur(Context context, Drawable drawable, int widthPixels, int heightPixels, int radius) {
        Bitmap bitmap = convertToBitmap(drawable, 200, 200);
        bitmap = blurRenderScript(context, bitmap, radius);
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static Bitmap blurRenderScript(Context context, Bitmap smallBitmap, int radius) {
        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            RenderScript renderScript = RenderScript.create(context);
            Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
            Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

            ScriptIntrinsicBlur blur = null;

            blur = ScriptIntrinsicBlur.create(renderScript,
                    Element.U8_4(renderScript));

            blur.setInput(blurInput);
            blur.setRadius(radius); // radius must be 0 < r <= 25
            blur.forEach(blurOutput);

            blurOutput.copyTo(bitmap);
            renderScript.destroy();
        }
        return bitmap;

    }

    private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }
}
