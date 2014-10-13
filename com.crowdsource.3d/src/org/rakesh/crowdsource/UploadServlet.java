package org.rakesh.crowdsource;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class UploadServlet extends HttpServlet {
	private BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
		// Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
		BlobKey blobKey = blobs.get("content");
		BlobKey previewPic = blobs.get("previewPic");
		// BlobKey blobKey = blobs.get("content");
		// BlobKey blobKey = blobs.get("content");
		// BlobKey blobKey = blobs.get("content");

		String title = req.getParameter("title");
		String desc = req.getParameter("desc");
		String group = req.getParameter("group");
		String type = req.getParameter("type");

		Date date = new Date();
		Key dataKey = KeyFactory.createKey("3DContents", "contentId");
		Entity newContent = new Entity("3DDATA", dataKey);
		newContent.setProperty("user", user);
		newContent.setProperty("date", date);
		newContent.setProperty("title", title);
		newContent.setProperty("desc", desc);
		newContent.setProperty("group", group);
		newContent.setProperty("type", type);
		newContent.setProperty("previewPic", previewPic);
		newContent.setProperty("content", blobKey.getKeyString());
		
		
		System.out.println(title);

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		datastore.put(newContent);
		
		
		Key key = KeyFactory.createKey("3DContents", "contentId");
		// Run an ancestor query to ensure we see the most up-to-date
		// view of the Greetings belonging to the selected Guestbook.
		Query query = new Query("3DDATA", key).addSort("date",
				Query.SortDirection.DESCENDING);
		List<Entity> contentList = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(5));
		
		
		
		if (!contentList.isEmpty()) {
			System.out.println(contentList);
		}
		else{
			System.out.println("List is empty");
		}
		
		
		

		res.sendRedirect("/");

		// if (blobKey == null) {
		// res.sendRedirect("/");
		// } else {
		// res.sendRedirect("/serve?blob-key=" + blobKey.getKeyString());
		// }
	}
}
