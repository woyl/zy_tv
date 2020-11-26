package com.zhongyou.meettvapplicaion.im;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.zhongyou.meettvapplicaion.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.rong.common.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.TvBaseAdapter;
import io.rong.imkit.destruct.DestructManager;
import io.rong.imkit.model.ConversationKey;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utilities.RongUtils;
import io.rong.imkit.utils.RongDateUtils;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.ProviderContainerView;
import io.rong.imkit.widget.adapter.MessageListAdapter;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.destruct.DestructionTaskManager;
import io.rong.imlib.location.message.RealTimeLocationJoinMessage;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.ReadReceiptInfo;
import io.rong.imlib.model.UnknownMessage;
import io.rong.imlib.model.UserInfo;
import io.rong.message.GroupNotificationMessage;
import io.rong.message.HistoryDividerMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.TextMessage;

/**
 * @author luopan@centerm.com
 * @date 2020-03-12 09:54.
 */
public class TVIMAdapter extends TvBaseAdapter {
	private List<Message> mMessageList;
	private Context mContext;
	private LayoutInflater mInflater;
	private static long readReceiptRequestInterval = 120L;
	protected boolean timeGone = false;

	public TVIMAdapter(List<Message> messageList, Context context) {
		mMessageList = messageList;
		mContext = context;
		this.mInflater = LayoutInflater.from(this.mContext);
	}

	@Override
	public int getCount() {
		return mMessageList == null ? 0 : mMessageList.size();
	}

	@Override
	public Object getItem(int position) {
		return position >= this.mMessageList.size() ? null : this.mMessageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View contentView, ViewGroup parent) {
		ViewHolder viewHolder = null;

		if (contentView != null) {
			viewHolder= (ViewHolder) contentView.getTag();
		} else {
			contentView= this.mInflater.inflate(R.layout.rc_item_message, null);
			viewHolder = new ViewHolder();
			viewHolder.leftIconView = (AsyncImageView) this.findViewById(contentView, R.id.rc_left);
			viewHolder.rightIconView = (AsyncImageView) this.findViewById(contentView, R.id.rc_right);
			viewHolder.nameView = (TextView) this.findViewById(contentView, R.id.rc_title);
			viewHolder.contentView = (ProviderContainerView) this.findViewById(contentView, R.id.rc_content);
			viewHolder.layout = (ViewGroup) this.findViewById(contentView, R.id.rc_layout);
			viewHolder.progressBar = (ProgressBar) this.findViewById(contentView, R.id.rc_progress);
			viewHolder.warning = (ImageView) this.findViewById(contentView, R.id.rc_warning);
			viewHolder.readReceipt = (TextView) this.findViewById(contentView, R.id.rc_read_receipt);
			viewHolder.readReceiptRequest = (TextView) this.findViewById(contentView, R.id.rc_read_receipt_request);
			viewHolder.readReceiptStatus = (TextView) this.findViewById(contentView, R.id.rc_read_receipt_status);
			viewHolder.message_check = (CheckBox) this.findViewById(contentView, R.id.message_check);
			viewHolder.checkboxLayout = (LinearLayout) this.findViewById(contentView, R.id.ll_message_check);
			viewHolder.time = (TextView) this.findViewById(contentView, R.id.rc_time);
			viewHolder.sentStatus = (TextView) this.findViewById(contentView, R.id.rc_sent_status);
			viewHolder.layoutItem = (RelativeLayout) this.findViewById(contentView, R.id.rc_layout_item_message);
			if (viewHolder.time.getVisibility() == View.GONE) {
				this.timeGone = true;
			} else {
				this.timeGone = false;
			}
			contentView.setTag(viewHolder);
		}
//		this.bindView(contentView,position,getItem(position));
		return contentView;
	}

	View v1 = null;

