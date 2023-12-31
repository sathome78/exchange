package me.exrates.service.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.exrates.model.dto.UserSummaryDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@NoArgsConstructor(access = AccessLevel.NONE)
public class ReportNineExcelGeneratorUtil {

    private static final DateTimeFormatter FORMATTER_FOR_REPORT = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH-mm");

    private static final String SHEET1_NAME = "Sheet1 - Выгрузить данные по юзерам";

    public static byte[] generate(List<UserSummaryDto> summaryData) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();

        CellStyle header1Style = getHeader1Style(workbook);
        CellStyle body1Style = getBode1Style(workbook);

        XSSFSheet sheet = workbook.createSheet(SHEET1_NAME);

        XSSFRow row;
        XSSFCell cell;

        row = sheet.createRow(0);

        //header
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Никнейм");
        cell.setCellStyle(header1Style);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Электронная почта");
        cell.setCellStyle(header1Style);

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Дата регистрации");
        cell.setCellStyle(header1Style);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("IP регистрации");
        cell.setCellStyle(header1Style);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Последний логин");
        cell.setCellStyle(header1Style);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("Последний IP");
        cell.setCellStyle(header1Style);

        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("Название монеты");
        cell.setCellStyle(header1Style);

        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue("Активный баланс");
        cell.setCellStyle(header1Style);

        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("Резервный баланс");
        cell.setCellStyle(header1Style);

        cell = row.createCell(9, CellType.STRING);
        cell.setCellValue("Дата последнего ордера");
        cell.setCellStyle(header1Style);

        cell = row.createCell(10, CellType.STRING);
        cell.setCellValue("Сумма ввода");
        cell.setCellStyle(header1Style);

        cell = row.createCell(11, CellType.STRING);
        cell.setCellValue("Дата последнего ввода");
        cell.setCellStyle(header1Style);

        cell = row.createCell(12, CellType.STRING);
        cell.setCellValue("Сумма вывода");
        cell.setCellStyle(header1Style);

        cell = row.createCell(13, CellType.STRING);
        cell.setCellValue("Дата последнего вывода");
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
        sheet.autoSizeColumn(11, true);
        sheet.setColumnWidth(11, sheet.getColumnWidth(11) + 256);
        sheet.autoSizeColumn(12, true);
        sheet.setColumnWidth(12, sheet.getColumnWidth(12) + 256);
        sheet.autoSizeColumn(13, true);
        sheet.setColumnWidth(13, sheet.getColumnWidth(13) + 256);

        //body
        int i = 0;
        for (UserSummaryDto sd : summaryData) {
            final String nickname = nonNull(sd.getNickname()) ? sd.getNickname() : StringUtils.EMPTY;
            final String email = nonNull(sd.getEmail()) ? sd.getEmail() : StringUtils.EMPTY;
            final String createdAt = nonNull(sd.getCreatedAt()) ? sd.getCreatedAt().format(FORMATTER_FOR_REPORT) : StringUtils.EMPTY;
            final String registrationIp = nonNull(sd.getRegistrationIp()) ? sd.getRegistrationIp() : StringUtils.EMPTY;
            final String lastEntryDate = nonNull(sd.getLastEntryDate()) ? sd.getLastEntryDate().format(FORMATTER_FOR_REPORT) : StringUtils.EMPTY;
            final String lastIp = nonNull(sd.getLastIp()) ? sd.getLastIp() : StringUtils.EMPTY;
            final String currencyName = sd.getCurrencyName();
            final double activeBalance = nonNull(sd.getActiveBalance()) ? sd.getActiveBalance().doubleValue() : 0;
            final double reservedBalance = nonNull(sd.getReservedBalance()) ? sd.getReservedBalance().doubleValue() : 0;
            final String lastOrderDate = nonNull(sd.getLastOrderDate()) ? sd.getLastOrderDate().format(FORMATTER_FOR_REPORT) : StringUtils.EMPTY;
            final double inputSummary = nonNull(sd.getInputSummary()) ? sd.getInputSummary().doubleValue() : 0;
            final String lastInputDate = nonNull(sd.getLastInputDate()) ? sd.getLastInputDate().format(FORMATTER_FOR_REPORT) : StringUtils.EMPTY;
            final double outputSummary = nonNull(sd.getOutputSummary()) ? sd.getOutputSummary().doubleValue() : 0;
            final String lastOutputDate = nonNull(sd.getLastOutputDate()) ? sd.getLastOutputDate().format(FORMATTER_FOR_REPORT) : StringUtils.EMPTY;

            row = sheet.createRow(i + 1);

            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(nickname);
            cell.setCellStyle(body1Style);

            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(email);
            cell.setCellStyle(body1Style);

            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue(createdAt);
            cell.setCellStyle(body1Style);

            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(registrationIp);
            cell.setCellStyle(body1Style);

            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue(lastEntryDate);
            cell.setCellStyle(body1Style);

            cell = row.createCell(5, CellType.STRING);
            cell.setCellValue(lastIp);
            cell.setCellStyle(body1Style);

            cell = row.createCell(6, CellType.STRING);
            cell.setCellValue(currencyName);
            cell.setCellStyle(body1Style);

            cell = row.createCell(7, CellType.NUMERIC);
            cell.setCellValue(activeBalance);
            cell.setCellStyle(body1Style);

            cell = row.createCell(8, CellType.NUMERIC);
            cell.setCellValue(reservedBalance);
            cell.setCellStyle(body1Style);

            cell = row.createCell(9, CellType.STRING);
            cell.setCellValue(lastOrderDate);
            cell.setCellStyle(body1Style);

            cell = row.createCell(10, CellType.NUMERIC);
            cell.setCellValue(inputSummary);
            cell.setCellStyle(body1Style);

            cell = row.createCell(11, CellType.STRING);
            cell.setCellValue(lastInputDate);
            cell.setCellStyle(body1Style);

            cell = row.createCell(12, CellType.NUMERIC);
            cell.setCellValue(outputSummary);
            cell.setCellStyle(body1Style);

            cell = row.createCell(13, CellType.STRING);
            cell.setCellValue(lastOutputDate);
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
