package org.smartregister.giz.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.giz.util.GizUtils;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DrishtiApplication.class})
public class GizEventRepositoryTest {

    private GizEventRepository repository;
    private static final String BASE_ENTITY_ID = "base_entity_id";
    private static final String EVENT_TYPE = "event_type";
    private static final String FORM_SUBMISSION_ID = "form_submission_id";

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private GizUtils gizUtils;

    @Mock
    private Cursor cursor;

    @Mock
    private Repository baseRepository;

    @Mock
    private DrishtiApplication drishtiApplication;

    @Before
    public void setUp() {
        repository = new GizEventRepository();
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(DrishtiApplication.class);
        when(DrishtiApplication.getInstance()).thenReturn(drishtiApplication);
        when(drishtiApplication.getRepository()).thenReturn(baseRepository);
        when(baseRepository.getReadableDatabase()).thenReturn(sqLiteDatabase);
    }

    @Test
    public void testHasEvent(){
        when(sqLiteDatabase.query(
                "event",
                new String[]{"baseEntityId"},
                "baseEntityId = ? and eventType = ? ",
                new String[]{BASE_ENTITY_ID, EVENT_TYPE},
                null,
                null,
                null,
                "1"
        )).thenReturn(cursor);

        when(cursor.getCount()).thenReturn(1);

        boolean hasEvent = repository.hasEvent(BASE_ENTITY_ID, EVENT_TYPE);

        assertTrue(hasEvent);
    }



    @Test
    public void testAddFormSubmissionIds() {
        when(baseRepository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(sqLiteDatabase.rawQuery("query", null)).thenReturn(cursor);
        when(cursor.getCount()).thenReturn(1);
        when(cursor.getColumnIndex("formSubmissionId")).thenReturn(0);
        when(cursor.moveToFirst()).thenReturn(true);
        when(cursor.getString(0)).thenReturn("formSubmissionId");

        List<String> formSubmissionIds = new ArrayList<>();
        repository.addFormSubmissionIds(formSubmissionIds, "query");

        assertFalse(formSubmissionIds.isEmpty());
    }
}
