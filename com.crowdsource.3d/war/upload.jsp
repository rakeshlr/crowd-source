<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%
	BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>
<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Upload your 3d content</title>
</head>
<body>
	<h1>
		3D Content Upload
	</h1>
	<form action="<%= blobstoreService.createUploadUrl("/upload") %>" 
		  method="post" enctype="multipart/form-data">
		  
		<input type="file" name="file" />
		<input type="submit" value="Upload" />
	</form>
</body>
</html>