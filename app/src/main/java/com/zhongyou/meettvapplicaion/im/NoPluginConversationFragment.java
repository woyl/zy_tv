package com.zhongyou.meettvapplicaion.im;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.orhanobut.logger.Logger;
import com.zhongyou.meettvapplicaion.Constant;
import com.zhongyou.meettvapplicaion.R;

import io.rong.imkit.InputBar;
import io.rong.imkit.ListViewTV;
import io.rong.imkit.RongExtension;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.widget.adapter.MessageListAdapter;

/**
 * @author luopan@centerm.com
 * @date 2020-03-10 13:13.
 */
public class NoPluginConversationFragment extends ConversationFragment {
	private RongExtension rongExtension;
	private ListViewTV listView;
	private EditText mEditText;
	private ConversionAdaterEx mConversionAdaterEx;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = super.onCreateView(inflater, container, savedInstanceState);

		//editText.setImeOptions(EditorInfo.IME_ACTION_SEND);
		if (v != null) {
			v.setBackgroundColor(getResources().getColor(R.color.transparent_80_black));
			View sendToggle = findViewById(v, R.id.rc_send_toggle);
			mEditText = findViewById(v, R.id.rc_edit_text);
			mEditText.setFocusable(true);
			mEditText.setFocusableInTouchMode(true);
			mEditText.requestFocus();
			mEditText.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					Logger.e("keyCode:" + keyCode);
					if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
						sendToggle.setFocusable(true);
						sendToggle.setFocusableInTouchMode(true);
						sendToggle.requestFocus();
					}
					return false;
				}
			});


			sendToggle.setBackground(getResources().getDrawable(R.drawable.bg_send_button_selector));
			sendToggle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						sendToggle.setBackground(getResources().getDrawable(R.drawable.bg_search_button_focused));
					} else {
						sendToggle.setBackground(getResources().getDrawable(R.drawable.bg_send_button_default));
					}
				}
			});

			rongExtension = (RongExtension) v.findViewById(R.id.rc_extension);
			findViewById(v, R.id.rc_voice_toggle).setVisibility(View.GONE);
			View messageListView = findViewById(v, R.id.rc_layout_msg_list);
			listView = findViewById(messageListView, R.id.rc_list);
			rongExtension.setInputBarStyle(InputBar.Style.STYLE_SWITCH_CONTAINER);

			if (Constant.isPadApplicaion) {
				findViewById(v, R.id.rc_emoticon_toggle).setVisibility(View.VISIBLE);
			} else {
				findViewById(v, R.id.rc_emoticon_toggle).setVisibility(View.GONE);
			}
		}

		return v;
	}


	@Override
	public MessageListAdapter onResolveAdapter(Context context) {
		mConversionAdaterEx = new ConversionAdaterEx(context);
		mConversionAdaterEx.setOnItemFocusListener(new ConversionAdaterEx.onItemFocusListener() {
			@Override
			public void onFocusdItemPosition(int position) {
				if (listView!=null){
					listView.setFocedPosition(position);
					int count = mConversionAdaterEx.getCount();
					listView.setItemsCount(count);
				}
			}
		});

		mConversionAdaterEx.setOnMessageClickListener(new ConversionAdaterEx.onMessageClickListener() {
			@Override
			public void onMessageClick(View view) {
				Logger.e("view被点击了……");

			}
		});
		return mConversionAdaterEx;
	}

}
