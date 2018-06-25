package jp.co.marinax.fileplayer.view.custom;

import jp.co.marinax.fileplayer.R;
import jp.co.marinax.fileplayer.app.config.DebugOption;
import jp.co.marinax.fileplayer.app.config.Define;
import jp.co.marinax.fileplayer.io.save.SessionData;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuBottomUpCus extends LinearLayout {
	private View view;
	private Context mContext;
	private long ANIMATION_TIME = 500;
	private Button mSelectAll;
	private Button mNewFolder;
	private Button mMove;
	private Button mDeselectAll;
	private Button mDelete;
	Button btnChangeFileName;
	private RelativeLayout rlDelete;

	private Button cancel;
	private TextView mItemSelected;
	private boolean clickAble = true;

	public MenuBottomUpCus(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public MenuBottomUpCus(Context context, AttributeSet attr) {
		super(context, attr);
		mContext = context;
		init();
	}

	public void init() {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.menu_bottom_up, this, false);
		rlDelete = (RelativeLayout) view.findViewById(R.id.rlDelete);
		mSelectAll = (Button) view.findViewById(R.id.selectAllId);
		mNewFolder = (Button) view.findViewById(R.id.newFolderId);
		mMove = (Button) view.findViewById(R.id.moveId);
		mDeselectAll = (Button) view.findViewById(R.id.deselectAllId);
		mDelete = (Button) view.findViewById(R.id.delelteId);
		cancel = (Button) view.findViewById(R.id.cancelId);
		mItemSelected = (TextView) view.findViewById(R.id.itemSelectedId);
		btnChangeFileName = (Button) view.findViewById(R.id.btnChangeFileName);
		action();
	}

	public void action() {
		mSelectAll.setOnClickListener(onCLick);
		mNewFolder.setOnClickListener(onCLick);
		mMove.setOnClickListener(onCLick);
		mDeselectAll.setOnClickListener(onCLick);
		mDelete.setOnClickListener(onCLick);
		rlDelete.setOnClickListener(onCLick);
		cancel.setOnClickListener(onCLick);
		btnChangeFileName.setOnClickListener(onCLick);

	}

	OnClickListener onCLick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (clickAble) {
				switch (v.getId()) {

				case R.id.selectAllId:
					((MenuClick) mContext).ClickItem(Define.SELECT_ALL);
					break;
				case R.id.newFolderId:
					((MenuClick) mContext).ClickItem(Define.NEW_FOLDER);
					break;
				case R.id.moveId:
					((MenuClick) mContext).ClickItem(Define.MOVE);
					break;
				case R.id.deselectAllId:
					((MenuClick) mContext).ClickItem(Define.DESELECT_ALL);
					break;
				case R.id.delelteId:
					((MenuClick) mContext).ClickItem(Define.DELETE);
					break;
				case R.id.rlDelete:
					((MenuClick) mContext).ClickItem(Define.RLDELETE);
					break;
				case R.id.cancelId:
					((MenuClick) mContext).ClickItem(Define.CANCEL);
					break;
					case R.id.btnChangeFileName:
						((MenuClick) mContext).ClickItem(Define.CHANGE_FILE_NAME);
						break;
				default:
					break;
				}
			}
		}
	};

	public void MenuDown() {
		int transitiony = 0;
		if (view.getHeight() > 0) {
			transitiony = view.getHeight();
		} else {
			transitiony = 600;
		}

		DebugOption.info("transitiony", "transitiony = " + transitiony);
		clickAble = false;
		DebugOption.info("HEIGHT : ", view.getHeight() + "");
		TranslateAnimation anim = new TranslateAnimation(0, 0, 0, transitiony);
		anim.setDuration(ANIMATION_TIME);
		anim.setFillAfter(true);
		anim.setFillAfter(true);
		anim.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// do nothing
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// do nothing
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.GONE);
			}
		});
		view.startAnimation(anim);

		((ShowHide) mContext).setShowHideDone(false);
	}

	public void isShowSelectAndDeSelect(boolean flag) {
		if (flag) {
			mSelectAll.setBackgroundResource(R.drawable.menu_item);
			mDeselectAll.setBackgroundResource(R.drawable.menu_item);
		} else {
			mSelectAll.setBackgroundResource(R.drawable.menu_item_disable);
			mDeselectAll.setBackgroundResource(R.drawable.menu_item_disable);
		}
	}

	public void setGoneDelteLayout(boolean flag) {
		if (flag) {
			rlDelete.setVisibility(View.GONE);
			mSelectAll.setVisibility(View.GONE);
			mDeselectAll.setEnabled(false);
			mSelectAll.setEnabled(false);
			cancel.setVisibility(View.VISIBLE);
			view.requestLayout();
		} else {
			rlDelete.setVisibility(View.VISIBLE);
			mSelectAll.setVisibility(View.VISIBLE);
			mDeselectAll.setEnabled(true);
			mSelectAll.setEnabled(true);
			cancel.setVisibility(View.GONE);
			view.requestLayout();
		}
	}

	public void setMoveVisibleOrHide(boolean flag) {
		DebugOption.info("flag 1 ", "flag 1 = " + flag);
		if (flag == true || SessionData.getMoveListItems().size() > 0) {
			// mMove.setVisibility(View.VISIBLE);
			mMove.setBackgroundResource(R.drawable.menu_item);
			mMove.setEnabled(true);
		} else {
			// mMove.setVisibility(View.GONE);
			mMove.setBackgroundResource(R.drawable.menu_item_disable);
			mMove.setEnabled(false);
		}

		if (flag) {
			mDelete.setBackgroundResource(R.drawable.menu_item);
			mDelete.setEnabled(true);
			mItemSelected.setText(SessionData.getSelectedItem()
					+ mContext.getResources().getString(
					R.string.item_selected));
		} else {
			mDelete.setBackgroundResource(R.drawable.menu_border_disable);
			mDelete.setEnabled(false);
			mItemSelected.setText(mContext.getResources().getString(
					R.string.no_item_selected));
		}
	}

	public void MenuUp() {
		view.setVisibility(View.VISIBLE);
		clickAble = true;
		DebugOption.info("HEIGHT : ", view.getHeight() + "");
		TranslateAnimation anim = new TranslateAnimation(0, 0,
				view.getHeight(), 0);
		anim.setDuration(ANIMATION_TIME);
		anim.setFillAfter(true);
		anim.setFillAfter(true);
		view.startAnimation(anim);
		((ShowHide) mContext).setShowHideDone(true);
	}

	public void setMoveLabel(String label) {
		mMove.setText(label);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		addView(view, 0);
	}

	public interface MenuClick {
		public void ClickItem(int id);
	}

	public interface ShowHide {
		public void setShowHideDone(boolean flag);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		DebugOption.info("touches", "touches image");
		return false;
	}
}
