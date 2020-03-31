package org.smartregister.giz.processor;

import android.content.Context;

import org.smartregister.anc.library.sync.MiniClientProcessorForJava;
import org.smartregister.maternity.processor.MaternityMiniClientProcessorForJava;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 31-03-2020.
 */
public class GizMaternityProcessorForJava extends MaternityMiniClientProcessorForJava implements MiniClientProcessorForJava {

    public GizMaternityProcessorForJava(Context context) {
        super(context);
    }
}
