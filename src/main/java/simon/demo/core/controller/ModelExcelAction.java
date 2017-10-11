package simon.demo.core.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import freemarker.template.Configuration;
import freemarker.template.Template;
import simon.demo.core.dao.ProductMapper;
import simon.demo.core.service.ProductService;
import simon.demo.core.util.DateUtil;
import simon.demo.core.util.simonexcel.ExcelDealByModelUtil;

@Controller
@RequestMapping(value = "/model")
public class ModelExcelAction {

	Logger logger = Logger.getLogger(ModelExcelAction.class);

	// @Autowired
	// HttpServletRequest request; //这里可以获取到request
	//
	//
	@Autowired
	ProductService productServiceImpl;
	@Autowired
	ProductMapper productMapper;
	
//	@Autowired
//	private FreeMarkerConfigurer freeMarkerConfigurer;

	@RequestMapping(value = "/index.do")
	public String inportIndex() throws Exception {
		return "poi/inportModel";
	}

	// ===================================================================================
	// ======================================= 模板导出
	// ====================================
	// ===================================================================================

	/**
	 * 当导出时，表格特别复杂，像项目路径doc 下ZZSScottareModel.xls
	 * 中，需要填写关键几个地方，可以用这个ZZSScottareModel.xls文件作为模板，填充模板中关键的 （# ）地方
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "exportModelList.do")
	public ResponseEntity<byte[]> exportModelList(HttpServletRequest request) {
		HttpSession httpSession = request.getSession();
		logger.debug("httpSession.getServletContext():" + httpSession.getServletContext());
		String basePath = httpSession.getServletContext().getRealPath("/WEB-INF");
		String filePath = basePath + "/excelModel/ZZSScottareModelLsit.xls";

		// data
		// 封装整个excel所需数据,key为sheet名字,String
		Map<String, List<String>> fieldData = new HashMap<String, List<String>>();
		// 封装一个sheet的数据
		List<String> cellData = new ArrayList<String>();
		cellData.add("附件1 值 税 纳 增  税  申 报 表3");
		cellData.add("税款所属时间：自2016 ");
		cellData.add("填表日期： 2016 年 12  月  30  日");
		cellData.add("金额单位：10000.00元 至角分");
		cellData.add("税款所属时间：自2015 ");
		cellData.add("填表日期： 2015 年 12  月  30  日");
		cellData.add("金额单位：10000.01元 至角分");
		fieldData.put("首页", cellData);

		// sheet2
		cellData = new ArrayList<String>();
		cellData.add("sheet2附件1 值 税 纳 增  税  申 报 表3");
		cellData.add("sheet2税款所属时间：自2016 ");
		cellData.add("sheet2填表日期： 2016 年 12  月  30  日");
		cellData.add("sheet2金额单位：10000.00元 至角分");
		cellData.add("sheet2税款所属时间：自2015 ");
		cellData.add("sheet2填表日期： 2015 年 12  月  30  日");
		cellData.add("sheet2金额单位：10000.01元 至角分");
		fieldData.put("Sheet2", cellData);
		// data

		try {
			File f = new File(filePath);
			if (!f.exists()) {
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
			header.setContentDispositionFormData("attachment",
					new String("增值税list.xls".getBytes("UTF-8"), "ISO8859-1"));
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		return new ResponseEntity<byte[]>(excel.toByteArray(), header, HttpStatus.CREATED);
	}

	/**
	 * 当导出时，表格特别复杂，像项目路径doc 下ZZSScottareModel.xls
	 * 中，需要填写关键几个地方，可以用这个ZZSScottareModel.xls文件作为模板，填充模板中关键的 （已知的 ）地方
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "exportModelMapUtil.do")
	public ResponseEntity<byte[]> exportModelMapUtil(HttpServletRequest request) {
		HttpSession httpSession = request.getSession();
		logger.debug("httpSession.getServletContext():" + httpSession.getServletContext());
		String basePath = httpSession.getServletContext().getRealPath("/WEB-INF");
		String filePath = basePath + "/excelModel/ZZSScottareModel.xls";

		ExcelDealByModelUtil excel = new ExcelDealByModelUtil();
		// data
		// 封装整个excel所需数据,key为sheet名字,String
		Map<String, Map<Integer, Map<Integer, String>>> fieldData = new HashMap<String, Map<Integer, Map<Integer, String>>>();
		// 封装一个sheet的数据,key为数据row行序号,int
		Map<Integer, Map<Integer, String>> rowMap = null;
		// 封装一行的数据,key为cell列序号,int
		// Map<Integer,String> cellMap = null;

		// sheet1 部分
		rowMap = new HashMap<Integer, Map<Integer, String>>(); // 实例化sheet1-rowMap
		// sheet1
		// rowMap:key=0,value:第0行的数据
		rowMap.put(0, excel.fillCellMapData(new Integer[] { 0 }, new String[] { "附件1 值 税 纳 增  税  申 报 表2" }));
		// rowMap:key=3,value:第3行的数据
		rowMap.put(3, excel.fillCellMapData(new Integer[] { 0, 17, 31 },
				new String[] { "税款所属时间：自2016 ", "填表日期： 2016 年 12  月  30  日", "金额单位：10000.00元 至角分" }));
		// rowMap:key=4,value:第3行的数据
		rowMap.put(4, excel.fillCellMapData(new Integer[] { 0, 5, 27 },
				new String[] { "税款所属时间：自2015 ", "填表日期： 2015 年 12  月  30  日", "金额单位：10000.01元 至角分" }));

		fieldData.put("首页", rowMap);// sheet1数据加入map中

		// sheet2部分
		rowMap = new HashMap<Integer, Map<Integer, String>>(); // 实例化sheet2-rowMap
		// rowMap:key=3,value:第3行的数据
		rowMap.put(3, excel.fillCellMapData(new Integer[] { 0, 16, 31 },
				new String[] { "税款所属时间：自2015 ", "填表日期： 2015 年 12  月  30  日", "金额单位：10000.01元 至角分" }));
		// rowMap:key=4,value:第3行的数据
		rowMap.put(4,
				excel.fillCellMapData(new Integer[] { 0, 5, 27 }, new String[] { "纳税人识别号2", "业务系统的标识2", "所属行业：2" }));
		// sheet2
		fieldData.put("Sheet2", rowMap);
		// data

		try {
			File f = new File(filePath);
			if (!f.exists()) {
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
			header.setContentDispositionFormData("attachment", new String("增值税.xls".getBytes("UTF-8"), "ISO8859-1"));
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		return new ResponseEntity<byte[]>(excel.toByteArray(), header, HttpStatus.CREATED);
	}

	/**
	 * 做一个map，对应bean类的属性名和excel的表头名
	 * 
	 * @return
	 */
	private LinkedHashMap<String, String> getFieldMap() {
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

	
	/**
	 * 当导出时，表格特别复杂，像项目路径doc 下ZZSScottareModel.xml
	 * 使用freemarker导出
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "exportModelFtl.do")
	public void exportModelFtl(HttpServletRequest request, HttpServletResponse response) {
		HttpSession httpSession = request.getSession();
		logger.debug("httpSession.getServletContext():" + httpSession.getServletContext());
		String basePath = httpSession.getServletContext().getRealPath("/WEB-INF");
		String filePath = basePath + "/excelModel/";
		
		Configuration configuration = new Configuration(Configuration.VERSION_2_3_0);
		Writer out = null;
		try {
			configuration.setDirectoryForTemplateLoading(new File(filePath));
			Template template = configuration.getTemplate("ZZSScottareModel.xml");
			File outFile = new File("/Users/simon/Desktop/excel/"+ DateUtil.format(new Date(), DateUtil.defaultDatePattern4)+".xls");  //生成文件的路径
	        
			String fileName = DateUtil.format(new Date(), DateUtil.defaultDatePattern4) + ".xls";
			
			//设置文件输出类型
		    response.setContentType("application/octet-stream");  
		    response.setHeader("Content-disposition", "attachment; filename="  
		        + new String(fileName.getBytes("utf-8"), "ISO8859-1")); 
		      
            out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "utf-8"));
            Map<String, String> model = new HashMap<String, String>();
            model.put("beginDate", "2017-09-09 10:03:00");
            model.put("endDate", "2017-09-10 10:03:00");
            model.put("date", "2017-09-10 10:03:00");
            model.put("user", "xiaotao");
            model.put("address", "北京互联网金融中心");
            model.put("authPeo", "涛哥");
            model.put("report", "还是涛哥");
            template.process(model, out);
            
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
}