package simon.demo.core.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
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

import com.google.gson.Gson;

import simon.demo.core.bean.Loan;
import simon.demo.core.bean.PartyMember;
import simon.demo.core.bean.Product;
import simon.demo.core.bean.R360Rusult;
import simon.demo.core.bean.RandFFutrueBean;
import simon.demo.core.bean.ReturnBean;
import simon.demo.core.dao.ProductMapper;
import simon.demo.core.service.ProductService;
import simon.demo.core.util.simonexcel.ExcelByAnnotationUtil;
import simon.demo.core.util.simonexcel.ExcelByMapUtil;
import simon.demo.core.util.simonexcel.ExcelDealByModelUtil;
import simon.demo.core.util.simonexcel.XxxExcelByMapUtil;

@Controller
@RequestMapping(value="/party")
public class PartyAction {
	
	Logger logger = Logger.getLogger(PartyAction.class);
	
//	@Autowired  
//	HttpServletRequest request; //这里可以获取到request
//	
//	
	@Autowired
    ProductService productServiceImpl;
	@Autowired
    ProductMapper productMapper;
	
	
	@RequestMapping(value="/index.do")
    public String importIndex() throws Exception {
        return "poi/excel_party";
    }
	
	/**
	 * @param resquest
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/importByMapUtilParty_0.do")
	public ResponseEntity importByMapUtilParty0(HttpServletRequest resquest,HttpServletResponse response){
		//初始化表头映射
		LinkedHashMap<String, String> pm = getPartyMap();
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
				if("xls".equals(name[1]) || "xlsx".equals(name[1])){
					ExcelByMapUtil excelutil = new ExcelByMapUtil();
					DateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
					excelutil.setDateFormat(sdf);
					List<PartyMember> list = excelutil.setExcelInputStream(mf.getInputStream()).setPropertyMapping(pm).getEntitiesHasNoHeader(4,PartyMember.class);
					
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
	/**
	 * @param resquest
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/importByAnnoUtilParty.do")
	public ResponseEntity importByAnnoUtilParty(HttpServletRequest resquest,HttpServletResponse response){
		//初始化表头映射
		LinkedHashMap<String, String> pm = getPartyMapChn();
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
				if("xls".equals(name[1]) || "xlsx".equals(name[1])){
					DateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
					ExcelByAnnotationUtil excelIn = new ExcelByAnnotationUtil();
					excelIn.setDateFormat(sdf).setExcelInputStream(mf.getInputStream()).setImportStartRow(4);
					List<PartyMember> list = excelIn.getEntities(PartyMember.class);
					
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
    /**
     * @param resquest
     * @param response
     * @return
     */
    @RequestMapping(value="/importByMapUtilParty.do")
    public ResponseEntity importByMapUtilParty(HttpServletRequest request,HttpServletResponse response){
    	//初始化表头映射
    	LinkedHashMap<String, String> pm = getPartyMap();
    	try {  
    		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;  
    		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
    		String xlsName;
    		String[] name;
    		HttpSession httpSession = request.getSession();   
	    	logger.debug("httpSession.getServletContext():"+httpSession.getServletContext());  
	    	String basePath = httpSession.getServletContext().getRealPath("/WEB-INF");
	    	String filePath = basePath+"/excelModel/";
	    	
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
    			if("xls".equals(name[1]) || "xlsx".equals(name[1])){
    				ExcelByMapUtil excelutil = new ExcelByMapUtil();
    				Workbook workbook = excelutil.setExcelInputStream(mf.getInputStream()).getWorkbook();
    				
    				String sheetNames[] = {"桃花村","古墩村","土地村"};
    				Sheet sheet = workbook.getSheet("桃花村");
    				DecimalFormat phoneDf = new DecimalFormat("0");
    				int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
    				Row row = null;
    				List<PartyMember> entities = new ArrayList<PartyMember>();
    				for (int i = 4; i < physicalNumberOfRows-1; i++) {
    					row = sheet.getRow(i);
    					int j = 0;
    					PartyMember pmm = new PartyMember();
    					pmm.setName(row.getCell(j++).toString());
    					pmm.setName(row.getCell(j++).toString());
    					pmm.setZhibu(row.getCell(j++).toString());
    					pmm.setIdNo(row.getCell(j++).toString());
    					pmm.setGender(row.getCell(j++).toString());
    					pmm.setNation(row.getCell(j++).toString());
    					pmm.setBirthday(getStrDate( row.getCell(j++))); 
    					pmm.setEnducational(row.getCell(j++).toString()); 
    					pmm.setPeoType(row.getCell(j++).toString());//", "人员类别")(row.getCell(j++).toString()); 
    					pmm.setInDate(getStrDate( row.getCell(j++)));//, "加入党组织日期")(row.getCell(j++).toString()); 
    					pmm.setTurnDate(getStrDate( row.getCell(j++)));//", "转为正式党员日期")(row.getCell(j++).toString()); 
    					pmm.setJob(row.getCell(j++).toString());//", "工作岗位")(row.getCell(j++).toString()); 
    					pmm.setPhone(phoneDf.format(row.getCell(j++).getNumericCellValue()));//", "手机号")(row.getCell(j++).toString()); 
    					pmm.setHomeTel(row.getCell(j++).toString());//", "固定电话")(row.getCell(j++).toString()); 
    					pmm.setAddress(row.getCell(j++).toString());//", "家庭住址")(row.getCell(j++).toString()); 
    					pmm.setPartyStatus(row.getCell(j++).toString());//", "党籍状态")(row.getCell(j++).toString()); 
    					pmm.setIsRelation(row.getCell(j++).toString());//", "是否为失联党员")(row.getCell(j++).toString()); 
    					pmm.setNoRelationDate(getStrDate( row.getCell(j++)));//", "失去联系日期")(row.getCell(j++).toString()); 
    					pmm.setIsFlowParty(row.getCell(j++).toString());//", "是否为流动党员")(row.getCell(j++).toString()); 
    					pmm.setFlowAddr(row.getCell(j++).toString());//", "外出流向"); 
    					
    					createExcel(pmm,i,basePath);
    					entities.add(pmm);
					}
    				
    		    	
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
    
    private void createExcel(PartyMember pmm, int i,String basePath) {
    	String filePath = basePath+"/excelModel/single_party_member.xls";
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
    	rowMap.put(2, excel.fillCellMapData(new Integer[]{1,7,9},new String[]{pmm.getName(),pmm.getGender(),pmm.getNation()}));
    	//rowMap:key=3,value:第3行的数据
    	rowMap.put(3, excel.fillCellMapData(new Integer[]{3},new String[]{pmm.getIdNo()}));
    	//rowMap:key=4,value:第3行的数据
    	rowMap.put(4, excel.fillCellMapData(new Integer[]{2},new String[]{pmm.getBirthday()}));
    	rowMap.put(5, excel.fillCellMapData(new Integer[]{2},new String[]{pmm.getEnducational()}));
    	rowMap.put(6, excel.fillCellMapData(new Integer[]{2},new String[]{pmm.getPeoType()}));
    	rowMap.put(7, excel.fillCellMapData(new Integer[]{5},new String[]{pmm.getZhibu()}));
    	rowMap.put(8, excel.fillCellMapData(new Integer[]{4},new String[]{pmm.getInDate()}));
    	rowMap.put(9, excel.fillCellMapData(new Integer[]{4},new String[]{pmm.getTurnDate()}));
    	rowMap.put(10, excel.fillCellMapData(new Integer[]{2},new String[]{pmm.getJob()}));
    	rowMap.put(11, excel.fillCellMapData(new Integer[]{4},new String[]{pmm.getPhone()}));
    	if(pmm.getHomeTel() !=null && !"".equals(pmm.getHomeTel())){
    		rowMap.put(12, excel.fillCellMapData(new Integer[]{3,7},new String[]{pmm.getHomeTel().split("-")[0],pmm.getHomeTel().split("-")[1]}));
    	}
    	rowMap.put(13, excel.fillCellMapData(new Integer[]{5},new String[]{pmm.getAddress()}));
    	rowMap.put(14, excel.fillCellMapData(new Integer[]{3},new String[]{pmm.getPartyStatus()}));
    	rowMap.put(15, excel.fillCellMapData(new Integer[]{3,9},new String[]{pmm.getIsRelation(),pmm.getNoRelationDate()}));
    	rowMap.put(16, excel.fillCellMapData(new Integer[]{9},new String[]{pmm.getIsRelation(),pmm.getIsFlowParty()}));
    	rowMap.put(17, excel.fillCellMapData(new Integer[]{4},new String[]{pmm.getIsRelation(),pmm.getFlowAddr()}));
    	
    	fieldData.put("党员基本信息采集表", rowMap);//sheet1数据加入map中
    	
    	
    	try {
    		File f = new File(filePath);
    		if(!f.exists()){
    			logger.error("文件路径不存在");
    		}
    	} catch (Exception e1) {
    		logger.error("文件路径不存在");
    	}
    	
    	// 
    	excel.setModelPath(filePath).writeDataByMap(fieldData);
    	excel.createExcelFileOnDisk("D:\\party_member\\party_member_"+pmm.getName()+".xls");
		
	}

	private String getStrDate(Cell hssfCell){
		DateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
    	return df.format(HSSFDateUtil.getJavaDate(hssfCell.getNumericCellValue()));
    }
    private LinkedHashMap<String, String> getPartyMap(){
    	LinkedHashMap<String, String> pm = new LinkedHashMap<String, String>();
    	pm.put("name", "1");
    	pm.put("zhibu", "2");
    	pm.put("idNo", "3");
    	pm.put("gender", "4");
    	pm.put("nation", "5");
    	pm.put("birthday", "6"); 
    	pm.put("enducational", "7"); 
    	pm.put("peoType", "8"); 
    	pm.put("inDate", "9"); 
    	pm.put("turnDate", "10"); 
    	pm.put("job", "11"); 
    	pm.put("phone", "12"); 
    	pm.put("homeTel", "13"); 
    	pm.put("address", "14"); 
    	pm.put("partyStatus", "15"); 
    	pm.put("isRelation", "16"); 
    	pm.put("noRelationDate", "17"); 
    	pm.put("isFlowParty", "18"); 
    	pm.put("flowAddr", "19"); 
    	return pm;
    }
    private LinkedHashMap<String, String> getPartyMapChn(){
    	LinkedHashMap<String, String> pm = new LinkedHashMap<String, String>();
    	pm.put("name", "姓名");
    	pm.put("zhibu", "所在党支部");
    	pm.put("idNo", "公民身份证号");
    	pm.put("gender", "性别");
    	pm.put("nation", "民族");
    	pm.put("birthday", "出生日期"); 
    	pm.put("enducational", "学历"); 
    	pm.put("peoType", "人员类别"); 
    	pm.put("inDate", "加入党组织日期"); 
    	pm.put("turnDate", "转为正式党员日期"); 
    	pm.put("job", "工作岗位"); 
    	pm.put("phone", "手机号"); 
    	pm.put("homeTel", "固定电话"); 
    	pm.put("address", "家庭住址"); 
    	pm.put("partyStatus", "党籍状态"); 
    	pm.put("isRelation", "是否为失联党员"); 
    	pm.put("noRelationDate", "失去联系日期"); 
    	pm.put("isFlowParty", "是否为流动党员"); 
    	pm.put("flowAddr", "外出流向"); 
    	return pm;
    }
    
}