package org.smartregister.giz.interactor;

import net.sqlcipher.Cursor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.smartregister.Context;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.util.GizConstants;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GizMalawiApplication.class)
public class NavigationInteractorTest {

    @Mock
    private GizMalawiApplication gizMalawiApplication;

    @Mock
    private Context context;

    @Mock
    private CommonRepository commonRepository;

    private NavigationInteractor navigationInteractor;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        navigationInteractor = new NavigationInteractor();
    }

    @Test
    public void getCountForChildRegisterCountIfResultIsOne() throws Exception {
        PowerMockito.mockStatic(GizMalawiApplication.class);
        PowerMockito.when(gizMalawiApplication.getContext()).thenReturn(context);
        PowerMockito.when(context.commonrepository(GizConstants.TABLE_NAME.CHILD)).thenReturn(commonRepository);
        Cursor cursor = Mockito.mock(Cursor.class);
        PowerMockito.when(cursor.moveToFirst()).thenReturn(true);
        PowerMockito.when(cursor.getInt(0)).thenReturn(1);
        PowerMockito.when(commonRepository.rawCustomQueryForAdapter("select count(*) from ec_child  where date_removed is null AND  ((( julianday('now') - julianday(dob))/365.25) <5);")).thenReturn(cursor);
        PowerMockito.when(GizMalawiApplication.getInstance()).thenReturn(gizMalawiApplication);
        int result  = Whitebox.invokeMethod(navigationInteractor, "getCount", GizConstants.TABLE_NAME.CHILD);
        Assert.assertEquals(1, result);
    }

}