package jp.co.marinax.fileplayer.view.adapter;

import java.util.ArrayList;

import jp.co.marinax.fileplayer.R;
import jp.co.marinax.fileplayer.app.config.DebugOption;
import jp.co.marinax.fileplayer.app.config.Define;
import jp.co.marinax.fileplayer.io.entity.FileEntity;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileAdapter extends ArrayAdapter<FileEntity> {
	private Context mContext;
	private ArrayList<FileEntity> mListFiles;

	public FileAdapter(Context context, ArrayList<FileEntity> listFile) {
		super(context, 1, listFile);
		this.mContext = context;
		this.mListFiles = listFile;
		DebugOption.info(" SIZE :", listFile.size() + "");
	}

	public class ViewHolder {
		public ImageView imgvTypeId;
		public TextView tvName;
		public TextView tvTime;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DebugOption.info("getView", "getView" + position);
		ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.entity_top, null);
			viewHolder = new ViewHolder();
			viewHolder.imgvTypeId = (ImageView) convertView
					.findViewById(R.id.imgvTypeId);
			viewHolder.tvName = (TextView) convertView
					.findViewById(R.id.nameId);
			viewHolder.tvTime = (TextView) convertView
					.findViewById(R.id.timeId);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (mListFiles.get(position).getType() == Define.FileType.TEXT) {
			viewHolder.imgvTypeId.setImageResource(R.drawable.ic_book);
		} else if (mListFiles.get(position).getType() == Define.FileType.AUDIO) {
			viewHolder.imgvTypeId.setImageResource(R.drawable.ic_media);
		} else {
			viewHolder.imgvTypeId.setImageBitmap(null);
		}

		if (mListFiles.get(position).getName().endsWith(Define.EXTENSION_AUDIO)) {
			MediaMetadataRetriever retriver = new MediaMetadataRetriever();
			retriver.setDataSource(mListFiles.get(position).getPath());
			String title = retriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
			// Log.e("PATH_____", "path: " + mListFiles.get(position).getPath() + ", title: " + title);
			if (title != null && !title.equals("")) {
				viewHolder.tvName.setText(title);
			} else {
				viewHolder.tvName.setText(mListFiles.get(position).getName()
						.replace(Define.EXTENSION_AUDIO, ""));
			}
		} else {
			viewHolder.tvName.setText(mListFiles.get(position).getName()
					.replace(Define.EXTENSION_AUDIO, ""));
		}

		viewHolder.tvTime.setText("");

		return convertView;
	}

	@Override
	public int getCount() {
		DebugOption.info("getCount()", "getCount() : " + mListFiles.size());
		return super.getCount();
	}

	@Override
	public FileEntity getItem(int position) {
		return mListFiles.get(position);
	}

	@Override
	public boolean isEnabled(int position) {
		if (mListFiles.get(position).getType() == Define.DEFAULT_INT) {
			return false;
		} else {
			return super.isEnabled(position);
		}
	}
}
