package com.infy.reports.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.infy.bpe.core.DataStore;
import com.infy.report.model.ReportType;
import com.infy.services.model.ApexPMDRules;
import com.infy.services.model.CustomRulesBPE;
import com.infy.services.model.HealthCheckParameter;
import com.infy.services.model.HealthCheckParams;

public class ReportGeneration {
	private static LinkedHashMap<String, String> sheetNames;

	public static void generateExcelOutput(List<ReportType> report, HealthCheckParameter healthCheckParameter) throws IOException {
		sheetNames = new LinkedHashMap<String, String>();
		LinkedHashMap<String, Integer> customSheetDtls = new LinkedHashMap<String, Integer>();
		LinkedHashMap<String, Integer> pmdSheetDtls = new LinkedHashMap<String, Integer>();
		File f = new File(DataStore.OUTPUT_FILENAME);
		if (!f.exists())
			f.createNewFile();
		Workbook wb = new XSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();
		short dateFormat = createHelper.createDataFormat().getFormat("yyyy-dd-MM hh:mm");
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setWrapText(true);
		cellStyle.setDataFormat(dateFormat);
		Map<String, CellStyle> styles = createStyles(wb);
		List<Sheet> workSheets = new ArrayList<Sheet>();
		Sheet workSheet;
		// customization rules of BPE
		int rowIndex;
		Row dataRow;
		Cell dataCell;
		for (CustomRulesBPE sheetName : CustomRulesBPE.values()) {
			rowIndex = 3;
			dataRow = null;
			dataCell = null;
			workSheet = createBasicSheet(wb, sheetName.getSheetDesc(), sheetName.isError());
			workSheets.add(workSheet);
			createTitleHeaderRow(workSheet, styles, sheetName.isLineNumberAvailable() ? 3 : 2, sheetName.getNotes());
			for (ReportType rt : report) {
				if (rt.getCategory() == (sheetName.ordinal() + 1)) {
					dataRow = workSheet.createRow(rowIndex++);					
					dataCell = dataRow.createCell(0);
					dataCell.setCellValue((rt.getClassName()));
					dataCell.setCellStyle(styles
							.get("string_data"));
					dataCell = dataRow.createCell(1);
					if (sheetName.isLineNumberAvailable()) {
						dataCell.setCellValue((rt.getLineNumber()));
						dataCell.setCellStyle(styles.get("string_data"));
						dataCell = dataRow.createCell(2);
					}
					
					dataCell.setCellValue(rt.getIssueDesc());
					dataCell.setCellStyle(styles.get("string_data"));
					dataCell = dataRow.createCell(dataRow.getLastCellNum());
					dataCell.setCellValue(rt.getLastModifiedBy());
					dataCell.setCellStyle(styles.get("string_data"));
					if (null != rt.getLastModifiedDate()) {
						dataCell = dataRow.createCell(dataRow.getLastCellNum());
						dataCell.setCellValue(rt.getLastModifiedDate());
						dataCell.setCellStyle(cellStyle);

					}
					
				}
			}
			customSheetDtls.put(sheetName.getSheetDesc(), rowIndex-3);
		}
		//create Apex PMD Rules Sheet
		for (ApexPMDRules sheetName : ApexPMDRules.values()) {
			rowIndex = 3;
			dataRow = null;
			dataCell = null;
			workSheet = createBasicSheet(wb, sheetName.getSheetDesc(), sheetName.isError());
			workSheets.add(workSheet);
			createTitleHeaderRow(workSheet, styles, sheetName.isLineNumberAvailable() ? 3 : 2, sheetName.getNotes());
			for (ReportType rt : report) {
				if (rt.getCategory() == sheetName.getCategory()) {
					dataRow = workSheet.createRow(rowIndex++);					
					dataCell = dataRow.createCell(0);
					dataCell.setCellValue((rt.getClassName()));
					dataCell.setCellStyle(styles
							.get("string_data"));
					dataCell = dataRow.createCell(1);
					if (sheetName.isLineNumberAvailable()) {
						dataCell.setCellValue((rt.getLineNumber()));
						dataCell.setCellStyle(styles.get("string_data"));
						dataCell = dataRow.createCell(2);
					}
					
					dataCell.setCellValue(rt.getIssueDesc());
					dataCell.setCellStyle(styles.get("string_data"));
					dataCell = dataRow.createCell(dataRow.getLastCellNum());
					dataCell.setCellValue(rt.getLastModifiedBy());
					dataCell.setCellStyle(styles.get("string_data"));
					if (null != rt.getLastModifiedDate()) {
						dataCell = dataRow.createCell(dataRow.getLastCellNum());
						dataCell.setCellValue(rt.getLastModifiedDate());
						dataCell.setCellStyle(cellStyle);

					}
					
				}
			}
			pmdSheetDtls.put(sheetName.getSheetDesc(), rowIndex-3);
		}
		
		createSummarySheet(wb, styles, healthCheckParameter,customSheetDtls,pmdSheetDtls);
		FileOutputStream out = new FileOutputStream(f);
		wb.write(out);
		out.close();
	}
	
