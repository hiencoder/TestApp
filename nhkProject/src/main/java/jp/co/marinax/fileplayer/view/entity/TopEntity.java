package jp.co.marinax.fileplayer.view.entity;

public class TopEntity {
	private int mType;
	private String mName;
	private String mDLTime;
	private int mBookId;
	private int mFileId;

	public TopEntity() {
		defaultvalue();
	}

	public TopEntity(int bookId, int fileId, String name, String title, int type) {
		defaultvalue();
		mBookId = bookId;
		mFileId = fileId;
		mName = name;
		mDLTime = title;
		mType = type;
	}

	public int getBookId() {
		return mBookId;
	}

	public void setBookId(int bookId) {
		mBookId = bookId;
	}

	public int getFileId() {
		return mFileId;
	}

	public void setFileId(int fileId) {
		mFileId = fileId;
	}

	public void defaultvalue() {
		mName = "";
		mDLTime = "";
		mType = -1;
	}

	public int getType() {
		return mType;
	}

	public void setType(int type) {
		mType = type;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getDLTime() {
		return mDLTime;
	}

	public void setDLTime(String dLTime) {
		mDLTime = dLTime;
	}

}
