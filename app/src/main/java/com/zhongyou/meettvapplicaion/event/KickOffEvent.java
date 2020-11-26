package com.zhongyou.meettvapplicaion.event;

/**
 * @author luopan@centerm.com
 * @date 2020-03-05 13:41.
 */
public class KickOffEvent {
	private boolean isKickOff;

	public KickOffEvent(boolean isKickOff) {
		this.isKickOff = isKickOff;
	}

	public boolean isKickOff() {
		return isKickOff;
	}

	public void setKickOff(boolean kickOff) {
		isKickOff = kickOff;
	}
}
