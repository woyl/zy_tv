package io.agora.openlive.model;

import android.content.Context;

import com.zhongyou.meettvapplicaion.BuildConfig;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.agora.rtc.IRtcEngineEventHandler;

public class MyEngineEventHandler {
	public MyEngineEventHandler(Context ctx, EngineConfig config) {
		this.mContext = ctx;
		this.mConfig = config;
	}

	private final EngineConfig mConfig;

	private final Context mContext;

	private final ConcurrentMap<AGEventHandler, Integer> mEventHandlerList = new ConcurrentHashMap<>();

	public void addEventHandler(AGEventHandler handler) {
		this.mEventHandlerList.put(handler, 0);
	}

	public void removeEventHandler(AGEventHandler handler) {
		this.mEventHandlerList.remove(handler);
	}

	final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
		private final Logger log = LoggerFactory.getLogger(this.getClass());

		@Override
		public void onActiveSpeaker(int uid) {
			if (BuildConfig.DEBUG) {
				/*ToastUtils.showToast("当前时间段声音最大的用户的 uid" + uid);*/
			}
		}

		@Override
		public void onRemoteAudioStats(RemoteAudioStats remoteAudioStats) {
			super.onRemoteAudioStats(remoteAudioStats);
			//话中远端音频流的统计信息回调。 可以用来判断视频传输情况 延迟数据
		}

