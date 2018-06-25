package jp.co.marinax.fileplayer.view.custom;

import java.util.Comparator;

import jp.co.marinax.fileplayer.io.entity.FolderEntity;

/**
 * A custom Comparator - Comparing 2 objects by Title
 * 
 * @author SONBX
 * 
 */
public class TitleComparator implements Comparator<FolderEntity> {

	@Override
	public int compare(FolderEntity lhs, FolderEntity rhs) {
		return lhs.getName().compareTo(rhs.getName());
	}

}
