package jp.co.marinax.fileplayer.app.check;

import jp.co.marinax.fileplayer.io.save.SessionData;
import jp.co.marinax.fileplayer.utils.NetworkUtil;
import jp.co.marinax.fileplayer.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NetworkBroadCastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		int status = NetworkUtil.getConnectivityStatusString(context);
		if (status == 0) {
			SessionData.setDownload(false);
			Toast.makeText(context,
					context.getResources().getString(R.string.not_connect_to_network),
					Toast.LENGTH_SHORT).show();
		} else {
			SessionData.setDownload(true);
		}
	}
}
