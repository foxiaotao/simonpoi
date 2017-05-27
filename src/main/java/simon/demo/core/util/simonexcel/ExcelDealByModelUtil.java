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
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ExcelAbstract setExcelInputStream(InputStream inputStream) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExcelAbstract setDateFormat(String format) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExcelAbstract setSheetName(String sheetName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExcelAbstract setImportStartRow(int startRow) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExcelAbstract setExcelFilePathIn(String excelFilePathIn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Workbook createWorkbookByFilePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExcelAbstract setOutFilePath(String outFilePath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createExcelFileOnDisk() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void formatContentCell(Cell cell, int rowIndex, int colIndex, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void formatHeadCell(Cell cell, int rowIndex, int colIndex) {
		// TODO Auto-generated method stub
		
	}
	

}
