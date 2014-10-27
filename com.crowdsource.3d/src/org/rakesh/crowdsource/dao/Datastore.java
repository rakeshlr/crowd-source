package org.rakesh.crowdsource.dao;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.rakesh.crowdsource.purchase.Payload;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;

public class Datastore {

	public static final String TYPE_3DDATA = "3DDATA";
	public static final String ID = "id";
	public static final String CONTENT_ID = "content";
	public static final String PREVIEW_PIC_ID = "previewPic";
	public static final String TYPE = "type";
	public static final String GROUP = "group";
	public static final String DESC = "desc";
	public static final String TITLE = "title";
	public static final String DATE = "date";
	public static final String USER = "user";
	public static final String PRICE = "price";
	private static final String TYPE_PURCHASE = null;
	private static final String BUYER = null;

	private static Datastore instance;
	private static DatastoreService datastore;

	private Datastore() {
	}

	public static Datastore getInstance() {
		if (instance == null)
			instance = new Datastore();
		return instance;
	}

	public DatastoreService getDatastore() {
		if (datastore == null)
			datastore = DatastoreServiceFactory.getDatastoreService();
		return datastore;
	}

	private Key getParentKey() {
		Key dataKey = KeyFactory.createKey("3DContents", "contentId");
		return dataKey;
	}

	public void put(Entity newContent) {
		getDatastore().put(newContent);
	}

	public List<Entity> getListOf3dContents() {
		Key key = getParentKey();
		// Run an ancestor query to ensure we see the most up-to-date
		// view of the Greetings belonging to the selected Guestbook.
		Query query = new Query(TYPE_3DDATA, key).addSort(DATE,
				Query.SortDirection.DESCENDING);
		List<Entity> contentList = getDatastore().prepare(query).asList(
				FetchOptions.Builder.withLimit(5));
		return contentList;
	}

	public Entity getEntityWithKey(String itemKey) {
		Entity entity = null;
		try {
			Key key = KeyFactory.stringToKey(itemKey);
			entity = getDatastore().get(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entity;
	}

	public void addPurchase(Payload payload_1, User user) {

		Entity newContent = new Entity(TYPE_PURCHASE, getParentKey());
		newContent.setProperty(BUYER, user);
		newContent.setProperty(DATE, new Date());
		newContent.setProperty(TITLE, payload_1.request_getter().name_getter());
		newContent.setProperty(DESC, payload_1.request_getter().name_getter());
		// newContent.setProperty(GROUP, req.getParameter(GROUP));
		put(newContent);

	}

	private String checkNull(String s) {
		if (s == null) {
			return "";
		}
		return s;
	}

	public void addItem(HttpServletRequest req, User user, BlobKey blobKey,
			BlobKey previewPic) {
		Entity newContent = new Entity(TYPE_3DDATA, getParentKey());
		newContent.setProperty(USER, user);
		newContent.setProperty(DATE, new Date());
		newContent.setProperty(TITLE, checkNull(req.getParameter(TITLE)));
		newContent.setProperty(DESC, checkNull(req.getParameter(DESC)));
		newContent.setProperty(GROUP, checkNull(req.getParameter(GROUP)));
		newContent.setProperty(TYPE, checkNull(req.getParameter(TYPE)));
		newContent.setProperty(PREVIEW_PIC_ID,
				checkNull(previewPic.getKeyString()));
		newContent.setProperty(CONTENT_ID, checkNull(blobKey.getKeyString()));
		newContent.setProperty(ID, newContent.getKey());
		put(newContent);
	}

}
