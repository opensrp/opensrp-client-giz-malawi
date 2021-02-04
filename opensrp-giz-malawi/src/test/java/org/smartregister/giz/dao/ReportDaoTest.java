package org.smartregister.giz.dao;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.giz.domain.EligibleChild;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ReportDaoTest extends ReportDao {

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase database;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        setRepository(repository);
    }

    @Test
    public void testFetchLiveEligibleChildrenReport() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "base_entity_id", "unique_id", "first_name", "last_name", "middle_name",
                "family_name", "dob", "gender", "location_id"
        });
        matrixCursor.addRow(new Object[]{
                "85e5dd54-ba27-46b1-b5c2-2bab06fd77e2", "12345", "meso", "bosh", "",
                "drip", "2021-02-20 22:22:34", "Male", "402ecf03-af72-4c93-b099-e1ce327d815b"
        });

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());


        List<EligibleChild> children = ReportDao.fetchLiveEligibleChildrenReport(new ArrayList<>(), new Date());

        Assert.assertEquals(children.size(), 0);
    }


    @Test
    public void testFetchLiveEligibleChildrenReportReturnsNewArrayList() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"base_entity_id"});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        List<EligibleChild> children = ReportDao.fetchLiveEligibleChildrenReport(null, new Date());
        Assert.assertEquals(children.size(), 0);
    }

    @Test
    public void testFetchAllVaccines() throws ParseException {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                VaccineRepository.CREATED_AT, VaccineRepository.ID_COLUMN, VaccineRepository.BASE_ENTITY_ID,
                VaccineRepository.PROGRAM_CLIENT_ID, VaccineRepository.NAME, VaccineRepository.CALCULATION,
                VaccineRepository.DATE, VaccineRepository.ANMID, VaccineRepository.LOCATION_ID,
                VaccineRepository.SYNC_STATUS, VaccineRepository.HIA2_STATUS, VaccineRepository.UPDATED_AT_COLUMN,
                VaccineRepository.EVENT_ID, VaccineRepository.FORMSUBMISSION_ID, VaccineRepository.OUT_OF_AREA,
                VaccineRepository.TEAM, VaccineRepository.TEAM_ID, VaccineRepository.CHILD_LOCATION_ID
        });
        matrixCursor.addRow(new Object[]{
                "2021-02-20 22:22:34", 1, "839e53i354-ba27-46b1-b5c2-2bab06fd77e2",
                "chw", "bcg", 1,
                "1567112400000", "meso", "402ecf03-af72-4c93-b099-e1ce327d815b",
                "Synced", "hia_2", "1584692554019",
                "23147a8f-d301-43a1-876e-93f30088e2d7", "d3e94182-a7c8-457a-a5a5-40354bfb37e4", 0,
                "Team A", "team_1", "4d30ecac-536b-4a90-b712-8613d3768717"
        });
        matrixCursor.addRow(new Object[]{
                "2021-02-20 22:22:34", 1, "839e53i354-ba27-46b1-b5c2-2bab06fd77e2",
                "chw", "opv_0", 0,
                "1567112400000", "meso", "402ecf03-af72-4c93-b099-e1ce327d815b",
                "Synced", "hia_2", "1584692554019",
                "23147a8f-d301-43a1-876e-93f30088e2d7", "d3e94182-a7c8-457a-a5a5-40354bfb37e4", 0,
                "Team A", "team_1", "4d30ecac-536b-4a90-b712-8613d3768717"
        });
        matrixCursor.addRow(new Object[]{
                "2021-02-20 22:22:34", 1, "89839e53i354-483d-48c2-baec-929b862f3ac1",
                "chw", "opv_0", 0,
                "1567112400000", "meso", "402ecf03-af72-4c93-b099-e1ce327d815b",
                "Synced", "hia_2", "1584692554019",
                "23147a8f-d301-43a1-876e-93f30088e2d7", "d3e94182-a7c8-457a-a5a5-40354bfb37e4", 0,
                "Team A", "team_1", "4d30ecac-536b-4a90-b712-8613d3768717"
        });
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        // test values
        Map<String, List<Vaccine>> providerList = ReportDao.fetchAllVaccines();
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());

        // test results
        Assert.assertEquals(providerList.size(), 2);
        List<Vaccine> vaccines = providerList.get("839e53i354-ba27-46b1-b5c2-2bab06fd77e2");
        Assert.assertEquals(vaccines.size(), 2);

        Vaccine vaccine = vaccines.get(0);
        Assert.assertEquals(vaccine.getCreatedAt(), EventClientRepository.dateFormat.parse("2021-02-20 22:22:34"));
    }

}
