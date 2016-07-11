<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div>
    <ul>
        <c:choose>
            <c:when test="${empty sessionScope.sessionUser }">
                <a href="<c:url value='/jsps/user/login.jsp'/>" target="_parent">会员登录</a> |&nbsp; 
                <a href="<c:url value='/jsps/user/regist.jsp'/>" target="_parent">注册会员</a>
            </c:when>
            <c:otherwise>
                会员：${sessionScope.sessionUser.loginname }&nbsp;&nbsp;|&nbsp;&nbsp;
                <a href="<c:url value='/CartItemServlet?method=myCart'/>" target="body">我的购物车</a>&nbsp;&nbsp;|&nbsp;&nbsp;
                <a href="<c:url value='/OrderServlet?method=myOrders'/>" target="body">我的订单</a>&nbsp;&nbsp;|&nbsp;&nbsp;
                <a href="<c:url value='/jsps/user/pwd.jsp'/>" target="body">修改密码</a>&nbsp;&nbsp;|&nbsp;&nbsp;
                <a href="<c:url value='/UserServlet?method=quit'/>" target="_parent">退出</a>
            </c:otherwise>
        </c:choose>
    </ul>
</div>
