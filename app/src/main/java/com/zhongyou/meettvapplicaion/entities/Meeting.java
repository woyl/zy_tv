package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Meeting implements Parcelable, Entity {

	/**
	 * 抓拍频次，单位秒，0是不需要抓拍
	 */
	public static final int SCREENSHOTFREQUENCY_INVALID = 0;

	private String id;
	private int isRecord;//是否录制

	public int getIsRecord() {
		return isRecord;
	}

	public void setIsRecord(int isRecord) {
		this.isRecord = isRecord;
	}

	private int isToken;
	private int isMeeting;

    public int getIsToken() {
        return isToken;
    }

    public void setIsToken(int isToken) {
        this.isToken = isToken;
    }

    public int getIsMeeting() {
        return isMeeting;
    }

    public void setIsMeeting(int isMeeting) {
        this.isMeeting = isMeeting;
    }

    private String title;

	private String description;

	private String startTime;

	private int totalParticipants;

	private int totalAttendance;

	private int resolvingPower;

	public int getResolvingPower() {
		return resolvingPower;
	}

	public void setResolvingPower(int resolvingPower) {
		this.resolvingPower = resolvingPower;
	}

	/**
	 * 抓拍频次，单位秒，0是不需要抓拍
	 */
	private int screenshotFrequency;

	/**
	 * 会议实况图片展示频率，单位毫秒
	 */
	private int screenshotScrollFrequency;

	/**
	 * 会议实况图片压缩率，设置为2的指数。tv端默认压缩率是8
	 */
	private int screenshotCompressionRatio;

	private int meetingProcess;

	private int approved;

	private int type;

	private int materialsCnt;

	private String roomUUID;

	private String roomToken;

	private String createDate;

	private String updateDate;

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

	public String getRoomUUID() {
		return roomUUID;
	}

	public void setRoomUUID(String roomUUID) {
		this.roomUUID = roomUUID;
	}

	public String getRoomToken() {
		return roomToken;
	}

	public void setRoomToken(String roomToken) {
		this.roomToken = roomToken;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
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

	public int getScreenshotFrequency() {
		return screenshotFrequency;
	}

	public void setScreenshotFrequency(int screenshotFrequency) {
		this.screenshotFrequency = screenshotFrequency;
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

	public int getScreenshotScrollFrequency() {
		return screenshotScrollFrequency;
	}

	public void setScreenshotScrollFrequency(int screenshotScrollFrequency) {
		this.screenshotScrollFrequency = screenshotScrollFrequency;
	}

	public int getScreenshotCompressionRatio() {
		return (screenshotCompressionRatio == 0 || screenshotCompressionRatio == 1) ? 8 : screenshotCompressionRatio;
	}

	public void setScreenshotCompressionRatio(int screenshotCompressionRatio) {
		this.screenshotCompressionRatio = screenshotCompressionRatio;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Meeting meeting = (Meeting) o;
		if (totalParticipants != meeting.totalParticipants) return false;
		if (totalAttendance != meeting.totalAttendance) return false;
		if (screenshotFrequency != meeting.screenshotFrequency) return false;
		if (screenshotScrollFrequency != meeting.screenshotScrollFrequency) return false;
		if (screenshotCompressionRatio != meeting.screenshotCompressionRatio) return false;
		if (meetingProcess != meeting.meetingProcess) return false;
		if (approved != meeting.approved) return false;
		if (id != null ? !id.equals(meeting.id) : meeting.id != null) return false;
		if (title != null ? !title.equals(meeting.title) : meeting.title != null) return false;
		if (description != null ? !description.equals(meeting.description) : meeting.description != null)
			return false;
		return startTime != null ? startTime.equals(meeting.startTime) : meeting.startTime == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (title != null ? title.hashCode() : 0);
		result = 31 * result + (description != null ? description.hashCode() : 0);
		result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
		result = 31 * result + totalParticipants;
		result = 31 * result + totalAttendance;
		result = 31 * result + screenshotFrequency;
		result = 31 * result + screenshotScrollFrequency;
		result = 31 * result + screenshotCompressionRatio;
		result = 31 * result + meetingProcess;
		result = 31 * result + approved;
		return result;
	}

	@Override
	public String toString() {
		return "Meeting{" +
				"id='" + id + '\'' +
				", title='" + title + '\'' +
				", description='" + description + '\'' +
				", startTime='" + startTime + '\'' +
				", totalParticipants=" + totalParticipants +
				", totalAttendance=" + totalAttendance +
				", screenshotFrequency=" + screenshotFrequency +
				", screenshotScrollFrequency=" + screenshotScrollFrequency +
				", screenshotCompressionRatio=" + screenshotCompressionRatio +
				", meetingProcess=" + meetingProcess +
				", approved=" + approved +
				", type=" + type +
				", materialsCnt=" + materialsCnt +
				", createDate=" + createDate +
				", updateDate=" + updateDate +
				'}';
	}

	public Meeting() {
	}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.isRecord);
        dest.writeInt(this.isToken);
        dest.writeInt(this.isMeeting);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.startTime);
        dest.writeInt(this.totalParticipants);
        dest.writeInt(this.totalAttendance);
        dest.writeInt(this.resolvingPower);
        dest.writeInt(this.screenshotFrequency);
        dest.writeInt(this.screenshotScrollFrequency);
        dest.writeInt(this.screenshotCompressionRatio);
        dest.writeInt(this.meetingProcess);
        dest.writeInt(this.approved);
        dest.writeInt(this.type);
        dest.writeInt(this.materialsCnt);
        dest.writeString(this.roomUUID);
        dest.writeString(this.roomToken);
        dest.writeString(this.createDate);
        dest.writeString(this.updateDate);
    }

    protected Meeting(Parcel in) {
        this.id = in.readString();
        this.isRecord = in.readInt();
        this.isToken = in.readInt();
        this.isMeeting = in.readInt();
        this.title = in.readString();
        this.description = in.readString();
        this.startTime = in.readString();
        this.totalParticipants = in.readInt();
        this.totalAttendance = in.readInt();
        this.resolvingPower = in.readInt();
        this.screenshotFrequency = in.readInt();
        this.screenshotScrollFrequency = in.readInt();
        this.screenshotCompressionRatio = in.readInt();
        this.meetingProcess = in.readInt();
        this.approved = in.readInt();
        this.type = in.readInt();
        this.materialsCnt = in.readInt();
        this.roomUUID = in.readString();
        this.roomToken = in.readString();
        this.createDate = in.readString();
        this.updateDate = in.readString();
    }

    public static final Creator<Meeting> CREATOR = new Creator<Meeting>() {
        @Override
        public Meeting createFromParcel(Parcel source) {
            return new Meeting(source);
        }

        @Override
        public Meeting[] newArray(int size) {
            return new Meeting[size];
        }
    };
}
