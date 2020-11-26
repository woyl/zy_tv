package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by whatisjava on 16-11-1.
 */


public class Version implements Parcelable, Entity {

	private String id;

	private int major;

	private int minor;

	private int patch;

	private int minVersionCode;

	private int importance; // 1:最新版，不用更新 2：小改动，可以不更新 3：建议更新

	private String pkgname;

	private String changelog;

	private String delFlag;

	private String versionCode;

	private String url;
	private String versionDesc;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getMajor() {
		return major;
	}

	public void setMajor(int major) {
		this.major = major;
	}

	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}

	public int getPatch() {
		return patch;
	}

	public void setPatch(int patch) {
		this.patch = patch;
	}

	public int getMinVersionCode() {
		return minVersionCode;
	}

	public void setMinVersionCode(int minVersionCode) {
		this.minVersionCode = minVersionCode;
	}

	public int getImportance() {
		return importance;
	}

	public void setImportance(int importance) {
		this.importance = importance;
	}

	public String getPkgname() {
		return pkgname;
	}

	public void setPkgname(String pkgname) {
		this.pkgname = pkgname;
	}

	public String getChangelog() {
		return changelog;
	}

	public void setChangelog(String changelog) {
		this.changelog = changelog;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Version version = (Version) o;

		if (major != version.major) return false;
		if (minor != version.minor) return false;
		if (patch != version.patch) return false;
		if (minVersionCode != version.minVersionCode) return false;
		if (importance != version.importance) return false;
		if (id != null ? !id.equals(version.id) : version.id != null) return false;
		if (pkgname != null ? !pkgname.equals(version.pkgname) : version.pkgname != null)
			return false;
		if (changelog != null ? !changelog.equals(version.changelog) : version.changelog != null)
			return false;
		if (delFlag != null ? !delFlag.equals(version.delFlag) : version.delFlag != null)
			return false;
		if (versionCode != null ? !versionCode.equals(version.versionCode) : version.versionCode != null)
			return false;
		return url != null ? url.equals(version.url) : version.url == null;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + major;
		result = 31 * result + minor;
		result = 31 * result + patch;
		result = 31 * result + minVersionCode;
		result = 31 * result + importance;
		result = 31 * result + (pkgname != null ? pkgname.hashCode() : 0);
		result = 31 * result + (changelog != null ? changelog.hashCode() : 0);
		result = 31 * result + (delFlag != null ? delFlag.hashCode() : 0);
		result = 31 * result + (versionCode != null ? versionCode.hashCode() : 0);
		result = 31 * result + (url != null ? url.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Version{" +
				"id='" + id + '\'' +
				", major=" + major +
				", minor=" + minor +
				", patch=" + patch +
				", minVersionCode=" + minVersionCode +
				", importance=" + importance +
				", pkgname='" + pkgname + '\'' +
				", changelog='" + changelog + '\'' +
				", delFlag='" + delFlag + '\'' +
				", versionCode='" + versionCode + '\'' +
				", url='" + url + '\'' +
				'}';
	}

	public Version() {
	}

	public String getVersionDesc() {
		return versionDesc;
	}

	public void setVersionDesc(String versionDesc) {
		this.versionDesc = versionDesc;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.id);
		dest.writeInt(this.major);
		dest.writeInt(this.minor);
		dest.writeInt(this.patch);
		dest.writeInt(this.minVersionCode);
		dest.writeInt(this.importance);
		dest.writeString(this.pkgname);
		dest.writeString(this.changelog);
		dest.writeString(this.delFlag);
		dest.writeString(this.versionCode);
		dest.writeString(this.url);
		dest.writeString(this.versionDesc);
		dest.writeString(this.name);
	}

	protected Version(Parcel in) {
		this.id = in.readString();
		this.major = in.readInt();
		this.minor = in.readInt();
		this.patch = in.readInt();
		this.minVersionCode = in.readInt();
		this.importance = in.readInt();
		this.pkgname = in.readString();
		this.changelog = in.readString();
		this.delFlag = in.readString();
		this.versionCode = in.readString();
		this.url = in.readString();
		this.versionDesc = in.readString();
		this.name = in.readString();
	}

	public static final Creator<Version> CREATOR = new Creator<Version>() {
		@Override
		public Version createFromParcel(Parcel source) {
			return new Version(source);
		}

		@Override
		public Version[] newArray(int size) {
			return new Version[size];
		}
	};
}

