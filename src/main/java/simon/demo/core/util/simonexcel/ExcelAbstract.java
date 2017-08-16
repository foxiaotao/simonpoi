package simon.demo.core.util.simonexcel;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.Assert;

/**
 * poi excel报表导入导出工具类
 * @author snutao
 *
 */
public abstract class ExcelAbstract  implements Closeable {

	protected static final Logger logger  = Logger.getLogger(ExcelAbstract.class);
    
    protected  Workbook workbook;
    /**
     * 时日类型的数据默认格式化方式
     */
    protected  DateFormat  dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected  int      importStartRow = 0;
    protected  String   sheetName = "Sheet1";
    
   /* protected  String   excelFilePathIn;//导出路径（传统 磁盘的方式 out了）
    protected  String   outFilePath;//导出路径（传统 磁盘的方式 out了）
    */
    protected  Sheet sheet;
    protected  CellStyle[] cellStyles;
	
    protected String modelPath = ""; //Excel模板路径（模板导出）
    /**第一层key(String):sheet名称,value(Map<Integer,Map<Integer,String>>):该sheet所有数据
	 * 第二层key(Integer):编辑行的行号,value(Map<Integer,String>):该行所有列所需数据
	 * 第三层key(Integer):一行中编辑列的序号,value(String):cell的值
	 */
	protected Map<String,Map<Integer,Map<Integer,String>>> fieldData = null; //excel数据内容,一个sheet的内容,外层map行数据,内层map,一行的每一列数据,key为列的序号
	/**  	list 形式
	 * 替换模板中的#_#  顺序一行一行顺序替换
	 */
	protected Map<String,List<String>> sheetData;
	protected int allRowCount = 0;
	protected int allCellCount = 0;
	
	
	   /**
     * 设置excel 输入流（import）
     * @param inputStream
     * @return
     */
	public ExcelAbstract setExcelInputStream(InputStream inputStream){
		try {
			this.workbook = WorkbookFactory.create(inputStream);
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			logger.error(e.getMessage());
		}
        return this;
	};
	/**
	 * 设置date格式
	 * @param format
	 * @return
	 */
	public ExcelAbstract setDateFormat(String dateFormat){
		this.dateFormat = new SimpleDateFormat(dateFormat);;
		return this;
	};
	/**
	 * 设置sheet名称
	 * @param sheetName
	 * @return
	 */
	public ExcelAbstract setSheetName(String sheetName){
		this.sheetName = sheetName;
		return this;
	};
	/**
	 * 设置 读 excel 开始的行数，默认值=1
	 * @param startRow
	 * @return
	 */
	public ExcelAbstract setImportStartRow(int startRow){
		if (startRow < 1) {
            throw new RuntimeException("最小为0");
        }
		this.importStartRow = startRow;
		return this;
	};
	/**
	 * 设置 excel 导入磁盘路径
	 * @param excelFilePathIn
	 * @return
	 */
	/*public ExcelAbstract setExcelFilePathIn(String excelFilePathIn){
		this.excelFilePathIn = excelFilePathIn;
		return this;
	};*/
	/**
	 * 通过 excelFilePathin 的路径来创建excel 源，获取workbook
	 * @return
	 */
	public ExcelAbstract createWorkbookByFilePath(String inFilePath){
		File file = new File(inFilePath);
        try {
			if (!file.exists()) {
			    logger.error("文件:{} 不存在！创建此文件！"+ inFilePath);
			    if (!file.createNewFile()) {
			        throw new IOException("文件创建失败");
			    }
			    this.workbook = new XSSFWorkbook();
			} else {
			    this.workbook = WorkbookFactory.create(file);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return this;
	};
	/**
	 * 设置 保存excel到磁盘的路径
	 * @param outFilePath
	 * @return
	 */
//	public ExcelAbstract setOutFilePath(String outFilePath){
//		this.outFilePath = outFilePath;
//		return this;
//	};
	/**
	 * 将生成的excel 保存到磁盘
	 */
	public ExcelAbstract createExcelFileOnDisk(String outFilePath){
		FileOutputStream fileOutputStream = null;
    	File file = new File(outFilePath);
        try {
			if (!file.exists()) {
			    if (!file.createNewFile()) {
			        throw new IOException("文件创建失败");
			    }
			}
			fileOutputStream = new FileOutputStream(file);
			workbook.write(fileOutputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.closeWorkbook();
		}
        return this;
	};
	
	/**设置模板路径
	 * @param modelPath
	 * @return
	 */
	public ExcelAbstract setModelPath(String modelPath){
		Assert.notNull(modelPath, "路径不能为空!");
		this.modelPath = modelPath;
		return this;
	}
	/**
	 * @param rowCount  总行数
	 * @param cellCount  总列数
	 * @return
	 */
	public ExcelAbstract setRowCellCount(int rowCount,int cellCount){
		this.allRowCount = rowCount;
		this.allCellCount = cellCount;
		return this;
	}
	/**
	 * @param rowCount  总行数
	 * @param cellCount  总列数
	 * @return
	 */
	public ExcelAbstract setModelRowCellCount(int rowCount,int cellCount){
		this.allRowCount = rowCount;
		this.allCellCount = cellCount;
		return this;
	}
	
	/**
	 * 根据map 写数据，map<sheet,map<row,map<cell,value>>>
	 * @return HSSFWorkbook
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	public ExcelAbstract writeDataByMap(Map<String,Map<Integer,Map<Integer,String>>> fieldData) {
		//拿到模板工作簿(模板只能xls文件,不能是xlsx文件,如果要支持office2007以上需要poi-ooxxml.jar包,使用XSSFWorkbook类)
		
		this.fieldData = fieldData;
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(modelPath);
			workbook = new HSSFWorkbook(fis);
			Sheet sheet = null;
			Row row = null;
			Cell cell = null;
			
			String sheetName = null;//sheet的名称
			int rowIndex = 0;//编辑行号
			int cellIndex = 0;//编辑列号
			String cellValue = null;//编辑列的值
			//写入数据
			for (Map.Entry<String, Map<Integer,Map<Integer,String>>> sheetMap : fieldData.entrySet()) {
				//sheet
				sheetName = sheetMap.getKey();
//				Sheet sheet2 = workbook.getSheet(sheetName);
				sheet = workbook.getSheet(sheetName);//得到指定sheet
				if(sheet!=null){
					for (Map.Entry<Integer, Map<Integer,String>> rowMap : sheetMap.getValue().entrySet()) {
						//得到行对象
						rowIndex = rowMap.getKey();
						row = sheet.getRow(rowIndex);
						if(row==null){
							//如果这个不存在,说明这行是空对象,那么创建对象
							row = sheet.createRow(rowIndex);
						}
						for (Map.Entry<Integer, String> cellMap : rowMap.getValue().entrySet()) {
							cellIndex = cellMap.getKey();//单元格列序号
							cellValue = cellMap.getValue();//单元格值
							//获取单元格
							cell = row.getCell(cellIndex);
							if(cell==null){
								//如果获取为空,说明这列是空对象,那么创建对象
								//log.error("excel POI操作" + sheetName + "第" + rowIndex + "行,第" + cellIndex + "列为空单元格,传入参数错误");
								cell = row.createCell(cellIndex);
							}
							cell.setCellValue(cellValue);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return this;
	}
	/**
	 * 替换模板中的#_#  顺序一行一行顺序替换
	 * @return HSSFWorkbook
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	public ExcelAbstract writeDataByList(Map<String,List<String>> mapdata) {
		//拿到模板工作簿(模板只能xls文件,不能是xlsx文件,如果要支持office2007以上需要poi-ooxxml.jar包,使用XSSFWorkbook类)
		
		this.sheetData = mapdata;
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(modelPath);
			workbook = new HSSFWorkbook(fis);
			Sheet sheet = null;
			Row row = null;
			Cell cell = null;
			
			String sheetName = null;//sheet的名称
			String cellValue = null;//编辑列的值
			//写入数据
			for (Map.Entry<String, List<String>> sheetData : mapdata.entrySet()) {
				//sheet
				sheetName = sheetData.getKey();
				sheet = workbook.getSheet(sheetName);//得到指定sheet
				//all rowl count
				int rowNum=0;	
				//all cell count
				int columnNum = 0;
				if(sheet!=null){
					rowNum=Math.max(allRowCount, sheet.getLastRowNum());
					Row hssfRow = sheet.getRow(0);
					if(hssfRow!=null){
						columnNum=Math.max(allCellCount, hssfRow.getLastCellNum());
					}
					//写数据
					//从上次替换处开始继续遍历
					int tem_j = 0;
					int tem_k = 0;
					for (int i = 0; sheetData.getValue()!=null && i < sheetData.getValue().size(); i++) {
						cellValue = sheetData.getValue().get(i);
						boolean flag = true;
						for (int j = tem_j; flag && j <= rowNum; j++) {
							row = sheet.getRow(j);
							for (int k = tem_k;flag && row!=null && k <= columnNum; k++) {
								cell = row.getCell(k);
								//HSSFCell.CELL_TYPE_STRING == cell.getCellType() String类型，需要替换的内容是#——#，确定是String类型
								if(cell!=null && HSSFCell.CELL_TYPE_STRING == cell.getCellType() && "#_#".equals(cell.getStringCellValue())){
									cell.setCellValue(cellValue);
									flag = false;//跳出 j ，k 循环，并记录j,k的值
									tem_j = j;
									tem_k = k;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return this;
	}
	
	
    /**
	 * 功能描述：设置EXCEL表格基本样式
	 */
    protected void setGeneralProperty(CellStyle style) {
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		style.setAlignment(HorizontalAlignment.CENTER);
	}
    /**
     * 功能描述：设置EXCEL表格基本样式 蓝色样式边框
     */
    protected void setGeneralProperty2(CellStyle style) {
    	style.setBorderBottom(BorderStyle.THIN);
    	style.setBottomBorderColor(HSSFColor.BLUE.index);
    	style.setBorderLeft(BorderStyle.THIN);
    	style.setLeftBorderColor(HSSFColor.BLUE.index);
    	style.setBorderRight(BorderStyle.THIN);
    	style.setRightBorderColor(HSSFColor.BLUE.index);
    	style.setBorderTop(BorderStyle.THIN);
    	style.setTopBorderColor(HSSFColor.BLUE.index);
    	style.setAlignment(HorizontalAlignment.CENTER);
    }
    
    /**
     * @return
     */
    public byte[] toByteArray() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            workbook.write(out);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally{
        	try {
				workbook.close();
				out.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
        }
        return out.toByteArray();
    }
 
    public void closeWorkbook(){
    	try {
			workbook.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
    }
    
    public Workbook getWorkbook() {
		return workbook;
	}
	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}
	public ExcelAbstract setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
		return this;
	}
    
    protected void formatContentCell(Cell cell, int rowIndex, int colIndex, Object value){
    	if(value == null) {
            cell.setCellValue("");
        }else {
            // 自适应宽度
            int cellLength = value.toString().getBytes().length;
            // excel有列宽限制的 255字符
            if (cellLength > 125) {
            	cellLength = 125;
            } else if (cellLength < 10) {
            	cellLength = 10;
            }
            sheet.setColumnWidth(colIndex, cellLength * 2 * 256);
        }
    	cell.setCellStyle(cellStyles[colIndex]);
    };

    protected void formatHeadCell(Cell cell, int rowIndex, int colIndex){
    	sheet.setColumnWidth(colIndex, 10 * 2 * 256);
    	setGeneralProperty(cellStyles[colIndex]);
    	cell.setCellStyle(cellStyles[colIndex]);
    };
	
	

}
