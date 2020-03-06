package org.smartregister.giz.repository;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.giz.BaseRobolectricTest;
import org.smartregister.giz.TestGizMalawiApplication;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.shadow.ShadowAssetHandler;
import org.smartregister.giz.shadow.ShadowBaseJob;
import org.smartregister.giz.shadow.ShadowSQLiteDatabase;

import static org.junit.Assert.*;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 06-03-2020.
 */

@Ignore
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {27}, shadows = {ShadowBaseJob.class, ShadowAssetHandler.class, ShadowSQLiteDatabase.class}, application = TestGizMalawiApplication.class)
public class GizMalawiRepositoryTest {

    private GizMalawiRepository gizMalawiRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;


    @Before
    public void setUp() throws Exception {
        Context context = Context.getInstance();
        context.updateApplicationContext(RuntimeEnvironment.application);
        //gizMalawiRepository = new GizMalawiRepository(RuntimeEnvironment.systemContext, context);
        gizMalawiRepository = Mockito.spy((GizMalawiRepository) GizMalawiApplication.getInstance().getRepository());
        ReflectionHelpers.setField(GizMalawiApplication.getInstance(), "repository", gizMalawiRepository);

        Mockito.doReturn(sqLiteDatabase).when(gizMalawiRepository).getReadableDatabase();
        Mockito.doReturn(sqLiteDatabase).when(gizMalawiRepository).getReadableDatabase(Mockito.anyString());
        Mockito.doReturn(sqLiteDatabase).when(gizMalawiRepository).getWritableDatabase();
        Mockito.doReturn(sqLiteDatabase).when(gizMalawiRepository).getWritableDatabase(Mockito.anyString());
    }

    // TODO: FIX THIS
    @Test
    public void onCreateShouldCreate41tables() {
        Mockito.doNothing().when(gizMalawiRepository).onUpgrade(Mockito.any(SQLiteDatabase.class), Mockito.anyInt(), Mockito.anyInt());
        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);
        gizMalawiRepository.onCreate(database);

        // TODO: Investigate this
        Mockito.verify(database, Mockito.times(41)).execSQL(Mockito.contains("CREATE TABLE"));
    }
}