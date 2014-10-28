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
<%@ page import="org.rakesh.crowdsource.dao.Dao"%>
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
<link rel="stylesheet" href="stylesheets/main.css" />
<link rel="stylesheet" href="stylesheets/gh-buttons.css" />
<!-- <link href="/stylesheets/screen.css" media="all" rel="stylesheet"
	type="text/css" /> -->
<meta name="viewport" content="width=device-width, initial-scale=1"></meta>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"
	type="text/javascript"></script>
<script type="text/javascript" src="http://www.google.com/jsapi"></script>
<script type="text/javascript">
	google.load('payments', '1.0', {
		'packages' : [ 'sandbox_config' ]
	});

	// Success handler
	var successHandler = function(status) {
		if (window.console != undefined) {
			console.log("Purchase completed successfully: ", status);
			alert("Purchase completed successfully: " + status);
			window.location.reload();
		}
	}

	// Failure handler
	var failureHandler = function(status) {
		if (window.console != undefined) {
			console.log("Purchase failed ", status);
		}
	}

	function purchase(generated_jwt) {
		if (generated_jwt == null) {
			alert("jwt token is null");
			return;
		}

		goog.payments.inapp.buy({
			'jwt' : generated_jwt,
			'success' : successHandler,
			'failure' : failureHandler
		});
	}

	function initiatePurchase(itemId) {
		if (itemId == null) {
			return;
		}
		var xmlhttp = new XMLHttpRequest();
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				//purchase(xmlhttp.responseText);
				xmlhttp
						.open("POST", "/main?jwt=" + xmlhttp.responseText,
								false);
				xmlhttp.send();
			}
		}
		xmlhttp.open("GET", "/main?item=" + itemId, true);
		xmlhttp.send();
	}

	function checkContentType() {
		if (document.getElementById("typeCombo").value == "Free") {
			document.getElementById("pricefield").disabled = true;
			document.getElementById("pricefield").value = "";
		} else {
			document.getElementById("pricefield").disabled = false;
			document.getElementById("pricefield").value = "100";
		}
	}
</script>

<script src="https://sandbox.google.com/checkout/inapp/lib/buy.js"></script>

</head>

