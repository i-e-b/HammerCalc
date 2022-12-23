package e.s.hammercalc;

import static android.content.res.Configuration.UI_MODE_NIGHT_YES;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * Single points for Android system calls.
 */
public class os {
    public static boolean isDarkMode(View v){
        int uiMode = v.getResources().getConfiguration().uiMode;
        return ((uiMode & UI_MODE_NIGHT_YES) > 0);
    }

    public static void setGrey(Paint mPaint, int c2) {
        mPaint.setARGB(255, c2, c2, c2);
    }

    public static void measureText(Paint mPaint, String txt, Rect rect){
        mPaint.getTextBounds(txt, 0,txt.length(), rect);
    }

    /** Draw text at x,y then return next y */
    public static float textLine(Canvas canvas, Paint mPaint, float x, float y, String txt){
        Rect r = new Rect();
        measureText(mPaint, "Xy", r);
        drawText(canvas,mPaint,x,y, txt);
        return y + (r.height()*1.2f);
    }

    public static void measureChr(Paint mPaint, String txt, Rect rect) {
        mPaint.getTextBounds(txt, 0,2, rect);
    }

    private static SharedPreferences getPrefs(Context c){
        return c.getSharedPreferences("scores", Context.MODE_PRIVATE);
    }

    public static String getPref(Context c, String key){
        return getPrefs(c).getString(key, "");
    }

    public static void setPref(Context c, String key, String value){
        SharedPreferences.Editor e = getPrefs(c).edit();
        e.putString(key, value);
        e.apply();
    }

    public static void drawText(Canvas canvas, Paint mPaint, float x, float y, String text) {
        canvas.drawText(text, x, y, mPaint);
    }

    public static void drawRect(Canvas canvas, Paint p, float l, float t, float r, float b) {
        canvas.drawRect(l,t,r,b, p);
    }
}
