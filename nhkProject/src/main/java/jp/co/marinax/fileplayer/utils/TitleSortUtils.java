package jp.co.marinax.fileplayer.utils;

import java.util.Comparator;

public class TitleSortUtils implements Comparator<String> {

	@Override
	public int compare(String lhs, String rhs) {

		return lhs.compareTo(rhs);
	}

}
