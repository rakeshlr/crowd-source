package org.rakesh.crowdsource.entity;

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
	String itemId;
	Long amount;
	Date dateOfPurchase;
	String orderId;
	boolean claimedBySellr = false;

	public Purchase(String seller, String buyer, String itemId, String price,
			String orderId) {
		super();
		this.seller = seller;
		this.buyer = buyer;
		this.itemId = itemId;
		this.amount = price.matches("[0-9]*") ? Long.parseLong(price) : 1;
		this.orderId = orderId;
		this.dateOfPurchase = new Date();
		this.claimedBySellr = false;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
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
