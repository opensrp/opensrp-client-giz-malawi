package org.smartregister.giz.repository;

import android.content.ContentValues;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.giz.BaseRobolectricTest;
import org.smartregister.repository.Hia2ReportRepository;

import java.util.Date;

public class GizHia2ReportRepositoryTest extends BaseRobolectricTest {

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    private GizHia2ReportRepository gizHia2ReportRepository;


    @Before
    public void setUp() {
        gizHia2ReportRepository = new GizHia2ReportRepository();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddReportIfReportDoesNotExistShouldCallInsert() throws JSONException {
        GizHia2ReportRepository gizHia2ReportRepositorySpy = Mockito.spy(gizHia2ReportRepository);
        JSONObject reportJsonObject = new JSONObject();
        reportJsonObject.put(GizHia2ReportRepository.ReportColumn.grouping.name(), "child");
        reportJsonObject.put(GizHia2ReportRepository.ReportColumn.reportType.name(), new JSONObject().toString());
        reportJsonObject.put(GizHia2ReportRepository.ReportColumn.updatedAt.name(), new Date());
        reportJsonObject.put(GizHia2ReportRepository.ReportColumn.reportType.name(), "Child Test Report");
        reportJsonObject.put(GizHia2ReportRepository.ReportColumn.formSubmissionId.name(), "2323-232334-34");

        Mockito.doReturn(sqLiteDatabase).when(gizHia2ReportRepositorySpy).getWritableDatabase();
        Mockito.doReturn(false).when(gizHia2ReportRepositorySpy)
                .checkIfExistsByFormSubmissionId(
                        Mockito.eq(Hia2ReportRepository.Table.hia2_report), Mockito.anyString());

        gizHia2ReportRepositorySpy.addReport(reportJsonObject);

        Mockito.verify(sqLiteDatabase, Mockito.times(1))
                .insert(Mockito.eq(Hia2ReportRepository.Table.hia2_report.name()), Mockito.eq(null), Mockito.any(ContentValues.class));

    }

    @Test
    public void testAddReportIfReportExistsShouldCallUpdate() throws JSONException {
        GizHia2ReportRepository gizHia2ReportRepositorySpy = Mockito.spy(gizHia2ReportRepository);
        JSONObject reportJsonObject = new JSONObject();
        reportJsonObject.put(GizHia2ReportRepository.ReportColumn.grouping.name(), "child");
        reportJsonObject.put(GizHia2ReportRepository.ReportColumn.reportType.name(), new JSONObject().toString());
        reportJsonObject.put(GizHia2ReportRepository.ReportColumn.updatedAt.name(), new Date());
        reportJsonObject.put(GizHia2ReportRepository.ReportColumn.reportType.name(), "Child Test Report");
        reportJsonObject.put(GizHia2ReportRepository.ReportColumn.formSubmissionId.name(), "2323-232334-34");

        Mockito.doReturn(sqLiteDatabase).when(gizHia2ReportRepositorySpy).getWritableDatabase();
        Mockito.doReturn(true).when(gizHia2ReportRepositorySpy)
                .checkIfExistsByFormSubmissionId(
                        Mockito.eq(Hia2ReportRepository.Table.hia2_report),
                        Mockito.eq(reportJsonObject.getString(GizHia2ReportRepository.ReportColumn.formSubmissionId.name())));

        gizHia2ReportRepositorySpy.addReport(reportJsonObject);

        Mockito.verify(gizHia2ReportRepositorySpy, Mockito.times(1))
                .checkIfExistsByFormSubmissionId(Mockito.eq(Hia2ReportRepository.Table.hia2_report),
                        Mockito.eq(reportJsonObject.getString(GizHia2ReportRepository.ReportColumn.formSubmissionId.name())));

        Mockito.verify(sqLiteDatabase, Mockito.never())
                .insert(Mockito.eq(Hia2ReportRepository.Table.hia2_report.name()),
                        Mockito.eq(null),
                        Mockito.any(ContentValues.class));

        Mockito.verify(sqLiteDatabase, Mockito.times(1))
                .update(Mockito.eq(Hia2ReportRepository.Table.hia2_report.name()),
                        Mockito.any(ContentValues.class),
                        Mockito.eq(GizHia2ReportRepository.ReportColumn.formSubmissionId.name() + "=?"),
                        Mockito.eq(new String[]{reportJsonObject.getString(GizHia2ReportRepository.ReportColumn.formSubmissionId.name())}));

    }
}