		@Override
		public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
			Iterator<AGEventHandler> iterator = mEventHandlerList.keySet().iterator();
			while (iterator.hasNext()) {
				AGEventHandler handler = iterator.next();
				handler.onRemoteVideoStateChanged(uid, state, reason, elapsed);
			}
		}

		// 注册 onFirstRemoteVideoDecoded 回调。
		// SDK 接收到第一帧远端视频并成功解码时，会触发该回调。
		// 可以在该回调中调用 setupRemoteVideo 方法设置远端视图。
		@Override
		public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
			log.debug("onFirstRemoteVideoDecoded " + (uid & 0xFFFFFFFFL) + width + " " + height + " " + elapsed);
			Iterator<AGEventHandler> iterator = mEventHandlerList.keySet().iterator();
			while (iterator.hasNext()) {
				AGEventHandler handler = iterator.next();
				handler.onFirstRemoteVideoDecoded(uid, width, height, elapsed);
			}
		}

		@Override
		public void onFirstLocalVideoFrame(int width, int height, int elapsed) {
			log.debug("onFirstLocalVideoFrame " + width + " " + height + " " + elapsed);
		}

		@Override
		public void onUserJoined(int uid, int elapsed) {
			Iterator<AGEventHandler> iterator = mEventHandlerList.keySet().iterator();
			while (iterator.hasNext()) {
				AGEventHandler handler = iterator.next();
				handler.onUserJoined(uid, elapsed);
			}
		}

		// 注册 onUserOffline 回调。
		// 远端用户离开频道或掉线时，会触发该回调。
		@Override
		public void onUserOffline(int uid, int reason) {
			// FIXME this callback may return times
			Iterator<AGEventHandler> iterator = mEventHandlerList.keySet().iterator();
			while (iterator.hasNext()) {
				AGEventHandler handler = iterator.next();
				handler.onUserOffline(uid, reason);
			}
		}

		@Override
		public void onUserMuteVideo(int uid, boolean muted) {
			Iterator<AGEventHandler> iterator = mEventHandlerList.keySet().iterator();
			while (iterator.hasNext()) {
				AGEventHandler handler = iterator.next();
				handler.onUserMuteVideo(uid, muted);
			}
		}

		@Override
		public void onUserMuteAudio(int uid, boolean muted) {
			Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
			while (it.hasNext()) {
				AGEventHandler handler = it.next();
				handler.onUserMuteAudio(uid, muted);
			}
		}

		@Override
		public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
			Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
			while (it.hasNext()) {
				AGEventHandler handler = it.next();
				handler.onAudioVolumeIndication(speakers, totalVolume);
			}
		}

		@Override
		public void onRtcStats(RtcStats stats) {
			Iterator<AGEventHandler> iterator = mEventHandlerList.keySet().iterator();
			while (iterator.hasNext()) {
				AGEventHandler handler = iterator.next();
				handler.onRtcStats(stats);
			}
		}

		@Override
		public void onLeaveChannel(RtcStats stats) {

		}

		@Override
		public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
			log.debug("onNetworkQuality " + uid + "" + txQuality + "" + rxQuality);
			Iterator<AGEventHandler> iterator = mEventHandlerList.keySet().iterator();
			while (iterator.hasNext()) {
				AGEventHandler handler = iterator.next();
				handler.onNetworkQuality(uid, txQuality, rxQuality);
			}
		}

		/**
		 * 报告本地用户的网络质量，该回调函数每 2 秒触发一次
		 * @param quality
		 */
		@Override
		public void onLastmileQuality(int quality) {
			log.debug("onLastmileQuality " + quality);
			Iterator<AGEventHandler> iterator = mEventHandlerList.keySet().iterator();
			while (iterator.hasNext()) {
				AGEventHandler handler = iterator.next();
				handler.onLastmileQuality(quality);
			}
		}

		@Override
		public void onError(int err) {
			log.debug("onError " + err);
			Iterator<AGEventHandler> iterator = mEventHandlerList.keySet().iterator();
			while (iterator.hasNext()) {
				AGEventHandler handler = iterator.next();
				handler.onError(err);
			}
		}

		// 注册 onJoinChannelSuccess 回调。
		// 本地用户成功加入频道时，会触发该回调。
		@Override
		public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
			log.debug("onJoinChannelSuccess " + channel + " " + uid + " " + (uid & 0xFFFFFFFFL) + " " + elapsed);
			Iterator<AGEventHandler> iterator = mEventHandlerList.keySet().iterator();
			while (iterator.hasNext()) {
				AGEventHandler handler = iterator.next();
				handler.onJoinChannelSuccess(channel, uid, elapsed);
			}
		}

		public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {
			log.debug("onRejoinChannelSuccess " + channel + " " + uid + " " + elapsed);
		}

		@Override
		public void onConnectionLost() {
			log.debug("onConnectionLost");
			Iterator<AGEventHandler> iterator = mEventHandlerList.keySet().iterator();
			while (iterator.hasNext()) {
				AGEventHandler handler = iterator.next();
				handler.onConnectionLost();
			}
		}

		@Override
		public void onConnectionInterrupted() {
			log.debug("onConnectionInterrupted");
			Iterator<AGEventHandler> iterator = mEventHandlerList.keySet().iterator();
			while (iterator.hasNext()) {
				AGEventHandler handler = iterator.next();
				handler.onConnectionInterrupted();
			}
		}

		public void onWarning(int warn) {
			log.debug("onWarning " + warn);
			Iterator<AGEventHandler> iterator = mEventHandlerList.keySet().iterator();
			while (iterator.hasNext()) {
				AGEventHandler handler = iterator.next();
				handler.onWarning(warn);
			}
		}

		@Override
		public void onConnectionStateChanged(int state, int reason) {
			com.orhanobut.logger.Logger.e("state:" + state + "  reason:-->" + reason);
			super.onConnectionStateChanged(state, reason);

		}

		@Override
		public void onLocalVideoStateChanged(int localVideoState, int error) {
			super.onLocalVideoStateChanged(localVideoState, error);
			Iterator<AGEventHandler> iterator = mEventHandlerList.keySet().iterator();
			while (iterator.hasNext()) {
				AGEventHandler handler = iterator.next();
				handler.onLocalVideoStateChanged(localVideoState, error);
			}

		}

		/**
		 * 远端音频状态发生改变回调
		 * */
		@Override
		public void onRemoteAudioStateChanged(int uid, int state, int reason, int elapsed) {
			super.onRemoteAudioStateChanged(uid, state, reason, elapsed);
			Iterator<AGEventHandler> iterator = mEventHandlerList.keySet().iterator();
			while (iterator.hasNext()) {
				AGEventHandler handler = iterator.next();
				handler.onRemoteAudioStateChanged(uid, state, reason, elapsed);
			}
		}
	};

}
