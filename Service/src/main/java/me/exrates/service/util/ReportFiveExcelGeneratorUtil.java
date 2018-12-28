package me.exrates.service.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.exrates.model.Currency;
import me.exrates.model.dto.ExternalWalletBalancesDto;
import me.exrates.model.dto.InternalWalletBalancesDto;
import me.exrates.model.dto.WalletBalancesDto;
import me.exrates.model.enums.UserRole;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@NoArgsConstructor(access = AccessLevel.NONE)
public class ReportFiveExcelGeneratorUtil {

    private static final DateTimeFormatter FORMATTER_FOR_REPORT = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH-mm");

    private static final String SHEET1_NAME = "Sheet1 - Балансы кошельков за период";

    public static byte[] generate(List<Currency> currencies,
                                  Map<String, WalletBalancesDto> firstBalancesMap,
                                  LocalDateTime firstCreatedAt,
                                  Map<String, WalletBalancesDto> secondBalancesMap,
                                  LocalDateTime secondCreatedAt,
                                  List<UserRole> roles,
                                  Map<String, Pair<BigDecimal, BigDecimal>> ratesMap) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();

        CellStyle headerStyle = getHeaderStyle(workbook);
        CellStyle body1Style = getBode1Style(workbook);
        CellStyle footer1Style = getFooter1Style(workbook);
        CellStyle footer2Style = getFooter2Style(workbook);

        XSSFSheet sheet = workbook.createSheet(SHEET1_NAME);

        XSSFRow row;
        XSSFCell cell;

        row = sheet.createRow(0);

