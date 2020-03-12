package org.smartregister.giz.widget;

import android.content.Context;

import com.vijay.jsonwizard.customviews.TreeViewDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class GizTreeViewDialog extends TreeViewDialog {

    public GizTreeViewDialog(Context context, JSONArray structure, ArrayList<String> defaultValue, ArrayList<String> value) throws JSONException {
        super(context, structure, defaultValue, value);
    }

    @Override
    public void init(JSONArray nodes, ArrayList<String> defaultValue, ArrayList<String> value) throws JSONException {
        setShouldExpandAllNodes(true);
        super.init(nodes, defaultValue, value);
    }
}
