package org.excel2db.write.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.log4j.Logger;
import org.excel2db.write.util.TypeEnum;

/**
 * 读取excel
 * 
 * @author zhaohui
 * 
 */
public class ExcelParse {

	private final static Logger logger = Logger.getLogger(ExcelParse.class);

	/** 数据开始行 **/
	private static int DATA_STAR_ROW = 5;
	/** 类型的行 **/
	private static int TYPE_ROW = 2;
	/** 名字的行 **/
	private static int NAME_ROW = 0;

	/** excel文件路径 **/
	private String filePath;

	/** 字段名称,字段类型,数据 **/
	private Map<String, List<String>> columnNameMap = new HashMap<String, List<String>>();
	private Map<String, List<TypeEnum>> columnTypeMap = new HashMap<String, List<TypeEnum>>();
	private Map<String, List<List<String>>> dataMap = new HashMap<String, List<List<String>>>();

	public ExcelParse(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * 读取excel
	 */
	public void readExcel() {
		logger.info("start read excel....");
		Workbook book = null;
		try {
			book = Workbook.getWorkbook(new File(filePath));

			Sheet sheets[] = book.getSheets();
			for (Sheet sheet : sheets) {
				int rows = sheet.getRows();
				int columns = sheet.getColumns();

				List<Integer> columnList = new ArrayList<Integer>();
				List<TypeEnum> typeList = new ArrayList<TypeEnum>();
				List<String> nameList = new ArrayList<String>();

				for (int column = 0; column < columns; column++) {
					String type = getValue(sheet, column, TYPE_ROW);
					TypeEnum typeEnum = TypeEnum.type(type);
					if (typeEnum != null) {
						typeList.add(typeEnum);
						nameList.add(getValue(sheet, column, NAME_ROW));
						columnList.add(column);
					}
				}

				columnNameMap.put(sheet.getName(), nameList);
				columnTypeMap.put(sheet.getName(), typeList);

				List<List<String>> dataList = new ArrayList<List<String>>();
				for (int row = DATA_STAR_ROW; row < rows; row++) {
					List<String> list = new ArrayList<String>();
					for (int index = 0; index < columnList.size(); index++) {
						int column = columnList.get(index);
						TypeEnum typeEnum = typeList.get(index);

						String content = getValue(sheet, column, row);
						String value = getInitValue(content);

						checkValue(typeEnum, value, row, column);
						list.add(value);
					}
					dataList.add(list);
				}

				dataMap.put(sheet.getName(), dataList);
			}
			logger.info("end read excel....");
		} catch (Exception e) {
			logger.error("readExcel error", e);
		} finally {
			if (book != null) {
				book.close();
			}
		}
	}

	/**
	 * 获取单元格中的数值
	 * 
	 * @param sheet
	 * @param column
	 * @param row
	 * @return
	 */
	private String getValue(Sheet sheet, int column, int row) {
		return sheet.getCell(column, row).getContents().trim();
	}

	/**
	 * 对excle中单元格数组就行检查
	 * 
	 * @param typeEnum
	 * @param value
	 * @param row
	 * @param column
	 */
	private void checkValue(TypeEnum typeEnum, String value, int row, int column) {
		try {
			if (typeEnum == TypeEnum.INT) {
				Integer.valueOf(value);
			} else if (typeEnum == TypeEnum.FLOAT) {
				Float.valueOf(value);
			} else if (typeEnum == TypeEnum.LONG) {
				Long.valueOf(value);
			} else if (typeEnum == TypeEnum.DOUBLE) {
				Double.valueOf(value);
			}
		} catch (Exception e) {
			throw new RuntimeException("numberFormatException:row=" + (row + 1)
					+ ",column=" + (column + 1));
		}
	}

	/**
	 * 获取非string类型的初始值
	 * 
	 * @param value
	 * @return
	 */
	private String getInitValue(String value) {
		if (value.equals("")) {
			return "0";
		}
		return value;
	}

	public Map<String, List<String>> getColumnNameMap() {
		return columnNameMap;
	}

	public Map<String, List<TypeEnum>> getColumnTypeMap() {
		return columnTypeMap;
	}

	public Map<String, List<List<String>>> getDataMap() {
		return dataMap;
	}

}