	protected void bindView(View v, int position, final UIMessage data) {

		if (data != null) {
			final ViewHolder holder = (ViewHolder) v.getTag();
			if (holder == null) {
				RLog.e("MessageListAdapter", "view holder is null !");
			} else {
				Object provider;
				ProviderTag tag;
				if (this.getNeedEvaluate(data)) {
					provider = RongContext.getInstance().getEvaluateProvider();
					tag = RongContext.getInstance().getMessageProviderTag(data.getContent().getClass());
				} else {
					if (RongContext.getInstance() == null || data.getContent() == null) {
						RLog.e("MessageListAdapter", "Message is null !");
						return;
					}

					provider = RongContext.getInstance().getMessageTemplate(data.getContent().getClass());
					if (provider == null) {
						provider = RongContext.getInstance().getMessageTemplate(UnknownMessage.class);
						tag = RongContext.getInstance().getMessageProviderTag(UnknownMessage.class);
					} else {
						tag = RongContext.getInstance().getMessageProviderTag(data.getContent().getClass());
					}

					if (provider == null) {
						RLog.e("MessageListAdapter", data.getObjectName() + " message provider not found !");
						return;
					}
				}

				if (data.getContent() != null && data.getContent().isDestruct() && data.getMessage() != null && data.getMessage().getReadTime() > 0L && data.getMessage().getContent() != null) {
					if (data.getMessageDirection() == Message.MessageDirection.SEND) {
						Log.d("delete", data.getObjectName());
						DestructManager.getInstance().deleteMessage(data.getMessage());
						this.remove(position);
						this.notifyDataSetChanged();
						return;
					}

					long readTime = data.getMessage().getReadTime();
					long serverTime = System.currentTimeMillis() - RongIMClient.getInstance().getDeltaTime();
					MessageContent messageContent = data.getMessage().getContent();
					long delay = messageContent.getDestructTime() - (serverTime - readTime) / 1000L;
					Log.d("delete delay", delay + "s");
					if (delay <= 0L) {
						Log.d("delete", data.getObjectName());
						DestructionTaskManager.getInstance().deleteMessage(data.getMessage());
						this.remove(position);
						this.notifyDataSetChanged();
						return;
					}
				}


				try {
					v1 = holder.contentView.inflate((IContainerItemProvider) provider);
				} catch (Exception var15) {
					RLog.e("MessageListAdapter", "bindView contentView inflate error", var15);
					provider = RongContext.getInstance().getMessageTemplate(UnknownMessage.class);
					tag = RongContext.getInstance().getMessageProviderTag(UnknownMessage.class);
					v1 = holder.contentView.inflate((IContainerItemProvider) provider);
				}

				((IContainerItemProvider) provider).bindView(v1, position, data);
				if (tag == null) {
					RLog.e("MessageListAdapter", "Can not find ProviderTag for " + data.getObjectName());
				} else {
					if (tag.hide()) {
						holder.contentView.setVisibility(View.GONE);
						holder.time.setVisibility(View.GONE);
						holder.nameView.setVisibility(View.GONE);
						holder.leftIconView.setVisibility(View.GONE);
						holder.rightIconView.setVisibility(View.GONE);
						holder.layoutItem.setVisibility(View.GONE);
						holder.layoutItem.setPadding(0, 0, 0, 0);
					} else {
						holder.contentView.setVisibility(View.VISIBLE);
						holder.layoutItem.setVisibility(View.VISIBLE);
						holder.layoutItem.setPadding(RongUtils.dip2px(8.0F), RongUtils.dip2px(6.0F), RongUtils.dip2px(8.0F), RongUtils.dip2px(6.0F));
					}

					UserInfo userInfo;
					GroupUserInfo groupUserInfo;
					if (data.getMessageDirection() == Message.MessageDirection.SEND) {
						if (tag.showPortrait()) {
							holder.rightIconView.setVisibility(View.VISIBLE);
							holder.leftIconView.setVisibility(View.GONE);
						} else {
							holder.leftIconView.setVisibility(View.GONE);
							holder.rightIconView.setVisibility(View.GONE);
						}

						if (!tag.centerInHorizontal()) {
							this.setGravity(holder.layout, 5);
							holder.contentView.containerViewRight();
							holder.nameView.setGravity(5);
						} else {
							this.setGravity(holder.layout, 17);
							holder.contentView.containerViewCenter();
							holder.nameView.setGravity(1);
							holder.contentView.setBackgroundColor(0);
						}

						boolean readRec = false;

						try {
							readRec = this.mContext.getResources().getBoolean(R.bool.rc_read_receipt);
						} catch (Resources.NotFoundException var14) {
							RLog.e("MessageListAdapter", "bindView rc_read_receipt not configure in rc_config.xml", var14);
						}

						if (data.getSentStatus() == Message.SentStatus.SENDING) {
							if (tag.showProgress()) {
								holder.progressBar.setVisibility(View.VISIBLE);
							} else {
								holder.progressBar.setVisibility(View.GONE);
							}

							holder.warning.setVisibility(View.GONE);
							holder.readReceipt.setVisibility(View.GONE);
						} else if (data.getSentStatus() == Message.SentStatus.FAILED) {
							holder.progressBar.setVisibility(View.GONE);
							holder.warning.setVisibility(View.VISIBLE);
							holder.readReceipt.setVisibility(View.GONE);
						} else if (data.getSentStatus() == Message.SentStatus.SENT) {
							holder.progressBar.setVisibility(View.GONE);
							holder.warning.setVisibility(View.GONE);
							holder.readReceipt.setVisibility(View.GONE);
						} else if (readRec && data.getSentStatus() == Message.SentStatus.READ) {
							holder.progressBar.setVisibility(View.GONE);
							holder.warning.setVisibility(View.GONE);
							if (data.getConversationType().equals(Conversation.ConversationType.PRIVATE) && tag.showReadState()) {
								holder.readReceipt.setVisibility(View.VISIBLE);
							} else {
								holder.readReceipt.setVisibility(View.GONE);
							}
						} else {
							holder.progressBar.setVisibility(View.GONE);
							holder.warning.setVisibility(View.GONE);
							holder.readReceipt.setVisibility(View.GONE);
						}

						holder.readReceiptRequest.setVisibility(View.GONE);
						holder.readReceiptStatus.setVisibility(View.GONE);
						if (readRec && RongContext.getInstance().isReadReceiptConversationType(data.getConversationType()) && (data.getConversationType().equals(Conversation.ConversationType.GROUP) || data.getConversationType().equals(Conversation.ConversationType.DISCUSSION))) {
							if (this.allowReadReceiptRequest(data.getMessage()) && !TextUtils.isEmpty(data.getUId())) {
								boolean isLastSentMessage = true;

								for (int i = position + 1; i < this.getCount(); ++i) {
									if (((UIMessage) this.getItem(i)).getMessageDirection() == Message.MessageDirection.SEND) {
										isLastSentMessage = false;
										break;
									}
								}

								long serverTime = System.currentTimeMillis() - RongIMClient.getInstance().getDeltaTime();
								if (serverTime - data.getSentTime() < readReceiptRequestInterval * 1000L && isLastSentMessage && (data.getReadReceiptInfo() == null || !data.getReadReceiptInfo().isReadReceiptMessage())) {
									holder.readReceiptRequest.setVisibility(View.VISIBLE);
								}
							}

							if (this.allowReadReceiptRequest(data.getMessage()) && data.getReadReceiptInfo() != null && data.getReadReceiptInfo().isReadReceiptMessage()) {
								if (data.getReadReceiptInfo().getRespondUserIdList() != null) {
									holder.readReceiptStatus.setText(String.format(v1.getResources().getString(R.string.rc_read_receipt_status), data.getReadReceiptInfo().getRespondUserIdList().size()));
								} else {
									holder.readReceiptStatus.setText(String.format(v1.getResources().getString(R.string.rc_read_receipt_status), 0));
								}

								holder.readReceiptStatus.setVisibility(View.VISIBLE);
							}
						}

						holder.nameView.setVisibility(View.GONE);
						holder.readReceiptRequest.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								RongIMClient.getInstance().sendReadReceiptRequest(data.getMessage(), new RongIMClient.OperationCallback() {
									public void onSuccess() {
										ReadReceiptInfo readReceiptInfo = data.getReadReceiptInfo();
										if (readReceiptInfo == null) {
											readReceiptInfo = new ReadReceiptInfo();
											data.setReadReceiptInfo(readReceiptInfo);
										}

										readReceiptInfo.setIsReadReceiptMessage(true);
										holder.readReceiptStatus.setText(String.format(v1.getResources().getString(R.string.rc_read_receipt_status), 0));
										holder.readReceiptRequest.setVisibility(View.GONE);
										holder.readReceiptStatus.setVisibility(View.VISIBLE);
									}

									public void onError(RongIMClient.ErrorCode errorCode) {
										RLog.e("MessageListAdapter", "sendReadReceiptRequest failed, errorCode = " + errorCode);
									}
								});
							}
						});
					/*	holder.readReceiptStatus.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								if (this.mOnItemHandlerListener != null) {
									this.mOnItemHandlerListener.onReadReceiptStateClick(data.getMessage());
								}

							}
						});*/
						if (!tag.showWarning()) {
							holder.warning.setVisibility(View.GONE);
						}
					} else {
						if (tag.showPortrait()) {
							holder.rightIconView.setVisibility(View.GONE);
							holder.leftIconView.setVisibility(View.VISIBLE);
						} else {
							holder.leftIconView.setVisibility(View.GONE);
							holder.rightIconView.setVisibility(View.GONE);
						}

						if (!tag.centerInHorizontal()) {
							this.setGravity(holder.layout, 3);
							holder.contentView.containerViewLeft();
							holder.nameView.setGravity(3);
						} else {
							this.setGravity(holder.layout, 17);
							holder.contentView.containerViewCenter();
							holder.nameView.setGravity(1);
							holder.contentView.setBackgroundColor(0);
						}

						holder.progressBar.setVisibility(View.GONE);
						holder.warning.setVisibility(View.GONE);
						holder.readReceipt.setVisibility(View.GONE);
						holder.readReceiptRequest.setVisibility(View.GONE);
						holder.readReceiptStatus.setVisibility(View.GONE);
						holder.nameView.setVisibility(View.VISIBLE);
						if (data.getConversationType() != Conversation.ConversationType.PRIVATE && tag.showSummaryWithName() && data.getConversationType() != Conversation.ConversationType.PUBLIC_SERVICE && data.getConversationType() != Conversation.ConversationType.APP_PUBLIC_SERVICE) {
							userInfo = data.getUserInfo();
							if (data.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE) && data.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
								if (userInfo == null && data.getMessage() != null && data.getMessage().getContent() != null) {
									userInfo = data.getMessage().getContent().getUserInfo();
								}

								if (userInfo != null) {
									holder.nameView.setText(userInfo.getName());
								} else {
									holder.nameView.setText(data.getSenderUserId());
								}
							} else if (data.getConversationType() == Conversation.ConversationType.GROUP) {
								groupUserInfo = RongUserInfoManager.getInstance().getGroupUserInfo(data.getTargetId(), data.getSenderUserId());
								if (groupUserInfo != null && !TextUtils.isEmpty(groupUserInfo.getNickname())) {
									holder.nameView.setText(groupUserInfo.getNickname());
								} else {
									if (userInfo == null) {
										userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
									}

									if (userInfo == null) {
										holder.nameView.setText(data.getSenderUserId());
									} else {
										holder.nameView.setText(userInfo.getName());
									}
								}
							} else {
								if (userInfo == null) {
									userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
								}

								if (userInfo == null) {
									holder.nameView.setText(data.getSenderUserId());
								} else {
									holder.nameView.setText(userInfo.getName());
								}
							}
						} else {
							holder.nameView.setVisibility(View.GONE);
						}
					}

