package me.exrates.service.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.exrates.model.dto.InOutReportDto;
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
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;

@Slf4j
@NoArgsConstructor(access = AccessLevel.NONE)
public class ReportTwoExcelGeneratorUtil {

    public static byte[] generate(Map<String, InOutReportDto> inOutMap,
                                  Map<String, Pair<BigDecimal, BigDecimal>> ratesMap) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();

        CellStyle header1Style = getHeader1Style(workbook);
        CellStyle body1Style = getBode1Style(workbook);
        CellStyle footer1Style = getFooter1Style(workbook);
        CellStyle footer2Style = getFooter2Style(workbook);

        XSSFSheet sheet = workbook.createSheet("Sheet1 - Статистика по вводу-выводу по валютам");

        XSSFRow row;
        XSSFCell cell;

        row = sheet.createRow(0);

        //header
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Id монеты");
        cell.setCellStyle(header1Style);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Название монеты");
        cell.setCellStyle(header1Style);

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Курс ($)");
        cell.setCellStyle(header1Style);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Курс (BTC)");
        cell.setCellStyle(header1Style);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Количество (ввод)");
        cell.setCellStyle(header1Style);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("Сумма (ввод)");
        cell.setCellStyle(header1Style);

        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("Количество (вывод)");
        cell.setCellStyle(header1Style);

        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue("Сумма (вывод)");
        cell.setCellStyle(header1Style);

        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("Разница");
        cell.setCellStyle(header1Style);

        cell = row.createCell(9, CellType.STRING);
        cell.setCellValue("Разница в USD");
        cell.setCellStyle(header1Style);

        cell = row.createCell(10, CellType.STRING);
        cell.setCellValue("Разница в BTC");
        cell.setCellStyle(header1Style);

        cell = row.createCell(11, CellType.STRING);
        cell.setCellValue("Комиссия за ввод");
        cell.setCellStyle(header1Style);

        cell = row.createCell(12, CellType.STRING);
        cell.setCellValue("Комиссия за вывод");
        cell.setCellStyle(header1Style);

        cell = row.createCell(13, CellType.STRING);
        cell.setCellValue("Сумма комиссий");
        cell.setCellStyle(header1Style);

        cell = row.createCell(14, CellType.STRING);
        cell.setCellValue("Сумма комиссий в USD");
        cell.setCellStyle(header1Style);

        cell = row.createCell(15, CellType.STRING);
        cell.setCellValue("Сумма комиссий в BTC");
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
        sheet.autoSizeColumn(14, true);
        sheet.setColumnWidth(14, sheet.getColumnWidth(14) + 256);
        sheet.autoSizeColumn(15, true);
        sheet.setColumnWidth(15, sheet.getColumnWidth(15) + 256);

        Set<String> currencies = inOutMap.keySet();
        final int bound = inOutMap.size();

