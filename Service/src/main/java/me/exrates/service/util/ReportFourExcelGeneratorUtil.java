package me.exrates.service.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.exrates.model.Currency;
import me.exrates.model.dto.CurrencyRateDto;
import me.exrates.model.dto.ExternalWalletBalancesDto;
import me.exrates.model.dto.InternalWalletBalancesDto;
import me.exrates.model.dto.WalletBalancesDto;
import me.exrates.model.enums.UserRole;
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

@Slf4j
@NoArgsConstructor(access = AccessLevel.NONE)
public class ReportFourExcelGeneratorUtil {

    private static final DateTimeFormatter FORMATTER_FOR_REPORT = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH-mm");

    private static final String SHEET1_NAME = "Sheet1 - Срез балансов кошельков";

    public static byte[] generate(List<Currency> currencies,
                                  Map<String, WalletBalancesDto> balancesMap,
                                  LocalDateTime createdAt) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();

        CellStyle headerStyle = getHeaderStyle(workbook);
        CellStyle body1Style = getBode1Style(workbook);
        CellStyle body2Style = getBode2Style(workbook);
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
        cell.setCellValue("Признак достоверности");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Курс ($)");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Курс (BTC)");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("Сумма фактического остатка на всех кошельках");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("Сумма обязательств перед пользователями");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(11, CellType.STRING);
        cell.setCellValue(String.format("Отклонение на %s", createdAt.format(FORMATTER_FOR_REPORT)));
        cell.setCellStyle(headerStyle);

        row = sheet.createRow(1);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("К-во монет на кошельках");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("Сумма монет на кошельках в USD");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue("Сумма монет на кошельках в BTC");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("К-во монет на кошельках");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(9, CellType.STRING);
        cell.setCellValue("Сумма монет на кошельках в USD");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(10, CellType.STRING);
        cell.setCellValue("Сумма монет на кошельках в BTC");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(11, CellType.STRING);
        cell.setCellValue("Количество монет");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(12, CellType.STRING);
        cell.setCellValue("Сумма в USD");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(13, CellType.STRING);
        cell.setCellValue("Сумма в BTC");
        cell.setCellStyle(headerStyle);

        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 3, 3));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 4, 4));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 5, 7));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 8, 10));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 11, 13));

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

        final int bound = balancesMap.size();

        //footer
        row = sheet.createRow(2);

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

        cell = row.createCell(6, CellType.NUMERIC);
        cell.setCellFormula("SUM(G" + 4 + ":G" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(7, CellType.NUMERIC);
        cell.setCellFormula("SUM(H" + 4 + ":H" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(9, CellType.NUMERIC);
        cell.setCellFormula("SUM(J" + 4 + ":J" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(10, CellType.NUMERIC);
        cell.setCellFormula("SUM(K" + 4 + ":K" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(11, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(12, CellType.NUMERIC);
        cell.setCellFormula("SUM(M" + 4 + ":M" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(13, CellType.NUMERIC);
        cell.setCellFormula("SUM(N" + 4 + ":N" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer2Style);

        //body
        int i = 0;
        for (Currency currency : currencies) {
            final int currencyId = currency.getId();
            final String currencyName = currency.getName();

            WalletBalancesDto balance = balancesMap.get(currencyName);
            if (isNull(balance)) {
                continue;
            }

            ExternalWalletBalancesDto exWallet = balance.getExternal();
            final boolean signOfCertainty = exWallet.isSignOfCertainty();
            final BigDecimal exWalletTotalBalance = exWallet.getTotalBalance();

            List<InternalWalletBalancesDto> inWallets = balance.getInternals();
            final BigDecimal inWalletsTotalBalance = inWallets.stream()
                    .filter(inWallet -> inWallet.getRoleName() != UserRole.BOT_TRADER)
                    .map(InternalWalletBalancesDto::getTotalBalance)
                    .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

            CurrencyRateDto rate = balance.getRate();
            if (isNull(rate)) {
                rate = new CurrencyRateDto(BigDecimal.ZERO, BigDecimal.ZERO);
            }

            final BigDecimal usdRate = rate.getUsdRate();
            final BigDecimal btcRate = rate.getBtcRate();

            row = sheet.createRow(i + 3);
            CellStyle style = signOfCertainty ? body2Style : body1Style;

            cell = row.createCell(0, CellType.NUMERIC);
            cell.setCellValue(currencyId);
            cell.setCellStyle(style);

            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(currencyName);
            cell.setCellStyle(style);

            cell = row.createCell(2, CellType.NUMERIC);
            cell.setCellValue(signOfCertainty ? 1 : 0);
            cell.setCellStyle(style);

            cell = row.createCell(3, CellType.NUMERIC);
            cell.setCellValue(usdRate.doubleValue());
            cell.setCellStyle(style);

            cell = row.createCell(4, CellType.NUMERIC);
            cell.setCellValue(btcRate.doubleValue());
            cell.setCellStyle(style);

            cell = row.createCell(5, CellType.NUMERIC);
            cell.setCellValue(exWalletTotalBalance.doubleValue());
            cell.setCellStyle(style);

            cell = row.createCell(6, CellType.NUMERIC);
            cell.setCellFormula("D" + (i + 4) + "*F" + (i + 4));
            cell.setCellStyle(style);

            cell = row.createCell(7, CellType.NUMERIC);
            cell.setCellFormula("E" + (i + 4) + "*F" + (i + 4));
            cell.setCellStyle(style);

            cell = row.createCell(8, CellType.NUMERIC);
            cell.setCellValue(inWalletsTotalBalance.doubleValue());
            cell.setCellStyle(style);

            cell = row.createCell(9, CellType.NUMERIC);
            cell.setCellFormula("D" + (i + 4) + "*I" + (i + 4));
            cell.setCellStyle(style);

            cell = row.createCell(10, CellType.NUMERIC);
            cell.setCellFormula("E" + (i + 4) + "*I" + (i + 4));
            cell.setCellStyle(style);

            cell = row.createCell(11, CellType.NUMERIC);
            cell.setCellFormula("(F" + (i + 4) + "-I" + (i + 4) + ") * C" + (i + 4));
            cell.setCellStyle(style);

            cell = row.createCell(12, CellType.NUMERIC);
            cell.setCellFormula("D" + (i + 4) + "*L" + (i + 4));
            cell.setCellStyle(style);

            cell = row.createCell(13, CellType.NUMERIC);
            cell.setCellFormula("E" + (i + 4) + "*L" + (i + 4));
            cell.setCellStyle(style);

            i++;
        }

        SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();

        ConditionalFormattingRule rule1 = sheetCF.createConditionalFormattingRule("AND(ISNUMBER($L4), $L4>0)");
        PatternFormatting fill1 = rule1.createPatternFormatting();
        fill1.setFillBackgroundColor(IndexedColors.GREEN.getIndex());
        fill1.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        ConditionalFormattingRule rule2 = sheetCF.createConditionalFormattingRule("AND(ISNUMBER($L4), $L4<0)");
        PatternFormatting fill2 = rule2.createPatternFormatting();
        fill2.setFillBackgroundColor(IndexedColors.RED.getIndex());
        fill2.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        ConditionalFormattingRule rule3 = sheetCF.createConditionalFormattingRule("AND(ISNUMBER($L4), $L4=0)");
        PatternFormatting fill3 = rule3.createPatternFormatting();
        fill3.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
        fill3.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        ConditionalFormattingRule[] cfRules = new ConditionalFormattingRule[]{rule1, rule2, rule3};

        CellRangeAddress[] regions = new CellRangeAddress[]{new CellRangeAddress(3, (bound - 1) + 3, 11, 13)};

        sheetCF.addConditionalFormatting(regions, cfRules);

        //footer
        row = sheet.createRow((bound - 1) + 4);

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

        cell = row.createCell(6, CellType.NUMERIC);
        cell.setCellFormula("SUM(G" + 4 + ":G" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(7, CellType.NUMERIC);
        cell.setCellFormula("SUM(H" + 4 + ":H" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(9, CellType.NUMERIC);
        cell.setCellFormula("SUM(J" + 4 + ":J" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(10, CellType.NUMERIC);
        cell.setCellFormula("SUM(K" + 4 + ":K" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(11, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(12, CellType.NUMERIC);
        cell.setCellFormula("SUM(M" + 4 + ":M" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(13, CellType.NUMERIC);
        cell.setCellFormula("SUM(N" + 4 + ":N" + ((bound - 1) + 4) + ")");
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

    private static CellStyle getBode2Style(XSSFWorkbook workbook) {
        CellStyle bodyStyle = workbook.createCellStyle();
        bodyStyle.setBorderBottom(BorderStyle.THIN);
        bodyStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        bodyStyle.setBorderLeft(BorderStyle.THIN);
        bodyStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        bodyStyle.setBorderRight(BorderStyle.THIN);
        bodyStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        bodyStyle.setBorderTop(BorderStyle.THIN);
        bodyStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        bodyStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        bodyStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
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
