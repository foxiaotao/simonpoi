package simon.demo.core.util.simonexcel;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 该类用于帮助解析和填充EXCEL,目前没有分页的实现，所以数据量太大可能导致类存溢出
 * 使用凡是如下，
 * 先定义一个javabean
 * 然后做标题和bean的属性的map映射，
 *      LinkedHashMap<String, String> pm = new LinkedHashMap<String, String>();
		pm.put("yearMonth", "月份");
		pm.put("multIplier", "中间价");
		pm.put("currency", "目标币种"); 
 *    定义属性值得映射MAP
 *    Map<String,  Map<String, String>> valuesMap = new HashMap<String, Map<String, String>>();
 *    
 *    然后初始化一个帮助类
 *    ExcelUtil<ExchangeRate> test = new ExcelUtil<ExchangeRate>(pm, sheet, ExchangeRate.class);
	       设  置 属性映射
	  test.setValueMapping(valuesMap);
	  getEntitiesHasNoHeader函数在你直接指导哪一行是数据行的开始，你可以直接闯入进行数据解析，但是这个时候pm的顺序一定要和标题行吻合
	      否者请使用getEntities(-1),它会自动找到合标题行吻合的行并从该行一下开始读取数据行
	  List<ExchangeRate> list = test.getEntitiesHasNoHeader(4);  
 * @author 孙涛
 *
 * @param <T>
 */
public class ExcelByMapUtil extends ExcelAbstract{
	private LinkedHashMap<String, String> propertyMapping;
	private Map<String, Short> map;
	private PropertyDescriptor[] propertyDescriptors;
	private Map<String, ValueConvert> valueMapping = new HashMap<String, ValueConvert>();
	
	/**
	 * @param propertyMapping LinkedHashMap：对象属性和excel表头对应，对象属性为key，表头中文名为value，map.put的顺序即为excel列的顺序
	 * @return
	 */
	public ExcelByMapUtil setPropertyMapping(LinkedHashMap<String, String> propertyMapping){
		this.propertyMapping = propertyMapping;
		this.map = new HashMap<String, Short>();
		Short i = 0;
		for(String e : propertyMapping.keySet()){
			this.map.put(e, i++);
		}
		return this;
	}
	
	
	/** 导入入口方法1
	 * 	有标题行(自动判断开始行，不用设定开始行)，获取表格数据
	 * @param headIndex
	 * @return
	 */
	public <T> List<T> getEntities(Class<T> c){
		List<T> list = new LinkedList<T>();
		int num = workbook.getNumberOfSheets();	
		//迭代excel中的每一个sheet
		for(int i = 0; i < num; i++){
			sheet = workbook.getSheetAt(i);
			
			int rows = sheet.getPhysicalNumberOfRows();
			Row headRow = null;
			int headIndex = getHeadIndex();
			if(headIndex != -1){
				headRow = sheet.getRow(headIndex);
			}
			//System.out.println("sheet.getRow(headIndex)="+sheet.getRow(headIndex));
			Short[] st = getStatus(headRow);
			for(int j = headIndex + 1 ; j < rows; j++){
				list.add(getEntity(sheet.getRow(j), st , c));
			}
			
		}
		
		return list;
	}
	/** 导入入口方法1
	 *  直接数据，没有标题行，所以没有对应关系，linkMap 的顺序 就是excel 列的顺序
	 */
	public <T> List<T> getEntitiesHasNoHeader(int startIndex,Class<T> c){
		List<T> list = new LinkedList<T>();
		int num = workbook.getNumberOfSheets();	
		//迭代excel中的每一个sheet
		for(int i = 0; i < num; i++){
			sheet = workbook.getSheetAt(i);
			int rows = sheet.getPhysicalNumberOfRows();
			//System.out.println("sheet.getRow(headIndex)="+sheet.getRow(headIndex));
			Short[] st = getStatus(null);
			for(int j = startIndex ; j < rows; j++){
				list.add(getEntity(sheet.getRow(j), st, c));
			}
		}
		return list;
	}
	
	/**
	 * 获取中文表头的index 第几行
	 * @return
	 */
	private int getHeadIndex(){
		int rows = sheet.getPhysicalNumberOfRows();
		int like = 0;
		for(int i = 0; i < rows; i++){
			Row row = sheet.getRow(i);
			int cells = row.getPhysicalNumberOfCells();
			for(int j = 0; j < cells; j++){
				Cell cell = row.getCell(j);
				//System.out.println("CellType:" + cell.getCellType());
				if(cell != null && cell.getCellType() == HSSFCell.CELL_TYPE_STRING && propertyMapping.values().contains(cell.getStringCellValue())){
					like++;
				}
				if(like > map.size()/2){
					return i;
				}			
			}
		}
		return -1;
	}
	
