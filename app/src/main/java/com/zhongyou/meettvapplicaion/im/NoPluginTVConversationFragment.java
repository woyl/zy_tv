package com.zhongyou.meettvapplicaion.im;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.autonavi.amap.mapcore.MsgProcessor;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;
import com.zhongyou.meettvapplicaion.Constant;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.business.BasisActivity;
import com.zhongyou.meettvapplicaion.business.ForumActivity;
import com.zhongyou.meettvapplicaion.business.SignInActivity;
import com.zhongyou.meettvapplicaion.entities.Meeting;
import com.zhongyou.meettvapplicaion.event.IMMessgeEvent;
import com.zhongyou.meettvapplicaion.event.KickOffEvent;
import com.zhongyou.meettvapplicaion.event.RecallMsgEvent;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.HintDialog;
import com.zhongyou.meettvapplicaion.utils.MyDialog;
import com.zhongyou.meettvapplicaion.utils.RxBus;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;
import com.zhongyou.meettvapplicaion.view.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.TvListView;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.utilities.RongUtils;
import io.rong.imkit.widget.provider.EvaluatePlugin;
import io.rong.imlib.CustomServiceConfig;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.FileMessage;
import io.rong.message.GroupNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.TextMessage;
import me.jessyan.autosize.utils.AutoSizeUtils;
import rx.Subscription;
import rx.functions.Action1;

/**
 * @author luopan@centerm.com
 * @date 2020-03-10 13:13.
 */
public class NoPluginTVConversationFragment extends Fragment {
	private RongExtension rongExtension;
	private RecyclerView listView;
	private EditText mEditText;
	private List<IMChatMessage> datalists = new ArrayList<>();
	private ConversationAdaterEx mConversationAdaterEx;
	private String mGroupId;
	private Subscription mSubscription;
	private String messageIds = "";
	private HintDialog mDialog;
	private Dialog imageReviewDialog;
	private ImageView imageReview;
	private LinearLayoutManager mLayoutManager;
	private View mV;
	private AlertDialog mAlertDialog;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mV = inflater.inflate(R.layout.fragment_no_plugin_tv_chat, null);
		mEditText = mV.findViewById(R.id.rc_edit_text);
		rongExtension = (RongExtension) mV.findViewById(R.id.rc_extension);

		rongExtension.setBackgroundColor(getResources().getColor(R.color.transparent));

		listView = mV.findViewById(R.id.rc_list);

		mEditText.setBackground(getResources().getDrawable(R.drawable.item_edit));
		mLayoutManager = new LinearLayoutManager(getActivity(),
				LinearLayoutManager.VERTICAL, true);
		mLayoutManager.setStackFromEnd(true);
		listView.setLayoutManager(mLayoutManager);
		listView.addItemDecoration(new SpaceItemDecoration(AutoSizeUtils.dp2px(getActivity(), 10), AutoSizeUtils.dp2px(getActivity(), 20), AutoSizeUtils.dp2px(getActivity(), 10), 0));
		mConversationAdaterEx = new ConversationAdaterEx(getActivity(), datalists);
		listView.setAdapter(mConversationAdaterEx);
		mConversationAdaterEx.setOnFocusedChange(new ConversationAdaterEx.OnFocusedChange() {
			@Override
			public void setFocused() {
//				listView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
				mConversationAdaterEx.setCanFocused(false);
			}
		});


		findViewById(mV, R.id.ext_common_phrases).setVisibility(View.GONE);
		if (Constant.isPadApplicaion) {
			findViewById(mV, R.id.rc_emoticon_toggle).setVisibility(View.GONE);
		} else {
			findViewById(mV, R.id.rc_emoticon_toggle).setVisibility(View.GONE);
		}


