package org.smartregister.giz.widget;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.giz.BaseRobolectricTest;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 05-03-2020.
 */

public class GizTreeViewDialogTest extends BaseRobolectricTest {

    @Ignore
    @Test
    public void initShouldCallExpandAllNodes() throws JSONException {
        GizTreeViewDialog gizTreeViewDialog = Mockito.spy(new GizTreeViewDialog(RuntimeEnvironment.application, new JSONArray(), new ArrayList<String>(), new ArrayList<>()));
        //Mockito.verify(gizTreeViewDialog).(Mockito.any);
    }
}