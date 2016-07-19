<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style type="text/css">

	/*	.left-nav:first-child{
			font: normal 16px 'Microsoft YaHei';
			padding: 8px;
			background-color: #B1191A !important;
		}*/
	.left-nav a{
		text-decoration: none;
		font: normal 14px 'Microsoft YaHei';
		cursor: pointer;
		float:left;
		padding: 8px;
	}
	.left-nav li{
		color: #fff;
		text-indent: 20px;
		list-style: none;
		float: left;
		width: 100%;
		text-align: center;
		/*padding: 5px 20px 5px 0px;*/
		border-bottom: 1px dotted #fff;
	}
	.left-nav li:hover{
		color: #c81623;
		background-color: #fff;
	}
	.left-nav-level-1 li{
		background-color: #c81623;
		border-left: 1px solid #c81623;
	}
	.left-nav-level-1 li:first-child{
		border-top: 1px solid #c81623;
	}
	.left-nav-level-1 li:last-child{
		border-bottom: 1px solid #c81623;
	}
	.left-nav-level-1 li:hover{
		background-color: #fff;
		color: #c81623;
	}
	.left-nav-level-2 {
		display: none;
	}
	.left-nav-level-2 li{
		background-color: #e81c1c;
		text-indent: 40px;
		border-top:none;
		border-left:none;
		color: #fff;
	}
	.left-nav-level-2 a{
		text-align: left;
		width: 100%;
		color: #fff;
	}
	.left-nav-level-2 li:hover, .left-nav-level-2 a:hover{
		color: #e81c1c;
	}
	.left-nav-level-2 li:first-child{
		/*border-top: 1px dotted #fff;*/
		/*margin-top: 3px;*/
	}
	.left-nav-level-2 li:last-child{
		border-bottom:none;
	}
</style>
<div class="left-nav">
	<ul class="left-nav-level-1">
		<li class="main-category">
			<a>图书分类</a>
		</li>
		<c:forEach items="${parents}" var="parent">
			<li class="level-1">
				<a>${parent.cname}</a>
				<ul class="left-nav-level-2">
					<c:forEach items="${parent.children}" var="child">
						<li>
							<a data-cid="${child.cid}">${child.cname}</a>
						</li>
					</c:forEach>
				</ul>
			</li>
		</c:forEach>
	</ul>
</div>
<script>
	$(function () {
		var path = '<c:url value='/BookServlet'/>', param = {method: 'findByCategory'};
		$.each($('.left-nav li').slice(1), function () {
			$(this).on('click', function () {
				if ($(this).hasClass('level-1')) {
					$('.left-nav-level-2').hide();
					$(this).children('ul').show();
				} else {
					$('.content').load(path, $.extend(param, $(this).attr('data-cid')));
				}
			});
		});
	});
</script>
