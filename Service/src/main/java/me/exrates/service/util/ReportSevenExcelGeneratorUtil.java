package me.exrates.service.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.exrates.model.dto.InvoiceReportDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@NoArgsConstructor(access = AccessLevel.NONE)
public class ReportSevenExcelGeneratorUtil {

    private static final String SHEET1_NAME = "Sheet1 - Выгрузить свод ввода-вывода";

    public static byte[] generate(List<InvoiceReportDto> resultList,
                                  Map<String, Pair<BigDecimal, BigDecimal>> ratesMap) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();

        CellStyle header1Style = getHeader1Style(workbook);
        CellStyle body1Style = getBode1Style(workbook);

        XSSFSheet sheet = workbook.createSheet(SHEET1_NAME);

        XSSFRow row;
        XSSFCell cell;

        row = sheet.createRow(0);

        //header
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Id счета");
        cell.setCellStyle(header1Style);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Название монеты");
        cell.setCellStyle(header1Style);

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Курс ($)");
        cell.setCellStyle(header1Style);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Дата создания");
        cell.setCellStyle(header1Style);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Электронная почта пользователя");
        cell.setCellStyle(header1Style);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("Банк получателя / Blockchain адрес");
        cell.setCellStyle(header1Style);

        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("Количество монет");
        cell.setCellStyle(header1Style);

        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue("Имя плательщика");
        cell.setCellStyle(header1Style);

        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("Банк плательщика");
        cell.setCellStyle(header1Style);

        cell = row.createCell(9, CellType.STRING);
        cell.setCellValue("Статус");
        cell.setCellStyle(header1Style);

        cell = row.createCell(10, CellType.STRING);
        cell.setCellValue("Электронная почта приемщика");
        cell.setCellStyle(header1Style);

        cell = row.createCell(11, CellType.STRING);
        cell.setCellValue("Дата приема / изменения");
        cell.setCellStyle(header1Style);

        cell = row.createCell(12, CellType.STRING);
        cell.setCellValue("Операция");
        cell.setCellStyle(header1Style);

        cell = row.createCell(13, CellType.STRING);
        cell.setCellValue("Система");
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
        sheet.setColumnWidth(12, sheet.getColumnWidth(11) + 256);
        sheet.autoSizeColumn(13, true);
        sheet.setColumnWidth(13, sheet.getColumnWidth(11) + 256);

        //body
        int i = 0;
        for (InvoiceReportDto ir : resultList) {
            final Integer docId = ir.getDocId();
            final String currency = ir.getCurrency();
            final String creationDate = ir.getCreationDate();
            final String userEmail = ir.getUserEmail();
            final String recipientBank = ir.getRecipientBank();
            final double amount = nonNull(ir.getAmount()) ? ir.getAmount().doubleValue() : 0;
            final String payerName = nonNull(ir.getPayerName()) ? ir.getPayerName() : StringUtils.EMPTY;
            final String payerBankCode = nonNull(ir.getPayerBankCode()) ? ir.getPayerBankCode() : StringUtils.EMPTY;
            final String status = ir.getStatus();
            final String acceptorUserEmail = nonNull(ir.getAcceptorUserEmail()) ? ir.getAcceptorUserEmail() : StringUtils.EMPTY;
            final String acceptanceDate = nonNull(ir.getAcceptanceDate()) ? ir.getAcceptanceDate() : StringUtils.EMPTY;
            final String operation = nonNull(ir.getOperation()) ? ir.getOperation() : StringUtils.EMPTY;
            final String system = nonNull(ir.getSystem()) ? ir.getSystem() : StringUtils.EMPTY;

            Pair<BigDecimal, BigDecimal> ratePair = ratesMap.get(currency);
            if (isNull(ratePair)) {
                ratePair = Pair.of(BigDecimal.ZERO, BigDecimal.ZERO);
            }
            final double usdRate = ratePair.getLeft().doubleValue();

            row = sheet.createRow(i + 1);

            cell = row.createCell(0, CellType.NUMERIC);
            cell.setCellValue(docId);
            cell.setCellStyle(body1Style);

            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(currency);
            cell.setCellStyle(body1Style);

            cell = row.createCell(2, CellType.NUMERIC);
            cell.setCellValue(usdRate);
            cell.setCellStyle(body1Style);

            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(creationDate);
            cell.setCellStyle(body1Style);

            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue(userEmail);
            cell.setCellStyle(body1Style);

            cell = row.createCell(5, CellType.STRING);
            cell.setCellValue(recipientBank);
            cell.setCellStyle(body1Style);

            cell = row.createCell(6, CellType.NUMERIC);
            cell.setCellValue(amount);
            cell.setCellStyle(body1Style);

            cell = row.createCell(7, CellType.STRING);
            cell.setCellValue(payerName);
            cell.setCellStyle(body1Style);

            cell = row.createCell(8, CellType.STRING);
            cell.setCellValue(payerBankCode);
            cell.setCellStyle(body1Style);

            cell = row.createCell(9, CellType.STRING);
            cell.setCellValue(status);
            cell.setCellStyle(body1Style);

            cell = row.createCell(10, CellType.STRING);
            cell.setCellValue(acceptorUserEmail);
            cell.setCellStyle(body1Style);

            cell = row.createCell(11, CellType.STRING);
            cell.setCellValue(acceptanceDate);
            cell.setCellStyle(body1Style);

            cell = row.createCell(12, CellType.STRING);
            cell.setCellValue(operation);
            cell.setCellStyle(body1Style);

            cell = row.createCell(13, CellType.STRING);
            cell.setCellValue(system);
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
