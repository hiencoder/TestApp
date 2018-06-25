package jp.co.marinax.fileplayer.io.entity;

import jp.co.marinax.fileplayer.app.config.Define;

public class FileEntity {
	private int id;
	private String name;
	private String url;
	private String key;
	private String path;
	private String downloaded_time;
	private int book_id;
	private int folder_id;
	private int type; // type = 0 : TEXT and type = 1: AUDIO
	private String created_time;
	private String modified_time;
	private String deleted_time;

	// initialization
	public FileEntity() {
		defaultValue();
	}

	public FileEntity(String nameM, String urlM, String keyM, String pathM,
			String downloaded_timeM, int book_idM, int folder_idM, int typeM, String created_timeM,
			String modified_timeM, String deleted_timeM) {
		defaultValue();
		name = nameM;
		url = urlM;
		key = keyM;
		path = pathM;
		downloaded_time = downloaded_timeM;
		book_id = book_idM;
		folder_id = folder_idM;
		type = typeM;
		created_time = created_timeM;
		modified_time = modified_timeM;
		deleted_time = deleted_timeM;
	}

	public FileEntity(int idM, String nameM, String urlM, String keyM, String pathM,
			String downloaded_timeM, int book_idM, int folder_idM, int typeM, String created_timeM,
			String modified_timeM, String deleted_timeM) {
		defaultValue();
		id = idM;
		name = nameM;
		url = urlM;
		key = keyM;
		path = pathM;
		downloaded_time = downloaded_timeM;
		book_id = book_idM;
		folder_id = folder_idM;
		type = typeM;
		created_time = created_timeM;
		modified_time = modified_timeM;
		deleted_time = deleted_timeM;
	}

	// default value
	public void defaultValue() {
		id = Define.DEFAULT_INT;
		name = Define.DEFAULT_STRING;
		url = Define.DEFAULT_STRING;
		key = Define.DEFAULT_STRING;
		path = Define.DEFAULT_STRING;
		downloaded_time = Define.DEFAULT_STRING;
		book_id = Define.DEFAULT_INT;
		folder_id = Define.DEFAULT_INT;
		created_time = Define.DEFAULT_STRING;
		modified_time = Define.DEFAULT_STRING;
		deleted_time = Define.DEFAULT_STRING;
	}

	// set , get method
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getKey() {
		return key;
	}

	public String getPath() {
		return path;
	}

	public String getDownloaded_time() {
		return downloaded_time;
	}

	public int getBook_id() {
		return book_id;
	}

	public int getFolder_id() {
		return folder_id;
	}

	public String getCreated_time() {
		return created_time;
	}

	public String getModified_time() {
		return modified_time;
	}

	public String getDeleted_time() {
		return deleted_time;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setDownloaded_time(String downloaded_time) {
		this.downloaded_time = downloaded_time;
	}

	public void setBook_id(int book_id) {
		this.book_id = book_id;
	}

	public void setFolder_id(int folder_id) {
		this.folder_id = folder_id;
	}

	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}

	public void setModified_time(String modified_time) {
		this.modified_time = modified_time;
	}

	public void setDeleted_time(String deleted_time) {
		this.deleted_time = deleted_time;
	}

	public int getType() {
		return type;
	}

	public void setType(int typeM) {
		type = typeM;
	}
}
