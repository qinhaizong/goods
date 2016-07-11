<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML>
<html lang="zh_CN">
    <head>
        <meta charset="UTF-8">
        <title>main</title>
        <link rel="stylesheet" type="text/css" href="<c:url value='/jsps/css/main.css'/>">
    </head>
    <body>
        <header>
            <jsp:include page="/jsps/top.jsp"/>
        </header>
        <main>
            <div class="search">
                <div class="logo"></div>
                <jsp:include page="/jsps/search.jsp"/>
            </div>
            <div class="navbar">
                <iframe frameborder="0" src="<c:url value='/CategoryServlet?method=findAll'/>" name="left"></iframe>
            </div>
            <div class="content">
                <iframe frameborder="0" src="<c:url value='/jsps/body.jsp'/>" name="body"></iframe>
            </div>
        </main>
    </body>
</html>
