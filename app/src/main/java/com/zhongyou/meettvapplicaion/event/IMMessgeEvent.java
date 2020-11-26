package com.zhongyou.meettvapplicaion.event;

import io.rong.imlib.model.Message;

/**
 * @author luopan@centerm.com
 * @date 2020-03-05 10:01.
 */
public class IMMessgeEvent {

	public IMMessgeEvent(Message imMessage, int left) {
		this.imMessage = imMessage;
		this.left = left;
	}

	private Message imMessage;
	private int left;

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public Message getImMessage() {
		return imMessage;
	}

	public void setImMessage(Message imMessage) {
		this.imMessage = imMessage;
	}
}
