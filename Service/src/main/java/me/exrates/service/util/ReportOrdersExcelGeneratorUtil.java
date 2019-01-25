package me.exrates.service.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.exrates.model.Currency;
import me.exrates.model.dto.InOutReportDto;
import me.exrates.model.dto.OrderReportInfoDto;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@NoArgsConstructor(access = AccessLevel.NONE)
public class ReportOrdersExcelGeneratorUtil {

    private static final String SHEET1_NAME = "Ордера";

    public static byte[] generate(List<OrderReportInfoDto> ordersForReport) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();

        CellStyle header1Style = getHeader1Style(workbook);
        CellStyle body1Style = getBode1Style(workbook);

        XSSFSheet sheet = workbook.createSheet(SHEET1_NAME);

        XSSFRow row;
        XSSFCell cell;

        row = sheet.createRow(0);

        //header
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("ID");
        cell.setCellStyle(header1Style);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Создан");
        cell.setCellStyle(header1Style);

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Валютная пара");
        cell.setCellStyle(header1Style);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Тип ордера");
        cell.setCellStyle(header1Style);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Курc");
        cell.setCellStyle(header1Style);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("Количество");
        cell.setCellStyle(header1Style);

        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("Создал");
        cell.setCellStyle(header1Style);

        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue("Роль пользователя-создателя");
        cell.setCellStyle(header1Style);

        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("Принял");
        cell.setCellStyle(header1Style);

        cell = row.createCell(9, CellType.STRING);
        cell.setCellValue("Роль пользователя-принял");
        cell.setCellStyle(header1Style);

        cell = row.createCell(10, CellType.STRING);
        cell.setCellValue("Статус");
        cell.setCellStyle(header1Style);

        sheet.autoSizeColumn(0, true);
        sheet.setColumnWidth(0, sheet.getColumnWidth(0) + 256);
        sheet.autoSizeColumn(1, true);
        sheet.setColumnWidth(1, sheet.getColumnWidth(1) + 256);
        sheet.autoSizeColumn(2, true);
        sheet.setColumnWidth(2, sheet.getColumnWidth(2) + 256);
        sheet.autoSizeColumn(3, true);
        sheet.setColumnWidth(3, sheet.getColumnWidth(3) + 256);
        sheet.autoSizeColumn(4, true);
        sheet.setColumnWidth(4, sheet.getColumnWidth(4) + 256);
        sheet.autoSizeColumn(5, true);
        sheet.setColumnWidth(5, sheet.getColumnWidth(5) + 256);
        sheet.autoSizeColumn(6, true);
        sheet.setColumnWidth(6, sheet.getColumnWidth(6) + 256);
        sheet.autoSizeColumn(7, true);
        sheet.setColumnWidth(7, sheet.getColumnWidth(7) + 256);
        sheet.autoSizeColumn(8, true);
        sheet.setColumnWidth(8, sheet.getColumnWidth(8) + 256);
        sheet.autoSizeColumn(9, true);
        sheet.setColumnWidth(9, sheet.getColumnWidth(9) + 256);
        sheet.autoSizeColumn(10, true);
        sheet.setColumnWidth(10, sheet.getColumnWidth(10) + 256);

        //body
        int i=0;
        for (OrderReportInfoDto order: ordersForReport) {

            row = sheet.createRow(i + 1);

            cell = row.createCell(0, CellType.NUMERIC);
            cell.setCellValue(order.getId());
            cell.setCellStyle(body1Style);

            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(order.getDateCreation().toString());
            cell.setCellStyle(body1Style);

            cell = row.createCell(2, CellType.NUMERIC);
            cell.setCellValue(order.getCurrencyPairName());
            cell.setCellStyle(body1Style);

            cell = row.createCell(3, CellType.NUMERIC);
            cell.setCellValue(order.getOrderTypeName());
            cell.setCellStyle(body1Style);

            cell = row.createCell(4, CellType.NUMERIC);
            cell.setCellValue(order.getExrate());
            cell.setCellStyle(body1Style);

            cell = row.createCell(5, CellType.NUMERIC);
            cell.setCellValue(order.getAmountBase());
            cell.setCellStyle(body1Style);

            cell = row.createCell(6, CellType.NUMERIC);
            cell.setCellValue(order.getOrderCreatorEmail());
            cell.setCellStyle(body1Style);

            cell = row.createCell(7, CellType.NUMERIC);
            cell.setCellValue(order.getCreatorRole());
            cell.setCellStyle(body1Style);

            cell = row.createCell(8, CellType.NUMERIC);
            cell.setCellValue(order.getOrderAcceptorEmail());
            cell.setCellStyle(body1Style);

            cell = row.createCell(9, CellType.NUMERIC);
            cell.setCellValue(order.getAcceptorRole());
            cell.setCellStyle(body1Style);

            cell = row.createCell(10, CellType.NUMERIC);
            cell.setCellValue(order.getOrderStatusName());
            cell.setCellStyle(body1Style);

            i++;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
            bos.close();
        } catch (IOException ex) {
            throw new Exception("Problem with convert workbook to byte array", ex);
        }
        return bos.toByteArray();
    }

    private static CellStyle getHeader1Style(XSSFWorkbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeight(10);
        font.setBold(true);
        headerStyle.setFont(font);

        headerStyle.setWrapText(true);

        return headerStyle;
    }

    private static CellStyle getBode1Style(XSSFWorkbook workbook) {
        CellStyle bodyStyle = workbook.createCellStyle();
        bodyStyle.setBorderBottom(BorderStyle.THIN);
        bodyStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        bodyStyle.setBorderLeft(BorderStyle.THIN);
        bodyStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        bodyStyle.setBorderRight(BorderStyle.THIN);
        bodyStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        bodyStyle.setBorderTop(BorderStyle.THIN);
        bodyStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        bodyStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeight(10);
        bodyStyle.setFont(font);

        return bodyStyle;
    }
}
