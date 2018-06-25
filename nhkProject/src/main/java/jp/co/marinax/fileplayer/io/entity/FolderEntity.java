package jp.co.marinax.fileplayer.io.entity;

import jp.co.marinax.fileplayer.app.config.Define;

import org.json.JSONException;
import org.json.JSONObject;

public class FolderEntity {
	private int id;
	private String name;
	private int parent_id;
	private int bookId;
	private int fileId;
	private int mType;
	private String created_date;
	private String modified_date;
	private String deleted_date;
	private String downloadTime;
	// New Start 20151008 SonBX
	private String temp_name;
	private int can_redownload;
	// New End 20151008 SonBX

	// other variable
	private boolean check;
	private int typeImage; // isFolder , is Audio , is Book
	private String mPath;

	// if is folder , it is a member FolderTable
	public FolderEntity() {
		defaultValue();
	}
	
	// Mod Start 20151008 SonBX
//	public FolderEntity(String nameM, int parentIdM, String create_dateM,
//			String modified_dateM, String delete_dateM) {
//		defaultValue();
//		name = nameM;
//		parent_id = parentIdM;
//		created_date = create_dateM;
//		modified_date = modified_dateM;
//		deleted_date = delete_dateM;
//
//	}
	// Mod 20151008
	public FolderEntity(String nameM, int parentIdM, String create_dateM,
			String modified_dateM, String delete_dateM, String temp_nameM, int can_redownloadM) {
		defaultValue();
		name = nameM;
		parent_id = parentIdM;
		created_date = create_dateM;
		modified_date = modified_dateM;
		deleted_date = delete_dateM;
		
		temp_name = temp_nameM;
		can_redownload = can_redownloadM;
	}
	// Mod End 20151008 SonBX

	public void defaultValue() {
		fileId = Define.DEFAULT_INT;
		bookId = Define.DEFAULT_INT;
		mType = Define.DEFAULT_INT;
		id = Define.DEFAULT_INT;
		name = Define.DEFAULT_STRING;
		parent_id = 0;
		created_date = Define.DEFAULT_STRING;
		modified_date = Define.DEFAULT_STRING;
		deleted_date = Define.DEFAULT_STRING;
		downloadTime = Define.DEFAULT_STRING;
		check = false;
		typeImage = Define.DEFAULT_INT;
		mPath = Define.DEFAULT_STRING;
		// New Start 20151008 SonBX
		temp_name = Define.DEFAULT_STRING;
		can_redownload = Define.DEFAULT_INT;
		// New End 20151008 SonBX
	}

	public String toString() {
		JSONObject json = new JSONObject();

		// put data
		try {
			json.put("01. id", id);
			json.put("02. name", name);
			json.put("03. parent_id", parent_id);
			json.put("04. bookId", bookId);
			json.put("05. fileId", fileId);
			json.put("06. mType", mType);
			json.put("07. created_date", created_date);
			json.put("08. modified_date", modified_date);
			json.put("09. deleted_date", deleted_date);
			json.put("10. downloadTime", downloadTime);

			json.put("11. check", check);
			json.put("12. typeImage", typeImage);
			json.put("13. mPath", mPath);
			// New Start 20151008 SonBX
			json.put("14. temp_name", temp_name);
			json.put("15. can_redownload", can_redownload);
			// New End 20151008 SonBX
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	public String getDownloadTime() {
		return downloadTime;
	}

	public void setDownloadTime(String downloadTime) {
		this.downloadTime = downloadTime;
	}

	public int getBookId() {
		return bookId;
	}

	public int getFileId() {
		return fileId;
	}

	public int getType() {
		return mType;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public void setType(int type) {
		mType = type;
	}

	public boolean isCheck() {
		return check;
	}

	public int getTypeImage() {
		return typeImage;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public void setTypeImage(int typeImage) {
		this.typeImage = typeImage;
	}

	// set get method
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getParent_id() {
		return parent_id;
	}

	public String getCreated_date() {
		return created_date;
	}

	public String getModified_date() {
		return modified_date;
	}

	public String getDeleted_date() {
		return deleted_date;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParent_id(int parent_id) {
		this.parent_id = parent_id;
	}

	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}

	public void setModified_date(String modified_date) {
		this.modified_date = modified_date;
	}

	public void setDeleted_date(String deleted_date) {
		this.deleted_date = deleted_date;
	}

	public String getPath() {
		return mPath;
	}

	public void setPath(String path) {
		mPath = path;
	}

	// New Start 20151008 SonBX
	public String getTemp_name() {
		return temp_name;
	}

	public void setTemp_name(String temp_name) {
		this.temp_name = temp_name;
	}

	public int getCan_redownload() {
		return can_redownload;
	}

	public void setCan_redownload(int can_redownload) {
		this.can_redownload = can_redownload;
	}
	// New End 20151008 SonBX
}