					Uri portrait;
					ConversationKey mKey;
					PublicServiceProfile publicServiceProfile;
					if (holder.rightIconView.getVisibility() == View.VISIBLE) {
						userInfo = data.getUserInfo();
						portrait = null;
						if (data.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE) && data.getUserInfo() != null && data.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
							if (userInfo != null) {
								portrait = userInfo.getPortraitUri();
							}

							holder.rightIconView.setAvatar(portrait != null ? portrait.toString() : null, 0);
						} else if ((data.getConversationType().equals(Conversation.ConversationType.PUBLIC_SERVICE) || data.getConversationType().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE)) && data.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
							if (userInfo != null) {
								portrait = userInfo.getPortraitUri();
								holder.rightIconView.setAvatar(portrait != null ? portrait.toString() : null, 0);
							} else {
								mKey = ConversationKey.obtain(data.getTargetId(), data.getConversationType());
								publicServiceProfile = RongContext.getInstance().getPublicServiceInfoFromCache(mKey.getKey());
								portrait = publicServiceProfile.getPortraitUri();
								holder.rightIconView.setAvatar(portrait != null ? portrait.toString() : null, 0);
							}
						} else if (!TextUtils.isEmpty(data.getSenderUserId())) {
							if (userInfo == null) {
								userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
							}

							if (userInfo != null && userInfo.getPortraitUri() != null) {
								holder.rightIconView.setAvatar(userInfo.getPortraitUri().toString(), 0);
							} else {
								holder.rightIconView.setAvatar((String) null, 0);
							}
						}
					} else if (holder.leftIconView.getVisibility() == View.VISIBLE) {
						userInfo = data.getUserInfo();
						groupUserInfo = null;
						if (data.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE) && data.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
							if (userInfo == null && data.getMessage() != null && data.getMessage().getContent() != null) {
								userInfo = data.getMessage().getContent().getUserInfo();
							}

							if (userInfo != null) {
								portrait = userInfo.getPortraitUri();
								holder.leftIconView.setAvatar(portrait != null ? portrait.toString() : null, R.drawable.rc_cs_default_portrait);
							}
						} else if ((data.getConversationType().equals(Conversation.ConversationType.PUBLIC_SERVICE) || data.getConversationType().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE)) && data.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
							if (userInfo != null) {
								portrait = userInfo.getPortraitUri();
								holder.leftIconView.setAvatar(portrait != null ? portrait.toString() : null, 0);
							} else {
								mKey = ConversationKey.obtain(data.getTargetId(), data.getConversationType());
								publicServiceProfile = RongContext.getInstance().getPublicServiceInfoFromCache(mKey.getKey());
								if (publicServiceProfile != null && publicServiceProfile.getPortraitUri() != null) {
									holder.leftIconView.setAvatar(publicServiceProfile.getPortraitUri().toString(), 0);
								} else {
									holder.leftIconView.setAvatar((String) null, 0);
								}
							}
						} else if (!TextUtils.isEmpty(data.getSenderUserId())) {
							if (userInfo == null) {
								userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
							}

							if (userInfo != null && userInfo.getPortraitUri() != null) {
								holder.leftIconView.setAvatar(userInfo.getPortraitUri().toString(), 0);
							} else {
								holder.leftIconView.setAvatar((String) null, 0);
							}
						}
					}

					v.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					});

