package org.rakesh.crowdsource.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	String user;
	String contentId;
	Date uploadDate;
	String title;
	String desc;
	String group;
	String type;
	String previewId;
	long price = 0;
	int downloads = 0;

	public Item(String user, String contentId, String title, String desc,
			String group, String type, String previewId, String price) {
		super();
		this.user = user;
		this.contentId = contentId;
		this.title = title;
		this.desc = desc;
		this.group = group;
		this.type = type;
		this.previewId = previewId;
		this.price = price.matches("[0-9]+") ? Long.parseLong(price) : 0;
		this.uploadDate = new Date();
	}

	@Override
	public String toString() {
		return user + "," + contentId + "," + title + "," + desc + "," + group
				+ "," + type + "," + previewId + "," + price;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public Date getUploadDate() {
		return uploadDate;
	}
	
	public String getUploadDateString() {
		SimpleDateFormat ft = new SimpleDateFormat("dd.mm.yyyy HH:mm");
		return ft.format(uploadDate);
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPreviewId() {
		return previewId;
	}

	public void setPreviewId(String previewId) {
		this.previewId = previewId;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public int getDownloads() {
		return downloads;
	}

	public void setDownloads(int downloads) {
		this.downloads = downloads;
	}

}
