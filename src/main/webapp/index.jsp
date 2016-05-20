<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import = "java.util.ResourceBundle" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en" ng-app="app">
<head>
<title>Video Content Portal</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<% ResourceBundle resource = ResourceBundle.getBundle("application");
  boolean production = Boolean.valueOf(resource.getString("production"));%>
<c:choose>
	<c:when test="<%=production%>">
		<link rel="stylesheet" href="/static/css/css-final.min.css" type="text/css" >
	</c:when>
	<c:otherwise>
		<link rel="stylesheet" href="/static/css/bootstrap.css">
		<link rel="stylesheet" href="/static/css/bootstrap-theme.css">
		<link rel="stylesheet" href="/static/css/dashboard.css">
		<link rel="stylesheet" href="/static/css/style.css" type='text/css' media="all" />
		<link rel="stylesheet" href="/static/css/app.css">
	</c:otherwise>
</c:choose>
  


</head>
<body ng-cloak>

	<div ng-include="'static/html/include/navbar.html'"></div>
	<ng-view></ng-view>
	
	<c:choose>
		<c:when test="<%=production%>">
			<script type="text/javascript" src="/static/js/js-final.min.js" ></script>


			
<!-- 			<script type="text/javascript" src="/static/js/js-final.min.js" ></script>   
			<script type="text/javascript" src="/static/angular/angular-final.min.js" ></script>  -->
		</c:when>
		<c:otherwise>
			<script type="text/javascript" src="/static/js/jquery-1.12.3.js"></script>
			<script type="text/javascript" src="/static/js/angular.js"></script>
			<script type="text/javascript" src="/static/js/angular-route.js"></script>
			<script type="text/javascript" src="/static/js/angular-resource.js"></script>
			<script type="text/javascript" src="/static/angular/app-filters.js"></script>
			<script type="text/javascript" src="/static/angular/common-controllers.js"></script>
			<script type="text/javascript" src="/static/angular/admin-controllers.js"></script>
			<script type="text/javascript" src="/static/angular/user-controllers.js"></script>
			<script type="text/javascript" src="/static/angular/app-services.js"></script>
			<script type="text/javascript" src="/static/angular/app-interceptors.js"></script>
			<script type="text/javascript" src="/static/angular/app.js"></script>
			<script type="text/javascript" src="/static/js/bootstrap.js"></script>
		  	<script type="text/javascript" src="/static/js/ng-file-upload.js"></script>
		  	<script type="text/javascript" src="/static/js/angular-validation-match.js"></script>
		</c:otherwise>
	</c:choose>
</body>
</html>