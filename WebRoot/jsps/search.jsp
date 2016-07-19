<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style type="text/css">
    #form1{
        margin: 10px;
    }
    .search-input {
        float: left;
        width: 300px;
        height: 29px;
        margin:0px;
        outline: none;
        border: 2px solid #e81c1c;
    }
    .search-input:active, .search-input:hover{
        border: 2px solid #e81c1c;
    }
    .search-btn{
        float: left;
        text-transform: none;
        text-decoration: none;
        font-size: 16px;
        font-family: "Microsoft YaHei";
        color: #fff;
        background-color: #e81c1c;
        padding: 6px 15px;
		cursor: pointer;
    }
	.search-more{
        float: left;
        text-transform: none;
        text-decoration: none;
        font-size: 16px;
        font-family: "Microsoft YaHei";
        padding: 5px 15px;
        color: #e81c1c;
        border: 1px solid #e81c1c;
        margin-left: 10px;
		cursor: pointer;
    }
</style>
<form action="<c:url value='/BookServlet'/>" method="get" target="body" id="form1">
    <input type="hidden" name="method" value="findByBname"/>
    <input class="search-input" type="text" name="bname"/>
    <a class="search-btn">搜索</a>
    <a class="search-more" target="body">高级搜索</a>
</form>
<script>
	$(function () {
		$('.search-more').on('click', function () {
			$('.content').load('<c:url value="/jsps/gj.jsp"/>');
		});
	});
</script>
