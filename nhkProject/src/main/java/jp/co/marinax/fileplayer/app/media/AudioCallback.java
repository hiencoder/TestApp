package jp.co.marinax.fileplayer.app.media;

public interface AudioCallback {

	void onAudioPlay();

	void onAudioPause();

	void onAudioToggle();

	void onAudioHeadseHook();

	void onAudioStop();

	void onAudioNext();

	void onAudioPrevious();

	void onUpdateIcon(boolean isPlaying);

	void onUpdateProgress();

	void onRemoveProgressTask();
}
