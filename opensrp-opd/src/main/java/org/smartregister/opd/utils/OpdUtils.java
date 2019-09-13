package org.smartregister.opd.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import org.smartregister.opd.R;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdUtils {

    public static String getTranslatedDate(String str_date, android.content.Context context) {
        return str_date
                .replace("d", context.getString(R.string.abbrv_days))
                .replace("w", context.getString(R.string.abbrv_weeks))
                .replace("m", context.getString(R.string.abbrv_months))
                .replace("y", context.getString(R.string.abbrv_years));
    }

    public static float convertDpToPixel(float dp, @NonNull Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
