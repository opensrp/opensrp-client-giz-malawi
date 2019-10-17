package org.smartregister.giz.util;

import android.content.ContentValues;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.domain.db.Client;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.giz.application.GizMalawiApplication;

@PrepareForTest({Utils.class, GizMalawiApplication.class, CoreLibrary.class})
@RunWith(PowerMockRunner.class)
public class GizUtilsTest {

    @Mock
    GizMalawiApplication gizMalawiApplication;

    @Mock
    Context context;

    @Captor
    ArgumentCaptor argumentCaptorUpdateChildTable;

    @Captor
    ArgumentCaptor argumentCaptorUpdateChildFtsTable;

    @Mock
    CoreLibrary coreLibrary;

    @Mock
    AllCommonsRepository allCommonsRepository;

    @Mock
    ChildMetadata childMetadata;

    @Test
    public void testUpdateChildDeath() {
        PowerMockito.mockStatic(GizMalawiApplication.class);
        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(gizMalawiApplication.context()).thenReturn(context);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(context.allCommonsRepositoryobjects(null)).thenReturn(allCommonsRepository);
        PowerMockito.when(GizMalawiApplication.getInstance()).thenReturn(gizMalawiApplication);
        childMetadata.childRegister = PowerMockito.mock(ChildMetadata.ChildRegister.class);
        PowerMockito.when(Utils.metadata()).thenReturn(childMetadata);
        Client client = new Client("123");
        client.setDeathdate(new DateTime());
        EventClient eventClient = new EventClient(new Event(), client);

        GizUtils.updateChildDeath(eventClient);

        Mockito.verify(allCommonsRepository).update((String) argumentCaptorUpdateChildTable.capture(), (ContentValues) argumentCaptorUpdateChildTable.capture(), (String) argumentCaptorUpdateChildTable.capture());
        Mockito.verify(allCommonsRepository).updateSearch((String) argumentCaptorUpdateChildFtsTable.capture());
        Assert.assertNull(argumentCaptorUpdateChildTable.getAllValues().get(0));
        Assert.assertNull(argumentCaptorUpdateChildTable.getAllValues().get(1).toString());
        Assert.assertEquals(client.getBaseEntityId(), argumentCaptorUpdateChildTable.getAllValues().get(2));
        Assert.assertEquals(client.getBaseEntityId(), argumentCaptorUpdateChildFtsTable.getValue());
    }
}