	/**
	 * 列序号和map key顺序对应关系，如果map的顺序就是excel列的顺序，那么这个status[0]=0,status[1]=1...status[n]=n
	 * @param row
	 * @return
	 */
	private Short[] getStatus(Row row){
		Short[] status = new Short[map.size()];
		//System.out.println("map.size():" + map.size());
		if(row == null){
			for(String key : map.keySet()){
				status[map.get(key)] = map.get(key); 
			}
		}else{
			int cells = row.getPhysicalNumberOfCells();
			for(int j = 0; j < cells; j++){
				//System.out.println("row.getCell[" + j + "]=" + row.getCell(j));
				if(row.getCell(j) != null){
					//System.out.println("row.getCell(" + j + ").getCellType=" + row.getCell(j).getCellType());
					//System.out.println("HSSFCell.CELL_TYPE_STRING=" + HSSFCell.CELL_TYPE_STRING);
					switch (row.getCell(j).getCellType()){
					case HSSFCell.CELL_TYPE_STRING :
						for(String key : propertyMapping.keySet()){
							//System.out.println("propertyMapping.get(" + key + ")=" +propertyMapping.get(key));
							//System.out.println("row.getCell("+j+").getStringCellValue()"+row.getCell(j).getStringCellValue());
							if(propertyMapping.get(key).equals(row.getCell(j).getStringCellValue())){
								status[map.get(key)] = (short) j;
								/*for (int i = 0; i < status.length; i++){
									System.out.println("status["+i+"]="+status[i]);
								}*/
							}
						}
						break;
					default:
						break;		
					}
				}
			}
		}
		return status;
	}
	
