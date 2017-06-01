package simon.demo.core.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import simon.demo.core.bean.Product;
import simon.demo.core.bean.RandFFutrueBean;
import simon.demo.core.bean.ReturnBean;
import simon.demo.core.service.ProductService;
import simon.demo.core.util.simonexcel.ExcelByAnnotationUtil;
import simon.demo.core.util.simonexcel.ExcelByMapUtil;
import simon.demo.core.util.simonexcel.ExcelDealByModelUtil;
import simon.demo.core.util.simonexcel.XxxExcelByMapUtil;

@Controller
@RequestMapping(value="/poi")
public class POIAction {
	
	Logger logger = Logger.getLogger(POIAction.class);
	
//	@Autowired  
//	HttpServletRequest request; //这里可以获取到request
//	
//	
	@Autowired
    ProductService productServiceImpl;
	
	@RequestMapping(value="/inp.do")
    public String inportIndex() throws Exception {
        return "poi/inportXls";
    }
	

    /**
     * poi 基础
     * response方式下载文件
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value="/exportBase.do")
    public void exportBaseByVoid(HttpServletRequest request,HttpServletResponse response) throws Exception {
    	logger.error("【导出log】");
    	OutputStream os = null;  
    	Workbook wb = null;  
    	List<Product> selectAll;
		try {
			wb = new HSSFWorkbook();
			Sheet sheet = wb.createSheet("产品sheet");
			selectAll = productServiceImpl.selectAll();
			//Row
			Row row = null;
			Cell cellId = null;
			Cell cellProduct = null;
			Cell cellUnit = null;
			Cell cellAttr = null;
			Cell cellStatus = null;
			for (int i = 0; i < selectAll.size(); i++) {
				row = sheet.createRow(i);
				//id号
				cellId = row.createCell(0);
				cellId.setCellValue(selectAll.get(i).getId());
				//商家名称
				cellProduct = row.createCell(1);
				cellProduct.setCellValue(selectAll.get(i).getProduct());
				//价格
				cellUnit = row.createCell(2);
				cellUnit.setCellValue(selectAll.get(i).getUnit());
				//属性
				cellAttr = row.createCell(3);
				cellAttr.setCellValue(selectAll.get(i).getAttr());
				//状态
				cellStatus = row.createCell(4);
				cellStatus.setCellValue(selectAll.get(i).getStatus());
			}
			//设置response
			String fileName="产品管理.xls";  
            response.setContentType("application/vnd.ms-excel");  
            response.setHeader("Content-disposition", "attachment;filename="+ URLEncoder.encode(fileName, "utf-8"));  
            os = response.getOutputStream();  
            wb.write(os); 
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally{
			os.flush();  
            try {
				os.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}  
		}
        return ;
    }
    
    /**
     * poi 基础
     * ResponseEntity 的方式返回
     * @param request
     * @param response
     * @return
     */
	@RequestMapping(value = "exportBase2.do")
    public ResponseEntity<byte[]> exportBaseResponseEntity(HttpServletRequest request, HttpServletResponse response){
    	//配置：org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
    	logger.error("【导出log】 by ResponseEntity");
    	ByteArrayOutputStream out = null;
    	Workbook wb = null;  
    	List<Product> selectAll;
		try {
			wb = new HSSFWorkbook();
			Sheet sheet = wb.createSheet("产品sheet");
			selectAll = productServiceImpl.selectAll();
			//Row
			Row row = null;
			Cell cellId = null;
			Cell cellProduct = null;
			Cell cellUnit = null;
			Cell cellAttr = null;
			Cell cellStatus = null;
			for (int i = 0; i < selectAll.size(); i++) {
				row = sheet.createRow(i);
				//id号
				cellId = row.createCell(0);
				cellId.setCellValue(selectAll.get(i).getId());
				//商家名称
				cellProduct = row.createCell(1);
				cellProduct.setCellValue(selectAll.get(i).getProduct());
				//价格
				cellUnit = row.createCell(2);
				cellUnit.setCellValue(selectAll.get(i).getUnit());
				//属性
				cellAttr = row.createCell(3);
				cellAttr.setCellValue(selectAll.get(i).getAttr());
				//状态
				cellStatus = row.createCell(4);
				cellStatus.setCellValue(selectAll.get(i).getStatus());
			}
			
			String fileName="产品管理2.xls";  
			HttpHeaders header = new HttpHeaders();
			try {
				header.setContentDispositionFormData("attachment", new String(fileName.getBytes("UTF-8"),"ISO8859-1"));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			//二进制数据
			header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			out = new ByteArrayOutputStream();
			wb.write(out);
			return new ResponseEntity<byte[]>(out.toByteArray(),header,HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
		}
    }
    
    
    /**
     * @param resquest
     * @param response
     * @return
     */
    @RequestMapping(value="importByMapUtil.do")
    public ResponseEntity importByMapUtil(HttpServletRequest resquest,HttpServletResponse response){
    	//初始化表头映射
    	LinkedHashMap<String, String> pm = getFieldMap();
    	try {  
    		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) resquest;  
    		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
    		String xlsName;
    		String[] name;
    		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {   
    			// 上传文件 
    			MultipartFile mf = entity.getValue();  
    			if(mf.getSize() > 4097152){
    				return new ResponseEntity(new ReturnBean(false,"上传失败,文件大小超过2M限制"), HttpStatus.OK);
    			}
    			xlsName = mf.getOriginalFilename();
    			//文件不能为空
    			Assert.notNull(xlsName);
    			name = xlsName.split("\\.");
    			//验证文件格式
    			if("xls".equals(name[1])){
    				ExcelByMapUtil excelutil = new ExcelByMapUtil();
    				List<RandFFutrueBean> entities = excelutil.setExcelInputStream(mf.getInputStream()).setPropertyMapping(pm).getEntities(RandFFutrueBean.class);//
    				System.out.println(entities.size());
//    				处理文件
    				System.out.println(entities.get(entities.size()-1).getAd_contract_id());
    				
    				return new ResponseEntity(new ReturnBean(true,"上传成功"), HttpStatus.OK);
    			}else{
    				return new ResponseEntity("上传失败,文件大小超过2M限制", HttpStatus.OK);
    			}
    		}  
    	} catch (IOException e) {
    		e.printStackTrace();
    		return new ResponseEntity(new ReturnBean(false,"上传成功"), HttpStatus.OK);
    	} 
    	
    	return null;
    }
    /** 导入，导出,ExcelByMapUtil 实现，默认excel表格样式
     * @param resquest
     * @param response
     * @return
     */
    @SuppressWarnings("resource")
	@RequestMapping(value="excelByMapUtil.do")
    public ResponseEntity<byte[]> excelByMapUtil_in_out(HttpServletRequest resquest,HttpServletResponse response){
    	//初始化表头映射
    	LinkedHashMap<String, String> pm = getFieldMap(); 
    	try {  
    		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) resquest;  
    		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
    		String xlsName;
    		String[] name;
    		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {   
    			// 上传文件 
    			MultipartFile mf = entity.getValue();  
    			if(mf.getSize() > 4097152){
    				//(MaxUploadSizeExceededException e) {
//    				return new ResponseEntity(new ReturnBean(false,"上传失败,文件大小超过2M限制"), HttpStatus.OK);
    			}
    			xlsName = mf.getOriginalFilename();
    			//文件不能为空
    			Assert.notNull(xlsName);
    			
    			name = xlsName.split("\\.");
    			//验证文件格式
    			if("xls".equals(name[1]) || "xlsx".equals(name[1])){
    				//处理文件
    				ExcelByMapUtil importUtil = new ExcelByMapUtil();
    				List<RandFFutrueBean> entities = importUtil.setExcelInputStream(mf.getInputStream()).setPropertyMapping(pm).getEntities(RandFFutrueBean.class);
    				importUtil.closeWorkbook();
    				
    				ExcelByMapUtil exportUtil = new ExcelByMapUtil();
    				exportUtil.setSheetName("pinci").setPropertyMapping(pm).createExcel(entities,RandFFutrueBean.class);
    				
    				String fileName="ExcelByMapUtil_demo.xls";  
    				HttpHeaders header = new HttpHeaders();
    				try {
    					header.setContentDispositionFormData("attachment", new String(fileName.getBytes("UTF-8"),"ISO8859-1"));
    				} catch (Exception e) {
    					logger.error(e.getMessage());
    				}
    				//二进制数据
    				header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
					return new ResponseEntity<byte[]>(exportUtil.toByteArray(),header,HttpStatus.CREATED);
//    				return new ResponseEntity(new ReturnBean(true,"上传成功"), HttpStatus.OK);
    			}else{
//    				return new ResponseEntity("请选择有效excel文件", HttpStatus.OK);
    			}
    		}  
    	} catch (IOException e) {
    		e.printStackTrace();
//    		return new ResponseEntity(new ReturnBean(false,"上传成功"), HttpStatus.OK);
    	} 
    	return null;
    	
    }
    /** 导入，导出,XxxExcelByMapUtil 实现，可自定义excel表格样式
     * @param resquest
     * @param response
     * @return
     */
    @SuppressWarnings("resource")
    @RequestMapping(value="xxxExcelByMapUtil.do")
    public ResponseEntity<byte[]> xxxExcelByMapUtil_in_out(HttpServletRequest resquest,HttpServletResponse response){
    	//初始化表头映射
    	LinkedHashMap<String, String> pm = getFieldMap(); 
    	try {  
    		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) resquest;  
    		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
    		String xlsName;
    		String[] name;
    		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {   
    			// 上传文件 
    			MultipartFile mf = entity.getValue();  
    			if(mf.getSize() > 4097152){
    				//(MaxUploadSizeExceededException e) {
//    				return new ResponseEntity(new ReturnBean(false,"上传失败,文件大小超过2M限制"), HttpStatus.OK);
    			}
    			xlsName = mf.getOriginalFilename();
    			//文件不能为空
    			Assert.notNull(xlsName);
    			
    			name = xlsName.split("\\.");
    			//验证文件格式
    			if("xls".equals(name[1]) || "xlsx".equals(name[1])){
    				//处理文件
    				XxxExcelByMapUtil importUtil = new XxxExcelByMapUtil();
    				List<RandFFutrueBean> entities = importUtil.setExcelInputStream(mf.getInputStream()).setPropertyMapping(pm).getEntities(RandFFutrueBean.class);
    				importUtil.closeWorkbook();
    				
    				XxxExcelByMapUtil exportUtil = new XxxExcelByMapUtil();
    				exportUtil.setSheetName("xxx").setPropertyMapping(pm).createExcel(entities,RandFFutrueBean.class);
    				
    				String fileName="XxxExcelByMapUtil_demo.xls";  
    				HttpHeaders header = new HttpHeaders();
    				try {
    					header.setContentDispositionFormData("attachment", new String(fileName.getBytes("UTF-8"),"ISO8859-1"));
    				} catch (Exception e) {
    					logger.error(e.getMessage());
    				}
    				//二进制数据
    				header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    				return new ResponseEntity<byte[]>(exportUtil.toByteArray(),header,HttpStatus.CREATED);
//    				return new ResponseEntity(new ReturnBean(true,"上传成功"), HttpStatus.OK);
    			}else{
//    				return new ResponseEntity("请选择有效excel文件", HttpStatus.OK);
    			}
    		}  
    	} catch (IOException e) {
    		e.printStackTrace();
//    		return new ResponseEntity(new ReturnBean(false,"上传成功"), HttpStatus.OK);
    	} 
    	return null;
    	
    }
 
    /** 导入，导出
     * @param resquest
     * @param response
     * @return
     */
    @SuppressWarnings("resource")
	@RequestMapping(value="excelByAnnotationUtil.do")
    public ResponseEntity<byte[]> excelByAnnotationUtil_in_out(HttpServletRequest resquest,HttpServletResponse response){
    	try {  
    		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) resquest;  
    		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
    		System.out.println(fileMap.size());
    		String xlsName;
    		String[] name;
    		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {   
    			// 上传文件 
    			MultipartFile mf = entity.getValue();  
    			if(mf.getSize() > 4097152){
    				//(MaxUploadSizeExceededException e) {
//    				return new ResponseEntity(new ReturnBean(false,"上传失败,文件大小超过2M限制"), HttpStatus.OK);
    			}
    			xlsName = mf.getOriginalFilename();
    			//文件不能为空
    			Assert.notNull(xlsName,"请选择上传文件");
    			Assert.isTrue(xlsName.length()>0,"请选择上传文件");
    			
    			name = xlsName.split("\\.");
    			//验证文件格式
    			if("xls".equals(name[1]) || "xlsx".equals(name[1])){
    				//处理文件
    				ExcelByAnnotationUtil excel = new ExcelByAnnotationUtil();
    				
    				excel.setExcelInputStream(mf.getInputStream()).setImportStartRow(1);
    				
    				long startTime = System.currentTimeMillis();
    				List<RandFFutrueBean> list = excel.getEntities(RandFFutrueBean.class);
    				excel.closeWorkbook();
    				System.out.println("list.size="+list.size());
    				logger.error("【excel耗时】"+ (System.currentTimeMillis()-startTime) + "ms");
    				
    				
//    				ExcelUtilAnnotation export = new ExcelUtilAnnotation();
    				ExcelByAnnotationUtil export = new ExcelByAnnotationUtil();
    				export.createExcel(list);
    				
    				String fileName="ExcelByAnnotationUtil.xls";  
    				HttpHeaders header = new HttpHeaders();
    				try {
    					header.setContentDispositionFormData("attachment", new String(fileName.getBytes("UTF-8"),"ISO8859-1"));
    				} catch (Exception e) {
    					logger.error(e.getMessage());
    				}
    				//二进制数据
    				header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    				return new ResponseEntity<byte[]>(export.toByteArray(),header,HttpStatus.CREATED);
    			}else{
    				logger.error("请选择有效excel文件");
    			}
    		}  
    	} catch (IOException e) {
    		e.printStackTrace();
//    		return new ResponseEntity(new ReturnBean(false,"上传成功"), HttpStatus.OK);
    	} 
    	return null;
    	
    }
    
    //===================================================================================
    //======================================= 模板导出 ====================================
    //===================================================================================
    
    /**
     * 当导出时，表格特别复杂，像项目路径doc 下ZZSScottareModel.xls 中，需要填写关键几个地方，可以用这个ZZSScottareModel.xls文件作为模板，填充模板中关键的 （# ）地方
     * @param request
     * @return
     */
    @RequestMapping(value="exportModelList.do")
    public ResponseEntity<byte[]> exportModelList(HttpServletRequest request){
    	HttpSession httpSession = request.getSession();   
    	logger.debug("httpSession.getServletContext():"+httpSession.getServletContext());  
    	String basePath = httpSession.getServletContext().getRealPath("/WEB-INF");
    	String filePath = basePath+"/excelModel/ZZSScottareModelLsit.xls";
    	
    	//data
    	//封装整个excel所需数据,key为sheet名字,String
    	Map<String,List<String>> fieldData = new HashMap<String, List<String>>();
    	//封装一个sheet的数据
    	List<String> cellData = new ArrayList<String>();
    	cellData.add("附件1 值 税 纳 增  税  申 报 表3");
    	cellData.add("税款所属时间：自2016 ");
    	cellData.add("填表日期： 2016 年 12  月  30  日");
    	cellData.add("金额单位：10000.00元 至角分");
    	cellData.add("税款所属时间：自2015 ");
    	cellData.add("填表日期： 2015 年 12  月  30  日");
    	cellData.add("金额单位：10000.01元 至角分");
    	fieldData.put("首页", cellData);
    	
    	//sheet2
    	cellData = new ArrayList<String>();
    	cellData.add("sheet2附件1 值 税 纳 增  税  申 报 表3");
    	cellData.add("sheet2税款所属时间：自2016 ");
    	cellData.add("sheet2填表日期： 2016 年 12  月  30  日");
    	cellData.add("sheet2金额单位：10000.00元 至角分");
    	cellData.add("sheet2税款所属时间：自2015 ");
    	cellData.add("sheet2填表日期： 2015 年 12  月  30  日");
    	cellData.add("sheet2金额单位：10000.01元 至角分");
    	fieldData.put("Sheet2", cellData);
    	//data
    	
    	try {
    		File f = new File(filePath);
    		if(!f.exists()){
    			logger.error("文件路径不存在");
    			return null;
    		}
    	} catch (Exception e1) {
    		logger.error("文件路径不存在");
    		return null;
    	}
    	ExcelDealByModelUtil excel = new ExcelDealByModelUtil();
    	excel.setModelPath(filePath).setRowCellCount(54, 40).writeDataByList(fieldData);
    	
    	HttpHeaders header = new HttpHeaders();
    	try {
    		header.setContentDispositionFormData("attachment", new String("增值税list.xls".getBytes("UTF-8"),"ISO8859-1"));
    	} catch (UnsupportedEncodingException e) {
    		logger.error(e.getMessage());
    	}
    	header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    	return new ResponseEntity<byte[]>(excel.toByteArray(),header,HttpStatus.CREATED);
    }
    
    /**
     * 当导出时，表格特别复杂，像项目路径doc 下ZZSScottareModel.xls 中，需要填写关键几个地方，可以用这个ZZSScottareModel.xls文件作为模板，填充模板中关键的 （已知的 ）地方
     * @param request
     * @return
     */
    @RequestMapping(value="exportModelMapUtil.do")
    public ResponseEntity<byte[]> exportModelMapUtil(HttpServletRequest request){
    	HttpSession httpSession = request.getSession();   
    	logger.debug("httpSession.getServletContext():"+httpSession.getServletContext());  
    	String basePath = httpSession.getServletContext().getRealPath("/WEB-INF");
    	String filePath = basePath+"/excelModel/ZZSScottareModel.xls";
    	
    	ExcelDealByModelUtil excel = new ExcelDealByModelUtil();
    	//data
    	//封装整个excel所需数据,key为sheet名字,String
    	Map<String,Map<Integer,Map<Integer,String>>> fieldData = new HashMap<String, Map<Integer,Map<Integer,String>>>();
    	//封装一个sheet的数据,key为数据row行序号,int
    	Map<Integer,Map<Integer,String>> rowMap = null;
    	//封装一行的数据,key为cell列序号,int
    	//Map<Integer,String> cellMap = null;
    	
    	//sheet1 部分
    	rowMap = new HashMap<Integer, Map<Integer,String>>();	//实例化sheet1-rowMap
    	//sheet1
    	//rowMap:key=0,value:第0行的数据
    	rowMap.put(0, excel.fillCellMapData(new Integer[]{0},new String[]{"附件1 值 税 纳 增  税  申 报 表2"}));
    	//rowMap:key=3,value:第3行的数据
    	rowMap.put(3, excel.fillCellMapData(new Integer[]{0,17,31},new String[]{"税款所属时间：自2016 ","填表日期： 2016 年 12  月  30  日","金额单位：10000.00元 至角分"}));
    	//rowMap:key=4,value:第3行的数据
    	rowMap.put(4, excel.fillCellMapData(new Integer[]{0,5,27},new String[]{"税款所属时间：自2015 ","填表日期： 2015 年 12  月  30  日","金额单位：10000.01元 至角分"}));
    	
    	fieldData.put("首页", rowMap);//sheet1数据加入map中
    	
    	//sheet2部分
    	rowMap = new HashMap<Integer, Map<Integer,String>>();	//实例化sheet2-rowMap
    	//rowMap:key=3,value:第3行的数据
    	rowMap.put(3, excel.fillCellMapData(new Integer[]{0,16,31},new String[]{"税款所属时间：自2015 ","填表日期： 2015 年 12  月  30  日","金额单位：10000.01元 至角分"}));
    	//rowMap:key=4,value:第3行的数据
    	rowMap.put(4, excel.fillCellMapData(new Integer[]{0,5,27},new String[]{"纳税人识别号2","业务系统的标识2","所属行业：2"}));
    	//sheet2
    	fieldData.put("Sheet2", rowMap);
    	//data
    	
    	try {
    		File f = new File(filePath);
    		if(!f.exists()){
    			logger.error("文件路径不存在");
    			return null;
    		}
    	} catch (Exception e1) {
    		logger.error("文件路径不存在");
    		return null;
    	}
    	
    	// 
    	excel.setModelPath(filePath).writeDataByMap(fieldData);
    	
    	HttpHeaders header = new HttpHeaders();
    	try {
    		header.setContentDispositionFormData("attachment", new String("增值税.xls".getBytes("UTF-8"),"ISO8859-1"));
    	} catch (UnsupportedEncodingException e) {
    		logger.error(e.getMessage());
    	}
    	header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    	return new ResponseEntity<byte[]>(excel.toByteArray(),header,HttpStatus.CREATED);
    }
    
    /**
     * 做一个map，对应bean类的属性名和excel的表头名
     * @return
     */
    private LinkedHashMap<String, String> getFieldMap(){
    	LinkedHashMap<String, String> pm = new LinkedHashMap<String, String>();
    	pm.put("ad_contract_id", "订单ID");
    	pm.put("contract_name", "订单名称");
    	pm.put("ad_cast_id", "投放ID"); 
    	pm.put("cast_name", "订单名称"); 
    	pm.put("ad_creative_id", "素材ID"); 
    	pm.put("creative_name", "素材名称"); 
    	pm.put("province_name", "省份"); 
    	pm.put("city_name", "城市"); 
    	pm.put("show1", "AD曝光1+UV"); 
    	pm.put("show2", "AD曝光2+UV"); 
    	pm.put("show3", "AD曝光3+UV"); 
    	pm.put("show4", "AD曝光4+UV"); 
    	pm.put("show5", "AD曝光5+UV"); 
    	pm.put("show6", "AD曝光6+以上UV"); 
    	pm.put("click_yt", "优土点击(次)"); 
    	pm.put("click_admaster", "AD点击(次)"); 
    	pm.put("click1", "AD点击1+UV"); 
    	pm.put("click2", "AD点击2+UV"); 
    	pm.put("click3", "AD点击3+UV"); 
    	pm.put("click4", "AD点击4+UV"); 
    	pm.put("click5", "AD点击5+UV"); 
    	pm.put("click6", "AD点击6+以上UV"); 
    	return pm;
    }
    
}