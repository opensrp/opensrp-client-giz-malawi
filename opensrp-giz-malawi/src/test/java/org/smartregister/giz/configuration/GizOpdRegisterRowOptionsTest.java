package org.smartregister.giz.configuration;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.widget.Button;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.giz.R;
import org.smartregister.opd.dao.VisitDao;
import org.smartregister.opd.holders.OpdRegisterViewHolder;
import org.smartregister.opd.utils.OpdDbConstants;

import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-12-04
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(VisitDao.class)
public class GizOpdRegisterRowOptionsTest {

    private GizOpdRegisterRowOptions gizOpdRegisterRowOptions;

    @Mock
    private VisitDao visitDao;

    @Before
    public void setUp() throws Exception {
        gizOpdRegisterRowOptions = new GizOpdRegisterRowOptions();
    }

    @Test
    public void populateClientRowShouldSetDueButtonTextToSeenToday() {
        PowerMockito.mockStatic(VisitDao.class);
        PowerMockito.when(visitDao.getSeenToday(Mockito.any())).thenReturn(true);

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

        Assert.assertEquals(R.string.seen_today, (int) intCaptor.getValue());
    }


    @Test
    public void populateClientRowShouldSetDueButtonTextToEmpty() {
        PowerMockito.mockStatic(VisitDao.class);
        PowerMockito.when(visitDao.getSeenToday(Mockito.any())).thenReturn(false);

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

        Assert.assertEquals(R.string.empty_text, (int) intCaptor.getValue());
    }
}