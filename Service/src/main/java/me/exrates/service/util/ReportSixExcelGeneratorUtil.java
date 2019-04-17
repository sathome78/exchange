package me.exrates.service.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.exrates.model.Currency;
import me.exrates.model.dto.ExternalWalletBalancesDto;
import me.exrates.model.dto.InOutReportDto;
import me.exrates.model.dto.InternalWalletBalancesDto;
import me.exrates.model.dto.WalletBalancesDto;
import me.exrates.model.dto.api.RateDto;
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

import static java.util.Objects.nonNull;

@Slf4j
@NoArgsConstructor(access = AccessLevel.NONE)
public class ReportSixExcelGeneratorUtil {

    private static final DateTimeFormatter FORMATTER_FOR_REPORT = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH-mm");

    private static final String SHEET1_NAME = "Sheet1 - Разбаланс учета монет";

    public static byte[] generate(List<Currency> currencies,
                                  Map<String, WalletBalancesDto> firstBalancesMap,
                                  LocalDateTime firstCreatedAt,
                                  Map<String, WalletBalancesDto> secondBalancesMap,
                                  LocalDateTime secondCreatedAt,
                                  List<UserRole> roles,
                                  Map<String, InOutReportDto> inOutMap,
                                  Map<String, RateDto> rates) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();

        CellStyle header1Style = getHeader1Style(workbook);
        CellStyle header2Style = getHeader2Style(workbook);
        CellStyle header3Style = getHeader3Style(workbook);
        CellStyle header4Style = getHeader4Style(workbook);
        CellStyle header5Style = getHeader5Style(workbook);
        CellStyle body1Style = getBode1Style(workbook);
        CellStyle footer1Style = getFooter1Style(workbook);

        XSSFSheet sheet = workbook.createSheet(SHEET1_NAME);

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
        cell.setCellValue("Обязательства на:");
        cell.setCellStyle(header2Style);

        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue(String.format("Изменения по обязательствам за период: %s - %s", secondCreatedAt.format(FORMATTER_FOR_REPORT), firstCreatedAt.format(FORMATTER_FOR_REPORT)));
        cell.setCellStyle(header2Style);

        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue(String.format("Разница между вводом и выводом за период: %s - %s", secondCreatedAt.format(FORMATTER_FOR_REPORT), firstCreatedAt.format(FORMATTER_FOR_REPORT)));
        cell.setCellStyle(header3Style);

        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("Разница значений (разница ввод/вывод минус изменения по обязательствам)");
        cell.setCellStyle(header4Style);

        cell = row.createCell(11, CellType.STRING);
        cell.setCellValue("Фактический остаток на кошельках на:");
        cell.setCellStyle(header2Style);

        cell = row.createCell(13, CellType.STRING);
        cell.setCellValue(String.format("Изменения по остаткам на кошелькам за период: %s - %s", secondCreatedAt.format(FORMATTER_FOR_REPORT), firstCreatedAt.format(FORMATTER_FOR_REPORT)));
        cell.setCellStyle(header2Style);

        cell = row.createCell(14, CellType.STRING);
        cell.setCellValue("Разница значений (изменения по остаткам на кошелькам минус изменения по обязательствам)");
        cell.setCellStyle(header5Style);

        cell = row.createCell(17, CellType.STRING);
        cell.setCellValue("Суммарный разбаланс с учетом всех расчетов (разница изменений по остаткам минус ввод/вывод)");
        cell.setCellStyle(header5Style);

        row = sheet.createRow(1);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue(firstCreatedAt.format(FORMATTER_FOR_REPORT));
        cell.setCellStyle(header2Style);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue(secondCreatedAt.format(FORMATTER_FOR_REPORT));
        cell.setCellStyle(header2Style);

        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("в монете");
        cell.setCellStyle(header4Style);

        cell = row.createCell(9, CellType.STRING);
        cell.setCellValue("в $");
        cell.setCellStyle(header4Style);

        cell = row.createCell(10, CellType.STRING);
        cell.setCellValue("в BTC");
        cell.setCellStyle(header4Style);

        cell = row.createCell(11, CellType.STRING);
        cell.setCellValue(firstCreatedAt.format(FORMATTER_FOR_REPORT));
        cell.setCellStyle(header2Style);