        //header
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Id монеты");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Название монеты");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Курс ($)");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Курс (BTC)");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue(String.format("Дата выгрузки: %s", secondCreatedAt.format(FORMATTER_FOR_REPORT)));
        cell.setCellStyle(headerStyle);

        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue(String.format("Выбранная дата среза информации: %s", firstCreatedAt.format(FORMATTER_FOR_REPORT)));
        cell.setCellStyle(headerStyle);

        cell = row.createCell(10, CellType.STRING);
        cell.setCellValue(String.format("Динамика изменений на %s", LocalDateTime.now().format(FORMATTER_FOR_REPORT)));
        cell.setCellStyle(headerStyle);

        row = sheet.createRow(1);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Сумма фактического остатка на всех кошельках");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("Сумма обязательств перед пользователями");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("Отклонение");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue("Сумма фактического остатка на всех кошельках");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("Сумма обязательств перед пользователями");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(9, CellType.STRING);
        cell.setCellValue("Отклонение");
        cell.setCellStyle(headerStyle);

        row = sheet.createRow(2);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("К-во монет на кошельках");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("К-во монет на кошельках");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("Количество монет");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue("К-во монет на кошельках");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("К-во монет на кошельках");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(9, CellType.STRING);
        cell.setCellValue("Количество монет");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(10, CellType.STRING);
        cell.setCellValue("Количество монет");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(11, CellType.STRING);
        cell.setCellValue("в USD");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(12, CellType.STRING);
        cell.setCellValue("в BTC");
        cell.setCellStyle(headerStyle);

        sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 1, 1));
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 3, 3));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 6));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 7, 9));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 10, 12));

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

        final int bound = currencies.size();

        //footer
        row = sheet.createRow(3);

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

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(9, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(10, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(11, CellType.NUMERIC);
        cell.setCellFormula("SUM(L" + 5 + ":L" + ((bound - 1) + 5) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(12, CellType.NUMERIC);
        cell.setCellFormula("SUM(M" + 5 + ":M" + ((bound - 1) + 5) + ")");
        cell.setCellStyle(footer2Style);

        //body
        int i = 0;
        for (Currency currency : currencies) {
            final int currencyId = currency.getId();
            final String currencyName = currency.getName();

            WalletBalancesDto firstBalance = firstBalancesMap.get(currencyName);

            BigDecimal exWalletTotalBalance1;
            BigDecimal inWalletsTotalBalance1;
            if (nonNull(firstBalance)) {
                ExternalWalletBalancesDto exWallet1 = firstBalance.getExternal();
                exWalletTotalBalance1 = exWallet1.getTotalBalance();

                List<InternalWalletBalancesDto> inWallets1 = firstBalance.getInternals();
                inWalletsTotalBalance1 = inWallets1.stream()
                        .filter(inWallet -> roles.contains(inWallet.getRoleName()))
                        .map(InternalWalletBalancesDto::getTotalBalance)
                        .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
            } else {
                exWalletTotalBalance1 = BigDecimal.ZERO;
                inWalletsTotalBalance1 = BigDecimal.ZERO;
            }

            WalletBalancesDto secondBalance = secondBalancesMap.get(currencyName);

            BigDecimal exWalletTotalBalance2;
            BigDecimal inWalletsTotalBalance2;
            if (nonNull(secondBalance)) {
                ExternalWalletBalancesDto exWallet2 = secondBalance.getExternal();
                exWalletTotalBalance2 = exWallet2.getTotalBalance();

                List<InternalWalletBalancesDto> inWallets2 = secondBalance.getInternals();
                inWalletsTotalBalance2 = inWallets2.stream()
                        .filter(inWallet -> roles.contains(inWallet.getRoleName()))
                        .map(InternalWalletBalancesDto::getTotalBalance)
                        .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
            } else {
                exWalletTotalBalance2 = BigDecimal.ZERO;
                inWalletsTotalBalance2 = BigDecimal.ZERO;
            }

            Pair<BigDecimal, BigDecimal> ratePair = ratesMap.get(currencyName);
            if (isNull(ratePair)) {
                ratePair = Pair.of(BigDecimal.ZERO, BigDecimal.ZERO);
            }
            final BigDecimal usdRate = ratePair.getLeft();
            final BigDecimal btcRate = ratePair.getRight();

            row = sheet.createRow(i + 4);

            cell = row.createCell(0, CellType.NUMERIC);
            cell.setCellValue(currencyId);
            cell.setCellStyle(body1Style);

            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(currencyName);
            cell.setCellStyle(body1Style);

            cell = row.createCell(2, CellType.NUMERIC);
            cell.setCellValue(usdRate.doubleValue());
            cell.setCellStyle(body1Style);

            cell = row.createCell(3, CellType.NUMERIC);
            cell.setCellValue(btcRate.doubleValue());
            cell.setCellStyle(body1Style);

            cell = row.createCell(4, CellType.NUMERIC);
            cell.setCellValue(exWalletTotalBalance2.doubleValue());
            cell.setCellStyle(body1Style);

            cell = row.createCell(5, CellType.NUMERIC);
            cell.setCellValue(inWalletsTotalBalance2.doubleValue());
            cell.setCellStyle(body1Style);

            cell = row.createCell(6, CellType.NUMERIC);
            cell.setCellFormula("E" + (i + 5) + "-F" + (i + 5));
            cell.setCellStyle(body1Style);

            cell = row.createCell(7, CellType.NUMERIC);
            cell.setCellValue(exWalletTotalBalance1.doubleValue());
            cell.setCellStyle(body1Style);

            cell = row.createCell(8, CellType.NUMERIC);
            cell.setCellValue(inWalletsTotalBalance1.doubleValue());
            cell.setCellStyle(body1Style);

            cell = row.createCell(9, CellType.NUMERIC);
            cell.setCellFormula("H" + (i + 5) + "-I" + (i + 5));
            cell.setCellStyle(body1Style);

            cell = row.createCell(10, CellType.NUMERIC);
            cell.setCellFormula("G" + (i + 5) + "-J" + (i + 5));
            cell.setCellStyle(body1Style);

            cell = row.createCell(11, CellType.NUMERIC);
            cell.setCellFormula("C" + (i + 5) + "*K" + (i + 5));
            cell.setCellStyle(body1Style);

            cell = row.createCell(12, CellType.NUMERIC);
            cell.setCellFormula("D" + (i + 5) + "*K" + (i + 5));
            cell.setCellStyle(body1Style);

            i++;
        }

        SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();

        ConditionalFormattingRule rule1 = sheetCF.createConditionalFormattingRule("AND(ISNUMBER($G5), $G5>0)");
        PatternFormatting fill1 = rule1.createPatternFormatting();
        fill1.setFillBackgroundColor(IndexedColors.GREEN.getIndex());
        fill1.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        ConditionalFormattingRule rule2 = sheetCF.createConditionalFormattingRule("AND(ISNUMBER($G5), $G5<0)");
        PatternFormatting fill2 = rule2.createPatternFormatting();
        fill2.setFillBackgroundColor(IndexedColors.RED.getIndex());
        fill2.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        ConditionalFormattingRule rule3 = sheetCF.createConditionalFormattingRule("AND(ISNUMBER($G5), $G5=0)");
        PatternFormatting fill3 = rule3.createPatternFormatting();
        fill3.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
        fill3.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        ConditionalFormattingRule[] cfRules = new ConditionalFormattingRule[]{rule1, rule2, rule3};

        CellRangeAddress[] regions = new CellRangeAddress[]{
                new CellRangeAddress(4, (bound - 1) + 4, 6, 6)
        };

        sheetCF.addConditionalFormatting(regions, cfRules);

        sheetCF = sheet.getSheetConditionalFormatting();

        rule1 = sheetCF.createConditionalFormattingRule("AND(ISNUMBER($J5), $J5>0)");
        fill1 = rule1.createPatternFormatting();
        fill1.setFillBackgroundColor(IndexedColors.GREEN.getIndex());
        fill1.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        rule2 = sheetCF.createConditionalFormattingRule("AND(ISNUMBER($J5), $J5<0)");
        fill2 = rule2.createPatternFormatting();
        fill2.setFillBackgroundColor(IndexedColors.RED.getIndex());
        fill2.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        rule3 = sheetCF.createConditionalFormattingRule("AND(ISNUMBER($J5), $J5=0)");
        fill3 = rule3.createPatternFormatting();
        fill3.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
        fill3.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        cfRules = new ConditionalFormattingRule[]{rule1, rule2, rule3};

        regions = new CellRangeAddress[]{
                new CellRangeAddress(4, (bound - 1) + 4, 9, 9)
        };

        sheetCF.addConditionalFormatting(regions, cfRules);

        sheetCF = sheet.getSheetConditionalFormatting();

        rule1 = sheetCF.createConditionalFormattingRule("AND(ISNUMBER($K5), $K5>0)");
        fill1 = rule1.createPatternFormatting();
        fill1.setFillBackgroundColor(IndexedColors.GREEN.getIndex());
        fill1.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        rule2 = sheetCF.createConditionalFormattingRule("AND(ISNUMBER($K5), $K5<0)");
        fill2 = rule2.createPatternFormatting();
        fill2.setFillBackgroundColor(IndexedColors.RED.getIndex());
        fill2.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        rule3 = sheetCF.createConditionalFormattingRule("AND(ISNUMBER($K5), $K5=0)");
        fill3 = rule3.createPatternFormatting();
        fill3.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
        fill3.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        cfRules = new ConditionalFormattingRule[]{rule1, rule2, rule3};

        regions = new CellRangeAddress[]{
                new CellRangeAddress(4, (bound - 1) + 4, 10, 12)
        };

        sheetCF.addConditionalFormatting(regions, cfRules);

        //footer
        row = sheet.createRow((bound - 1) + 5);

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

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(9, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(10, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(11, CellType.NUMERIC);
        cell.setCellFormula("SUM(L" + 5 + ":L" + ((bound - 1) + 5) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(12, CellType.NUMERIC);
        cell.setCellFormula("SUM(M" + 5 + ":M" + ((bound - 1) + 5) + ")");
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

    private static CellStyle getHeaderStyle(XSSFWorkbook workbook) {
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
        footerStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
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
