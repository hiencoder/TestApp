package jp.co.marinax.fileplayer.io.entity;

import jp.co.marinax.fileplayer.app.config.Define;

public class BookEntity {
	private int id;
	private String name;
	private int folder_id;
	private String created_date;
	private String modified_date;
	private String deleted_date;
	// New Start 20151223 SonBX
	private String display_name;
	// New End 20151223 SonBX

	public BookEntity() {
		defaultValue();
	}
	
	// Mod Start 20151223 SonBX
//	public BookEntity(String nameM, int folder_idM, String created_dateM,
//			String modified_nameM, String deleted_dateM) {
//		defaultValue();
//		name = nameM;
//		folder_id = folder_idM;
//		created_date = created_dateM;
//		modified_date = modified_nameM;
//		deleted_date = deleted_dateM;
//	}
	// Mod 20151223
	public BookEntity(String nameM, String display_nameM, int folder_idM, String created_dateM,
			String modified_nameM, String deleted_dateM) {
		defaultValue();
		name = nameM;
		display_name = display_nameM;
		folder_id = folder_idM;
		created_date = created_dateM;
		modified_date = modified_nameM;
		deleted_date = deleted_dateM;
	}
	// Mod End 20151223 SonBX

	// Mod Start 20151223 SonBX
//	public BookEntity(int idM, String nameM, int folder_idM,
//			String created_dateM, String modified_nameM, String deleted_dateM) {
//		defaultValue();
//		id = idM;
//		name = nameM;
//		folder_id = folder_idM;
//		created_date = created_dateM;
//		modified_date = modified_nameM;
//		deleted_date = deleted_dateM;
//	}
	// Mod 20151223
	public BookEntity(int idM, String nameM, String display_nameM, int folder_idM,
			String created_dateM, String modified_nameM, String deleted_dateM) {
		defaultValue();
		id = idM;
		name = nameM;
		display_name = display_nameM;
		folder_id = folder_idM;
		created_date = created_dateM;
		modified_date = modified_nameM;
		deleted_date = deleted_dateM;
	}
	// Mod End 20151223

	public void defaultValue() {
		id = Define.DEFAULT_INT;
		name = Define.DEFAULT_STRING;
		folder_id = Define.DEFAULT_INT;
		created_date = Define.DEFAULT_STRING;
		modified_date = Define.DEFAULT_STRING;
		deleted_date = Define.DEFAULT_STRING;
		// New Start 20151223 SonBX
		display_name = Define.DEFAULT_STRING;
		// New End 20151223 SonBX
	}

	// set , get method
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getFolder_id() {
		return folder_id;
	}

	public void setFolder_id(int folder_id) {
		this.folder_id = folder_id;
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
	
	// New Start 20151223 SonBX
	public String getDisplay_name() {
		return display_name;
	}
	// New End 20151223 SonBX

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
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
	
	// New Start 20151223 SonBX
	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}
	// New End 20151223 SonBX
}
