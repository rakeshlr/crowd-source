package org.rakesh.crowdsource;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rakesh.crowdsource.dao.Dao;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class UploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

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
		Dao.INSTANCE.addItem(req, user, blobKey, previewPic);

		// List<Entity> contentList = datastore.getListOf3dContents();
		// if (!contentList.isEmpty()) {
		// System.out.println(contentList);
		// } else {
		// System.out.println("List is empty");
		// }

		res.sendRedirect("/");
		// res.sendRedirect("/serve?blob-key=" + blobKey.getKeyString());
	}

}
