package org.rakesh.crowdsource.dao;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rakesh.crowdsource.entity.Item;
import org.rakesh.crowdsource.entity.Purchase;

import static org.junit.Assert.*;

public class DaoTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Test
	public void testInsert1() {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		assertEquals(0,
				ds.prepare(new Query("Purchase")).countEntities(withLimit(10)));
		ds.put(new Entity("Purchase"));
		ds.put(new Entity("Purchase"));
		assertEquals(2,
				ds.prepare(new Query("Purchase")).countEntities(withLimit(10)));
	}

	@Test
	public void testPurchase() {
		Dao.INSTANCE.addPurchase("seller", "buyer", 123, "345", 10);
		List<Purchase> listPurchases = Dao.INSTANCE.listPurchases();
		assertEquals(1, listPurchases.size());
		assertEquals(1, Dao.INSTANCE.getPurchases("buyer").size());
		assertEquals(1, Dao.INSTANCE.getSales("seller").size());
		boolean itemPurchased = Dao.INSTANCE.isItemPurchased("buyer", 123);
		assertEquals(true, itemPurchased);
		System.out.println(listPurchases);

	}

	@Test
	public void testItems() {
		String user = "User_Provider";
		Item item = new Item(user, "ContentId", "title", "desc", "Others",
				"Paid", "2423424", "10");
		Dao dao = Dao.INSTANCE;
		dao.addItem(item);
		List<Item> items = dao.listItems();
		System.out.println(items);
		System.out.println(dao.getItem(items.get(0).getId()));
		assertEquals(1, items.size());
		assertEquals(1, dao.searchItems("others", user).size());
		assertEquals(1, dao.searchItems("paid", user).size());
		assertEquals(1, dao.searchItems("myuploads", user).size());
	}

}
