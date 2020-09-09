package org.smartregister.giz.repository;

import org.robolectric.annotation.Config;
import org.smartregister.giz.BaseRobolectricTest;
import org.smartregister.giz.shadow.ShadowSQLiteDatabase;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 06-03-2020.
 */
@Config(shadows = {ShadowSQLiteDatabase.class})
public class GizMalawiRepositoryTest extends BaseRobolectricTest {

//    @Rule
//    public MockitoRule mockitoRule = MockitoJUnit.rule();
//
//    private GizMalawiRepository gizMalawiRepository;
//
//    @Mock
//    private SQLiteDatabase sqLiteDatabase;

//    @Before
//    public void setUp() throws Exception {
//        gizMalawiRepository = Mockito.spy((GizMalawiRepository) GizMalawiApplication.getInstance().getRepository());
//
//        Mockito.doReturn(sqLiteDatabase).when(gizMalawiRepository).getReadableDatabase();
//        Mockito.doReturn(sqLiteDatabase).when(gizMalawiRepository).getReadableDatabase(Mockito.anyString());
//        Mockito.doReturn(sqLiteDatabase).when(gizMalawiRepository).getWritableDatabase();
//        Mockito.doReturn(sqLiteDatabase).when(gizMalawiRepository).getWritableDatabase(Mockito.anyString());
//
//        ReflectionHelpers.setField(GizMalawiApplication.getInstance(), "repository", gizMalawiRepository);
//    }

//    // TODO: FIX THIS
//    @Test
//    public void onCreateShouldCreate41tables() {
//        Mockito.doNothing().when(gizMalawiRepository).onUpgrade(Mockito.any(SQLiteDatabase.class), Mockito.anyInt(), Mockito.anyInt());
//        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);
//        gizMalawiRepository.onCreate(database);
//
//        // TODO: Investigate this counter
//        Mockito.verify(database, Mockito.times(50)).execSQL(Mockito.contains("CREATE TABLE"));
//    }
}