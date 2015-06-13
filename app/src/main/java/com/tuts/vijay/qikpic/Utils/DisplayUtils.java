package com.tuts.vijay.qikpic.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by vijay on 6/13/15.
 */
public class DisplayUtils {

    public static float fromDpToPx(Context c, int dp) {
        Resources r = c.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
