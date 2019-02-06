package me.exrates.service.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.exrates.model.dto.CurrencyPairTurnoverReportDto;
import me.exrates.model.dto.CurrencyReportInfoDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@NoArgsConstructor(access = AccessLevel.NONE)
public class ReportStatsByCoinExcelGeneratorUtil {

    private static final String SHEET1_NAME = "Баланс пользователей по монете с датой последнего пополнения";

    public static byte[] generate(List<CurrencyReportInfoDto> statsByCoin) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();

        CellStyle header1Style = getHeader1Style(workbook);
        CellStyle body1Style = getBode1Style(workbook);
        CellStyle footer1Style = getFooter1Style(workbook);

        XSSFSheet sheet1 = workbook.createSheet(SHEET1_NAME);

        XSSFRow row;
        XSSFCell cell;

        //header
        int bound = statsByCoin.size();

        row = sheet1.createRow(0);

        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Итого:");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(1, CellType.NUMERIC);
        cell.setCellFormula("SUM(B" + 3 + ":B" + (bound + 2) + ")");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(2, CellType.NUMERIC);
        cell.setCellFormula("SUM(C" + 3 + ":C" + (bound + 2) + ")");
        cell.setCellStyle(footer1Style);

        row = sheet1.createRow(1);

        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Email");
        cell.setCellStyle(header1Style);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Активный баланс");
        cell.setCellStyle(header1Style);

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Резервный баланс");
        cell.setCellStyle(header1Style);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Регистрация пользователя");
        cell.setCellStyle(header1Style);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Последнее пополнение");
        cell.setCellStyle(header1Style);

        sheet1.autoSizeColumn(0, false);
        sheet1.setColumnWidth(0, sheet1.getColumnWidth(0) + 256);
        sheet1.autoSizeColumn(1, false);
        sheet1.setColumnWidth(1, sheet1.getColumnWidth(1) + 256);
        sheet1.autoSizeColumn(2, false);
        sheet1.setColumnWidth(2, sheet1.getColumnWidth(2) + 256);
        sheet1.autoSizeColumn(3, false);
        sheet1.setColumnWidth(3, sheet1.getColumnWidth(3) + 256);
        sheet1.autoSizeColumn(4, false);
        sheet1.setColumnWidth(4, sheet1.getColumnWidth(4) + 256);

        //body
        int i = 0;
        for (CurrencyReportInfoDto stat : statsByCoin) {
            String email = stat.getEmail();
            String activeBalance = stat.getActiveBalance();
            String reserveBalance = stat.getReservedBalance();

            String dateUserRegistration = stat.getDateUserRegistration() != null ? stat.getDateUserRegistration().toString() : "";
            String dateLastRefillByUser = stat.getDateLastRefillByUser() != null ? stat.getDateUserRegistration().toString() : "";

            row = sheet1.createRow(i + 2);

            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(email);
            cell.setCellStyle(body1Style);

            cell = row.createCell(1, CellType.NUMERIC);
            cell.setCellValue(Double.valueOf(activeBalance));
            cell.setCellStyle(body1Style);

            cell = row.createCell(2, CellType.NUMERIC);
            cell.setCellValue(Double.valueOf(reserveBalance));
            cell.setCellStyle(body1Style);

            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(dateUserRegistration);
            cell.setCellStyle(body1Style);

            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue(dateLastRefillByUser);
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

    private static CellStyle getFooter1Style(XSSFWorkbook workbook) {
        CellStyle footerStyle = workbook.createCellStyle();
        footerStyle.setBorderBottom(BorderStyle.THIN);
        footerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        footerStyle.setBorderLeft(BorderStyle.THIN);
        footerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        footerStyle.setBorderRight(BorderStyle.THIN);
        footerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        footerStyle.setBorderTop(BorderStyle.THIN);
        footerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        footerStyle.setFillForegroundColor(IndexedColors.CORAL.getIndex());
        footerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        footerStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeight(10);
        font.setBold(true);
        footerStyle.setFont(font);

        return footerStyle;
    }
}
