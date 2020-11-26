package com.zhongyou.meettvapplicaion.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.zhongyou.meettvapplicaion.entities.MeetingJoin;

/**
 * @author golangdorid@gmail.com
 * @date 2020/4/29 12:45 PM.
 * @
 */
public class NewMeetingJoin implements Parcelable {

	/**
	 * errcode : 0
	 * data : {"role":0,"meeting":{"id":"59739a22777648febc9dabf8757b53e6","createDate":"2020-03-27 10:56:25","updateDate":"2020-03-27 10:56:25","delFlag":"0","title":"22","startTime":"2020-03-27 10:56:04","actualStartTime":"2020-03-27 18:30:12","totalParticipants":22,"totalAttendance":0,"hostToken":"4294","participantsToken":"6634","screenshotFrequency":0,"screenshotCompressionRatio":16,"screenshotScrollFrequency":1000,"meetingProcess":1,"approved":1,"createById":"21","duration":0,"type":0,"materialsCnt":0,"userId":"","audienceToken":"4127","resolvingPower":1,"isRecord":0},"hostUser":{"clientUid":"260706877","hostUserName":"lozzow","hostUserId":"607d687a9ec9411dab26940df0795161","status":1}}
	 * errmsg : 处理成功
	 */

	private int errcode;
	private DataBean data;
	private String errmsg;

