package org.rakesh.crowdsource.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Purchase {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Long id;
	String seller;
	String buyer;
	Long itemId;
	Long amount;
	Date dateOfPurchase;
	String orderId;
	boolean claimedBySellr = false;

	public Purchase(String seller, String buyer, Long itemId, Long price,
			String orderId) {
		super();
		this.seller = seller;
		this.buyer = buyer;
		this.itemId = itemId;
		this.amount = (price == null || price == 0) ? price : 1;
		this.orderId = orderId;
		this.dateOfPurchase = new Date();
		this.claimedBySellr = false;
	}

	@Override
	public String toString() {
		return seller + "," + buyer + "," + itemId + "," + seller + ","
				+ amount + "," + orderId + "," + dateOfPurchase + ","
				+ claimedBySellr;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public String getBuyer() {
		return buyer;
	}

	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Date getDateOfPurchase() {
		return dateOfPurchase;
	}

	public String getDateOfPurchaseString() {
		SimpleDateFormat ft = new SimpleDateFormat("dd.mm.yyyy HH:mm");
		return ft.format(dateOfPurchase);
	}

	public void setDateOfPurchase(Date dateOfPurchase) {
		this.dateOfPurchase = dateOfPurchase;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public boolean isClaimedBySellr() {
		return claimedBySellr;
	}

	public void setClaimedBySellr(boolean claimedBySellr) {
		this.claimedBySellr = claimedBySellr;
	}

}
