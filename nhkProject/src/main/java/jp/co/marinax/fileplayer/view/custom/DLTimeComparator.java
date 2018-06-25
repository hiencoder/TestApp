package jp.co.marinax.fileplayer.view.custom;

import java.util.Comparator;

import jp.co.marinax.fileplayer.io.entity.FolderEntity;

/**
 * A custom Comparator - Comparing 2 objects by Downloaded time
 * 
 * @author SONBX
 * 
 */
public class DLTimeComparator implements Comparator<FolderEntity> {

	@Override
	public int compare(FolderEntity lhs, FolderEntity rhs) {
		return rhs.getDownloadTime().compareTo(lhs.getDownloadTime());
	}

}
