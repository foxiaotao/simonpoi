package simon.demo.core.util.simonexcel;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.Assert;

public class ExcelDealByModelUtil extends ExcelAbstract {


	
	/**封装一行row多有cell的数据到map中
	 * @param cellNos:一个cell所有需要写值的cellNo
	 * @param cellValues:每个cell对应的value
	 * @return	Map<Integer,String> cellMap:一个封装了cell数据的map
	 */
	public Map<Integer,String> fillCellMapData(Integer[] cellNos,String[] cellValues){
		Map<Integer, String> cellMapData = new HashMap<Integer, String>();	//实例化cellMap,新的对象,封装第n行信息
		if(cellNos.length>0 && cellValues.length== cellNos.length){
			for (int i = 0; i < cellNos.length; i++) {
				cellMapData.put(cellNos[i], cellValues[i]);
			}
		}
		return cellMapData;
	}


	@Override
	public ExcelDealByModelUtil setExcelInputStream(InputStream inputStream) {
		super.setExcelInputStream(inputStream);
		return this;
	}

	@Override
	public ExcelDealByModelUtil setDateFormat(String format) {
		super.setDateFormat(dateFormat);
		return this;
	}

	@Override
	public ExcelDealByModelUtil setSheetName(String sheetName) {
		super.setSheetName(sheetName);
		return this;
	}

	@Override
	public ExcelDealByModelUtil setImportStartRow(int startRow) {
		super.setImportStartRow(startRow);
		return this;
	}


	@Override
	public ExcelDealByModelUtil createWorkbookByFilePath(String inFilePath) {
		super.createWorkbookByFilePath(inFilePath);
		return this;
	}

	@Override
	public ExcelDealByModelUtil createExcelFileOnDisk(String path,String fileName) {
		super.createExcelFileOnDisk(path,fileName);
		return this;
	}

	@Override
	protected void formatContentCell(Cell cell, int rowIndex, int colIndex, Object value) {
		super.formatContentCell(cell, rowIndex, colIndex, value);
	}

	@Override
	protected void formatHeadCell(Cell cell, int rowIndex, int colIndex) {
		super.formatHeadCell(cell, rowIndex, colIndex);
	}
	
	@Override
	public void close() throws IOException {}

}
