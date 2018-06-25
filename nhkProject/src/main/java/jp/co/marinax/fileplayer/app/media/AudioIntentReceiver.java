/*   
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.marinax.fileplayer.app.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Receives broadcasted intents. In particular, we are interested in the
 * android.media.AUDIO_BECOMING_NOISY and android.intent.action.MEDIA_BUTTON
 * intents, which is broadcast, for example, when the user disconnects the
 * headphones. This class works because we are declaring it in a
 * &lt;receiver&gt; tag in AndroidManifest.xml.
 */
public class AudioIntentReceiver extends BroadcastReceiver {
	private static final String TAG = "AudioIntentReceiver";
	private static final float TIME_PREVIOUS = 1500;
	
	private static long sLastPressed = 0;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e(TAG, "onReceive");
		if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
			KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
			if (keyEvent.getAction() != KeyEvent.ACTION_DOWN) return;

			switch (keyEvent.getKeyCode()) {
			case KeyEvent.KEYCODE_HEADSETHOOK:
				Log.e(TAG, "HEADSETHOOK");
				break;
			case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
				Log.e(TAG, "TOGGLE");
				context.startService(new Intent(AudioService.ACTION_TOGGLE_PLAYBACK));
				break;
			case KeyEvent.KEYCODE_MEDIA_PLAY:
				Log.e(TAG, "PLATY");
				context.startService(new Intent(AudioService.ACTION_PLAY));
				break;
			case KeyEvent.KEYCODE_MEDIA_PAUSE:
				Log.e(TAG, "PAUSE");
				context.startService(new Intent(AudioService.ACTION_PAUSE));
				break;
			case KeyEvent.KEYCODE_MEDIA_STOP:
				Log.e(TAG, "STOP");
				context.startService(new Intent(AudioService.ACTION_STOP));
				break;
			case KeyEvent.KEYCODE_MEDIA_NEXT:
				Log.e(TAG, "NEXT");
				context.startService(new Intent(AudioService.ACTION_NEXT));
				break;
			case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
				Log.e(TAG, "PREVIOUS");
				if (System.currentTimeMillis() - sLastPressed < TIME_PREVIOUS) {
					context.startService(new Intent(AudioService.ACTION_PREVIOUS));
				} else {
					context.startService(new Intent(AudioService.ACTION_REWIND));
				}
				sLastPressed = System.currentTimeMillis();
				break;
			case KeyEvent.KEYCODE_MEDIA_REWIND:
				Log.e(TAG, "REWIND");
				context.startService(new Intent(AudioService.ACTION_REWIND));
				break;
			}
		} else if(intent.getAction().equals(AudioService.ACTION_PREVIOUS)) {
			if (System.currentTimeMillis() - sLastPressed < TIME_PREVIOUS) {
				context.startService(new Intent(AudioService.ACTION_PREVIOUS));
			} else {
				context.startService(new Intent(AudioService.ACTION_REWIND));
			}
			sLastPressed = System.currentTimeMillis();
		}
	}
}
