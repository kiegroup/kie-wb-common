package org.kie.workbench.common.forms.integration.tests.valueprocessing;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestUtils {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public static Date createDate(String date) {
        Date d1 = null;
        try {
            d1 = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d1;
    }
}
