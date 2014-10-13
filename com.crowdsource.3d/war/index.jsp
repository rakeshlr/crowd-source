<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@ page
	import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.Entity"%>
<%@ page import="com.google.appengine.api.datastore.FetchOptions"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ page import="com.google.appengine.api.datastore.Query"%>
<%@ page import="java.util.List"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page
	import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%
	BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();
	UserService userService = UserServiceFactory.getUserService();
%>

<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>Crowd source 3d</title>
<link rel="stylesheet"
	href="//ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/themes/base/jquery-ui.css" />
<link rel="stylesheet" href="stylesheets/photohunt.css" />
<link rel="stylesheet" href="stylesheets/gh-buttons.css" />
</head>

<body>
	<div id="container">
		<header>
			<div id="topHeader">
				<h1>3D content repository</h1>
				<div id="profileInfo">
					<span id="signin"> <%
 	User user = userService.getCurrentUser();
 	if (user != null) {
 		out.println("Welcome, " + user.getNickname());
 		out.println("<br><a href='"
 				+ userService.createLogoutURL(request.getRequestURI())
 				+ "'> <b> LogOut <b/> </a>");
 	} else {
 		out.println("Please <a href='"
 				+ userService.createLoginURL(request.getRequestURI())
 				+ "'> <b> LogIn <b/> </a>");
 	}
 %>

					</span>
				</div>
			</div>
		</header>


		<section>
			<h1>Welcome to 3D ContentRepository!</h1>

			3D ContentRepository is an online community of users/developers who
			share and download user contributed 3D content of various categories.

			<br>

			<h2>Recently added</h2>

			<%
				DatastoreService datastore = DatastoreServiceFactory
						.getDatastoreService();
				Key key = KeyFactory.createKey("3DContents", "contentId");
				// Run an ancestor query to ensure we see the most up-to-date
				// view of the Greetings belonging to the selected Guestbook.
				Query query = new Query("3DDATA", key).addSort("date",
						Query.SortDirection.DESCENDING);
				List<Entity> contentList = datastore.prepare(query).asList(
						FetchOptions.Builder.withLimit(5));
				if (contentList.isEmpty()) {
			%>

			<p>No content available at present. Come back after some time.</p>
			<%
				} else {
			%>
			<table style="width: 100%">
				<tr>
					<%
						int count = 1;
							for (Entity entry : contentList) {
					%>
					<%-- <td width="20%"><%=entry.getProperty("title")%></td> --%>

					<img src="/serve?blob-key=<%=entry.getProperty("previewPic")%>"
						alt="" border='1' height='100' width='100' />

					<%
						if (count++ > 8)
									break;

							}
					%>
				</tr>
			</table>
			<%
				}
			%>
			<h2>Categories</h2>
		</section>



		<%
			if (user != null) {
		%>
		<br>
		<p>
			<i><b> You can upload your 3D content here! </b></i>
		</p>
		<form action="<%=blobstoreService.createUploadUrl("/upload")%>"
			method="post" enctype="multipart/form-data">
			<table>
				<tbody>
					<tr>
						<td>3D content file</td>
						<td><input type="file" name="content"></td>
					</tr>
					<tr>
						<td>Title</td>
						<td><input type="text" name="title"></td>
					<tr>
						<td>Basic Description</td>
						<td><TEXTAREA NAME="desc" ROWS="4"></TEXTAREA></td>
					</tr>
					<tr>
						<td>Group</td>
						<td><select name="group" size="1" id="type">
								<option>Hydrocarbons</option>
								<option>Organic</option>
								<option>Protein</option>
								<option>Others</option>
						</select></td>
					</tr>
					<tr>
						<td>Content Type</td>
						<td><select name="type" size="1" id="type">
								<option>Free</option>
								<option>Paid</option>
						</select></td>
					</tr>
					<tr>
						<td>Preview Image</td>
						<td><input type="file" name="previewPic" accept="image/*"></td>
					</tr>
					<tr>
						<td></td>
						<td><input type="submit" value="Upload" /></td>
					</tr>
				</tbody>
			</table>
			<br>
		</form>

		<%
			}
		%>
		<footer> Crowd source platform - Copyright (c) 2004, All
			Rights Reserved. </footer>
	</div>
</body>
</html>
