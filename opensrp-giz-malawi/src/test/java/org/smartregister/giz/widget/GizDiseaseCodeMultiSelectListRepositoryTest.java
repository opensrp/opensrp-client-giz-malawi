package org.smartregister.giz.widget;

import com.vijay.jsonwizard.domain.MultiSelectItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.smartregister.giz.BaseUnitTest;

import java.util.List;

public class GizDiseaseCodeMultiSelectListRepositoryTest extends BaseUnitTest {

    @Test
    public void testProcessOptionsJsonArray() throws JSONException {
        GizDiseaseCodeMultiSelectListRepository repository =  new GizDiseaseCodeMultiSelectListRepository();
        JSONArray array = new JSONArray("[{\"openmrsentity\":\"\",\"property\":{\"presumed-id\":\"\",\"code\":\"code_1\",\"confirmed-id\":null},\"openmrsentityid\":\"\",\"text\":\"Polio* (1)\",\"openmrsentityparent\":\"\",\"key\":\"code_1\"},{\"openmrsentity\":\"\",\"property\":{\"presumed-id\":\"\",\"code\":\"code_1b\",\"confirmed-id\":null},\"openmrsentityid\":\"\",\"text\":\"Acute flaccid paralysis* (1b)\",\"openmrsentityparent\":\"\",\"key\":\"code_1b\"},{\"openmrsentity\":\"\",\"property\":{\"presumed-id\":\"\",\"code\":\"code_2\",\"confirmed-id\":null},\"openmrsentityid\":\"\",\"text\":\"Diphtheria* (2)\",\"openmrsentityparent\":\"\",\"key\":\"code_2\"},{\"openmrsentity\":\"\",\"property\":{\"presumed-id\":\"\",\"code\":\"code_3\",\"confirmed-id\":null},\"openmrsentityid\":\"\",\"text\":\"Measles* (3)\",\"openmrsentityparent\":\"\",\"key\":\"code_3\"}]");
        List<MultiSelectItem> items = repository.processOptionsJsonArray(array);
        Assert.assertEquals(4, items.size());
    }

}