	/**
	 * 获取某一行的数据
	 */
	private <T> T getEntity(Row row, Short[] status,Class<T> c){ 
		setPropertyDescriptors(c);
		T t = null;
			try {
				t = c.newInstance();
				for(int i = 0; i < propertyDescriptors.length; i++){
					try{
						PropertyDescriptor pd = propertyDescriptors[i];
						String propertyName = pd.getName();
//						System.out.println(" pd.getName()="+ propertyName); 
						 
						if(propertyMapping.keySet().contains(propertyName)){
							Method method = pd.getWriteMethod();
//							System.out.println("===" + status[map.get(propertyName)]);
							@SuppressWarnings("deprecation")
							Cell cell = row.getCell(status[map.get(propertyName)]);
							Object value = null;
							if(cell != null){
								switch(cell.getCellType()){
								case HSSFCell.CELL_TYPE_NUMERIC : 
//									value = cell.getNumericCellValue();
									BigDecimal result = new BigDecimal(cell.getNumericCellValue());
									if(pd.getPropertyType() == Integer.class || pd.getPropertyType() == int.class){
										value = result.intValue();
									}else if(pd.getPropertyType() == Float.class || pd.getPropertyType() == float.class){
										value = result.floatValue();
									}else if(pd.getPropertyType() == Double.class || pd.getPropertyType() == double.class){
										value = cell.getNumericCellValue();
									}else if(pd.getPropertyType() == Long.class || pd.getPropertyType() == long.class){
										value = result.longValue();
									}else if(pd.getPropertyType() == Short.class || pd.getPropertyType() == short.class){
										value = result.longValue();
									}else if(pd.getPropertyType() == BigDecimal.class){
										value = result;
									}else if(pd.getPropertyType() == String.class){
										value = result.toString();
									} 
									break;
								case HSSFCell.CELL_TYPE_STRING :
									String resultStr = cell.getStringCellValue() == null ? "" : cell.getStringCellValue().trim();
									if(pd.getPropertyType() == Integer.class || pd.getPropertyType() == int.class){
										value = Integer.parseInt(resultStr);
									}else if(pd.getPropertyType() == Float.class || pd.getPropertyType() == float.class){
										value = Float.parseFloat(resultStr);
									}else if(pd.getPropertyType() == Double.class || pd.getPropertyType() == double.class){
										value = Double.parseDouble(resultStr);
									}else if(pd.getPropertyType() == Long.class || pd.getPropertyType() == long.class){
										value = Long.parseLong(resultStr);
									}else if(pd.getPropertyType() == Short.class || pd.getPropertyType() == short.class){
										value = Short.parseShort(resultStr);
									}else if(pd.getPropertyType() == String.class){
										 value = resultStr;
									} 
									
									break;
								default :
									break;
								}	
							}
							String type = pd.getPropertyType().getName();
	//					    System.out.println("------type:" + type);
							if(type.endsWith("Date") && value != null){	 
									value = dateFormat.parseObject(String.valueOf(value));							
							}
							
							if(valueMapping != null && valueMapping.containsKey(propertyName)){
								ValueConvert vc = valueMapping.get(propertyName);  
								value = vc.convert(value);
//								System.out.println("-----------value1----------------" + value);
//								for(String key : values.keySet()){
//									if(String.valueOf(value).indexOf(values.get(key)) > -1){
//										value = String.valueOf(value).replace(values.get(key), key);
//									}
//								}
//								System.out.println("-----------value2----------------" + value);
//								if(values.containsValue(value)){
//									for(String key : values.keySet()){
//										if(value.equals(values.get(key))){
//											value = key;
//											break;
//										}
//									}
//								}
								
							}		
//							System.out.println("value==" + value);
							//System.out.println(value.getClass().getName());  
							if(value != null){ 
								method.invoke(t, value);	
							} 
						}
					}catch(Exception e){
						e.printStackTrace();

					}
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		return t;				
	}
	private <T> void setPropertyDescriptors(Class<T> c) {
		BeanInfo beaninfo;
		try {
			beaninfo = Introspector.getBeanInfo(c);
			this.propertyDescriptors = beaninfo.getPropertyDescriptors();
		} catch (IntrospectionException e) {
			logger.error(e.getMessage(),e);
		}
	}


//===============================================导出=======================================
//===============================================导出=======================================
//===============================================导出=======================================
	
	
    /**导出主要入口方法
     * @param datas
     * @param clazz
     * @return
     */
    public <T> ExcelByMapUtil createExcel(List<?> datas,Class<T> clazz) {
    	setPropertyDescriptors(clazz);
    	workbook =  new XSSFWorkbook();
//    	workbook =  new SXSSFWorkbook(5000);
		if(this.sheetName==null){
			sheet = workbook.createSheet();
		}else{
			sheet = workbook.createSheet(sheetName);
		}
    	//初始化 样式数组大小
    	cellStyles = new CellStyle[propertyMapping.size()];
    	//写表头按照map 的顺序
    	Row titleRow = sheet.createRow(0); // 头
    	int titleCellIndex = 0;
    	for (String titleName : propertyMapping.values()) {
    		Cell cell = titleRow.createCell(titleCellIndex);
    		cellStyles[titleCellIndex] = workbook.createCellStyle();
            cell.setCellValue(titleName);
            formatHeadCell(cell, 0, titleCellIndex++);
		}
    	
    	//数据
        if(datas != null && !datas.isEmpty()) {
            for (int i = 0; i < datas.size(); i++) {
                Object dataObj = datas.get(i);
                Row row = sheet.createRow(i + 1);
                Object[] fields = propertyMapping.keySet().toArray();
                for(int j = 0; j < fields.length; j++){
                	Cell cell = row.createCell(j);
                	for (int p =0 ; p < propertyDescriptors.length ; p++) {
                		String name = propertyDescriptors[p].getName();
                		if(fields[j].toString().equals(name)){
                			Method method = propertyDescriptors[p].getReadMethod();
                        	Object value = null;
        					try {
        						value = method.invoke(dataObj, null);
        					} catch (Exception e) {
        						logger.error(e.getMessage(),e);
        					}
                        	if(value == null) {
                                cell.setCellValue("");
                            }else {
                                if(value instanceof String) {
                                	cell.setCellValue((String)value);
                                }else if(value instanceof Integer) {
                                    cell.setCellValue((double)value);
                                }
                            }
                            formatContentCell(cell, i, j,value);
                		}
					}
                	
                }
                
//                
//                for (int j = 0; j < columnNames.length; j++) {
//                    Cell cell = row.createCell(j);
//                    Method method = ReflectionUtils.findMethod(dataObj.getClass(), "get" + StringUtil.firstWordToUpperCase(columnNames[j]));
//                    Object value = ReflectionUtils.invokeMethod(method, dataObj);
//                    
//                    if(value == null) {
//                        cell.setCellValue("");
//                    }else {
//                        if(value instanceof String) {
//                        	cell.setCellValue((String)value);
//                        }else if(value instanceof Integer) {
//                            cell.setCellValue((double)value);
//                        }
//                    }
//                    formatContentCell(cell, i, j,value);
//                }
            }
        }
        return this;
    }

	
//===============================================导出=======================================

	
	private static class ValueConvert{
		
		private String reg = null;
		
		private Map<String, String> valueMap = new HashMap<String, String>();  
		
		/**
		 * 
		 * @param valueMap
		 */
		public ValueConvert(Map<String, String> valueMap){
			StringBuffer regStr = new StringBuffer("("); 
			for(String key : valueMap.keySet()){
				if(regStr.length() > 1){
					regStr.append("|");
				}
				regStr.append(valueMap.get(key));
				this.valueMap.put(valueMap.get(key) ,key);
			}
			regStr.append(")");
			
			this.reg = regStr.toString(); 
		} 
		/**
		 * 
		 */
		public Object convert(Object value) {
			if(this.reg == null){
				return value;
			}
			Pattern p = Pattern.compile(this.reg);
			Matcher m = p.matcher(String.valueOf(value));
			StringBuffer result = new StringBuffer(); 
			while(m.find()){
				String v = valueMap.get(m.group(1)); 
				if(value != null){
					m.appendReplacement(result, v);
				}
			}
			m.appendTail(result);
			return result.toString();
		}
		
	}

	/**
	 * 
	 */
	public void setValueMapping(Map<String,  Map<String, String>> valueMapping) {
		for(String key : valueMapping.keySet()){
			this.valueMapping.put(key, new ValueConvert(valueMapping.get(key)));
		} 
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ExcelByMapUtil setExcelInputStream(InputStream inputStream) {
		try {
			this.workbook = WorkbookFactory.create(inputStream);
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			logger.error(e.getMessage());
		}
        return this;
	}

	/**
	 * 设置时间转换规则
	 */
	@Override
	public ExcelByMapUtil setDateFormat(String format) {
		this.dateFormat = new SimpleDateFormat(format);
        return this;
	}

	/**
	 * 设置导出excel 的sheet名称（有默认值）
	 */
	@Override
	public ExcelByMapUtil setSheetName(String sheetName) {
		this.sheetName = sheetName;
		return this;
	}

	/**
	 * 设置导出起始行号（默认值0）
	 */
	@Override
	public ExcelByMapUtil setImportStartRow(int startRow) {
		this.importStartRow = startRow;
		return this;
	}

	/**
	 * 设置导出路径（导出到磁盘才用到）
	 */
	@Override
	public ExcelByMapUtil setExcelFilePathIn(String excelFilePathIn) {
		this.excelFilePathIn = excelFilePathIn;
		return this;
	}

	/**
	 * （导出到磁盘才用到）
	 */
	@Override
	public Workbook createWorkbookByFilePath() {
		Workbook workbook = null;
        File file = new File(this.excelFilePathIn);
        try {
			if (!file.exists()) {
			    logger.error("文件:{} 不存在！创建此文件！"+ this.excelFilePathIn);
			    if (!file.createNewFile()) {
			        throw new IOException("文件创建失败");
			    }
			    workbook = new XSSFWorkbook();
			} else {
			    workbook = WorkbookFactory.create(file);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return workbook;
	}

	/**
	 * （导出到磁盘才用到）
	 */
	@Override
	public ExcelByMapUtil setOutFilePath(String outFilePath) {
		this.outFilePath = outFilePath;
    	return this;
	}

	/**
	 * （导出到磁盘才用到）
	 */
	@Override
	public void createExcelFileOnDisk() {
		FileOutputStream fileOutputStream = null;
    	File file = new File(this.outFilePath);
        try {
			if (!file.exists()) {
			    if (!file.createNewFile()) {
			        throw new IOException("文件创建失败");
			    }
			}
			fileOutputStream = new FileOutputStream(file);
			workbook.write(fileOutputStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
   
	
    /**
     *     需要子类实现抽象方法，目的每个导出所需样式 可 个性化配置，excel主体的样式
     */
    @Override
    protected void formatContentCell(Cell cell, int rowIndex, int colIndex,Object value) {
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
    }
    
    /**
     *     需要子类实现抽象方法，目的每个导出所需样式 可 个性化配置，excel主体的样式
     */
    @Override
    protected void formatHeadCell(Cell cell, int rowIndex, int colIndex) {
    	sheet.setColumnWidth(colIndex, 10 * 2 * 256);
    	setGeneralProperty(cellStyles[colIndex]);
    	cell.setCellStyle(cellStyles[colIndex]);
    }
}

