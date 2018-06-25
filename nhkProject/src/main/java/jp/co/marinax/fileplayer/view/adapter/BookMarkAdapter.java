package jp.co.marinax.fileplayer.view.adapter;

import java.util.List;

import jp.co.marinax.fileplayer.io.entity.BookMarkEntity;
import jp.co.marinax.fileplayer.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BookMarkAdapter extends ArrayAdapter<BookMarkEntity> {

	private List<BookMarkEntity> listBookMarks;
	private Context mContext;
	private boolean mFlag = false;
	public BookMarkAdapter(Context context, List<BookMarkEntity> objects) {
		super(context, 1, objects);
		mContext = context;
		listBookMarks = objects;
	}


		public class ViewHolder {
		public ImageView imgvDeleteItem;
		public TextView tvName;
		public TextView tvLinkUrl;
		}

		// grant view for each item
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.entity_book_mark, null);
				viewHolder = new ViewHolder();
			viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvNameId);
			viewHolder.tvLinkUrl = (TextView) convertView.findViewById(R.id.tvLinkUrlId);
			viewHolder.imgvDeleteItem = (ImageView) convertView.findViewById(R.id.imgDeleteItemId);
			convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
		viewHolder.tvName.setText(listBookMarks.get(position).getName());
		viewHolder.tvLinkUrl.setText(listBookMarks.get(position).getUrl());

		((ChangeStatus) mContext).change(mFlag);
		if (mFlag) {
			viewHolder.imgvDeleteItem.setVisibility(View.VISIBLE);
		} else {
			viewHolder.imgvDeleteItem.setVisibility(View.GONE);
		}

		viewHolder.imgvDeleteItem.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((DeleteItem) mContext).position(position);
			}
		});
			return convertView;
		}

	public interface DeleteItem {
		public void position(int position);
	}
	
	public interface ChangeStatus {
		public void change(boolean flag);
	}
	
	public void setShowDelete(boolean flag) {
		mFlag = flag;
	}
}
