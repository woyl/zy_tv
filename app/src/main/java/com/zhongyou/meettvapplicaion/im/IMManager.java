package com.zhongyou.meettvapplicaion.im;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.orhanobut.logger.Logger;
import com.zhongyou.meettvapplicaion.Constant;
import com.zhongyou.meettvapplicaion.event.IMMessgeEvent;
import com.zhongyou.meettvapplicaion.event.KickOffEvent;
import com.zhongyou.meettvapplicaion.event.RecallMsgEvent;
import com.zhongyou.meettvapplicaion.utils.RxBus;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;

import org.w3c.dom.Text;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.RecallNotificationMessage;

/**
 * @author luopan@centerm.com
 * @date 2020-03-02 17:14.
 */
public class IMManager {
	protected String TAG = this.getClass().getSimpleName();
	private static volatile IMManager instance;
	private Context mContext;

	private IMManager() {
	}


	public static IMManager getInstance() {
		if (instance == null) {
			synchronized (IMManager.class) {
				if (instance == null) {
					instance = new IMManager();
				}
			}
		}
		return instance;
	}

	public void init(Context context, String appkey) {
		this.mContext = context.getApplicationContext();
		initRongIM(context, appkey);


		// 初始化会话界面相关内容
		initConversation();

		// 初始化会话列表界面相关内容
//		initConversationList();

		// 初始化连接状态变化监听
		initConnectStateChangeListener(context);

		// 初始化消息监听
		initOnReceiveMessage(context);

		// 初始化聊天室监听
//		initChatRoomActionListener();

	}

	/**
	 * 初始化消息监听
	 */
	private void initOnReceiveMessage(Context context) {
		RongIM.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {


			/**
			 * 收到消息的处理。
			 *
			 * @param message 收到的消息实体。
			 * @param left    剩余未拉取消息数目。
			 * @return 收到消息是否处理完成，true 表示自己处理铃声和后台通知，false 走融云默认处理方式。
			 */
			@Override
			public boolean onReceived(Message message, int left) {
				Logger.e("onReceived: "+JSON.toJSONString(message) );
				/*if (!TextUtils.isEmpty(message.getTargetId())&& Constant.currentGroupId.equals(message.getTargetId())){
					RxBus.sendMessage(new IMMessgeEvent(message,left));
				}*/
				if (!TextUtils.isEmpty(message.getTargetId()) && Constant.currentGroupId.equals(message.getTargetId())) {
					Intent intent = new Intent();
					intent.setAction(Constant.KEY_ONMessageArrived);
					intent.putExtra(Constant.KEY_IMMESSAGE_ARRIVE, message);
					mContext.sendBroadcast(intent);
				}

				return true;
			}
		});