		mSubscription = RxBus.handleMessage(new Action1() {
			@Override
			public void call(Object o) {
				if (o instanceof IMMessgeEvent) {
					IMMessgeEvent msgEvent = (IMMessgeEvent) o;
					Message msg = msgEvent.getImMessage();
					if (getActivity() != null) {
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								insertData(msg);
							}
						});
					}
				} else if (o instanceof KickOffEvent) {
					KickOffEvent event = (KickOffEvent) o;
					if (getActivity() == null) {
						return;
					}
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (event.isKickOff()) {
								showKickOffDialog();
							} else {
								showErrorDialog();
							}
						}
					});

				} else if (o instanceof RecallMsgEvent) {
					RecallMsgEvent msgEvent = (RecallMsgEvent) o;
					Message message = msgEvent.getMessage();
					message.setObjectName("RC:GrpNtf");

					for (int i = 0; i < datalists.size(); i++) {
						if (datalists.get(i).getMsg().getMessageId() == message.getMessageId()) {
							//Logger.e("i: " + i + "meesgeId:" + datalists.get(i).getMsg().getMessageId() + "----" + message.getMessageId());
							IMChatMessage msg = datalists.get(i);
							msg.setMsg(message);
							msg.setItemType(IMChatMessage.NOTIFY);
							msg.setMsg(message);
							mConversationAdaterEx.notifyItemRangeChanged(i, 1);
							break;
						}
					}
				}
			}
		});

		setListener();
		initImageReviewDialog();
		return mV;
	}

	public List<IMChatMessage> getData() {
		return datalists == null ? (new ArrayList<>()) : datalists;
	}

	public ConversationAdaterEx getConversationAdaterEx() {
		return mConversationAdaterEx == null ? new ConversationAdaterEx(getActivity(), new ArrayList<>()) : mConversationAdaterEx;
	}

	@SuppressLint("ClickableViewAccessibility")
	private void setListener() {
		mConversationAdaterEx.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {
				showSelectionDialog(datalists.get(position));
//				Logger.e("点击发生了");
			}
		});

		listView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				try {
					if (getActivity() == null) {
						return false;
					}
					((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return false;
			}
		});

		listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);

			}

			@Override
			public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
				LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
				int firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
				if (firstCompletelyVisibleItemPosition == 0 && recyclerView.canScrollVertically(-1)) {
					newMessageCount = 0;
					findViewById(mV, R.id.rc_new_message_count).setVisibility(View.GONE);
					findViewById(mV, R.id.rc_new_message_number).setVisibility(View.GONE);
				}


			}
		});

		findViewById(mV, R.id.rc_send_toggle).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mentionUserIdList != null && mentionUserIdList.size() > 0) {//@某人
					MentionedInfo mentionedInfo = new MentionedInfo(MentionedInfo.MentionedType.PART, mentionUserIdList, null);
					TextMessage textMessage = TextMessage.obtain(mEditText.getText().toString().trim());
					textMessage.setMentionedInfo(mentionedInfo);
					IMChatMessage mentionedMessage = new IMChatMessage();
					Message msg = new Message();
					msg.setConversationType(Conversation.ConversationType.GROUP);
					msg.setSenderUserId(Preferences.getUserId());
					msg.setTargetId(mGroupId);
					msg.setContent(textMessage);
					msg.getContent().setUserInfo(new UserInfo(Preferences.getUserId(), Preferences.getUserName(), Uri.parse(Preferences.getUserPhoto())));
					mentionedMessage.setMsg(msg);
					resendMessage(mentionedMessage.getMsg(), true);
					mEditText.setText("");

				} else { //发送消息
					TextMessage textMessage = TextMessage.obtain(mEditText.getText().toString().trim());
					UserInfo userInfo = new UserInfo(Preferences.getUserId(), Preferences.getUserName(), Uri.parse(Preferences.getUserPhoto()));
					textMessage.setUserInfo(userInfo);
					Message message = Message.obtain(mGroupId, Conversation.ConversationType.GROUP, textMessage);
//				message.setMessageId(datalists.size() + 1);

					message.setObjectName("RC:TxtMsg");
					message.setSenderUserId(Preferences.getUserId());
					message.setSentStatus(Message.SentStatus.SENDING);

					mEditText.setText("");
					resendMessage(message, true);
				}
			}
		});

		mEditText.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
					findViewById(mV, R.id.rc_send_toggle).setFocusable(true);
					findViewById(mV, R.id.rc_send_toggle).setFocusableInTouchMode(true);

				} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
					mConversationAdaterEx.setCanFocused(true);