//					this.bindViewClickEvent(v, v1, position, data);
					if (tag.hide()) {
						holder.time.setVisibility(View.GONE);
					} else {
						if (!this.timeGone) {
							String time = RongDateUtils.getConversationFormatDate(data.getSentTime(), v1.getContext());
							holder.time.setText(time);
							if (position == 0) {
								if (data.getMessage() != null && data.getMessage().getContent() != null && data.getMessage().getContent() instanceof HistoryDividerMessage) {
									holder.time.setVisibility(View.GONE);
								} else {
									holder.time.setVisibility(View.VISIBLE);
								}
							} else {
								UIMessage pre = (UIMessage) this.getItem(position - 1);
								if (RongDateUtils.isShowChatTime(data.getSentTime(), pre.getSentTime(), 180)) {
									holder.time.setVisibility(View.VISIBLE);
								} else {
									holder.time.setVisibility(View.GONE);
								}
							}
						}

						if (this.isShowCheckbox() && this.allowShowCheckButton(data.getMessage())) {
							holder.checkboxLayout.setVisibility(View.VISIBLE);
							holder.message_check.setFocusable(false);
							holder.message_check.setClickable(false);
							holder.message_check.setChecked(data.isChecked());
						} else {
							holder.checkboxLayout.setVisibility(View.GONE);
							data.setChecked(false);
						}

						/*if (this.messageCheckedChanged != null) {
							this.messageCheckedChanged.onCheckedEnable(this.getCheckedMessage().size() > 0);
						}
*/
					}
				}
			}
		}


		v1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Object provider;
				if (getNeedEvaluate(data)) {
					provider = RongContext.getInstance().getEvaluateProvider();
				} else {
					provider = RongContext.getInstance().getMessageTemplate(data.getContent().getClass());
				}

				if (provider != null) {
					((IContainerItemProvider.MessageProvider) provider).onItemLongClick(v, position, data.getContent(), data);
				} else {
					Logger.e("provider==null");
				}

			}

		});

		v.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
					Logger.e("KEYCODE_DPAD_CENTER  v 被点击了……");
					Object provider;
					if (getNeedEvaluate(data)) {
						provider = RongContext.getInstance().getEvaluateProvider();
					} else {
						provider = RongContext.getInstance().getMessageTemplate(data.getContent().getClass());
					}

					if (provider != null) {
						((IContainerItemProvider.MessageProvider) provider).onItemLongClick(v, position, data.getContent(), data);
					} else {
						Logger.e("provider==null");
					}
				}
				return false;
			}
		});
		v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Logger.e("v 被点击了……");
				Object provider;
				if (getNeedEvaluate(data)) {
					provider = RongContext.getInstance().getEvaluateProvider();
				} else {
					provider = RongContext.getInstance().getMessageTemplate(data.getContent().getClass());
				}

				if (provider != null) {
					((IContainerItemProvider.MessageProvider) provider).onItemLongClick(v, position, data.getContent(), data);
				} else {
					Logger.e("provider==null");
				}

			}
		});

	}


	protected boolean getNeedEvaluate(UIMessage data) {
		String extra = "";
		String robotEva = "";
		String sid = "";
		if (data != null && data.getConversationType() != null && data.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
			if (data.getContent() instanceof TextMessage) {
				extra = ((TextMessage) data.getContent()).getExtra();
				if (TextUtils.isEmpty(extra)) {
					return false;
				}

				try {
					JSONObject jsonObj = new JSONObject(extra);
					robotEva = jsonObj.optString("robotEva");
					sid = jsonObj.optString("sid");
				} catch (JSONException var6) {
				}
			}

			if (data.getMessageDirection() == Message.MessageDirection.RECEIVE && data.getContent() instanceof TextMessage && this.evaForRobot && this.robotMode && !TextUtils.isEmpty(robotEva) && !TextUtils.isEmpty(sid) && !data.getIsHistoryMessage()) {
				return true;
			}
		}

		return false;
	}


	public void remove(int position) {
		this.mMessageList.remove(position);
	}

	protected void setGravity(View view, int gravity) {
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
		params.gravity = gravity;
	}

	public boolean allowReadReceiptRequest(Message message) {
		return message != null && message.getContent() != null && message.getContent() instanceof TextMessage;
	}

	private boolean isShowCheckbox;

	public boolean isShowCheckbox() {
		return this.isShowCheckbox;
	}

	public void setShowCheckbox(boolean showCheckbox) {
		this.isShowCheckbox = showCheckbox;
	}


	protected boolean allowShowCheckButton(Message message) {
		if (message != null) {
			MessageContent messageContent = message.getContent();
			if (messageContent != null && (messageContent instanceof InformationNotificationMessage || messageContent instanceof UnknownMessage || messageContent instanceof GroupNotificationMessage || messageContent instanceof RecallNotificationMessage || messageContent instanceof RealTimeLocationJoinMessage)) {
				return false;
			}
		}

		return true;
	}

	boolean evaForRobot = false;
	boolean robotMode = true;

	public void setEvaluateForRobot(boolean needEvaluate) {
		this.evaForRobot = needEvaluate;
	}

	public void setRobotMode(boolean robotMode) {
		this.robotMode = robotMode;
	}

	protected class ViewHolder {
		public AsyncImageView leftIconView;
		public AsyncImageView rightIconView;
		public TextView nameView;
		public ProviderContainerView contentView;
		public ProgressBar progressBar;
		public ImageView warning;
		public TextView readReceipt;
		public TextView readReceiptRequest;
		public TextView readReceiptStatus;
		public ViewGroup layout;
		public TextView time;
		public TextView sentStatus;
		public RelativeLayout layoutItem;
		public CheckBox message_check;
		public LinearLayout checkboxLayout;

		protected ViewHolder() {
		}
	}

	protected <T extends View> T findViewById(View view, int id) {
		return view.findViewById(id);
	}
}