	private static void createCustomizationSummary(Sheet sheet,int rowNumber,HealthCheckParameter healthCheckParameter,Map<String, CellStyle> styles){
		Row dataRow = sheet.createRow(rowNumber);
		dataRow.setHeightInPoints(35);
		for (int i = 0; i <= 2; i++) {
			dataRow.createCell(i).setCellStyle(styles.get("title"));
		}
		// create table title
		Cell dataCell = dataRow.getCell(0);
		dataCell.setCellValue("Customization Summary");
		rowNumber = rowNumber + 1;
		sheet.addMergedRegion(CellRangeAddress.valueOf("$A$" + rowNumber + ":$C$" + rowNumber));
		// create column header
		dataRow = sheet.createRow(rowNumber++);
		dataCell = dataRow.createCell(0);
		dataCell.setCellStyle(styles.get("header"));
		dataCell.setCellValue("Rule Name");
		dataCell = dataRow.createCell(1);
		dataCell.setCellStyle(styles.get("header"));
		dataCell.setCellValue("Total Count");
		for (HealthCheckParams healthParam : HealthCheckParams.values()) {
			dataRow = sheet.createRow(rowNumber++);
			dataCell = dataRow.createCell(0);
			dataCell.setCellValue(healthParam.getDesc());
			dataCell.setCellStyle(styles.get("border_data"));
			dataCell = dataRow.createCell(1);
			dataCell.setCellStyle(styles.get("border_data"));
			switch (healthParam) {
			case SECURITYSCORE:
				dataCell.setCellValue(healthCheckParameter.getSecurityScore());
				break;
			case APEXCUSTOMLINES:
				dataCell.setCellValue(healthCheckParameter.getApexClassLines());
				break;
			case VFCUSTOMLINES:
				dataCell.setCellValue(healthCheckParameter.getApexPageLines());
				break;
			case CODECOVERAGE:
				dataCell.setCellValue(healthCheckParameter.getLessCodeCoverageCnt());
				break;
			case SOQLSTATEMENT:
				dataCell.setCellValue(
						healthCheckParameter.getApexClsSoql() + healthCheckParameter.getApexTriggerSoql());
				break;
			case DMLSTATEMENT:
				dataCell.setCellValue(healthCheckParameter.getApexClsDML() + healthCheckParameter.getApexTriggerDML());
				break;
			case TOTALAPEXCLASS:
				dataCell.setCellValue(healthCheckParameter.getTotalApexCls());
				break;
			case TOTALAPEXPAGES:
				dataCell.setCellValue(healthCheckParameter.getTotalApexPage());
				break;
			case TOTALAPEXCOMPONENTS:
				dataCell.setCellValue(healthCheckParameter.getTotalApexComponents());
				break;
			}
		}
	}

	
	
	static Sheet createBasicSheet(Workbook wb, String name, boolean isError) {
		XSSFSheet sheet = (XSSFSheet) wb.createSheet(name);
		sheetNames.put(sheet.getSheetName(), name);
		sheet.setPrintGridlines(false);
		sheet.setDisplayGridlines(true);
		sheet.createFreezePane(4, 3);

		PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);

		sheet.setColumnWidth(0, 45 * 256);
		sheet.setColumnWidth(1, 25 * 256);
		sheet.setColumnWidth(2, 25 * 256);
		sheet.setColumnWidth(3, 25 * 256);

