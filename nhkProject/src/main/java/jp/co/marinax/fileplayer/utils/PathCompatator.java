package jp.co.marinax.fileplayer.utils;

import java.util.Comparator;

import jp.co.marinax.fileplayer.io.entity.FileEntity;

public class PathCompatator implements Comparator<FileEntity> {

	@Override
	public int compare(FileEntity lhs, FileEntity rhs) {
		return lhs.getPath().compareTo(rhs.getPath());
	}
}