        cell = row.createCell(12, CellType.STRING);
        cell.setCellValue(secondCreatedAt.format(FORMATTER_FOR_REPORT));
        cell.setCellStyle(header2Style);

        cell = row.createCell(14, CellType.STRING);
        cell.setCellValue("в монете");
        cell.setCellStyle(header5Style);

        cell = row.createCell(15, CellType.STRING);
        cell.setCellValue("в $");
        cell.setCellStyle(header5Style);

        cell = row.createCell(16, CellType.STRING);
        cell.setCellValue("в BTC");
        cell.setCellStyle(header5Style);

        cell = row.createCell(17, CellType.STRING);
        cell.setCellValue("в монете");
        cell.setCellStyle(header5Style);

        cell = row.createCell(18, CellType.STRING);
        cell.setCellValue("в $");
        cell.setCellStyle(header5Style);

        cell = row.createCell(19, CellType.STRING);
        cell.setCellValue("в BTC");
        cell.setCellStyle(header5Style);

        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 3, 3));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 5));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 6, 6));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 7, 7));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 8, 10));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 11, 12));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 13, 13));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 14, 16));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 17, 19));

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
        sheet.autoSizeColumn(16, true);
        sheet.setColumnWidth(16, sheet.getColumnWidth(16) + 256);
        sheet.autoSizeColumn(17, true);
        sheet.setColumnWidth(17, sheet.getColumnWidth(17) + 256);
        sheet.autoSizeColumn(18, true);
        sheet.setColumnWidth(18, sheet.getColumnWidth(18) + 256);
        sheet.autoSizeColumn(19, true);
        sheet.setColumnWidth(19, sheet.getColumnWidth(19) + 256);

        final int bound = currencies.size();

        //footer
        row = sheet.createRow(2);

        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("-");
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
        cell.setCellValue("Итого:");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(9, CellType.NUMERIC);
        cell.setCellFormula("SUM(J" + 4 + ":J" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(10, CellType.NUMERIC);
        cell.setCellFormula("SUM(K" + 4 + ":K" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(11, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(12, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(13, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(14, CellType.STRING);
        cell.setCellValue("Итого:");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(15, CellType.NUMERIC);
        cell.setCellFormula("SUM(P" + 4 + ":P" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(16, CellType.NUMERIC);
        cell.setCellFormula("SUM(Q" + 4 + ":Q" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(17, CellType.STRING);
        cell.setCellValue("Итого:");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(18, CellType.NUMERIC);
        cell.setCellFormula("SUM(S" + 4 + ":S" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(19, CellType.NUMERIC);
        cell.setCellFormula("SUM(T" + 4 + ":T" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer1Style);

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

            BigDecimal input;
            BigDecimal output;
            InOutReportDto inOutReportDto = inOutMap.get(currencyName);
            if (nonNull(inOutReportDto)) {
                input = inOutReportDto.getInput();
                output = inOutReportDto.getOutput();
            } else {
                input = BigDecimal.ZERO;
                output = BigDecimal.ZERO;
            }
            final BigDecimal inOutDifference = input.subtract(output);

            RateDto rateDto = rates.getOrDefault(currencyName, RateDto.zeroRate(currencyName));

            final BigDecimal usdRate = rateDto.getUsdRate();
            final BigDecimal btcRate = rateDto.getBtcRate();

            row = sheet.createRow(i + 3);

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
            cell.setCellValue(inWalletsTotalBalance1.doubleValue());
            cell.setCellStyle(body1Style);

            cell = row.createCell(5, CellType.NUMERIC);
            cell.setCellValue(inWalletsTotalBalance2.doubleValue());
            cell.setCellStyle(body1Style);

            cell = row.createCell(6, CellType.NUMERIC);
            cell.setCellFormula("F" + (i + 4) + "-E" + (i + 4));
            cell.setCellStyle(body1Style);

            cell = row.createCell(7, CellType.NUMERIC);
            cell.setCellValue(inOutDifference.doubleValue());
            cell.setCellStyle(body1Style);

            cell = row.createCell(8, CellType.NUMERIC);
            cell.setCellFormula("H" + (i + 4) + "-G" + (i + 4));
            cell.setCellStyle(body1Style);

            cell = row.createCell(9, CellType.NUMERIC);
            cell.setCellFormula("I" + (i + 4) + "*C" + (i + 4));
            cell.setCellStyle(body1Style);

            cell = row.createCell(10, CellType.NUMERIC);
            cell.setCellFormula("I" + (i + 4) + "*D" + (i + 4));
            cell.setCellStyle(body1Style);

            cell = row.createCell(11, CellType.NUMERIC);
            cell.setCellValue(exWalletTotalBalance1.doubleValue());
            cell.setCellStyle(body1Style);

            cell = row.createCell(12, CellType.NUMERIC);
            cell.setCellValue(exWalletTotalBalance2.doubleValue());
            cell.setCellStyle(body1Style);

            cell = row.createCell(13, CellType.NUMERIC);
            cell.setCellFormula("M" + (i + 4) + "-L" + (i + 4));
            cell.setCellStyle(body1Style);

            cell = row.createCell(14, CellType.NUMERIC);
            cell.setCellFormula("N" + (i + 4) + "-G" + (i + 4));
            cell.setCellStyle(body1Style);

            cell = row.createCell(15, CellType.NUMERIC);
            cell.setCellFormula("O" + (i + 4) + "*C" + (i + 4));
            cell.setCellStyle(body1Style);

            cell = row.createCell(16, CellType.NUMERIC);
            cell.setCellFormula("O" + (i + 4) + "*D" + (i + 4));
            cell.setCellStyle(body1Style);

            cell = row.createCell(17, CellType.NUMERIC);
            cell.setCellFormula("O" + (i + 4) + "-I" + (i + 4));
            cell.setCellStyle(body1Style);

            cell = row.createCell(18, CellType.NUMERIC);
            cell.setCellFormula("R" + (i + 4) + "*C" + (i + 4));
            cell.setCellStyle(body1Style);

            cell = row.createCell(19, CellType.NUMERIC);
            cell.setCellFormula("R" + (i + 4) + "*D" + (i + 4));
            cell.setCellStyle(body1Style);

            i++;
        }

        SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();

        ConditionalFormattingRule rule1 = sheetCF.createConditionalFormattingRule("AND(ISNUMBER($R4), $R4>0)");
        PatternFormatting fill1 = rule1.createPatternFormatting();
        fill1.setFillBackgroundColor(IndexedColors.GREEN.getIndex());
        fill1.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        ConditionalFormattingRule rule2 = sheetCF.createConditionalFormattingRule("AND(ISNUMBER($R4), $R4<0)");
        PatternFormatting fill2 = rule2.createPatternFormatting();
        fill2.setFillBackgroundColor(IndexedColors.RED.getIndex());
        fill2.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        ConditionalFormattingRule rule3 = sheetCF.createConditionalFormattingRule("AND(ISNUMBER($R4), $R4=0)");
        PatternFormatting fill3 = rule3.createPatternFormatting();
        fill3.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
        fill3.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        ConditionalFormattingRule[] cfRules = new ConditionalFormattingRule[]{rule1, rule2, rule3};

        CellRangeAddress[] regions = new CellRangeAddress[]{new CellRangeAddress(3, (bound - 1) + 3, 17, 19)};

        sheetCF.addConditionalFormatting(regions, cfRules);

        //footer
        row = sheet.createRow((bound - 1) + 4);

        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("-");
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
        cell.setCellValue("Итого:");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(9, CellType.NUMERIC);
        cell.setCellFormula("SUM(J" + 4 + ":J" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(10, CellType.NUMERIC);
        cell.setCellFormula("SUM(K" + 4 + ":K" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(11, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(12, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(13, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(14, CellType.STRING);
        cell.setCellValue("Итого:");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(15, CellType.NUMERIC);
        cell.setCellFormula("SUM(P" + 4 + ":P" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(16, CellType.NUMERIC);
        cell.setCellFormula("SUM(Q" + 4 + ":Q" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(17, CellType.STRING);
        cell.setCellValue("Итого:");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(18, CellType.NUMERIC);
        cell.setCellFormula("SUM(S" + 4 + ":S" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(19, CellType.NUMERIC);
        cell.setCellFormula("SUM(T" + 4 + ":T" + ((bound - 1) + 4) + ")");
        cell.setCellStyle(footer1Style);

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

    private static CellStyle getHeader2Style(XSSFWorkbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
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

    private static CellStyle getHeader3Style(XSSFWorkbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
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

    private static CellStyle getHeader4Style(XSSFWorkbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
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

    private static CellStyle getHeader5Style(XSSFWorkbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
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
