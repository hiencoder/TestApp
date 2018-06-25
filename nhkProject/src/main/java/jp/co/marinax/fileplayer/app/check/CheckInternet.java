package jp.co.marinax.fileplayer.app.check;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternet {
	private Context mContext;

	public CheckInternet() {

	}

	public void init(Context context) {
		mContext = context;
	}

	public boolean checkConnecttionToInternet() {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			// get all can connect Internet : 3G, wireless ...
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
			if (networkInfo != null) {
				for (int i = 0; i < networkInfo.length; i++) {
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