        //footer
        row = sheet.createRow(1);

        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Итого:");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(4, CellType.NUMERIC);
        cell.setCellFormula("SUM(E" + 3 + ":E" + ((bound - 1) + 3) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(6, CellType.NUMERIC);
        cell.setCellFormula("SUM(G" + 3 + ":G" + ((bound - 1) + 3) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(9, CellType.NUMERIC);
        cell.setCellFormula("SUM(J" + 3 + ":J" + ((bound - 1) + 3) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(10, CellType.NUMERIC);
        cell.setCellFormula("SUM(K" + 3 + ":K" + ((bound - 1) + 3) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(11, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(12, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(13, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(14, CellType.NUMERIC);
        cell.setCellFormula("SUM(O" + 3 + ":O" + ((bound - 1) + 3) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(15, CellType.NUMERIC);
        cell.setCellFormula("SUM(P" + 3 + ":P" + ((bound - 1) + 3) + ")");
        cell.setCellStyle(footer2Style);

        //body
        int i = 0;
        for (String currency : currencies) {
            InOutReportDto inOutReportDto = inOutMap.get(currency);

            final int currencyId = inOutReportDto.getCurrencyId();

            final int inputCount = inOutReportDto.getInputCount();
            final BigDecimal input = inOutReportDto.getInput();
            final int outputCount = inOutReportDto.getOutputCount();
            final BigDecimal output = inOutReportDto.getOutput();
            final BigDecimal inputCommission = inOutReportDto.getInputCommission();
            final BigDecimal outputCommission = inOutReportDto.getOutputCommission();

            Pair<BigDecimal, BigDecimal> ratePair = ratesMap.get(currency);
            if (isNull(ratePair)) {
                ratePair = Pair.of(BigDecimal.ZERO, BigDecimal.ZERO);
            }
            final BigDecimal usdRate = ratePair.getLeft();
            final BigDecimal btcRate = ratePair.getRight();

            row = sheet.createRow(i + 2);

            cell = row.createCell(0, CellType.NUMERIC);
            cell.setCellValue(currencyId);
            cell.setCellStyle(body1Style);

            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(currency);
            cell.setCellStyle(body1Style);

            cell = row.createCell(2, CellType.NUMERIC);
            cell.setCellValue(usdRate.doubleValue());
            cell.setCellStyle(body1Style);

            cell = row.createCell(3, CellType.NUMERIC);
            cell.setCellValue(btcRate.doubleValue());
            cell.setCellStyle(body1Style);

            cell = row.createCell(4, CellType.NUMERIC);
            cell.setCellValue(inputCount);
            cell.setCellStyle(body1Style);

            cell = row.createCell(5, CellType.NUMERIC);
            cell.setCellValue(input.doubleValue());
            cell.setCellStyle(body1Style);

            cell = row.createCell(6, CellType.NUMERIC);
            cell.setCellValue(outputCount);
            cell.setCellStyle(body1Style);

            cell = row.createCell(7, CellType.NUMERIC);
            cell.setCellValue(output.doubleValue());
            cell.setCellStyle(body1Style);

            cell = row.createCell(8, CellType.NUMERIC);
            cell.setCellFormula("F" + (i + 3) + "-H" + (i + 3));
            cell.setCellStyle(body1Style);

            cell = row.createCell(9, CellType.NUMERIC);
            cell.setCellFormula("I" + (i + 3) + "*C" + (i + 3));
            cell.setCellStyle(body1Style);

            cell = row.createCell(10, CellType.NUMERIC);
            cell.setCellFormula("I" + (i + 3) + "*D" + (i + 3));
            cell.setCellStyle(body1Style);

            cell = row.createCell(11, CellType.NUMERIC);
            cell.setCellValue(inputCommission.doubleValue());
            cell.setCellStyle(body1Style);

            cell = row.createCell(12, CellType.NUMERIC);
            cell.setCellValue(outputCommission.doubleValue());
            cell.setCellStyle(body1Style);

            cell = row.createCell(13, CellType.NUMERIC);
            cell.setCellFormula("L" + (i + 3) + "+M" + (i + 3));
            cell.setCellStyle(body1Style);

            cell = row.createCell(14, CellType.NUMERIC);
            cell.setCellFormula("N" + (i + 3) + "*C" + (i + 3));
            cell.setCellStyle(body1Style);

            cell = row.createCell(15, CellType.NUMERIC);
            cell.setCellFormula("N" + (i + 3) + "*D" + (i + 3));
            cell.setCellStyle(body1Style);

            i++;
        }

        //footer
        row = sheet.createRow((bound - 1) + 3);

        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Итого:");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(4, CellType.NUMERIC);
        cell.setCellFormula("SUM(E" + 3 + ":E" + ((bound - 1) + 3) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(6, CellType.NUMERIC);
        cell.setCellFormula("SUM(G" + 3 + ":G" + ((bound - 1) + 3) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(9, CellType.NUMERIC);
        cell.setCellFormula("SUM(J" + 3 + ":J" + ((bound - 1) + 3) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(10, CellType.NUMERIC);
        cell.setCellFormula("SUM(K" + 3 + ":K" + ((bound - 1) + 3) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(11, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(12, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(13, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(14, CellType.NUMERIC);
        cell.setCellFormula("SUM(O" + 3 + ":O" + ((bound - 1) + 3) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(15, CellType.NUMERIC);
        cell.setCellFormula("SUM(P" + 3 + ":P" + ((bound - 1) + 3) + ")");
        cell.setCellStyle(footer2Style);

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

    private static CellStyle getFooter2Style(XSSFWorkbook workbook) {
        CellStyle footerStyle = workbook.createCellStyle();
        footerStyle.setBorderBottom(BorderStyle.THIN);
        footerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        footerStyle.setBorderLeft(BorderStyle.THIN);
        footerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        footerStyle.setBorderRight(BorderStyle.THIN);
        footerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        footerStyle.setBorderTop(BorderStyle.THIN);
        footerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        footerStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
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