	public int getErrcode() {
		return errcode;
	}

	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}

	public DataBean getData() {
		return data;
	}

	public void setData(DataBean data) {
		this.data = data;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public static class DataBean implements Parcelable {
		/**
		 * role : 0
		 * meeting : {"id":"59739a22777648febc9dabf8757b53e6","createDate":"2020-03-27 10:56:25","updateDate":"2020-03-27 10:56:25","delFlag":"0","title":"22","startTime":"2020-03-27 10:56:04","actualStartTime":"2020-03-27 18:30:12","totalParticipants":22,"totalAttendance":0,"hostToken":"4294","participantsToken":"6634","screenshotFrequency":0,"screenshotCompressionRatio":16,"screenshotScrollFrequency":1000,"meetingProcess":1,"approved":1,"createById":"21","duration":0,"type":0,"materialsCnt":0,"userId":"","audienceToken":"4127","resolvingPower":1,"isRecord":0}
		 * hostUser : {"clientUid":"260706877","hostUserName":"lozzow","hostUserId":"607d687a9ec9411dab26940df0795161","status":1}
		 */

		private int role;
		private MeetingBean meeting;
		private HostUserBean hostUser;

		public int getRole() {
			return role;
		}

		public void setRole(int role) {
			this.role = role;
		}

		public MeetingBean getMeeting() {
			return meeting;
		}

		public void setMeeting(MeetingBean meeting) {
			this.meeting = meeting;
		}

		public HostUserBean getHostUser() {
			return hostUser;
		}

		public void setHostUser(HostUserBean hostUser) {
			this.hostUser = hostUser;
		}

		public static class MeetingBean implements Parcelable {
			/**
			 * id : 59739a22777648febc9dabf8757b53e6
			 * createDate : 2020-03-27 10:56:25
			 * updateDate : 2020-03-27 10:56:25
			 * delFlag : 0
			 * title : 22
			 * startTime : 2020-03-27 10:56:04
			 * actualStartTime : 2020-03-27 18:30:12
			 * totalParticipants : 22
			 * totalAttendance : 0
			 * hostToken : 4294
			 * participantsToken : 6634
			 * screenshotFrequency : 0
			 * screenshotCompressionRatio : 16
			 * screenshotScrollFrequency : 1000
			 * meetingProcess : 1
			 * approved : 1
			 * createById : 21
			 * duration : 0
			 * type : 0
			 * materialsCnt : 0
			 * userId :
			 * audienceToken : 4127
			 * resolvingPower : 1
			 * isRecord : 0
			 */

			private int isMeeting;

			public int getIsMeeting() {
				return isMeeting;
			}

			public void setIsMeeting(int isMeeting) {
				this.isMeeting = isMeeting;
			}

			private String id;
			private String createDate;
			private String updateDate;
			private String delFlag;
			private String title;
			private String startTime;
			private String actualStartTime;
			private int totalParticipants;
			private int totalAttendance;
			private String hostToken;
			private String participantsToken;
			private int screenshotFrequency;
			private int screenshotCompressionRatio;
			private int screenshotScrollFrequency;
			private int meetingProcess;
			private int approved;
			private String createById;
			private int duration;
			private int type;
			private int materialsCnt;
			private String userId;
			private String audienceToken;
			private int resolvingPower;
			private int isRecord;

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}

			public String getCreateDate() {
				return createDate;
			}

			public void setCreateDate(String createDate) {
				this.createDate = createDate;
			}

			public String getUpdateDate() {
				return updateDate;
			}

			public void setUpdateDate(String updateDate) {
				this.updateDate = updateDate;
			}

			public String getDelFlag() {
				return delFlag;
			}

			public void setDelFlag(String delFlag) {
				this.delFlag = delFlag;
			}

			public String getTitle() {
				return title;
			}

			public void setTitle(String title) {
				this.title = title;
			}

			public String getStartTime() {
				return startTime;
			}

			public void setStartTime(String startTime) {
				this.startTime = startTime;
			}

			public String getActualStartTime() {
				return actualStartTime;
			}

			public void setActualStartTime(String actualStartTime) {
				this.actualStartTime = actualStartTime;
			}

			public int getTotalParticipants() {
				return totalParticipants;
			}

			public void setTotalParticipants(int totalParticipants) {
				this.totalParticipants = totalParticipants;
			}

			public int getTotalAttendance() {
				return totalAttendance;
			}

			public void setTotalAttendance(int totalAttendance) {
				this.totalAttendance = totalAttendance;
			}

			public String getHostToken() {
				return hostToken;
			}

			public void setHostToken(String hostToken) {
				this.hostToken = hostToken;
			}

			public String getParticipantsToken() {
				return participantsToken;
			}

			public void setParticipantsToken(String participantsToken) {
				this.participantsToken = participantsToken;
			}

			public int getScreenshotFrequency() {
				return screenshotFrequency;
			}

			public void setScreenshotFrequency(int screenshotFrequency) {
				this.screenshotFrequency = screenshotFrequency;
			}

			public int getScreenshotCompressionRatio() {
				return screenshotCompressionRatio;
			}

			public void setScreenshotCompressionRatio(int screenshotCompressionRatio) {
				this.screenshotCompressionRatio = screenshotCompressionRatio;
			}

			public int getScreenshotScrollFrequency() {
				return screenshotScrollFrequency;
			}

			public void setScreenshotScrollFrequency(int screenshotScrollFrequency) {
				this.screenshotScrollFrequency = screenshotScrollFrequency;
			}

			public int getMeetingProcess() {
				return meetingProcess;
			}

			public void setMeetingProcess(int meetingProcess) {
				this.meetingProcess = meetingProcess;
			}

			public int getApproved() {
				return approved;
			}

			public void setApproved(int approved) {
				this.approved = approved;
			}

			public String getCreateById() {
				return createById;
			}

			public void setCreateById(String createById) {
				this.createById = createById;
			}

			public int getDuration() {
				return duration;
			}

			public void setDuration(int duration) {
				this.duration = duration;
			}

			public int getType() {
				return type;
			}

			public void setType(int type) {
				this.type = type;
			}

			public int getMaterialsCnt() {
				return materialsCnt;
			}

			public void setMaterialsCnt(int materialsCnt) {
				this.materialsCnt = materialsCnt;
			}

			public String getUserId() {
				return userId;
			}

			public void setUserId(String userId) {
				this.userId = userId;
			}

			public String getAudienceToken() {
				return audienceToken;
			}

			public void setAudienceToken(String audienceToken) {
				this.audienceToken = audienceToken;
			}

			public int getResolvingPower() {
				return resolvingPower;
			}

			public void setResolvingPower(int resolvingPower) {
				this.resolvingPower = resolvingPower;
			}

			public int getIsRecord() {
				return isRecord;
			}

			public void setIsRecord(int isRecord) {
				this.isRecord = isRecord;
			}

			@Override
			public int describeContents() {
				return 0;
			}

			@Override
			public void writeToParcel(Parcel dest, int flags) {
				dest.writeString(this.id);
				dest.writeString(this.createDate);
				dest.writeString(this.updateDate);
				dest.writeString(this.delFlag);
				dest.writeString(this.title);
				dest.writeString(this.startTime);
				dest.writeString(this.actualStartTime);
				dest.writeInt(this.totalParticipants);
				dest.writeInt(this.totalAttendance);
				dest.writeString(this.hostToken);
				dest.writeString(this.participantsToken);
				dest.writeInt(this.screenshotFrequency);
				dest.writeInt(this.screenshotCompressionRatio);
				dest.writeInt(this.screenshotScrollFrequency);
				dest.writeInt(this.meetingProcess);
				dest.writeInt(this.approved);
				dest.writeString(this.createById);
				dest.writeInt(this.duration);
				dest.writeInt(this.type);
				dest.writeInt(this.materialsCnt);
				dest.writeString(this.userId);
				dest.writeString(this.audienceToken);
				dest.writeInt(this.resolvingPower);
				dest.writeInt(this.isRecord);
			}

			public MeetingBean() {
			}

			protected MeetingBean(Parcel in) {
				this.id = in.readString();
				this.createDate = in.readString();
				this.updateDate = in.readString();
				this.delFlag = in.readString();
				this.title = in.readString();
				this.startTime = in.readString();
				this.actualStartTime = in.readString();
				this.totalParticipants = in.readInt();
				this.totalAttendance = in.readInt();
				this.hostToken = in.readString();
				this.participantsToken = in.readString();
				this.screenshotFrequency = in.readInt();
				this.screenshotCompressionRatio = in.readInt();
				this.screenshotScrollFrequency = in.readInt();
				this.meetingProcess = in.readInt();
				this.approved = in.readInt();
				this.createById = in.readString();
				this.duration = in.readInt();
				this.type = in.readInt();
				this.materialsCnt = in.readInt();
				this.userId = in.readString();
				this.audienceToken = in.readString();
				this.resolvingPower = in.readInt();
				this.isRecord = in.readInt();
			}

			public static final Creator<MeetingBean> CREATOR = new Creator<MeetingBean>() {
				@Override
				public MeetingBean createFromParcel(Parcel source) {
					return new MeetingBean(source);
				}

				@Override
				public MeetingBean[] newArray(int size) {
					return new MeetingBean[size];
				}
			};
		}

		public static class HostUserBean implements Parcelable {
			/**
			 * clientUid : 260706877
			 * hostUserName : lozzow
			 * hostUserId : 607d687a9ec9411dab26940df0795161
			 * status : 1
			 */

			private String clientUid;
			private String hostUserName;
			private String hostUserId;
			private int status;

			public String getClientUid() {
				return clientUid;
			}

			public void setClientUid(String clientUid) {
				this.clientUid = clientUid;
			}

			public String getHostUserName() {
				return hostUserName;
			}

			public void setHostUserName(String hostUserName) {
				this.hostUserName = hostUserName;
			}

			public String getHostUserId() {
				return hostUserId;
			}

			public void setHostUserId(String hostUserId) {
				this.hostUserId = hostUserId;
			}

			public int getStatus() {
				return status;
			}

			public void setStatus(int status) {
				this.status = status;
			}

			@Override
			public int describeContents() {
				return 0;
			}

			@Override
			public void writeToParcel(Parcel dest, int flags) {
				dest.writeString(this.clientUid);
				dest.writeString(this.hostUserName);
				dest.writeString(this.hostUserId);
				dest.writeInt(this.status);
			}

			public HostUserBean() {
			}

			protected HostUserBean(Parcel in) {
				this.clientUid = in.readString();
				this.hostUserName = in.readString();
				this.hostUserId = in.readString();
				this.status = in.readInt();
			}

			public static final Creator<HostUserBean> CREATOR = new Creator<HostUserBean>() {
				@Override
				public HostUserBean createFromParcel(Parcel source) {
					return new HostUserBean(source);
				}

				@Override
				public HostUserBean[] newArray(int size) {
					return new HostUserBean[size];
				}
			};
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(this.role);
			dest.writeParcelable(this.meeting, flags);
			dest.writeParcelable(this.hostUser, flags);
		}

		public DataBean() {
		}

		protected DataBean(Parcel in) {
			this.role = in.readInt();
			this.meeting = in.readParcelable(MeetingBean.class.getClassLoader());
			this.hostUser = in.readParcelable(HostUserBean.class.getClassLoader());
		}

		public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
			@Override
			public DataBean createFromParcel(Parcel source) {
				return new DataBean(source);
			}

			@Override
			public DataBean[] newArray(int size) {
				return new DataBean[size];
			}
		};
	}

	public NewMeetingJoin() {
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.errcode);
		dest.writeParcelable(this.data, flags);
		dest.writeString(this.errmsg);
	}

	protected NewMeetingJoin(Parcel in) {
		this.errcode = in.readInt();
		this.data = in.readParcelable(DataBean.class.getClassLoader());
		this.errmsg = in.readString();
	}

	public static final Creator<NewMeetingJoin> CREATOR = new Creator<NewMeetingJoin>() {
		@Override
		public NewMeetingJoin createFromParcel(Parcel source) {
			return new NewMeetingJoin(source);
		}

		@Override
		public NewMeetingJoin[] newArray(int size) {
			return new NewMeetingJoin[size];
		}
	};
}
