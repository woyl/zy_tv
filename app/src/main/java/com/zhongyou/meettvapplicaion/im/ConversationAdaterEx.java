package com.zhongyou.meettvapplicaion.im;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.qiniu.android.utils.Json;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.business.IMChatActivity;
import com.zhongyou.meettvapplicaion.business.adapter.BaseRecyclerAdapter;
import com.zhongyou.meettvapplicaion.business.adapter.BaseRecyclerHolder;
import com.zhongyou.meettvapplicaion.persistence.Preferences;

import org.w3c.dom.Text;

import java.util.List;

import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.model.Message;
import io.rong.message.GroupNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.NotificationMessage;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.TextMessage;
import io.rong.photoview.PhotoView;

/**
 * @author luopan@centerm.com
 * @date 2020-03-12 12:58.
 */
public class ConversationAdaterEx extends BaseMultiItemQuickAdapter<IMChatMessage, BaseViewHolder> {
	private Context mContext;

	public ConversationAdaterEx(Context context, List<IMChatMessage> data) {
		super(data);
		this.mContext = context;
		addItemType(IMChatMessage.MY_TXT, R.layout.item_my_chat_text);
		addItemType(IMChatMessage.MY_IMG, R.layout.item_my_chat_img);
		addItemType(IMChatMessage.MY_LOCATION, R.layout.item_my_chat_location);
		addItemType(IMChatMessage.IMG, R.layout.item_chat_img);
		addItemType(IMChatMessage.TXT, R.layout.item_chat_text);
		addItemType(IMChatMessage.LOCATION, R.layout.item_chat_location);
		addItemType(IMChatMessage.NOTIFY, R.layout.item_chat_notify);
	}


	private boolean canFocused;

	public void setCanFocused(boolean canFocused) {
		this.canFocused = canFocused;
		notifyDataSetChanged();
	}

	public boolean isItemFocused() {
		for (IMChatMessage datum : getData()) {
			if (datum.isIsfocused()) {
				return true;
			}
		}
		return false;
	}


