package com.emprovise.util.documents;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {
	
	private XSSFWorkbook workBook;
	private XSSFSheet currentWorkSheet;
	private Short rowHeight;
	private short rowCounter;
	private static final String NUMBER_FORMAT = "^-?\\d+$";
	private static final String DECIMAL_FORMAT = "^-?\\d+\\.\\d*$";
	private static final String DEFAULT_DATE_FORMAT = "dd-mmm-yyyy";
	
	public ExcelUtil(){
		workBook = new XSSFWorkbook();
	}
	
	public ExcelUtil(String sheetName){
		workBook = new XSSFWorkbook();
		currentWorkSheet = workBook.createSheet(sheetName);
		rowCounter=0;
	}
	
	public void setCenterHeader(String centerHeader, String fontName, String fontStyle, int fontSize) {
		Header header = currentWorkSheet.getHeader();
		header.setCenter(HSSFHeader.font(fontName, fontStyle) + HSSFHeader.fontSize((short) fontSize) + centerHeader);
	}

	public void setCurrentWorkSheet(String sheetName) {
		XSSFSheet workSheet = workBook.getSheet(sheetName);
		
		if(workSheet == null) {
			workSheet = workBook.createSheet(sheetName);
		}
		
		this.currentWorkSheet = workSheet;
	}
	
	public XSSFSheet getWorkSheet(String sheetName) {
		return this.workBook.getSheet(sheetName);
	}
	
	public XSSFSheet getCurrentWorkSheet() {
		return this.currentWorkSheet;
	}
	
	public void createWorksheet(String sheetName) {
		currentWorkSheet = workBook.createSheet(sheetName);
		rowCounter=0;
	}
	
	public int getNumberOfSheets() {
        return workBook.getNumberOfSheets();
    }
	
	public XSSFWorkbook getWorkBook() {
		return this.workBook;
	}
	
	public void setRowHeight(int height) {
		this.rowHeight = (short) height;
	}
	
	public short getCurrentRow() {
		return this.rowCounter;
	}
	
	public void setCurrentRow(int currentRow) {
		this.rowCounter = (short) currentRow;
	}
	
	public void autoResizeAllColumns(int totalColumns) {
		for (int i = 0; i < totalColumns; i++) {
			currentWorkSheet.autoSizeColumn(i);
	    }
	}
	
	public void autoResizeColumn(int column) {
		currentWorkSheet.autoSizeColumn(column);
	}

	public void formatColumn(int columnNumber, int totalRows, String format) {
		formatColumn(columnNumber, 0, totalRows, format);
	}
	
	public void formatColumn(int columnNumber, int startRow, int endRow, String format) {
		
		for (int i = startRow; i < endRow; i++) {
			Row row = currentWorkSheet.getRow(i);
			if(row != null && row.getCell(columnNumber) != null) {
				Cell cell = row.getCell(columnNumber);
				XSSFCellStyle newCellStyle = cloneCellStyle(cell.getCellStyle());
				newCellStyle.setDataFormat(createDataFormat(format));
				cell.setCellStyle(newCellStyle);
			}
	    }
	}
	
	public void matchAndFormatCells(int columnNumber, String regularExpression, CellStyle cellStyle) {

		for (Row row : currentWorkSheet) {
			if (row != null) {

				Cell cell = row.getCell(columnNumber);

				if (cell != null && cell.toString() != null && cell.toString().matches(regularExpression)) {
					cell.setCellStyle(cellStyle);
				}
			}
		}
	}
	
	public Cell getCell(int rowNumber, int columnNumber){
		Row row = currentWorkSheet.getRow(rowNumber);
		if(row != null) {
			return row.getCell(columnNumber);
		}
		return null;
	}
	
	public boolean formatCell(int rowNumber, int columnNumber, String format){
		Cell cell = getCell(rowNumber, columnNumber);

		if (cell != null && cell.toString() != null) {
			XSSFCellStyle newCellStyle = cloneCellStyle(cell.getCellStyle());
			newCellStyle.setDataFormat(createDataFormat(format));
			cell.setCellStyle(newCellStyle);
			return true;
		}
		return false;
	}

	public XSSFCellStyle cloneCellStyle(CellStyle cellStyle) {
		XSSFCellStyle newCellStyle = workBook.createCellStyle();
		newCellStyle.cloneStyleFrom(cellStyle);
		return newCellStyle;
	}
	
	public void writeToExcel(File file) throws Exception{
		FileOutputStream fileOut = new FileOutputStream(file);
		workBook.write(fileOut);
		fileOut.close();
	}
	
	public void CreateHeaderRow(List<String> headers, XSSFCellStyle style) {
		Row row = CreateNewRow();
		int i = 0;
		while (i < headers.size()) {
			Cell cell = row.createCell(i);
			cell.setCellValue(headers.get(i));
			cell.setCellStyle(style);
			i++;
		}
	}
	
	public Row CreateNewRow(){
		Row row = currentWorkSheet.createRow(rowCounter);
		
		if(rowHeight != null) {
			row.setHeight(rowHeight);
		}
		rowCounter++;
		return row;
	}
	
	public Row CreateNewRows(int rows){
		Row row = null;
		for (int i = 0; i < rows; i++) {
			row = CreateNewRow();
		}
		return row;
	}
	
	public <T> Cell CreateNewCell(int index, T value, XSSFCellStyle style){
		Row row = currentWorkSheet.getRow(rowCounter-1);
		if(row == null) {
			row = CreateNewRow();
		}
		return CreateNewCell(row, index, value, style);
	}
	
	public <T> Cell CreateNewCells(int start, int cells, T value,  XSSFCellStyle style) {
		Cell cell = null;
		for (int i = start; i < cells; i++) {
			cell = CreateNewCell(i, value, style);
		}
		return cell;
	}
	
	public <T> Cell CreateNewCell(Row row, int index, T value){
		return CreateNewCell(row, index, value, null);
	}
	
	public <T> Cell CreateNewCell(Row row, int index, T value, XSSFCellStyle style){
		Cell cell = row.createCell(index);
		short format = 0;
		
		if(value == null) {
			cell.setBlank();
			cell.setCellValue("");
		}
		else if(value instanceof Integer || value instanceof Long ) {
			cell.setCellValue(Long.parseLong(value.toString()));
			format = HSSFDataFormat.getBuiltinFormat("0");
		}
		else if(value instanceof Float || value instanceof Double ) {
			cell.setCellValue(Double.parseDouble(value.toString()));
			format = HSSFDataFormat.getBuiltinFormat("0.00");
		}
		else if(value instanceof Boolean ){
			cell.setCellValue((Boolean)value);
		}
		else if(value instanceof Date){
			cell.setCellValue((Date) value);
			format = createDataFormat(DEFAULT_DATE_FORMAT);
		}
		else {
			if(value.toString().matches(NUMBER_FORMAT)){
				cell.setCellValue(Long.parseLong(value.toString()));
				format = HSSFDataFormat.getBuiltinFormat("0");
			}else if(value.toString().matches(DECIMAL_FORMAT)){
				cell.setCellValue(Double.parseDouble(value.toString()));
				
			}else if(Boolean.TRUE.toString().equalsIgnoreCase(value.toString()) || Boolean.FALSE.toString().equalsIgnoreCase(value.toString())){
				cell.setCellValue(Boolean.parseBoolean(value.toString()));

			}else{
				cell.setCellValue(value.toString());
			}
		}
		
		if(style != null) {
			if(style.getDataFormat() == 0 && format != 0) {
				style.setDataFormat(format);
			}
			cell.setCellStyle(style);
		}
		
		return cell;
	}
	
	public Row CreateCriteriaRow(String cell0Value, String cell1Value, XSSFCellStyle styleCell0, XSSFCellStyle styleCell1) {
		return CreateCriteriaRow(cell0Value, cell1Value, styleCell0, styleCell1, 0);
	}
	
	public Row CreateCriteriaRow(String cell0Value, String cell1Value, XSSFCellStyle styleCell0, XSSFCellStyle styleCell1, int columnNumber) {
		Row row = CreateNewRow();
		CreateNewCell(row, columnNumber, cell0Value, styleCell0);
		CreateNewCell(row, columnNumber+1, cell1Value, styleCell1);
		return row;
	}
	
	public void SetColumnWidth(int headerColumns) {
		for (int columnIndex = 0; columnIndex < headerColumns; columnIndex++) {
			if (columnIndex == 0) {
				currentWorkSheet.setColumnWidth(columnIndex, 5200);

			} else if (columnIndex == 1) {
				int cell1Width = 0;
				int length= 0;
				for (int rowIndex = 0; rowIndex < currentWorkSheet.getPhysicalNumberOfRows(); rowIndex++) {
					Row row = currentWorkSheet.getRow(rowIndex);

					if (row != null && row.getCell(1) != null) {
						if(row.getCell(1).getCellType() == CellType.NUMERIC){
							length = String.valueOf(row.getCell(1).getNumericCellValue()).length();
						} else {
							length = row.getCell(1).getStringCellValue().length();
						}
						
						int columnWidth = (length * 3) * 85;
						if (rowIndex == 0 || columnWidth > cell1Width) {
							cell1Width = columnWidth;
						}
					}
				}
				if (cell1Width < 20000) {
					currentWorkSheet.setColumnWidth(columnIndex, cell1Width);
				} else {
					currentWorkSheet.setColumnWidth(columnIndex, 16000);
				}
			} else if (columnIndex == 2) {
				currentWorkSheet.setColumnWidth(columnIndex, 3500);
			} else {
				currentWorkSheet.setColumnWidth(columnIndex, 4800);
			}
		}
	}
	
	public void SetDefaultColumnWidth(int headerColumns){
		for(int columnIndex = 0; columnIndex < headerColumns; columnIndex++){
			currentWorkSheet.setColumnWidth(columnIndex, 5500);
		}
	}
	
	public Cell mergeRowCells(Row row, String cellValue, int fromCell, int toCell, XSSFCellStyle style) {
		Cell cell = row.createCell((short) fromCell);
		cell.setCellValue(cellValue);
		cell.setCellStyle(style);
		currentWorkSheet.addMergedRegion(new CellRangeAddress(
				row.getRowNum(), //first row (0-based)
				row.getRowNum(), //last row  (0-based)
		        fromCell, //first column (0-based)
		        toCell  //last column  (0-based)
		));
		return cell;
	}
	
	public Cell createSumFormulaCell(int rowNumber, int cellNumber, int sumColumnNumber, int fromRowNumber, int toRowNumber, XSSFCellStyle style) {
		Row row = currentWorkSheet.getRow(rowNumber);
		if(row == null || fromRowNumber > toRowNumber) {
			return null;
		}
		
		Cell cell = row.createCell(cellNumber);
	    cell.setCellFormula(CellType.FORMULA.toString());
	    cell.setCellStyle(style);
	    
	    String fromCellAddress = getCellAddress(fromRowNumber, sumColumnNumber);
	    String toCellAddress = getCellAddress(toRowNumber, sumColumnNumber);
	    
	    if(fromCellAddress != null && toCellAddress != null) {
		    cell.setCellFormula(String.format("SUM(%s:%s)", fromCellAddress, toCellAddress));
	    }
		return cell;
	}
	
	public String getCellAddress(int rowNumber, int cellNumber) {
		Row row = currentWorkSheet.getRow(rowNumber);
		if(row != null && row.getCell(cellNumber) != null) {
			Cell cell = row.getCell(cellNumber);
			CellReference cellReference = new CellReference(cell);
			return cellReference.formatAsString();
		}
		return null;
	}
	
	/**
	 * Exports the list of objects as a table in excel with a single generic style for all data columns.
	 * 
	 * @param exportDataList
	 * 			List of generic objects to export into excel as a table.
	 * @param propertiesToInclude
	 * 			Array of class properties which are to be exported as data columns of the table.  
	 * @param style
	 * 			Global style for all the data columns.
	 * @throws Exception
	 */
	public void exportDataToCells(List<?> exportDataList, String[] propertiesToInclude, XSSFCellStyle style) throws Exception{

		if (exportDataList == null || exportDataList.isEmpty()) {
			return;
		}
		
		// Build list of fields
		Object objDummy = exportDataList.get(0);
		Field[] fields = findFieldsByPropertyNames(objDummy.getClass(), propertiesToInclude);
		
		for (Object dataObject : exportDataList) {

			Row row = CreateNewRow();
			int cellnum = 0;
			
			for (Field field : fields) {
				if(field != null) {
					CreateNewCell(row, cellnum++, field.getType().cast(field.get(dataObject)), cloneCellStyle(style));
				}
			}
		}
	}
	
	/**
	 * Exports the list of objects as a table in excel with custom styles for each data column
	 *  
	 * @param exportDataList
	 * 			List of generic objects to export into excel as a table.
	 * @param propertiesToInclude
	 * 			Map with class property to be exported as data column of the table as key while the value is the custom style for each column.  
	 * @throws Exception
	 */
	public void exportDataToCells(List<?> exportDataList, String[] propertiesToInclude, XSSFCellStyle[] styles) throws Exception{

		if (exportDataList == null || propertiesToInclude == null || exportDataList.isEmpty() || propertiesToInclude.length == 0 || styles == null) {
			return;
		}
		
		// Build list of fields
		Object objDummy = exportDataList.get(0);
		Field[] fields = findFieldsByPropertyNames(objDummy.getClass(), propertiesToInclude);
		Map<String, XSSFCellStyle> propertyStyleMap = new HashMap<String, XSSFCellStyle>();
		
		for (int i = 0; i < propertiesToInclude.length; i++) {
			XSSFCellStyle style = null;
			if(i < styles.length) {
				style = styles[i];
			}
			propertyStyleMap.put(propertiesToInclude[i], style);
		}
		
		for (Object dataObject : exportDataList) {
			
			Row row = CreateNewRow();
			int cellnum = 0;
			
			for (Field field : fields) {
				if(field != null) {
					XSSFCellStyle style = propertyStyleMap.get(field.getName());
					CreateNewCell(row, cellnum++, field.getType().cast(field.get(dataObject)), cloneCellStyle(style));
				}
			}
		}
	}

	private Field[] findFieldsByPropertyNames(Class<?> targetClass, String[] properties) throws Exception {
		int i = 0;
		Field[] fields = new Field[properties.length];

		for (String propertyName : properties) {
			for (Field field : targetClass.getDeclaredFields()) {
				if (propertyName.equals(field.getName())) {
					field.setAccessible(true);
					fields[i++] = field;
				}
			}
		}
		return fields;
	}
	
	public XSSFCellStyle createXSSFCellStyle(Font font, short alignment, boolean wrap, String dataFormat) {
		XSSFCellStyle style = workBook.createCellStyle();
		
		if(font != null) {
			style.setFont(font);
		}
		
		if(alignment == HorizontalAlignment.LEFT.getCode() || alignment == HorizontalAlignment.CENTER.getCode() || alignment == HorizontalAlignment.RIGHT.getCode() ||
		   alignment == HorizontalAlignment.FILL.getCode() || alignment == HorizontalAlignment.JUSTIFY.getCode() || alignment == HorizontalAlignment.CENTER_SELECTION.getCode()) {
			style.setAlignment(HorizontalAlignment.forInt(alignment));
		}
		style.setWrapText(wrap);
		
		if(dataFormat != null) {
			style.setDataFormat(createDataFormat(dataFormat));
		}
		return style;
	}
	
	public Font createFont(String fontName, boolean isBold, short height, IndexedColors color, byte underline){
		Font font = workBook.createFont();
		
		if(height > 0 && height < 100) {
			font.setFontHeightInPoints(height);
		}
		
		if(fontName != null) {
			font.setFontName(fontName);
		}
		
		font.setBold(isBold);

		if(underline == Font.U_SINGLE || underline == Font.U_DOUBLE || underline == Font.U_SINGLE_ACCOUNTING  || underline == Font.U_DOUBLE_ACCOUNTING) {
			font.setUnderline(underline);
		}
		
		font.setColor(color.getIndex());
	    return font;
	}
	
	public short createDataFormat(String format) {
		XSSFDataFormat dataFormat = workBook.createDataFormat();
		// XSSFDataFormat dataFormat = workBook.getCreationHelper().createDataFormat();
		return dataFormat.getFormat(format);
	}
	
	public Hyperlink createHyperLink(String url) {
		CreationHelper createHelper = workBook.getCreationHelper();
		Hyperlink urlLink = createHelper.createHyperlink(HyperlinkType.URL);
		urlLink.setAddress(url);
		return urlLink;
	}

	public void setCellBorderStyle(XSSFCellStyle style, short topStyle, IndexedColors topColor, short bottomStyle, IndexedColors bottomColor, 
								                        short leftStyle, IndexedColors leftColor, short rightStyle, IndexedColors rightColor) {
		if(style == null) {
			return;
		}



		if(topStyle > 0 && topStyle < 14) {
			style.setBorderTop(BorderStyle.valueOf(topStyle));
		}
		if(bottomStyle > 0 && bottomStyle < 14) {
			style.setBorderBottom(BorderStyle.valueOf(bottomStyle));
		}
		if(leftStyle > 0 && leftStyle < 14) {
			style.setBorderLeft(BorderStyle.valueOf(leftStyle));
		}
		if(rightStyle > 0 && rightStyle < 14) {
			style.setBorderRight(BorderStyle.valueOf(rightStyle));
		}
		if (topColor != null) {
			style.setBottomBorderColor(topColor.getIndex());
		}
		if (bottomColor != null) {
			style.setLeftBorderColor(bottomColor.getIndex());
		}
		if (leftColor != null) {
			style.setRightBorderColor(leftColor.getIndex());
		}
		if (rightColor != null) {
			style.setTopBorderColor(rightColor.getIndex());
		}
	}
}