		RongIMClient.setOnRecallMessageListener(new RongIMClient.OnRecallMessageListener() {
			@Override
			public boolean onMessageRecalled(Message message, RecallNotificationMessage recallNotificationMessage) {
				/*Logger.e(JSON.toJSONString(message));
				Logger.e(JSON.toJSONString(recallNotificationMessage));*/
				if (!TextUtils.isEmpty(message.getTargetId()) && Constant.currentGroupId.equals(message.getTargetId())) {
					RxBus.sendMessage(new RecallMsgEvent(message, recallNotificationMessage));
				}
				if (!TextUtils.isEmpty(message.getTargetId()) && Constant.currentGroupId.equals(message.getTargetId())) {
					Intent intent = new Intent();
					intent.setAction(Constant.KEY_IMMESSAGE_RECALL);
					intent.putExtra(Constant.KEY_IMMESSAGE_RECALL_MESSAGE, message);
//					intent.putExtra(Constant.KEY_IMMESSAGE_RECALL, recallNotificationMessage);
					mContext.sendBroadcast(intent);
				}
				return false;
			}
		});
	}

	/**
	 * 初始化连接状态监听
	 */
	private void initConnectStateChangeListener(Context context) {
		RongIM.setConnectionStatusListener(new RongIMClient.ConnectionStatusListener() {
			@Override
			public void onChanged(ConnectionStatus connectionStatus) {
				Log.d(TAG, "ConnectionStatus onChanged = " + connectionStatus.getMessage());
				if (connectionStatus.equals(ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT)) {
					//被其他提出时，需要返回登录界面
					//kickedOffline.postValue(true);
					RxBus.sendMessage(new KickOffEvent(true));
				} else if (connectionStatus == ConnectionStatus.TOKEN_INCORRECT) {
					//TODO token 错误时，重新登录
					ToastUtils.showToast("登陆信息异常 请重新登陆");
					RxBus.sendMessage(new KickOffEvent(false));
				}
			}
		});
	}

	/**
	 * 初始化会话列表相关事件
	 */
	private void initConversationList() {
		// 设置会话列表行为监听
		RongIM.setConversationListBehaviorListener(new RongIM.ConversationListBehaviorListener() {
			@Override
			public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String s) {
				//如果是群通知，点击头像进入群通知页面
				if (s.equals("__group_apply__")) {
					/*Intent noticeListIntent = new Intent(context, GroupNoticeListActivity.class);
					context.startActivity(noticeListIntent);*/
					return true;
				}
				return false;
			}

			@Override
			public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String s) {
				return false;
			}

			@Override
			public boolean onConversationLongClick(Context context, View view, UIConversation uiConversation) {
				return false;
			}

			@Override
			public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {
				/*
				 * 当点击会话列表中通知添加好友消息时，判断是否已成为好友
				 * 已成为好友时，跳转到私聊界面
				 * 非好友时跳转到新的朋友界面查看添加好友状态
				 */
				/*MessageContent messageContent = uiConversation.getMessageContent();
				if (messageContent instanceof ContactNotificationMessage) {
					ContactNotificationMessage contactNotificationMessage = (ContactNotificationMessage) messageContent;
					if (contactNotificationMessage.getOperation().equals("AcceptResponse")) {
						// 被加方同意请求后
						if (contactNotificationMessage.getExtra() != null) {
							ContactNotificationMessageData bean = null;
							try {
								Gson gson = new Gson();
								bean = gson.fromJson(contactNotificationMessage.getExtra(), ContactNotificationMessageData.class);
							} catch (Exception e) {
								e.printStackTrace();
							}
							RongIM.getInstance().startPrivateChat(context, uiConversation.getConversationSenderId(), bean.getSourceUserNickname());
						}
					} else {
						context.startActivity(new Intent(context, NewFriendListActivity.class));
					}
					return true;
				} else if (messageContent instanceof GroupApplyMessage) {
					Intent noticeListIntent = new Intent(context, GroupNoticeListActivity.class);
					context.startActivity(noticeListIntent);
					return true;
				}*/
				return false;
			}
		});
	}

	/**
	 * 调用初始化 RongIM
	 *
	 * @param context
	 */
	private void initRongIM(Context context, String appKey) {

		RongIM.init(context, appKey, true);
	}

	/**
	 * 初始化会话相关
	 */
	private void initConversation() {
		// 启用会话界面新消息提示
		RongIM.getInstance().enableNewComingMessageIcon(true);
		// 启用会话界面未读信息提示
		RongIM.getInstance().enableUnreadMessageIcon(true);


		// 添加会话界面点击事件
		RongIM.setConversationClickListener(new RongIM.ConversationClickListener() {
			@Override
			public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo, String s) {
				/*if (conversationType != Conversation.ConversationType.CUSTOMER_SERVICE) {
					Intent intent = new Intent(context, UserDetailActivity.class);
					intent.putExtra(IntentExtra.STR_TARGET_ID, userInfo.getUserId());
					if (conversationType == Conversation.ConversationType.GROUP) {
						Group groupInfo = RongUserInfoManager.getInstance().getGroupInfo(s);
						if (groupInfo != null) {
							intent.putExtra(IntentExtra.GROUP_ID, groupInfo.getId());
							intent.putExtra(IntentExtra.STR_GROUP_NAME, groupInfo.getName());
						}
					}
					context.startActivity(intent);
				}*/
				return true;
			}

			@Override
			public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo, String s) {
				/*if (conversationType == Conversation.ConversationType.GROUP) {
					// 当在群组时长按用户在输入框中加入 @ 信息
					ThreadManager.getInstance().runOnWorkThread(() -> {
						// 获取该群成员的用户名并显示在 @ 中的信息
						if (userInfo != null) {
							userInfo.setName("@" + userInfo.getName());
							ThreadManager.getInstance().runOnUIThread(new Runnable() {
								@Override
								public void run() {
									RongMentionManager.getInstance().mentionMember(userInfo);
									// 填充完用户@信息后弹出软键盘
									*//*if (context instanceof ConversationActivity) {
										((ConversationActivity) context).showSoftInput();
									}*//*
								}
							});
						}
					});
					return true;
				}*/
				return false;
			}

			@Override
			public boolean onMessageClick(Context context, View view, Message message) {
				/*if (message.getContent() instanceof ImageMessage) {
					Intent intent = new Intent(view.getContext(), SealPicturePagerActivity.class);
					intent.setPackage(view.getContext().getPackageName());
					intent.putExtra("message", message);
					view.getContext().startActivity(intent);
					return true;
				}*/
				return false;
			}

			@Override
			public boolean onMessageLinkClick(Context context, String s, Message message) {
				return false;
			}

			@Override
			public boolean onMessageLongClick(Context context, View view, Message message) {
				return false;
			}
		});
	}

}