	@Override
	protected void convert(@NonNull BaseViewHolder holder, IMChatMessage item) {
		if (item.getMsg().getSentStatus() == Message.SentStatus.SENT) {
			holder.getView(R.id.rc_progress).setVisibility(View.GONE);
			holder.getView(R.id.send_error).setVisibility(View.GONE);
		} else if (item.getMsg().getSentStatus() == Message.SentStatus.SENDING) {
			holder.getView(R.id.rc_progress).setVisibility(View.VISIBLE);
			holder.getView(R.id.send_error).setVisibility(View.GONE);
		} else if (item.getMsg().getSentStatus() == Message.SentStatus.FAILED) {
			holder.getView(R.id.rc_progress).setVisibility(View.GONE);
			holder.getView(R.id.send_error).setVisibility(View.VISIBLE);
		}


		int layoutPosition = holder.getLayoutPosition();


		holder.itemView.setFocusable(canFocused);
		holder.itemView.setFocusableInTouchMode(canFocused);
		holder.itemView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
					return true;
				}
				if (layoutPosition == 0 && keyCode != KeyEvent.KEYCODE_DPAD_UP) {
					if (mOnFocusedChange != null) {
						mOnFocusedChange.setFocused();
					}
				} else if ((layoutPosition == getItemCount() - 1) && keyCode == KeyEvent.KEYCODE_DPAD_UP && event.getAction() == KeyEvent.ACTION_DOWN) {
					if (mOnFocusedChange != null) {
						mOnFocusedChange.setFocused();
					}
				}
				return false;
			}
		});

		holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				item.setIsfocused(hasFocus);

			}
		});

		switch (holder.getItemViewType()) {
			case IMChatMessage.MY_IMG:
			case IMChatMessage.IMG:
				if (item.getMsg().getContent() instanceof ImageMessage) {
					ImageView view = holder.getView(R.id.chat_img);
					ImageMessage message = (ImageMessage) item.getMsg().getContent();
					Glide.with(mContext).asBitmap().load(message.getThumUri()).placeholder(R.drawable.rc_loading).error(R.drawable.rc_image_error).into(view);

					try {
						holder.setText(R.id.chatName, item.isSend() ? Preferences.getUserName() : TextUtils.isEmpty(message.getUserInfo().getName()) ? "" : message.getUserInfo().getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						Glide.with(mContext).asBitmap().load(item.isSend() ? Preferences.getUserPhoto() : message.getUserInfo().getPortraitUri()).into((AsyncImageView) holder.getView(R.id.headView));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				holder.getView(R.id.chat_img).setBackground(mContext.getResources().getDrawable(item.isSend() ? R.drawable.rc_ic_bubble_right_file : R.drawable.rc_ic_bubble_left_file));

				break;
			case IMChatMessage.MY_TXT:
			case IMChatMessage.TXT:
				if (item.getMsg().getContent() instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) item.getMsg().getContent();

					holder.setText(R.id.chatText, textMessage.getContent());
					try {
						holder.setText(R.id.chatName, item.isSend() ? Preferences.getUserName() : TextUtils.isEmpty(textMessage.getUserInfo().getName()) ? "" : textMessage.getUserInfo().getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						Glide.with(mContext).asBitmap().load(item.isSend() ? Preferences.getUserPhoto() : textMessage.getUserInfo().getPortraitUri()).into((AsyncImageView) holder.getView(R.id.headView));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				holder.getView(R.id.chatText).setBackground(mContext.getResources().getDrawable(item.isSend() ? R.drawable.rc_ic_bubble_right : R.drawable.rc_ic_bubble_left));
				break;
			case IMChatMessage.MY_LOCATION:
			case IMChatMessage.LOCATION:
				if (item.getMsg().getContent() instanceof LocationMessage) {
					LocationMessage locationMessage = (LocationMessage) item.getMsg().getContent();
					holder.setText(R.id.locationName, locationMessage.getPoi());
					try {
						holder.setText(R.id.chatName, item.isSend() ? Preferences.getUserName() : TextUtils.isEmpty(locationMessage.getUserInfo().getName()) ? "" : locationMessage.getUserInfo().getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						Glide.with(mContext).asBitmap().load(item.isSend() ? Preferences.getUserPhoto() : locationMessage.getUserInfo().getPortraitUri()).into((AsyncImageView) holder.getView(R.id.headView));
					} catch (Exception e) {
						e.printStackTrace();
					}

					holder.getView(R.id.mapView).setBackground(mContext.getResources().getDrawable(item.isSend() ? R.drawable.rc_ic_bubble_right_file : R.drawable.rc_ic_bubble_left_file));
//				https://restapi.amap.com/v3/staticmap?location=116.481485,39.990464&zoom=10&size=750*300&markers=mid,,A:116.481485,39.990464&key=<用户的key>
					String locationImage = "https://restapi.amap.com/v3/staticmap?location=" + locationMessage.getLng() +
							"," + locationMessage.getLat() + "&zoom=17&size=800*800&markers=mid,,A:" + locationMessage.getLng() + "," + locationMessage.getLat() + "&key=3f45adc4d4816096bbc00bd8bb2928c0";

					Logger.e(locationImage);
					Glide.with(mContext).asBitmap().load(locationImage).into((ImageView) holder.getView(R.id.mapView));
				}
				break;
			case IMChatMessage.NOTIFY:

				if (item.getMsg().getContent() instanceof GroupNotificationMessage) {
					GroupNotificationMessage message = (GroupNotificationMessage) item.getMsg().getContent();

					if (!TextUtils.isEmpty(message.getData())) {
						JSONObject jsonObject = JSON.parseObject(message.getData());
						String displayNames = jsonObject.getString("targetUserDisplayNames").replace("[\"".trim(), "").replace("\"]".trim(), "");
						String msg = message.getMessage();
						holder.setText(R.id.chat_notify, displayNames + msg);
					} else {
						JSONObject jsonObject = JSON.parseObject(message.toString());
						if (jsonObject.containsKey("recallTime")) {
							holder.getView(R.id.chat_notify).setVisibility(View.VISIBLE);
							holder.setText(R.id.chat_notify, message.getUserInfo().getName() + "撤回了一条消息");
						} else {
							holder.getView(R.id.chat_notify).setVisibility(View.GONE);
						}

					}
				} else if (item.getMsg().getContent() instanceof RecallNotificationMessage) {
					RecallNotificationMessage message = (RecallNotificationMessage) item.getMsg().getContent();
					holder.getView(R.id.chat_notify).setVisibility(View.VISIBLE);
					holder.setText(R.id.chat_notify, item.isSend() ? "您撤回了一条消息" : message.getUserInfo().getName() + "撤回了一条消息");
				}
				break;
		}
	}

	public interface OnFocusedChange {
		void setFocused();
	}

	private OnFocusedChange mOnFocusedChange;

	public void setOnFocusedChange(OnFocusedChange onFocusedChange) {
		mOnFocusedChange = onFocusedChange;
	}
}