<body>
	<div id="container">
		<header>
			<div id="topHeader">
				<h1>3D content repository</h1>
				<!-- 				<p>3D ContentRepository is an online community of
					users/developers who share and download user contributed 3D content
					of various categories.</p> -->

				<div id="profileInfo">
					<span id="signin"> <%
 	User user = userService.getCurrentUser();
 	if (user != null) {
 		out.println("Welcome, " + user.getNickname());
 		out.println("<br><a href='"
 				+ userService.createLogoutURL(request.getRequestURI())
 				+ "'> <b> LogOut <b/> </a>");
 	} else {
 		out.println("<a href='"
 				+ userService.createLoginURL(request.getRequestURI())
 				+ "'> <b> LogIn <b/> </a>");
 	}
 %>

					</span>
				</div>
			</div>
		</header>


		<section>

			<div id="tabs">
				<ul>
					<li><a href="?category=recent">All Recent</a></li>
					<li><a href="?category=hydrocarbons">Hydrocarbons</a></li>
					<li><a href="?category=organic">Organic</a></li>
					<li><a href="?category=proteins">Proteins</a></li>
					<li><a href="?category=others">Others</a></li>
					<li><a href="?category=free">Free</a></li>
					<li><a href="?category=paid">Paid</a></li>
					<%
						if (user != null) {
					%>
					<li><a href="?category=myuploads">My-Uploads</a></li>
					<%
						}
					%>
				</ul>
				<br>
			</div>
			<div id="contentList">
				<%
					String cat = (String) request.getParameter("category");
					System.out.println(cat);
					DatastoreService datastore = DatastoreServiceFactory
							.getDatastoreService();
					Key key = KeyFactory.createKey("3DContents", "contentId");
					// Run an ancestor query to ensure we see the most up-to-date
					// view of the Greetings belonging to the selected Guestbook.
					Query query = null;
					if (cat == null || cat.equals("recent")) {
						query = new Query("3DDATA", key).addSort("date",
								Query.SortDirection.DESCENDING);
						cat = "recent";
					} else if (cat.equals("hydrocarbons")) {
						query = new Query("3DDATA", key).addFilter("group",
								Query.FilterOperator.EQUAL, "Hydrocarbons");
					} else if (cat.equals("proteins")) {
						query = new Query("3DDATA", key).addFilter("group",
								Query.FilterOperator.EQUAL, "Protein");
					} else if (cat.equals("organic")) {
						query = new Query("3DDATA", key).addFilter("group",
								Query.FilterOperator.EQUAL, "Organic");
					} else if (cat.equals("others")) {
						query = new Query("3DDATA", key).addFilter("group",
								Query.FilterOperator.EQUAL, "Others");
					} else if (cat.equals("free")) {
						query = new Query("3DDATA", key).addFilter("type",
								Query.FilterOperator.EQUAL, "Free");
					} else if (cat.equals("paid")) {
						query = new Query("3DDATA", key).addFilter("type",
								Query.FilterOperator.EQUAL, "Paid");
					} else if (cat.equals("myuploads")) {
						query = new Query("3DDATA", key).addFilter("user",
								Query.FilterOperator.EQUAL, user);
					} else {
						query = new Query("3DDATA", key).addSort("date",
								Query.SortDirection.DESCENDING);
					}
					out.println("<h2>" + cat.toUpperCase() + "</h2>");
					List<Entity> contentList = datastore.prepare(query).asList(
							FetchOptions.Builder.withLimit(5));
					if (contentList.isEmpty()) {
				%>
				<p>No content available at present. Come back after some time.</p>
				<%
					} else {
						Object pageAtr = request.getParameter("page");
						System.out.println(pageAtr);
						int pageId = pageAtr == null ? 0
								: new Integer((String) pageAtr);
						int count = (pageId * 10 + 1);
				%>
				<table style="width: 100%">
					<%
						for (Entity entry : contentList) {
								String keystring = KeyFactory.keyToString(entry.getKey());
					%>
					<tr>
						<td width="20%"><img
							src="/serve?blob-key=<%=entry.getProperty("previewPic")%>" alt=""
							border='0' height='160' width='160' /></td>
						<td width="60%">
							<h3><%=entry.getProperty("title")%></h3> Uploaded on <%=entry.getProperty("date")%>
							<br>Category : <%=entry.getProperty("group")%> , Type : <%=entry.getProperty("type")%>
							<p><%=entry.getProperty("desc")%></p> <br> <%
 	if ("Free".equals(entry.getProperty("type"))   || ( user!=null && Dao.INSTANCE.isItemPurchased(user.toString(), keystring))) {
 %> <a href="/serve?blob-key=<%=entry.getProperty("content")%>">Download</a>
							<%
								} else {
							%>
							<button class="buy-button" type="button"
								onClick="initiatePurchase('<%=keystring%>');">
								Buy (Rs
								<%=entry.getProperty("price")%>)
							</button> <%
 	}
 %>
						</td>
					</tr>
					<%
						if (count++ > 10)
									break;
							}
					%>
				</table>
				<%-- 				<%
					//if(contentList.size()>count)
				%> --%>
				<a href="?page=<%=count / 10%>">Next</a>
				<%
					}
				%>
				<!-- 	<h2>Categories</h2> -->
			</div>
		</section>

		<%
			if (user != null) {
		%>
		<br>
		<h2>
			<i><b> You can upload your 3D content here! </b></i>
		</h2>
		<form action="<%=blobstoreService.createUploadUrl("/upload")%>"
			method="post" enctype="multipart/form-data">
			<table>
				<tbody>
					<tr>
						<td>3D content file</td>
						<td><input type="file" name="content" required="true"></td>
					</tr>
					<tr>
						<td>Title</td>
						<td><input type="text" name="title" required="true"></td>
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
						<td><select id="typeCombo" name="type" size="1" id="type"
							onchange="checkContentType();">
								<option>Free</option>
								<option>Paid</option>
						</select></td>
					</tr>
					<tr>
						<td>Price</td>
						<td><input type="number" id="pricefield" name="price"
							disabled="true"></td>
					</tr>
					<tr>
						<td>Preview Image</td>
						<td><input type="file" name="previewPic" accept="image/*"
							required="true"></td>
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
		<div id="ajaxOutput"></div>
	</div>
</body>
</html>


