package org.jxls.transform.poi;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.ConditionalFormatting;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.Test;
import org.jxls.command.TestWorkbook;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;

public class ConditionalFormattingTest {
    private static final double EPSILON = 0.001;

    @Test
    public void shouldCopyConditionalFormatInEachCommandLoop() throws IOException {
        // Prepare
        InputStream is = getClass().getResourceAsStream("cond_format_template.xlsx");
        String outputFileName = "target/cond_format_output.xlsx";
        File outputFile = new File(outputFileName);
        FileOutputStream out = new FileOutputStream(outputFile);
        Context context = new Context();
        List<Integer> list = Arrays.asList(2, 1, 4, 3, 5);
        context.putVar("numbers", list);
        context.putVar("val1", 0);
        context.putVar("val2", 7);
        
        // Test
        JxlsHelper.getInstance().processTemplate(is, out, context);

        // Verify
        try (TestWorkbook xls = new TestWorkbook(outputFile)) {
            xls.selectSheet(0);
            for (int i = 0; i < list.size(); i++) {
                double val = xls.getCellValueAsDouble(i + 2, 2);
                assertEquals(list.get(i).doubleValue(), val, EPSILON);
            }

            SheetConditionalFormatting sheetConditionalFormatting = xls.getSheetConditionalFormatting();
            int conditionalFormattingCount = 0;
            for (int i = 0; i < sheetConditionalFormatting.getNumConditionalFormattings(); i++) {
                ConditionalFormatting conditionalFormatting = sheetConditionalFormatting.getConditionalFormattingAt(i);
                CellRangeAddress[] ranges = conditionalFormatting.getFormattingRanges();
                if (ranges.length > 0) {
                    conditionalFormattingCount++;
                }
            }
            assertEquals(list.size() + 2, conditionalFormattingCount);
        }
    }
}
