<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../base.jsp" %>     

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>simon最专业的的excel工具</title>

</head>
<body>
<h2>Hello Man!</h2>测试excel在项目doc下，RandFFutrueBean-import-data.xls




	<h2>-----------excel Map begin-----------</h2>
	<form action="${ctx}/poi/importByMapUtil.do" method="post" enctype="multipart/form-data">
		<span>用map的方式，导入数据：</span><input type="file" name="uploadfile" />
		<input type="submit" value="上传"/>
	</form>
	-----------------------------------
	<form action="${ctx}/poi/excelByMapUtil.do" method="post" enctype="multipart/form-data">
		<span>用map的方式，导入导出，map工具类：</span><input type="file" name="uploadfile" />
		<input type="submit" value="上传-下载"/>
	</form>
	<form action="${ctx}/poi/xxxExcelByMapUtil.do" method="post" enctype="multipart/form-data">
		<span>用map的方式，导入导出，xxx——map工具类：</span><input type="file" name="uploadfile" />
		<input type="submit" value="上传-下载"/>
	</form>
	<h2>-----------excel Map end  -----------</h2>
	
	<h1>===========================================================</h1>
	
	<h2>-----------excel Annotation begin-----------</h2>
	<form action="${ctx}/poi/excelByAnnotationUtil.do" method="post"  enctype="multipart/form-data">
	<span>用注解的方式，处理excel，导入导出：</span><input type="file" name="uploadfile" />
		<input type="submit" value="上传-下载"/>
	</form>
	<h2>-----------excel Annotation end  -----------</h2>
	
	<h1>===========================================================</h1>
	
	<h2>-----------excel 模板   begin-----------</h2>
	<form action="${ctx}/poi/exportModelMapUtil.do" method="post">
	<span>map处理模板：</span>
		<input type="submit" value="下载"/>
	</form>
	<form action="${ctx}/poi/exportModelList.do" method="post">
	<span>list处理模板：</span>
		<input type="submit" value="下载"/>
	</form>
	
	<h2>-----------excel 模板   end  -----------</h2>
	
	
	
	

	
	
	<span>作者：小涛</span>
</body>
</html>
