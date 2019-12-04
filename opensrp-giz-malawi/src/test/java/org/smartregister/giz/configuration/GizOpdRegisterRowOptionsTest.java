package org.smartregister.giz.configuration;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.giz.R;
import org.smartregister.opd.holders.OpdRegisterViewHolder;
import org.smartregister.opd.utils.OpdDbConstants;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-12-04
 */
@RunWith(MockitoJUnitRunner.class)
public class GizOpdRegisterRowOptionsTest {

    private GizOpdRegisterRowOptions gizOpdRegisterRowOptions;

    @Before
    public void setUp() throws Exception {
        gizOpdRegisterRowOptions = new GizOpdRegisterRowOptions();
    }

    @Test
    public void populateClientRowShouldSetDueButtonTextToDiagnoseAndTreat() {
        Button dueBtn = Mockito.mock(Button.class);
        ArgumentCaptor<Integer> intCaptor = ArgumentCaptor.forClass(Integer.class);

        HashMap<String, String> details = new HashMap<>();
        details.put(OpdDbConstants.Column.OpdDetails.PENDING_DIAGNOSE_AND_TREAT, "1");

        CommonPersonObjectClient client = new CommonPersonObjectClient("caseId", details, "John Doe");
        client.setColumnmaps(details);

        OpdRegisterViewHolder opdRegisterViewHolder = Mockito.mock(OpdRegisterViewHolder.class);
        opdRegisterViewHolder.dueButton = dueBtn;
        Mockito.doNothing().when(dueBtn).setText(intCaptor.capture());
        Context context = Mockito.mock(Context.class);
        Mockito.doReturn(context).when(dueBtn).getContext();
        Mockito.doReturn(Mockito.mock(Resources.class)).when(context).getResources();

        gizOpdRegisterRowOptions.populateClientRow(Mockito.mock(Cursor.class), client, client, opdRegisterViewHolder);

        assertEquals(R.string.diagnose_and_treat, (int) intCaptor.getValue());
    }


    @Test
    public void populateClientRowShouldSetDueButtonTextToCheckIn() {
        Button dueBtn = Mockito.mock(Button.class);
        ArgumentCaptor<Integer> intCaptor = ArgumentCaptor.forClass(Integer.class);

        HashMap<String, String> details = new HashMap<>();
        details.put(OpdDbConstants.Column.OpdDetails.PENDING_DIAGNOSE_AND_TREAT, "0");

        CommonPersonObjectClient client = new CommonPersonObjectClient("caseId", details, "John Doe");
        client.setColumnmaps(details);

        OpdRegisterViewHolder opdRegisterViewHolder = Mockito.mock(OpdRegisterViewHolder.class);
        opdRegisterViewHolder.dueButton = dueBtn;
        Mockito.doNothing().when(dueBtn).setText(intCaptor.capture());
        Context context = Mockito.mock(Context.class);
        Mockito.doReturn(context).when(dueBtn).getContext();
        Mockito.doReturn(Mockito.mock(Resources.class)).when(context).getResources();

        gizOpdRegisterRowOptions.populateClientRow(Mockito.mock(Cursor.class), client, client, opdRegisterViewHolder);

        assertEquals(R.string.check_in, (int) intCaptor.getValue());
    }
}