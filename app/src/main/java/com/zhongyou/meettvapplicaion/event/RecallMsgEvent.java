package com.zhongyou.meettvapplicaion.event;

import io.rong.imlib.model.Message;
import io.rong.message.RecallNotificationMessage;

/**
 * @author luopan@centerm.com
 * @date 2020-03-13 12:04.
 */
public class RecallMsgEvent {
	private Message mMessage;
	private RecallNotificationMessage mRecallNotificationMessage;

	public RecallNotificationMessage getRecallNotificationMessage() {
		return mRecallNotificationMessage;
	}

	public RecallMsgEvent(Message message, RecallNotificationMessage recallNotificationMessage) {
		mMessage = message;
		mRecallNotificationMessage = recallNotificationMessage;
	}

	public void setRecallNotificationMessage(RecallNotificationMessage recallNotificationMessage) {
		mRecallNotificationMessage = recallNotificationMessage;
	}



	public Message getMessage() {
		return mMessage;
	}

	public void setMessage(Message message) {
		mMessage = message;
	}
}
