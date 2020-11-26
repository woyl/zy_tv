package com.zhongyou.meettvapplicaion.im;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.orhanobut.logger.Logger;
import com.zhongyou.meettvapplicaion.persistence.Preferences;

import io.rong.imlib.model.Message;

/**
 * @author luopan@centerm.com
 * @date 2020-03-12 13:02.
 */
public class IMChatMessage implements MultiItemEntity {
	public final static int IMG = 0;
	public final static int TXT = 1;
	public final static int LOCATION = 2;
	public final static int NOTIFY = 3;

	public final static int MY_TXT = 4;
	public final static int MY_IMG = 5;
	public final static int MY_LOCATION = 6;
	private long xxid;
	private boolean isfocused;
	private int itemType;
	public boolean isIsfocused() {
		return isfocused;
	}

	public void setIsfocused(boolean isfocused) {
		this.isfocused = isfocused;
	}

	public long getXxid() {
		return xxid;
	}

	public void setXxid(long xxid) {
		this.xxid = xxid;
	}

	private Message msg;

	public Message getMsg() {
		return msg;
	}

	public void setMsg(Message msg) {
		this.msg = msg;
	}

	public void setItemType(int type){
		this.itemType=type;
	}

	@Override
	public int getItemType() {
		if (getMsg().getObjectName().equals("RC:ImgMsg")&&!isSend()) {
			itemType=IMG;
		} else if (getMsg().getObjectName().equals("RC:TxtMsg") && !isSend()) {
			itemType=TXT;
		} else if (getMsg().getObjectName().equals("RC:LBSMsg")&&!isSend()) {
			itemType=LOCATION;
		} else if (getMsg().getObjectName().equals("RC:GrpNtf")) {
			itemType=NOTIFY;
		} else if (getMsg().getObjectName().equals("RC:TxtMsg") && isSend()) {
			itemType=MY_TXT;
		}else if (getMsg().getObjectName().equals("RC:ImgMsg")&&isSend()){
			itemType=MY_IMG;
		}else if (getMsg().getObjectName().equals("RC:LBSMsg")&&isSend()) {
			itemType=MY_LOCATION;
		}else {
			itemType=NOTIFY;
		}
		return itemType;
	}

	public boolean isSend() {
		return getMsg().getSenderUserId().equals(Preferences.getUserId());
	}

}
