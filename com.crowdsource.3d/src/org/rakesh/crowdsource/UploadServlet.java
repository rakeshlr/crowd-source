package org.rakesh.crowdsource;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rakesh.crowdsource.dao.Datastore;

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
	/**
	 * 
	 */
	private static final long serialVersionUID = 5656825457880117827L;
	private BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		System.out.println("Adding new 3d item ");
		User user = (User) req.getAttribute("user");
		if (user == null) {
			UserService userService = UserServiceFactory.getUserService();
			user = userService.getCurrentUser();
		}

		Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
		// Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
		BlobKey blobKey = blobs.get("content");
		BlobKey previewPic = blobs.get("previewPic");

		Datastore datastore = Datastore.getInstance();
		datastore.addItem(req, user, blobKey, previewPic);

//		List<Entity> contentList = datastore.getListOf3dContents();
//		if (!contentList.isEmpty()) {
//			System.out.println(contentList);
//		} else {
//			System.out.println("List is empty");
//		}

		res.sendRedirect("/");

		// if (blobKey == null) {
		// res.sendRedirect("/");
		// } else {
		// res.sendRedirect("/serve?blob-key=" + blobKey.getKeyString());
		// }
	}

}
