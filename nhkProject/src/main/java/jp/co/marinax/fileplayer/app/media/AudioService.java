package jp.co.marinax.fileplayer.app.media;

import java.io.IOException;

import jp.co.marinax.fileplayer.R;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.RemoteControlClient;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class AudioService extends Service implements OnCompletionListener,
		OnPreparedListener, OnErrorListener, MusicFocusable {
	// The tag we put on debug messages
	final static String TAG = "AudioService";

	public static final String ACTION_TOGGLE_PLAYBACK = "jp.co.marinax.fileplayer.action.TOGGLE_PLAYBACK";
	public static final String ACTION_PLAY = "jp.co.marinax.fileplayer.action.PLAY";
	public static final String ACTION_PAUSE = "jp.co.marinax.fileplayer.action.PAUSE";
	public static final String ACTION_STOP = "jp.co.marinax.fileplayer.action.STOP";
	public static final String ACTION_NEXT = "jp.co.marinax.fileplayer.action.NEXT";
	public static final String ACTION_REWIND = "jp.co.marinax.fileplayer.action.REWIND";
	public static final String ACTION_PREVIOUS = "jp.co.marinax.fileplayer.action.PREVIOUS";
	
	public static final float DUCK_VOLUME = 0.1f;
	public static final int NOTIFICATION_ID = 99;

	// indicates the state our service:
	enum State {
		Stopped, Preparing, Playing, Paused
	}

	private State mState = State.Stopped;

	// our media player
	private MediaPlayer mPlayer = null;

	private AudioFocusHelper mAudioFocusHelper = null;
	// The component name of MusicIntentReceiver, for use with media button and
	// remote control
	// APIs
	private ComponentName mMediaButtonReceiverComponent;

	private AudioManager mAudioManager;
	private RemoteViews mNotificationView;
	private NotificationManager mNotificationManager;

	private NotificationCompat.Builder mNotificationBuilder = null;
	private String mWhatToPlay = null;

	// do we have audio focus?
	enum AudioFocus {
		NoFocusNoDuck, NoFocusCanDuck, Focused
	}

	AudioFocus mAudioFocus = AudioFocus.NoFocusNoDuck;

	// title of the song we are currently playing
	private String mSongTitle = "";
	private String mAlbumName = "";

	// our RemoteControlClient object, which will use remote control APIs
	// available in
	// SDK level >= 14, if they're available.
	RemoteControlClientCompat mRemoteControlClientCompat;

	// Dummy album art we will pass to the remote control (if the APIs are
	// available).
	Bitmap mDummyAlbumArt;
	private static long sLastPressed = 0;
	private boolean isPrepare = false;

	/**
	 * Makes sure the media player exists and has been reset. This will create
	 * the media player if needed, or reset the existing media player if one
	 * already exists.
	 */
	void createMediaPlayerIfNeeded() {
		if (mPlayer == null) {
			mPlayer = new MediaPlayer();

			// Make sure the media player will acquire a wake-lock while
			// playing. If we don't do
			// that, the CPU might go to sleep while the song is playing,
			// causing playback to stop.
			//
			// Remember that to use this, we have to declare the
			// android.permission.WAKE_LOCK
			// permission in AndroidManifest.xml.
			mPlayer.setWakeMode(getApplicationContext(),
					PowerManager.PARTIAL_WAKE_LOCK);

			// we want the media player to notify us when it's ready preparing,
			// and when it's done
			// playing:
			mPlayer.setOnPreparedListener(this);
			mPlayer.setOnCompletionListener(this);
			mPlayer.setOnErrorListener(this);
		} else {
			mPlayer.reset();
		}
	}

	private AudioCallback mCallback;

	private final AudioIBinder mBinder = new AudioIBinder();

	public class AudioIBinder extends Binder {
		public AudioService getService() {
			return AudioService.this;
		}
	}

	public void setAudioCallback(AudioCallback callback) {
		mCallback = callback;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		relaxResources(true);
		return super.onUnbind(intent);
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "debug: Creating service");

		// mNotificationManager = (NotificationManager)
		// getSystemService(NOTIFICATION_SERVICE);
		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// create the Audio Focus Helper, if the Audio Focus feature is
		// available (SDK 8 or above)
		if (android.os.Build.VERSION.SDK_INT >= 8)
			mAudioFocusHelper = new AudioFocusHelper(getApplicationContext(),
					this);
		else
			mAudioFocus = AudioFocus.Focused; // no focus feature, so we always
												// "have" audio focus

		mDummyAlbumArt = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_app);

		mMediaButtonReceiverComponent = new ComponentName(this,
				AudioIntentReceiver.class);
	}

	/**
	 * Called when we receive an Intent. When we receive an intent sent to us
	 * via startService(), this is the method that gets called. So here we react
	 * appropriately depending on the Intent's action, which specifies what is
	 * being requested of us.
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();
		Log.e(TAG, "onStartCommand: " + action);
		if (action.equals(ACTION_TOGGLE_PLAYBACK)) {
			processTogglePlaybackRequest();
		} else if (action.equals(ACTION_PLAY)) {
			processPlayRequest();
		} else if (action.equals(ACTION_PAUSE)) {
			processPauseRequest();
		} else if (action.equals(ACTION_STOP)) {
			processStopRequest();
		} else if (action.equals(ACTION_NEXT)) {
			processNextRequest();
		} else if (action.equals(ACTION_PREVIOUS)) {
			processPreviousRequest();
		} else if (action.equals(ACTION_REWIND)) {
			processRewindRequest();
		}
		buildNotification();
		return START_NOT_STICKY; // Means we started the service, but don't want
									// it to
		// restart in case it's killed.
	}

	public void processTogglePlaybackRequest() {
		if (mState == State.Paused || mState == State.Stopped) {
			processPlayRequest();
			updateNotification(true);
		} else {
			processPauseRequest();
			updateNotification(false);
		}
	}

	public void processPlayRequest() {
		Log.e(TAG, "processPlayRequest");
		if (mCallback != null) {
			mCallback.onUpdateIcon(true);
			mCallback.onUpdateProgress();
		}
		tryToGetAudioFocus();

		// actually play the song

		if (mState == State.Stopped) {
			// If we're stopped, just go ahead to the next song and start
			// playing
			playNextSong(mWhatToPlay);
		} else if (mState == State.Paused) {
			// If we're paused, just continue playback and restore the
			// 'foreground service' state.
			mState = State.Playing;
			// setUpAsForeground(mSongTitle + " (playing)");
			setUpAsForeground(mSongTitle + " (playing)");
			configAndStartMediaPlayer();
		}

		// Tell any remote controls that our playback state is 'playing'.
		if (mRemoteControlClientCompat != null) {
			mRemoteControlClientCompat
					.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
		}
	}

	public void processPauseRequest() {
		Log.e(TAG, "processPauseRequest");
		if (mState == State.Playing) {
			// Pause media player and cancel the 'foreground service' state.
			mState = State.Paused;
			mPlayer.pause();
			// relaxResources(false);

			// do not give up audio focus
			if (mCallback != null) {
				mCallback.onUpdateIcon(false);
				mCallback.onRemoveProgressTask();
			}
		}

		// Tell any remote controls that our playback state is 'paused'.
		if (mRemoteControlClientCompat != null) {
			mRemoteControlClientCompat
					.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
		}
	}

	public void processStopRequest() {
		Log.e(TAG, "processStopRequest");
		processStopRequest(false);
	}

	public void processStopRequest(boolean force) {
		Log.e(TAG, "processStopRequest2");
		if (mState == State.Playing || mState == State.Paused || force) {
			mState = State.Stopped;

			// let go of all resources...
			// relaxResources(true);
			giveUpAudioFocus();

			// Tell any remote controls that our playback state is 'paused'.
			if (mRemoteControlClientCompat != null) {
				mRemoteControlClientCompat
						.setPlaybackState(RemoteControlClient.PLAYSTATE_STOPPED);
			}

			// service is no longer necessary. Will be started again if needed.
			stopSelf();
		}
	}

	void processRewindRequest() {
        if (mState == State.Playing || mState == State.Paused)
            mPlayer.seekTo(0);
    }
	
	private void processNextRequest() {
		Log.e(TAG, "processSkipRequest");
		if (mState == State.Playing || mState == State.Paused) {
			tryToGetAudioFocus();
			if (mCallback != null) {
				mCallback.onAudioNext();
			}
		}
	}

	private void processPreviousRequest() {
		Log.e(TAG, "processSkipRequest");
		if (mState == State.Playing || mState == State.Paused) {
			tryToGetAudioFocus();
			if (mCallback != null) {
				mCallback.onAudioPrevious();
			}
		}
	}

	/**
	 * Releases resources used by the service for playback. This includes the
	 * "foreground service" status and notification, the wake locks and possibly
	 * the MediaPlayer.
	 * 
	 * @param releaseMediaPlayer
	 *            Indicates whether the Media Player should also be released or
	 *            not
	 */
	public void relaxResources(boolean releaseMediaPlayer) {
		Log.e(TAG, "relaxResources");
		// stop being a foreground service
		stopForeground(true);

		// stop and release the Media Player, if it's available
		if (releaseMediaPlayer && mPlayer != null) {
			mPlayer.reset();
			mPlayer.release();
			mPlayer = null;
			if (mCallback != null) {
				mCallback.onRemoveProgressTask();
			}
		}
	}

	public void giveUpAudioFocus() {
		if (mAudioFocus == AudioFocus.Focused && mAudioFocusHelper != null
				&& mAudioFocusHelper.abandonFocus())
			mAudioFocus = AudioFocus.NoFocusNoDuck;
	}

	/**
	 * Reconfigures MediaPlayer according to audio focus settings and
	 * starts/restarts it. This method starts/restarts the MediaPlayer
	 * respecting the current audio focus state. So if we have focus, it will
	 * play normally; if we don't have focus, it will either leave the
	 * MediaPlayer paused or set it to a low volume, depending on what is
	 * allowed by the current focus settings. This method assumes mPlayer !=
	 * null, so if you are calling it, you have to do so from a context where
	 * you are sure this is the case.
	 */
	public void configAndStartMediaPlayer() {
		if (mAudioFocus == AudioFocus.NoFocusNoDuck) {
			// If we don't have audio focus and can't duck, we have to pause,
			// even if mState
			// is State.Playing. But we stay in the Playing state so that we
			// know we have to resume
			// playback once we get the focus back.
			if (mPlayer.isPlaying())
				mPlayer.pause();
			return;
		} else if (mAudioFocus == AudioFocus.NoFocusCanDuck) {
			mPlayer.setVolume(DUCK_VOLUME, DUCK_VOLUME); // we'll be relatively
															// quiet
		} else {
			mPlayer.setVolume(1.0f, 1.0f); // we can be loud
		}

		if (!mPlayer.isPlaying()) {
			mPlayer.start();
			if (mCallback != null) {
				mCallback.onUpdateIcon(true);
				mCallback.onUpdateProgress();
			}
		}
	}

	public void tryToGetAudioFocus() {
		if (mAudioFocus != AudioFocus.Focused && mAudioFocusHelper != null
				&& mAudioFocusHelper.requestFocus())
			mAudioFocus = AudioFocus.Focused;
	}

	/**
	 * Starts playing the next song. If manualUrl is null, the next song will be
	 * randomly selected from our Media Retriever (that is, it will be a random
	 * song in the user's device). If manualUrl is non-null, then it specifies
	 * the URL or path to the song that will be played next.
	 */
	@SuppressWarnings("deprecation")
	public void playNextSong(String manualUrl) {
		mState = State.Stopped;
		isPrepare = false;
		relaxResources(false); // release everything except MediaPlayer

		try {
			if (manualUrl != null) {
				// set the source of the media player to a manual URL or path
				createMediaPlayerIfNeeded();
				mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mPlayer.setDataSource(manualUrl);
			} else {
				return;
			}

			MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			retriever.setDataSource(manualUrl);
			String artist = retriever
					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
			String title = retriever
					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
			String album = retriever
					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);

			mSongTitle = title;
			mAlbumName = album;
			mState = State.Preparing;
			setUpAsForeground(mSongTitle + " (loading)");

			// Use the media button APIs (if available) to register ourselves
			// for media button
			// events

			MediaButtonHelper.registerMediaButtonEventReceiverCompat(
					mAudioManager, mMediaButtonReceiverComponent);

			// Use the remote control APIs (if available) to set the playback
			// state

			if (mRemoteControlClientCompat == null) {
				Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
				intent.setComponent(mMediaButtonReceiverComponent);
				mRemoteControlClientCompat = new RemoteControlClientCompat(
						PendingIntent.getBroadcast(this /* context */, 0 /*
																		 * requestCode
																		 * ,
																		 * ignored
																		 */,
								intent /* intent */, 0 /* flags */));
				RemoteControlHelper.registerRemoteControlClient(mAudioManager,
						mRemoteControlClientCompat);
			}

			mRemoteControlClientCompat
					.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);

			mRemoteControlClientCompat
					.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PLAY
							| RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
							| RemoteControlClient.FLAG_KEY_MEDIA_NEXT
							| RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS
							| RemoteControlClient.FLAG_KEY_MEDIA_POSITION_UPDATE
							| RemoteControlClient.FLAG_KEY_MEDIA_RATING
							| RemoteControlClient.FLAG_KEY_MEDIA_STOP);

			// Update the remote controls
			mRemoteControlClientCompat
					.editMetadata(true)
					.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST,
							artist)
					.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, album)
					.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, title)
					// .putLong(MediaMetadataRetriever.METADATA_KEY_DURATION,
					// 1000)
					// .putBitmap(RemoteControlClientCompat.MetadataEditorCompat.METADATA_KEY_ARTWORK,
					// mDummyAlbumArt)
					.apply();

			mPlayer.prepareAsync();
		} catch (IOException ex) {
			Log.e("MusicService",
					"IOException playing next song: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * Called when media player is done playing current song.
	 */
	public void onCompletion(MediaPlayer player) {
		// The media player finished playing the current song, so we go ahead
		// and start the next.
		if (mCallback != null) {
			mCallback.onAudioNext();
		}
	}

	/**
	 * Called when media player is done preparing.
	 */
	public void onPrepared(MediaPlayer player) {
		mState = State.Playing;
		isPrepare = true;
		updateNotification(true);
		configAndStartMediaPlayer();
	}

	/**
	 * Called when there's an error playing media. When this happens, the media
	 * player goes to the Error state. We warn the user about the error and
	 * reset the media player.
	 */
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Toast.makeText(getApplicationContext(),
				"Media player error! Resetting.", Toast.LENGTH_SHORT).show();
		Log.e(TAG,
				"Error: what=" + String.valueOf(what) + ", extra="
						+ String.valueOf(extra));

		mState = State.Stopped;
		relaxResources(true);
		giveUpAudioFocus();
		return true; // true indicates we handled the error
	}

	public void onGainedAudioFocus() {
		Toast.makeText(getApplicationContext(), "gained audio focus.",
				Toast.LENGTH_SHORT).show();
		mAudioFocus = AudioFocus.Focused;

		// restart media player with new focus settings
		if (mState == State.Playing) {
			configAndStartMediaPlayer();
		}
	}

	public void onLostAudioFocus(boolean canDuck) {
		Toast.makeText(getApplicationContext(),
				"lost audio focus." + (canDuck ? "can duck" : "no duck"),
				Toast.LENGTH_SHORT).show();
		mAudioFocus = canDuck ? AudioFocus.NoFocusCanDuck
				: AudioFocus.NoFocusNoDuck;

		// start/restart/pause media player with new focus settings
		if (mPlayer != null && mPlayer.isPlaying()) {
			configAndStartMediaPlayer();
		}
	}

	public void onMusicRetrieverPrepared() {
		// Done retrieving!
		mState = State.Stopped;

		// If the flag indicates we should start playing after retrieving, let's
		// do that now.
		tryToGetAudioFocus();
		playNextSong(mWhatToPlay == null ? null : mWhatToPlay.toString());
	}

	@Override
	public void onDestroy() {
		// Service is being killed, so make sure we release our resources
		mState = State.Stopped;
		relaxResources(true);
		giveUpAudioFocus();
	}

	public int getDuration() {
		if (mPlayer != null && isPrepare) {
			return mPlayer.getDuration();
		}
		return -1;
	}

	public int getCurrentPosition() {
		if (mPlayer != null && isPrepare) {
			return mPlayer.getCurrentPosition();
		}
		return -1;
	}

	public void seekTo(int position) {
		if (mPlayer != null && isPrepare) {
			mPlayer.seekTo(position);
		}
	}

	public void setWhatToPlay(String whatToPlay) {
		this.mWhatToPlay = whatToPlay;
	}

	private void buildNotification() {

	}

	/** Updates the notification. */
	void updateNotification(boolean isPlay) {
		if (isPlay) {
			mNotificationView.setImageViewResource(R.id.ib_play_pause,
					android.R.drawable.ic_media_pause);
		} else {
			mNotificationView.setImageViewResource(R.id.ib_play_pause,
					android.R.drawable.ic_media_play);
		}
		mNotificationBuilder.setContent(mNotificationView);
		mNotificationManager.notify(NOTIFICATION_ID,
				mNotificationBuilder.build());
	}

	/**
	 * Configures service as a foreground service. A foreground service is a
	 * service that's doing something the user is actively aware of (such as
	 * playing music), and must appear to the user as a notification. That's why
	 * we create the notification here.
	 */
	void setUpAsForeground(String text) {
		mNotificationView = new RemoteViews(getPackageName(),
				R.layout.custom_notification);
		mNotificationBuilder = new NotificationCompat.Builder(
				getApplicationContext()).setSmallIcon(R.drawable.ic_app)
				.setContent(mNotificationView).setTicker(text)
				.setWhen(System.currentTimeMillis())
				.setOngoing(true);
		// Add actions
		{
			Intent preIntent = new Intent();
			preIntent.setAction(ACTION_PREVIOUS);
			PendingIntent contentIntent = PendingIntent.getBroadcast(this, 1,
					preIntent, 2);

			mNotificationView.setOnClickPendingIntent(R.id.ib_previous,
					contentIntent);
		}

		{
			Intent nextIntent = new Intent();
			nextIntent.setAction(ACTION_NEXT);
			PendingIntent contentIntent = PendingIntent.getService(this, 1,
					nextIntent, 2);

			mNotificationView.setOnClickPendingIntent(R.id.ib_next,
					contentIntent);
		}

		{
			Intent playIntent = new Intent();
			playIntent.setAction(ACTION_TOGGLE_PLAYBACK);
			PendingIntent contentIntent = PendingIntent.getService(this, 1,
					playIntent, 2);

			mNotificationView.setOnClickPendingIntent(R.id.ib_play_pause,
					contentIntent);
		}

		// Set View
		{
			mNotificationView.setTextViewText(R.id.tv_album, mSongTitle);
			mNotificationView.setTextViewText(R.id.tv_title, mAlbumName);
		}
		startForeground(NOTIFICATION_ID, mNotificationBuilder.build());
	}
}
