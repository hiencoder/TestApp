package jp.co.marinax.fileplayer.io.entity;

import jp.co.marinax.fileplayer.app.config.Define;

public class BookMarkEntity {
	private int mId;
	private String mName;
	private String mUrl;
	
	public BookMarkEntity() {
		defaultValue();
	}
	
	public BookMarkEntity(String name, String url) {
		defaultValue();
		mName = name;
		mUrl = url;
	}

	public void defaultValue() {
		mId = Define.DEFAULT_INT;
		mName = Define.DEFAULT_STRING;
		mUrl = Define.DEFAULT_STRING;
	}

	public int getId() {
		return mId;
	}

	public String getName() {
		return mName;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setId(int id) {
		mId = id;
	}

	public void setName(String name) {
		mName = name;
	}

	public void setUrl(String url) {
		mUrl = url;
	}
}
