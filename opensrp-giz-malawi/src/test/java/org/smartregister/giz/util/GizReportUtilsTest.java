package org.smartregister.giz.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.giz.BaseRobolectricTest;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.domain.ReportHia2Indicator;
import org.smartregister.repository.Hia2ReportRepository;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 05-03-2020.
 */
public class GizReportUtilsTest extends BaseRobolectricTest {

    @Test
    public void createReportAndSaveReport() throws JSONException {
        Date month = new Date();
        String reportType = "Immunization Monthly Report";
        ArrayList<ReportHia2Indicator> reportHia2Indicators = new ArrayList<>();
        reportHia2Indicators.add(new ReportHia2Indicator());
        reportHia2Indicators.add(new ReportHia2Indicator());

        Hia2ReportRepository hia2ReportRepository = Mockito.spy(GizMalawiApplication.getInstance().hia2ReportRepository());
        ReflectionHelpers.setField(GizMalawiApplication.getInstance(), "hia2ReportRepository", hia2ReportRepository);

        Mockito.doNothing().when(hia2ReportRepository).addReport(Mockito.any(JSONObject.class));

        GizReportUtils.createReportAndSaveReport(reportHia2Indicators, month, reportType);
        ArgumentCaptor<JSONObject> jsonObjectArgumentCaptor = ArgumentCaptor.forClass(JSONObject.class);
        Mockito.verify(hia2ReportRepository).addReport(jsonObjectArgumentCaptor.capture());

        JSONObject reportJson = jsonObjectArgumentCaptor.getValue();
        assertEquals(reportType, reportJson.getString("reportType"));
        assertEquals(2, reportJson.getJSONArray("hia2Indicators").length());
    }

    @Test
    public void getStringIdentifierShouldReturnCleanString() {
        Assert.assertEquals("giz_report_for_vaccinationimmunization", GizReportUtils.getStringIdentifier("GIZ REPORT FOR VACCINATION/IMMUNIZATION"));
    }
}