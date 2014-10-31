package org.rakesh.crowdsource.dao;

import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.rakesh.crowdsource.entity.Item;
import org.rakesh.crowdsource.entity.Purchase;
import org.rakesh.crowdsource.purchase.Payload;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
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
		synchronized (this) {
			EntityManager em = EMFService.get().createEntityManager();
			Item item = new Item(user.toString(), contentId.getKeyString(),
					checkNull(req.getParameter(Datastore.TITLE)),
					checkNull(req.getParameter(Datastore.DESC)),
					checkNull(req.getParameter(Datastore.GROUP)),
					checkNull(req.getParameter(Datastore.TYPE)),
					previewPicId.getKeyString(),
					checkNull(req.getParameter(Datastore.PRICE)));
			em.persist(item);
			em.close();
		}
	}

	private String checkNull(String s) {
		if (s == null) {
			return "";
		}
		return s;
	}

	public List<Purchase> listPurchases() {
		EntityManager em = EMFService.get().createEntityManager();
		// read the existing entries
		Query q = em.createQuery("select t from Purchase t");
		List<Purchase> purchases = q.getResultList();
		return purchases;
	}

	public void addPurchase(String seller, String buyer, String itemId,
			String orderId, String amount) {
		synchronized (this) {
			EntityManager em = EMFService.get().createEntityManager();
			Purchase purchase = new Purchase(seller, buyer, itemId, amount,
					orderId);
			em.persist(purchase);
			em.close();
		}
	}

	public void addPurchase(User user, Payload payload) {

		String itemKey = payload.request_getter().sellerData_getter();
		Entity item = Datastore.getInstance().getEntityWithKey(itemKey);

		addPurchase(item.getProperty(Datastore.USER).toString(),
				user.toString(), KeyFactory.keyToString(item.getKey()), new Random(10000).nextLong() + "",
				item.getProperty(Datastore.PRICE).toString());
		// addPurchase(item.getProperty(Datastore.USER).toString(),
		// user.toString(), item.getKey().toString(), payload
		// .response_getter().orderId_getter(),
		// item.getProperty(Datastore.PRICE).toString());
	}

	public List<Purchase> getPurchases(String buyerId) {
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em
				.createQuery("select t from Purchase t where t.buyer = :userId");
		q.setParameter("userId", buyerId);
		List<Purchase> pruchases = q.getResultList();
		return pruchases;
	}

	public List<Purchase> getSales(String sellerId) {
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em
				.createQuery("select t from Purchase t where t.seller = :userId");
		q.setParameter("userId", sellerId);
		List<Purchase> pruchases = q.getResultList();
		return pruchases;
	}

	public boolean isItemPurchased(String buyerId, String itemId) {
//		System.out.println(listPurchases().isEmpty());
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em
				.createQuery("select t from Purchase t where t.seller = :userId and t.itemId = :item");
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

	public void addPurchase(User user, String itemKey) {
		Entity item = Datastore.getInstance().getEntityWithKey(itemKey);

		addPurchase(item.getProperty(Datastore.USER).toString(),
				user.toString(), itemKey, Math.random() + "",
				item.getProperty(Datastore.PRICE).toString());	// TODO Auto-generated method stub
		
	}
}
