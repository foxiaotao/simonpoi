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

import com.google.gson.Gson;

import simon.demo.core.bean.Loan;
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
@RequestMapping(value="/r360")
public class R360Action {
	
	Logger logger = Logger.getLogger(R360Action.class);
	
//	@Autowired  
//	HttpServletRequest request; //这里可以获取到request
//	
//	
	@Autowired
    ProductService productServiceImpl;
	@Autowired
    ProductMapper productMapper;
	
	
	@RequestMapping(value="/inp.do")
    public String inportIndex() throws Exception {
        return "poi/inportXls";
    }
	
    /**
     * @param resquest
     * @param response
     * @return
     */
    @RequestMapping(value="importByMapUtilR360.do")
    public ResponseEntity importByMapUtilR360(HttpServletRequest resquest,HttpServletResponse response){
    	//初始化表头映射
    	LinkedHashMap<String, String> pm = getR360Map();
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
    				List<R360Rusult> entities = excelutil.setExcelInputStream(mf.getInputStream()).setPropertyMapping(pm).getEntities(R360Rusult.class);//
    				System.out.println(entities.size());
//    				处理文件
    				for (R360Rusult r360Rusult : entities) {
    					productMapper.updater360status(r360Rusult);
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
    /**
     * @param resquest
     * @param response
     * @return
     */
    @RequestMapping(value="pushMqJson.do")
    public ResponseEntity pushMqJson(HttpServletRequest resquest,HttpServletResponse response){
    	//初始化表头映射
    	LinkedHashMap<String, String> pm = getLoanMap();
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
    				List<Loan> entities = excelutil.setExcelInputStream(mf.getInputStream()).setPropertyMapping(pm).getEntities(Loan.class);//
    				System.out.println(entities.size());
//    				处理文件
    				System.out.println(new Gson().toJson(entities));
    				
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
    private LinkedHashMap<String, String> getR360Map(){
    	LinkedHashMap<String, String> pm = new LinkedHashMap<String, String>();
    	pm.put("orderNo", "订单号");
    	pm.put("r360Status", "融360订单状态");
    	pm.put("createdAt", "订单生成时间"); 
    	return pm;
    }
    private LinkedHashMap<String, String> getLoanMap(){
    	LinkedHashMap<String, String> pm = new LinkedHashMap<String, String>();
    	pm.put("loanId", "订单号");
    	pm.put("loanProgress", "状态");
    	pm.put("termNo", "期数"); 
    	return pm;
    }
    
}