//					listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

				}
				return false;
			}
		});

		mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					listView.smoothScrollToPosition(0);
				}
			}
		});


	}

	private void resendMessage(Message message, boolean needAdd) {
		RongIM.getInstance().sendMessage(message, null, null, new IRongCallback.ISendMessageCallback() {
			@Override
			public void onAttached(Message message) {
				Logger.e(JSON.toJSONString(message));
				IMChatMessage msg = new IMChatMessage();
				msg.setMsg(message);
				if (needAdd) {
					datalists.add(0, msg);
				}
				mConversationAdaterEx.notifyDataSetChanged();
				listView.scrollToPosition(0);
				messageIds += message.getMessageId() + ",";

			}

			@Override
			public void onSuccess(Message message) {
				Logger.e(JSON.toJSONString(message));
				for (int i = 0; i < datalists.size(); i++) {
					if (messageIds.contains(message.getMessageId() + ",")) {
						datalists.get(i).getMsg().setSentStatus(Message.SentStatus.SENT);
						datalists.get(i).getMsg().setUId(message.getUId());
						mConversationAdaterEx.notifyDataSetChanged();
						messageIds.replace(message.getMessageId() + ",", "");
						return;
					}
				}

				if (mAlertDialog != null) {
					mAlertDialog.dismiss();
				}
			}

			@Override
			public void onError(Message message, RongIMClient.ErrorCode errorCode) {
				//消息发送失败的回调
				for (int i = 0; i < datalists.size(); i++) {
					if (messageIds.contains(message.getMessageId() + ",")) {
						datalists.get(i).getMsg().setSentStatus(Message.SentStatus.FAILED);
						mConversationAdaterEx.notifyDataSetChanged();
						messageIds.replace(message.getMessageId() + ",", "");
						return;
					}
				}
			}
		});
	}


	private void initData() {
		RongIM.getInstance().getHistoryMessages(Conversation.ConversationType.GROUP, mGroupId, -1, Integer.MAX_VALUE, new RongIMClient.ResultCallback<List<Message>>() {
			@Override
			public void onSuccess(List<Message> messages) {

				Logger.e(JSON.toJSONString(messages));
				for (int i = 0; i < messages.size(); i++) {
					Message msg = messages.get(i);
					IMChatMessage message = new IMChatMessage();
					message.setMsg(msg);
					datalists.add(message);
				}

				mConversationAdaterEx.setNewData(datalists);
				listView.smoothScrollToPosition(0);

			}

			@Override
			public void onError(RongIMClient.ErrorCode errorCode) {
				Logger.e("errorCode: " + errorCode.getValue());
			}
		});
	}

	public void setGroupID(String groupID) {
		this.mGroupId = groupID;
		initData();
	}

	protected <T extends View> T findViewById(View view, int id) {
		return view.findViewById(id);
	}


	private int newMessageCount;

	//接收到消息时
	public void insertData(Message message) {
		newMessageCount++;
		if (datalists != null && mConversationAdaterEx != null) {
			IMChatMessage msg = new IMChatMessage();
			msg.setMsg(message);
			datalists.add(0, msg);
			mConversationAdaterEx.notifyItemRangeInserted(0, 1);

			//如果当前列表中的item有焦点获取到 就只提示新消息到了 没有焦点获取 就滑动到最后
			if (!mConversationAdaterEx.isItemFocused()) {
				listView.smoothScrollToPosition(0);
			} else {
				findViewById(mV, R.id.rc_new_message_count).setVisibility(View.VISIBLE);
				TextView newMessage = findViewById(mV, R.id.rc_new_message_number);
				newMessage.setVisibility(View.VISIBLE);
				newMessage.setText(newMessageCount + " ");
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mSubscription != null) {
			mSubscription.unsubscribe();
		}
		if (mDialog != null) {
			mDialog.dismiss();
		}
		if (imageReviewDialog != null) {
			imageReviewDialog.dismiss();
		}
		if (mAlertDialog != null) {
			mAlertDialog.dismiss();
		}
	}

	private void showErrorDialog() {
		if (getActivity() == null) {
			return;
		}
		HintDialog.Builder builder = new HintDialog.Builder(getActivity());
		builder.setMessage("账号异常");
		builder.setTitle("账号异常");
		builder.setPositiveButtonMsg("确定");
		builder.setTitle("当前登陆信息已失效 请重新登陆");
		builder.setOnClickListener(new HintDialog.ClickListener() {
			@Override
			public void onClick(int tags) {
				switch (tags) {
					case MyDialog.BUTTON_POSITIVE:
						mDialog.dismiss();
						startActivity(new Intent(getActivity(), SignInActivity.class));
						getActivity().finish();
						break;
				}
			}
		});
		mDialog = builder.create();
		mDialog.show();
	}

	private void showKickOffDialog() {
		if (getActivity() == null) {
			return;
		}
		HintDialog.Builder builder = new HintDialog.Builder(getActivity());
		builder.setMessage("您的账号在别的地方进行了登陆 请重新登陆");
		builder.setTitle("账号异常");
		builder.setPositiveButtonMsg("确定");
		builder.setOnClickListener(new HintDialog.ClickListener() {
			@Override
			public void onClick(int tags) {
				switch (tags) {
					case MyDialog.BUTTON_POSITIVE:
						mDialog.dismiss();
						startActivity(new Intent(getActivity(), SignInActivity.class));
						getActivity().finish();
						break;
				}
			}
		});
		mDialog = builder.create();
		mDialog.show();
	}

	private void initImageReviewDialog() {
		if (getActivity() == null) {
			return;
		}
		imageReviewDialog = new Dialog(getActivity());
		Window dialogWindow = imageReviewDialog.getWindow();
		assert dialogWindow != null;
		dialogWindow.requestFeature(Window.FEATURE_NO_TITLE);
		imageReview = new ImageView(getActivity());
		dialogWindow.setContentView(imageReview);
		dialogWindow.setGravity(Gravity.CENTER);
	}

	List<String> mStringList = new ArrayList<>();
	public final String recall = "撤回";
	public final String delete = "删除";
	public final String quote = "@";
	public final String resend = "重发";
	public final String copy = "复制";
	public final String checkImage = "查看大图";

	private void showSelectionDialog(IMChatMessage message) {

		if (message.getMsg().getContent() instanceof GroupNotificationMessage || message.getMsg().getContent() instanceof RecallNotificationMessage) {
			return;
		}
		mStringList.clear();
		mStringList.add(recall);
		mStringList.add(delete);
		mStringList.add(quote);
		mStringList.add(resend);

		if (getActivity() == null) {
			return;
		}
		/*if (message.getMsg().getContent() instanceof ImageMessage
				|| message.getMsg().getContent() instanceof LocationMessage
				|| message.getMsg().getContent() instanceof FileMessage) {
			mStringList.remove(copy);
		}*/

		if (message.getMsg().getContent() instanceof ImageMessage) {
			mStringList.add(checkImage);
		} else {
			mStringList.remove(checkImage);
		}

		if (message.getMsg().getContent().getUserInfo().getUserId().equals(Preferences.getUserId())) {
			mStringList.remove(quote);
		} else {
			for (int i = 0; i < mStringList.size(); i++) {
				if (mStringList.get(i).equals(quote)) {
					mStringList.remove(quote);
					mStringList.add(quote + message.getMsg().getContent().getUserInfo().getName());
				}
			}
		}
		if (System.currentTimeMillis() - message.getMsg().getSentTime() > 2 * 60 * 1000) {
			mStringList.remove(recall);
		}

		if (!message.isSend()) {
			mStringList.remove(recall);
		}


		if (message.getMsg().getSentStatus() != Message.SentStatus.FAILED) {
			mStringList.remove(resend);
		}


		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 0);
		builder.setSingleChoiceItems(mStringList.toArray(new String[mStringList.size()]), 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int position) {
				String s = mStringList.get(position);
				Logger.e(JSON.toJSONString(message.getMsg()));
				if (s.equals(recall)) {

					recallMessage(message);

				} else if (s.equals(resend)) {

					message.getMsg().setSentStatus(Message.SentStatus.SENDING);
					mConversationAdaterEx.notifyDataSetChanged();
					resendMessage(message.getMsg(), false);

				} else if (s.equals(delete)) {

					deleteMessage(message);

				} else if (s.contains(quote)) {

					quoteMessage(message);

				} else if (s.equals(checkImage)) {

					ImageMessage msg = (ImageMessage) message.getMsg().getContent();
					Picasso.with(getActivity()).load(msg.getRemoteUri()).config(Bitmap.Config.RGB_565).into(imageReview);
					imageReviewDialog.show();

				}/*else if (s.equals(copy)) {
					copyMessage(message);
				}*/
				if (mAlertDialog != null) {
					mAlertDialog.dismiss();
				}
			}

		});
		mAlertDialog = builder.create();
		if (!mAlertDialog.isShowing()) {
			mAlertDialog.show();
		}
	}


	private void copyMessage(IMChatMessage message) {
	}

	List<String> mentionUserIdList = new ArrayList<>();

	private void quoteMessage(IMChatMessage message) {
		mentionUserIdList.clear();
		mentionUserIdList.add(message.getMsg().getContent().getUserInfo().getUserId());
		mEditText.setText(String.format("@%s", message.getMsg().getContent().getUserInfo().getName()));

		mEditText.setSelection(mEditText.getText().toString().trim().length());
		if (mAlertDialog != null) {
			mAlertDialog.dismiss();
		}
	}

	private void deleteMessage(IMChatMessage message) {
		RongIMClient.getInstance().deleteMessages(new int[]{message.getMsg().getMessageId()}, new RongIMClient.ResultCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean aBoolean) {
				if (aBoolean) {
					int i = datalists.indexOf(message);
					datalists.remove(message);
					mConversationAdaterEx.notifyItemRemoved(i);
				} else {
					ToastUtils.showToast("删除失败");
				}
				if (mAlertDialog != null) {
					mAlertDialog.dismiss();
				}
			}

			@Override
			public void onError(RongIMClient.ErrorCode errorCode) {
				Logger.e(errorCode.getValue() + "");
			}
		});
	}


	public void recallMessage(IMChatMessage message) {
		RongIMClient.getInstance().recallMessage(message.getMsg(), "", new RongIMClient.ResultCallback<RecallNotificationMessage>() {
			@Override
			public void onSuccess(RecallNotificationMessage recallNotificationMessage) {
				JSON.toJSONString(recallNotificationMessage);
				int i = datalists.indexOf(message);
				datalists.remove(message);
				IMChatMessage msg = new IMChatMessage();
				Message message1 = new Message();
				message1.setSenderUserId(Preferences.getUserId());
				message1.setObjectName("RC:GrpNtf");
				message1.setContent(recallNotificationMessage);
				message1.getContent().setUserInfo(new UserInfo(Preferences.getUserId(), Preferences.getUserName(), Uri.parse(Preferences.getUserPhoto())));
				msg.setMsg(message1);
				datalists.add(i, msg);
				mConversationAdaterEx.notifyDataSetChanged();
				if (mAlertDialog != null) {
					mAlertDialog.dismiss();
				}

			}

			@Override
			public void onError(RongIMClient.ErrorCode errorCode) {
				Logger.e(errorCode.getValue() + "");
			}
		});
	}

	public EditText getEditText() {
		return mEditText;
	}

	public int getEditTextID() {
		return R.id.rc_edit_text;
	}


}
