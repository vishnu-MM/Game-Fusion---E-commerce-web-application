package com.example.gamefusion.Configuration.UtilityClasses;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import com.example.gamefusion.Entity.OrderMain;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.ss.usermodel.CellStyle;
import jakarta.servlet.ServletOutputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import java.io.IOException;
import java.util.List;

public class SalesReportExcelExporter {
    private XSSFSheet sheet;
    private final XSSFWorkbook workbook;
    private final List<OrderMain> orderMainList;

    public SalesReportExcelExporter(List<OrderMain> orderMainList) {
        workbook = new XSSFWorkbook();
        this.orderMainList = orderMainList;
    }


    private void writeHeaderLine() {
        sheet = workbook.createSheet("Sales Report");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "Order ID", style);
        createCell(row, 1, "Full Name", style);
        createCell(row, 2, "E-mail", style);
        createCell(row, 3, "Total", style);
        createCell(row, 4, "Date", style);
        createCell(row, 5, "Status", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue(String.valueOf(value));
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (OrderMain orderMain : orderMainList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            String name = (orderMain.getUser().getLastName() == null) ?
                           orderMain.getUser().getFirstName() :
                           orderMain.getUser().getFirstName()+" "+orderMain.getUser().getLastName();

            createCell(row, columnCount++, orderMain.getOrderId(), style);
            createCell(row, columnCount++, name, style);
            createCell(row, columnCount++, orderMain.getUser().getUsername(), style);
            createCell(row, columnCount++, orderMain.getAmount(), style);
            createCell(row, columnCount++, orderMain.getDate(), style);
            createCell(row, columnCount++, orderMain.getStatus(), style);

        }
    }

    public void export(HttpServletResponse response) {
        writeHeaderLine();
        writeDataLines();
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
