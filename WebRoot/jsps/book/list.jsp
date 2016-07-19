<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" type="text/css" href="<c:url value='/jsps/css/book/list.css'/>">
<link rel="stylesheet" type="text/css" href="<c:url value='/jsps/pager/pager.css'/>" />
<!--    <script type="text/javascript" src="<c:url value='/jsps/pager/pager.js'/>"></script>-->
<!--	<script type="text/javascript" src="<c:url value='/jquery/jquery-1.5.1.js'/>"></script>-->
<script type="text/javascript" src="<c:url value='/jsps/js/book/list.js'/>"></script>
<ul class="books">
	<c:forEach items="${pb.dataList }" var="book">
		<c:url value='/BookServlet' var="findByAuthorUrl">
			<c:param name="method" value="findByAuthor"/>
			<c:param name="author" value="${book.author }"/>
		</c:url>
		<c:url value='/BookServlet' var="findByPressUrl">
			<c:param name="method" value="findByPress"/>
			<c:param name="press" value="${book.press }"/>
		</c:url>
		<li>
			<div class="inner">
				<a class="pic" href="<c:url value='/BookServlet?method=load&bid=${book.bid }'/>"><img src="<c:url value='/${book.image_b }'/>" border="0"/></a>
				<p class="price">
					<span class="price_n">&yen;${book.currPrice }</span>
					<span class="price_r">&yen;${book.price }</span>
					(<span class="price_s">${book.discount}折</span>)
				</p>
				<p><a id="bookname" title="${book.bname }" href="<c:url value='/BookServlet?method=load&bid=${book.bid }'/>">${book.bname }</a></p>
				<p><a href="${findByAuthorUrl }" name='P_zz' title='${book.author }'>${book.author }</a></p>
				<p class="publishing">
					<span>出 版 社：</span><a href="${findByPressUrl }">${book.press }</a>
				</p>
				<p class="publishing_time"><span>出版时间：</span>${book.publishtime }</p>
			</div>
		</li>
	</c:forEach>
</ul>

<div style="float:left; width: 100%; text-align: center;">
	<hr/>
	<br/>
	<%@include file="/jsps/pager/pager.jsp" %>
</div>

