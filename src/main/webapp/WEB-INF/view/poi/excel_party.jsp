<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../base.jsp" %>     

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>simon最专业的的excel工具party</title>

</head>
<body>
<h2>Hello Man!</h2>测试excel在项目doc下，RandFFutrueBean-import-data.xls

	<h2>-----------党员  -----------</h2>
	<form action="${ctx}/party/importByMapUtilParty_0.do" method="post" enctype="multipart/form-data">
		<span>用map的方式，导入数据：</span><input type="file" name="uploadfile" />
		<input type="submit" value="上传"/>
	</form>
	<h2>-----------  ===  -----------</h2>
	<form action="${ctx}/party/importByAnnoUtilParty.do" method="post" enctype="multipart/form-data">
		<span>用注解的方式，导入数据：</span><input type="file" name="uploadfile" />
		<input type="submit" value="上传"/>
	</form>
	<h2>-----------  ===  -----------</h2>
	<form action="${ctx}/party/importByMapUtilParty.do" method="post" enctype="multipart/form-data">
		<span>entity set get：</span><input type="file" name="uploadfile" />
		<input type="submit" value="上传"/>
	</form>
	<h2>-----------党员  -----------</h2>
	
	
	<span>作者：小涛</span>
</body>
</html>
