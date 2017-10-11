<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../base.jsp" %>     

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>simon最专业的的excel工具</title>

</head>
<body>
<h2>Hello Man!</h2>测试excel在项目doc下，RandFFutrueBean-import-data.xls




	<h1>===========================================================</h1>
	
	<h2>-----------excel 模板   begin-----------</h2>
	<form action="${ctx}/model/exportModelMapUtil.do" method="post">
	<span>map处理模板：</span>
		<input type="submit" value="下载"/>
	</form>
	<h1>===========================================================</h1>
	<form action="${ctx}/model/exportModelList.do" method="post">
	<span>list处理模板：</span>
		<input type="submit" value="下载"/>
	</form>
	<h1>===========================================================</h1>
	<form action="${ctx}/model/exportModelFtl.do" method="post">
	<span>freemarker处理模板：</span>
		<input type="submit" value="下载"/>
	</form>
	<h2>-----------excel 模板   end  -----------</h2>
	
	
	
	
	<span>作者：小涛</span>
</body>
</html>
