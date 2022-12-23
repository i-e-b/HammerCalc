package e.s.hammercalc;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Stack;

/**
 * View for doing decimal calculations (normal calculator)
 * This is a basic RPN setup
 */
@SuppressLint("ViewConstructor")
public class DecimalView extends View {
    private final Paint mPaint = new Paint();
    private final MainActivity parent;

    private final Stack<BigDecimal> stack = new Stack<>();
    private final MathContext displayRounding, scaleRounding;

    private float touchY = 0.0f;
    private float scrollY = 0.0f;
    private float offset = 0.0f;

    public DecimalView(MainActivity context) {
        super(context);
        parent = context;

        displayRounding = new MathContext(15, RoundingMode.FLOOR);
        scaleRounding = new MathContext(1, RoundingMode.FLOOR);

        stack.push(new BigDecimal("123E-10"));
        stack.push(new BigDecimal("123.0"));
        stack.push(new BigDecimal("123333.7"));
        stack.push(new BigDecimal("1233333333333333456777777.7"));
        stack.push(new BigDecimal("1233333333333333456777777.7888888888E1"));
        stack.push(new BigDecimal("123333333333333345677700000000000000000777.7888888888E1"));
        stack.push(new BigDecimal("0.555E20"));
    }


    @Override
    public void onDrawForeground(final Canvas canvas) {
        int btnGrey = 200;
        int textGrey = 0;
        if (os.isDarkMode(this)) {
            canvas.drawARGB(255, 0, 0, 0);
            btnGrey = 55;
            textGrey = 200;
        } else {
            canvas.drawARGB(255, 255, 255, 255);
        }

        mPaint.setAntiAlias(true);
        mPaint.setTypeface(Typeface.MONOSPACE);
        mPaint.setTextSize(50);
        os.setGrey(mPaint, textGrey);
        float y = 50 + offset + scrollY;

        int j = stack.size();
        for (BigDecimal value : stack) {
            y = os.textLine(canvas, mPaint, 10.0f, y, loc(j) + normalString(value));
            j--;
        }
        canvas.drawText("x> ", 10.0f, y, mPaint);
    }

    private String loc(int j) {
        switch (j) {
            case  1: return "y> ";
            case  2: return "z> ";
            case  3: return "w> ";
            default: return "   ";
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = event.getY();
                scrollY = moveY - touchY;
                break;
            case MotionEvent.ACTION_UP:
                offset += scrollY;
                scrollY = 0.0f;
                break;
            default:
        }

        invalidate(); // draw a frame
        return true; // event handled
    }

    /** Display a decimal, with SI prefix where known */
    private String normalString(BigDecimal d){
        // TODO: fix the to string mess, or get a better number library.
        String scale = d.round(scaleRounding).toEngineeringString();
        String display = d.round(displayRounding).toEngineeringString();

        // https://www.nist.gov/pml/owm/metric-si-prefixes
        if (scale.endsWith("E+30")) return display + " Q quetta";
        if (scale.endsWith("E+27")) return display + " R ronna";
        if (scale.endsWith("E+24")) return display + " Y yotta";
        if (scale.endsWith("E+21")) return display + " Z zetta";
        if (scale.endsWith("E+18")) return display + " E exa";
        if (scale.endsWith("E+15")) return display + " P peta";
        if (scale.endsWith("E+12")) return display + " T tera";
        if (scale.endsWith("E+9"))  return display + " G giga";
        if (scale.endsWith("E+6"))  return display + " M mega";
        if (scale.endsWith("E+3"))  return display + " k kilo";

        if (scale.endsWith("E-3"))  return display + " m milli";
        if (scale.endsWith("E-6"))  return display + " µ micro";
        if (scale.endsWith("E-9"))  return display + " n nano";
        if (scale.endsWith("E-12")) return display + " p pico";
        if (scale.endsWith("E-15")) return display + " f femto";
        if (scale.endsWith("E-18")) return display + " a atto";
        if (scale.endsWith("E-21")) return display + " z zepto";
        if (scale.endsWith("E-24")) return display + " y yocto";
        if (scale.endsWith("E-27")) return display + " r ronto";
        if (scale.endsWith("E-30")) return display + " q quecto";

        return display;
    }
}