		sheet.setTabColor(isError ? IndexedColors.RED.index : IndexedColors.LIGHT_ORANGE.index);
		return sheet;

	}
	
	private static void createSummarySheet(Workbook wb, Map<String, CellStyle> styles,
			HealthCheckParameter healthCheckParameter, LinkedHashMap<String, Integer> sheetDetails,LinkedHashMap<String, Integer> pmdShtDetails) {
		XSSFSheet sheet = (XSSFSheet) wb.createSheet("Summary");
		Row titleRow = sheet.createRow(0);
		titleRow.setHeightInPoints(35);
		for (int i = 0; i <= 2; i++) {
			titleRow.createCell(i).setCellStyle(styles.get("title"));
		}
		Cell titleCell = titleRow.getCell(0);
		titleCell.setCellValue("Best Practices Enforcer");
		titleRow = sheet.createRow(1);
		for (int i = 0; i <= 3; i++) {
			titleRow.createCell(i).setCellStyle(styles.get("title"));
		}
		titleCell = titleRow.getCell(0);
		titleCell.setCellValue("Custom Rules Configured");
		titleCell = titleRow.getCell(3);
		titleCell.setCellValue("Apex PMD Rules");
		sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$C$1"));
		sheet.setColumnWidth(0, 45 * 256);
		sheet.setColumnWidth(1, 25 * 256);
		sheet.setColumnWidth(2, 25 * 256);
		sheet.setColumnWidth(3, 25 * 256);
		sheet.setColumnWidth(4, 25 * 256);
		createSummaryHeader(sheet, styles);
		CellStyle hlink_style = wb.createCellStyle();
		final Font hyperlinkFont = wb.createFont();
		hyperlinkFont.setUnderline(Font.U_SINGLE);
		hyperlinkFont.setColor(IndexedColors.BLUE.getIndex());
		hlink_style.setFont(hyperlinkFont);
		Row dataRow = null;
		Cell dataCell;
		int rowNumber = 3;
		XSSFHyperlink href;
		CreationHelper createHelper = wb.getCreationHelper();
		for (String sheetName : sheetDetails.keySet()) {
			dataRow = sheet.createRow(rowNumber++);
			dataCell = dataRow.createCell(0);
			dataCell.setCellValue(sheetName);
			href = (XSSFHyperlink) createHelper.createHyperlink(XSSFHyperlink.LINK_DOCUMENT);
			href.setAddress("#'" + sheetName + "'!A1");
			dataCell.setHyperlink(href);
			dataCell.setCellStyle(hlink_style);
			dataCell = dataRow.createCell(1);
			dataCell.setCellStyle(styles.get("string_data"));
			dataCell.setCellValue(sheetDetails.get(sheetName));
		}
		
		createApexPMDRuleSummaryTable(sheet,styles,hlink_style,pmdShtDetails);
		createCustomizationSummary(sheet, rowNumber+1, healthCheckParameter, styles);
		wb.setSheetOrder(sheet.getSheetName(), 0);
		sheet.setSelected(true);
	}

	private static void createApexPMDRuleSummaryTable(XSSFSheet sheet, Map<String, CellStyle> styles, CellStyle hlink_style, LinkedHashMap<String, Integer> pmdShtDetails) {
		int rowNumber = 3;
		Cell dataCell;
		XSSFHyperlink href;
		Row dataRow = sheet.getRow(2);
		Cell cell = dataRow.createCell(3);
		cell.setCellValue("Sheet Name ");
		cell.setCellStyle(styles.get("header"));
		cell = dataRow.createCell(4);
		cell.setCellValue("Issue Total Count");
		cell.setCellStyle(styles.get("header"));
		for (String sheetName : pmdShtDetails.keySet()) {
			dataRow = (null==sheet.getRow(rowNumber))?sheet.createRow(rowNumber):sheet.getRow(rowNumber);
			rowNumber++;
			dataCell = dataRow.createCell(3);
			dataCell.setCellValue(sheetName);
			href = (XSSFHyperlink) sheet.getWorkbook().getCreationHelper().createHyperlink(XSSFHyperlink.LINK_DOCUMENT);
			href.setAddress("#'" + sheetName + "'!A1");
			dataCell.setHyperlink(href);
			dataCell.setCellStyle(hlink_style);
			dataCell = dataRow.createCell(4);
			dataCell.setCellStyle(styles.get("string_data"));
			dataCell.setCellValue(pmdShtDetails.get(sheetName));
		}
		
	}

	private static void createTitleHeaderRow(Sheet sheet, Map<String, CellStyle> styles, int cols, String notes) {
		Row titleRow = sheet.createRow(0);
		titleRow.setHeightInPoints(35);
		for (int i = 0; i <= 2; i++) {
			titleRow.createCell(i).setCellStyle(styles.get("title"));
		}
		Cell titleCell = titleRow.getCell(0);
		titleCell.setCellValue(sheetNames.get(sheet.getSheetName()));
		sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$E$1"));
		createHeaderRow(sheet, styles, cols, notes);
	}

	private static void createNoteRow(Sheet sheet, Map<String, CellStyle> styles, String noteString) {
		Row row = sheet.createRow(1);
		Cell cell = row.createCell(0);
		cell.setCellValue(noteString);
		cell.setCellStyle(styles.get("string_data"));
		sheet.addMergedRegion(CellRangeAddress.valueOf("$A$2:$E$2"));
	}

	private static void createHeaderRow(Sheet sheet, Map<String, CellStyle> styles, int cols, String notes) {
		createNoteRow(sheet, styles, notes);
		Row row = sheet.createRow(2);
		Cell cell = row.createCell(0);
		cell.setCellValue("Component");
		cell.setCellStyle(styles.get("header"));
		cell = row.createCell(1);
		if (cols == 3) {
			cell.setCellValue(" line Number");
			cell.setCellStyle(styles.get("header"));
			cell = row.createCell(2);
		}
		cell.setCellValue("Issue Type");
		cell.setCellStyle(styles.get("header"));
		cell = row.createCell(row.getLastCellNum());
		cell.setCellValue("Modified By");
		cell.setCellStyle(styles.get("header"));
		cell = row.createCell(row.getLastCellNum());
		cell.setCellValue("Modified Date");
		cell.setCellStyle(styles.get("header"));

	}

	private static void createSummaryHeader(Sheet sheet, Map<String, CellStyle> styles) {
		Row row = sheet.createRow(2);
		Cell cell = row.createCell(0);
		cell.setCellValue("Sheet Name ");
		cell.setCellStyle(styles.get("header"));
		cell = row.createCell(1);
		cell.setCellValue("Issue Total Count");
		cell.setCellStyle(styles.get("header"));
	}



	private static Map<String, CellStyle> createStyles(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();

		CellStyle style;
		Font titleFont = wb.createFont();
		titleFont.setFontHeightInPoints((short) 14);
		titleFont.setFontName("Georgia");
		style = wb.createCellStyle();
		style.setFont(titleFont);
		style.setBorderBottom(CellStyle.BORDER_THICK);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		styles.put("title", style);

		Font itemFont = wb.createFont();
		itemFont.setFontHeightInPoints((short) 9);
		itemFont.setFontName("Georgia");
		itemFont.setColor(IndexedColors.WHITE.getIndex());
		itemFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(itemFont);
		style.setFillForegroundColor(IndexedColors.BLACK.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setWrapText(true);
		styles.put("header", style);

		itemFont = wb.createFont();
		itemFont.setFontHeightInPoints((short) 9);
		itemFont.setFontName("Georgia");
		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(itemFont);
		style.setWrapText(true);
		styles.put("string_data", style);

		itemFont = wb.createFont();
		itemFont.setFontHeightInPoints((short) 9);
		itemFont.setFontName("Georgia");
		style = wb.createCellStyle();
		style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		style.setBorderTop(XSSFCellStyle.BORDER_THIN);
		style.setBorderRight(XSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(itemFont);
		style.setWrapText(true);
		styles.put("border_data", style);

		itemFont = wb.createFont();
		itemFont.setFontHeightInPoints((short) 9);
		itemFont.setFontName("Cambria");
		itemFont.setColor(IndexedColors.GREY_40_PERCENT.getIndex());
		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(itemFont);
		style.setDataFormat(wb.createDataFormat().getFormat("mmm - dd - yyyy hh:MM:SS"));
		styles.put("timestamp", style);

		itemFont = wb.createFont();
		itemFont.setFontHeightInPoints((short) 9);
		itemFont.setFontName("Cambria");
		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(itemFont);
		styles.put("number_data", style);

		itemFont = wb.createFont();
		itemFont.setFontHeightInPoints((short) 9);
		itemFont.setFontName("Cambria");
		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(itemFont);
		style.setDataFormat(wb.createDataFormat().getFormat("0.000%"));
		styles.put("percent_data", style);

		itemFont = wb.createFont();
		itemFont.setFontHeightInPoints((short) 9);
		itemFont.setFontName("Georgia");
		itemFont.setColor(Font.COLOR_RED);
		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(itemFont);
		styles.put("red_string_data", style);

		itemFont = wb.createFont();
		itemFont.setFontHeightInPoints((short) 9);
		itemFont.setFontName("Cambria");
		itemFont.setColor(Font.COLOR_RED);
		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(itemFont);
		styles.put("red_number_data", style);

		itemFont = wb.createFont();
		itemFont.setFontHeightInPoints((short) 9);
		itemFont.setFontName("Cambria");
		itemFont.setColor(Font.COLOR_RED);
		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(itemFont);
		style.setDataFormat(wb.createDataFormat().getFormat("0.000%"));
		styles.put("red_percent_data", style);

		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_LEFT);
		itemFont = wb.createFont();
		itemFont.setFontHeightInPoints((short) 9);
		itemFont.setFontName("Cambria");
		itemFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style.setFont(itemFont);
		style.setBorderBottom(CellStyle.BORDER_THICK);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THICK);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		styles.put("total", style);

		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		itemFont = wb.createFont();
		itemFont.setFontHeightInPoints((short) 9);
		itemFont.setFontName("Cambria");
		itemFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style.setFont(itemFont);
		style.setBorderBottom(CellStyle.BORDER_THICK);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THICK);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		styles.put("total_number", style);

		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		itemFont = wb.createFont();
		itemFont.setFontHeightInPoints((short) 9);
		itemFont.setFontName("Cambria");
		itemFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style.setFont(itemFont);
		style.setBorderBottom(CellStyle.BORDER_THICK);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THICK);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setDataFormat(wb.createDataFormat().getFormat("0.000%"));
		styles.put("total_percent", style);

		return styles;
	}

}
