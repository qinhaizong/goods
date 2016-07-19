<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<form action="<c:url value='/BookServlet'/>" method="get">
	<input type="hidden" name="method" value="findByCombination"/>
	<table align="center">
		<tr>
			<td>书名：</td>
			<td><input type="text" name="bname"/></td>
		</tr>
		<tr>
			<td>作者：</td>
			<td><input type="text" name="author"/></td>
		</tr>
		<tr>
			<td>出版社：</td>
			<td><input type="text" name="press"/></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>
				<input type="submit" value="搜　　索"/>
				<input type="reset" value="重新填写"/>
			</td>
		</tr>
	</table>
</form>
