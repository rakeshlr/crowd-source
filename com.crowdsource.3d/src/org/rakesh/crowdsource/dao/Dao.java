package org.rakesh.crowdsource.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.rakesh.crowdsource.entity.Item;
import org.rakesh.crowdsource.entity.Purchase;
import org.rakesh.crowdsource.purchase.Payload;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.users.User;

public enum Dao {
	INSTANCE;

	public List<Item> listItems() {
		EntityManager em = EMFService.get().createEntityManager();
		// read the existing entries
		Query q = em.createQuery("select m from Item m");
		List<Item> items = q.getResultList();
		return items;
	}

	public void addItem(HttpServletRequest req, User user, BlobKey contentId,
			BlobKey previewPicId) {
		Item item = new Item(user.toString(), contentId.getKeyString(),
				checkNull(req.getParameter(Datastore.TITLE)),
				checkNull(req.getParameter(Datastore.DESC)),
				checkNull(req.getParameter(Datastore.GROUP)),
				checkNull(req.getParameter(Datastore.TYPE)),
				previewPicId.getKeyString(),
				checkNull(req.getParameter(Datastore.PRICE)));
		addItem(item);
	}

	void addItem(Item item) {
		synchronized (this) {
			EntityManager em = EMFService.get().createEntityManager();
			em.persist(item);
			em.close();
		}
	}

	public List<Item> searchItems(String category, String user) {
		List<Item> result = null;

		if (category == null)
			category = "recent";

		category = category.toLowerCase();
		EntityManager em = EMFService.get().createEntityManager();
		// read the existing entries
		Query q = null;

		switch (category) {
		case "hydrocarbons":
			q = em.createQuery("select t from Item t where t.group = :group");
			q.setParameter("group", "Hydrocarbons");
			break;
		case "organic":
			q = em.createQuery("select t from Item t where t.group = :group");
			q.setParameter("group", "Organic");
			break;
		case "proteins":
			q = em.createQuery("select t from Item t where t.group = :group");
			q.setParameter("group", "Hydrocarbons");
			break;
		case "others":
			q = em.createQuery("select t from Item t where t.group = :group");
			q.setParameter("group", "Others");
			break;
		case "free":
			q = em.createQuery("select t from Item t where t.type = :group");
			q.setParameter("group", "Free");
			break;
		case "paid":
			q = em.createQuery("select t from Item t where t.type = :group");
			q.setParameter("group", "Paid");
			break;
		case "myuploads":
			q = em.createQuery("select t from Item t where t.user = :userId");
			q.setParameter("userId", user);
			break;
		default: // "recent" or default
			q = em.createQuery("select t from Item t ORDER BY t.uploadDate DESC");
			break;
		}

		if (q != null)
			result = q.getResultList();
		if ("mypurchases".equals(category)) {
			ArrayList<Item> list = new ArrayList<Item>();
			for (Item item : result) {
				if (isItemPurchased(user, item.getId()))
					list.add(item);
			}
			result = list;
		}
		return result == null ? Collections.EMPTY_LIST : result;
	}

	public Item getItem(Long itemKey) {
		synchronized (this) {
			EntityManager em = EMFService.get().createEntityManager();
			Item item = em.find(Item.class, itemKey);
			return item;
		}
	}

	private String checkNull(Object s) {
		if (s == null) {
			return "";
		}
		return s.toString();
	}

	public List<Purchase> listPurchases() {
		EntityManager em = EMFService.get().createEntityManager();
		// read the existing entries
		Query q = em.createQuery("select t from Purchase t");
		List<Purchase> purchases = q.getResultList();
		return purchases;
	}

	public void addPurchase(String seller, String buyer, long itemId,
			String orderId, long amount) {
		long start = System.currentTimeMillis();
		synchronized (this) {
			EntityManager em = EMFService.get().createEntityManager();
			EntityTransaction transaction = em.getTransaction();
			transaction.begin();
			Purchase purchase = new Purchase(seller, buyer, itemId, amount,
					orderId);
			em.persist(purchase);
			em.flush();
			transaction.commit();
			em.close();
		}
		System.out.println("Purchase Insertion completed in :"
				+ (System.currentTimeMillis() - start));
	}

	/**
	 * @param buyer
	 * @param payload
	 */
	public void addPurchase(Payload payload) {
		// try {
		String itemKey = getSellerData(payload, "item_id");
		long itemId = Long.parseLong(itemKey);
		Item item = getItem(itemId);
		String buyer = getSellerData(payload, "buyer");
		addPurchase(item.getUser(), buyer, itemId, payload.response_getter()
				.orderId_getter(), item.getPrice());

		boolean itemPurchased = isItemPurchased(buyer, itemId);
		assert itemPurchased;
		// } catch (Exception e) {
		// System.out.println("Purchase Failed : " + payload);
		// e.printStackTrace();
		// }
	}

	private String getSellerData(Payload payload, String key) {
		String sellerData = payload.request_getter().sellerData_getter();
		for (String keyValue : sellerData.split(",")) {
			String[] keyVal = keyValue.split(":");
			if (keyVal.length == 2 && keyVal[0].equals(key)) {
				return keyVal[1];
			}
		}
		// String itemKey = payload.request_getter().sellerData_getter();
		// long itemId = Long.parseLong(itemKey);
		return null;
	}

	public List<Purchase> getPurchases(String buyerId) {
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em
				.createQuery("select t from Purchase t where t.buyer = :userId");
		q.setParameter("userId", buyerId);
		List<Purchase> purchases = q.getResultList();
		return purchases;
	}

	public List<Purchase> getSales(String sellerId) {
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em
				.createQuery("select t from Purchase t where t.seller = :userId");
		q.setParameter("userId", sellerId);
		List<Purchase> pruchases = q.getResultList();
		return pruchases;
	}

	public boolean isItemPurchased(String buyerId, long itemId) {
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em
				.createQuery("select t from Purchase t where t.buyer = :userId and t.itemId = :item");
		q.setParameter("userId", buyerId);
		q.setParameter("item", itemId);
		List<Purchase> pruchases = q.getResultList();
		return !pruchases.isEmpty();
	}

	public void removePurchase(long id) {
		EntityManager em = EMFService.get().createEntityManager();
		try {
			Purchase purchase = em.find(Purchase.class, id);
			em.remove(purchase);
		} finally {
			em.close();
		}
	}

	public void addPurchase(String user, String itemKey, String orderId)
			throws Exception {
		if (itemKey != null) {
			Long itemId = Long.parseLong(itemKey);
			Item item = getItem(itemId);
			addPurchase(item.getUser().toString(), user, itemId, orderId,
					item.getPrice());
		} else
			throw new Exception("Item id is null");

	}
}
