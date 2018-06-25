package jp.co.marinax.fileplayer.view.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import jp.co.marinax.fileplayer.R;
import jp.co.marinax.fileplayer.app.config.DebugOption;
import jp.co.marinax.fileplayer.app.config.Define;
import jp.co.marinax.fileplayer.io.entity.FolderEntity;
import jp.co.marinax.fileplayer.io.save.SessionData;

public class FolderAdapter extends ArrayAdapter<FolderEntity> {
	private List<FolderEntity> listFolder;
	private Context mContext;
	private boolean chkBoxM;
	private int selectedItem = 0;

	public FolderAdapter(Context context, ArrayList<FolderEntity> list) {
		super(context, 1, list);
		mContext = context;
		listFolder = list;
	}

	public class ViewHolder {
		public CheckBox chkBox;
		public ImageView imgv; // it is may be : folder , book or audio;
		public TextView name;
		public TextView createDate;
	}

	// grant view for each item
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.entity_folder, null);
			viewHolder = new ViewHolder();
			viewHolder.chkBox = (CheckBox) convertView
					.findViewById(R.id.chkSelectId);
			viewHolder.imgv = (ImageView) convertView
					.findViewById(R.id.imgvType);
			viewHolder.name = (TextView) convertView.findViewById(R.id.nameID);

			viewHolder.createDate = (TextView) convertView
					.findViewById(R.id.createDateID);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		FolderEntity entity = listFolder.get(position);
		if (listFolder.get(position).getId() == Define.DEFAULT_INT) {
			viewHolder.chkBox.setVisibility(View.GONE);
		} else {
			if (chkBoxM) {
				viewHolder.chkBox.setVisibility(View.VISIBLE);
			} else {
				viewHolder.chkBox.setVisibility(View.GONE);
			}
		}

		viewHolder.chkBox.setChecked(entity.isCheck());
		if (entity.getName().endsWith(Define.EXTENSION_AUDIO)) {
			MediaMetadataRetriever retriver = new MediaMetadataRetriever();
			DebugOption.error("File name: ", entity.getName());
			DebugOption.error("File path: ", entity.getPath());
			retriver.setDataSource(entity.getPath());
			String title = retriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
//			String title = "";
			// Log.e("PATH_____", "path: " + entity.getPath() + ", title: " + title);
			if (title != null && !title.equals("")) {
				viewHolder.name.setText(title);
			} else {
				viewHolder.name.setText(entity.getName().replace(Define.EXTENSION_AUDIO, ""));
			}
		} else {
			viewHolder.name.setText(entity.getName().replace(Define.EXTENSION_AUDIO, ""));
		}
		if (listFolder.get(position).getTypeImage() == Define.TYPE_FOLDER) {
			viewHolder.createDate.setText(entity.getCreated_date());
		} else {
			viewHolder.createDate.setText(entity.getDownloadTime());
		}
		if (SessionData.getMoveListItems().size() > 0
				&& entity.getTypeImage() != Define.TYPE_FOLDER) {
			viewHolder.name.setTextColor(mContext.getResources().getColor(
					R.color.color_grey_blur));
			viewHolder.createDate.setTextColor(mContext.getResources()
					.getColor(R.color.color_grey_blur));
		} else {
			viewHolder.name.setTextColor(mContext.getResources().getColor(
					android.R.color.black));
			viewHolder.createDate.setTextColor(mContext.getResources()
					.getColor(android.R.color.black));
		}

		if (listFolder.get(position).getId() == Define.DEFAULT_INT) {
			viewHolder.imgv.setImageBitmap(null);
		} else if (entity.getTypeImage() == Define.TYPE_FOLDER) {
			viewHolder.imgv.setImageResource(R.drawable.ic_folder);
		} else if (entity.getTypeImage() == Define.TYPE_BOOK) {
			viewHolder.imgv.setImageResource(R.drawable.ic_book);
		} else if (entity.getTypeImage() == Define.TYPE_FILES) {
			viewHolder.imgv.setImageResource(R.drawable.ic_media);
		}

		viewHolder.chkBox.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DebugOption.info("Checkbox click", "Checkbox click = "
						+ position);

				listFolder.get(position)
						.setCheck(viewHolder.chkBox.isChecked());
				int selectedItem = 0;
				for (FolderEntity enFolder : listFolder) {
					if (enFolder.isCheck()) {
						selectedItem++;
					}
				}
				DebugOption.debug("selectedItem", "selectedItem = " + selectedItem);
				SessionData.setSelectedItem(selectedItem);
				if (viewHolder.chkBox.isChecked()) {
					DebugOption.info("IFFFFFFFFFF", "IFFFFFFFFFF");
					// update
					((MoveClass) mContext).moveFlag(true);
				} else {
					// check list
					DebugOption.info("ELSEEEEEEEEEE", "ELSEEEEEEEEE");
					new CheckClick().execute();
				}

			}
		});

		return convertView;
	}

	public void setCheckVisible(boolean flag) {
		chkBoxM = flag;
	}

	public boolean isShow() {
		return chkBoxM;
	}

	public interface UpdateList {
		public void update(List<FolderEntity> arrFolder);
	}

	@Override
	public FolderEntity getItem(int position) {

		return listFolder.get(position);
	}

	@Override
	public int getCount() {
		return listFolder.size();
	}

	@Override
	public boolean isEnabled(int position) {
		if (listFolder.get(position).getId() == Define.DEFAULT_INT) {
			return false;
		} else {
			return super.isEnabled(position);
		}
	}

	public interface MoveClass {
		public void moveFlag(boolean moveflag);
	}

	private class CheckClick extends AsyncTask<Void, Void, Void> {
		boolean move = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			for (FolderEntity entity : listFolder) {
				DebugOption.info("check :  ", "check = " + entity.isCheck());
				if (entity.isCheck()) {
					move = true;
					break;
				}
			}
			DebugOption.info("SELECTED ITEM :", "SELECTED ITEM  = " + selectedItem);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			DebugOption.info("MOVE : ", "MOVE FLAG = " + move);
			((MoveClass) mContext).moveFlag(move);
		}

	}
}
