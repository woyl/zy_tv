package com.zhongyou.meettvapplicaion.entity;

import java.util.List;

/**
 * @author golangdorid@gmail.com
 * @date 2020/4/15 11:36 AM.
 * @
 */
public class RecomandData {


	/**
	 * chuangkeList : [{"isSignUp":0,"pictureURL":"http://syimage.zhongyouie.com//收藏.png","isRecord":"0","sort":4,"type":2,"urlId":"6db7218463744a6685eb3ab0f4684f6a","seriesId":"","isToken":"1","isDefaultImg":0,"urlType":1,"pageType":0,"name":"受邀直播会议预告测试","linkType":1,"isPunchCard":0,"id":"dc233e9aadaf4004a6712683aedb854e","isNeedRecord":0},{"isSignUp":0,"pictureURL":"http://syimage.zhongyouie.com//3211586920543_.pic_hd.jpg","isRecord":"","sort":1,"type":2,"urlId":"2c06e66bfa5a4406ac7a8b9559f386c9","seriesId":"de643c3d0a954c6fb09ac685b7b2deee","isToken":"","isDefaultImg":0,"urlType":1,"pageType":1,"name":"详情页连接-直播111","linkType":2,"isPunchCard":0,"id":"ca978eb1e33c46d1ae7f3ffa52e9550e","isNeedRecord":1},{"isSignUp":0,"pictureURL":"http://syimage.zhongyouie.com//3211586920543_.pic_hd.jpg","isRecord":"","sort":2,"type":2,"urlId":"0eaf6dc447504fbeb108e4f090d21f3f","seriesId":"","isToken":"","isDefaultImg":0,"urlType":1,"pageType":3,"name":"视频子页面连接","linkType":2,"isPunchCard":0,"id":"026573e761fa4727bab1ecfb3fc91047","isNeedRecord":1},{"isSignUp":0,"pictureURL":"http://syimage.zhongyouie.com//3211586920543_.pic_hd.jpg","isRecord":"","sort":3,"type":2,"urlId":"eb5e8d2511a842808afb8f685451a19e","seriesId":"","isToken":"","isDefaultImg":0,"urlType":1,"pageType":4,"name":"音频子页面连接","linkType":2,"isPunchCard":0,"id":"092c76cbd9664f3c983fa4dc497b13d3","isNeedRecord":1},{"isSignUp":0,"pictureURL":"http://syimage.zhongyouie.com//收藏.png","isRecord":"","sort":555555555,"type":2,"urlId":"09f3b48594d04dd5a6fc5f6b294f0639","seriesId":"","isToken":"","isDefaultImg":1,"urlType":1,"pageType":2,"name":"吴宇链接","linkType":2,"isPunchCard":0,"id":"5e2248c0262f4025940f45755cb9c8ff","isNeedRecord":0}]
	 * meetingAdvance : [{"id":"da564438891c4f55997623ecfef73252","createDate":"2020-05-06 09:13:06","delFlag":"0","comment":"受邀会议预告测试","meetingId":"6db7218463744a6685eb3ab0f4684f6a","sort":3,"closeTime":"2020-05-13 00:00:00","title":"受邀直播会议预告测试"},{"id":"3709843f10614ffe97b113f296c73dec","createDate":"2020-05-06 09:11:48","delFlag":"0","comment":"普通会议预告测试赛","meetingId":"6e66367613be4c98ba193c13342d4452","sort":2,"closeTime":"2020-05-13 00:00:00","title":"普通会议预告测试"},{"id":"c6b499b5063949d3b1c2675288fd47df","createDate":"2020-05-06 09:10:47","delFlag":"0","comment":"直播预告测试","meetingId":"f02b927e54764e99b4681748af67a3cf","sort":1,"closeTime":"2020-05-11 00:00:00","title":"直播会议预告连接"}]
	 * changeLessonByDay : {"delFlag":"0","name":"VR音频","anchorName":"李老师","belongTime":"2020-04-29","onLineTime":"2020-04-29 15:03:25.0","vioceURL":"http://syvideo.zhongyouie.com//李博士直播彩排第一期录音.mp3","comentURL":"http://syimage.zhongyouie.com//58fd993ecaf8d.jpg","totalDate":"01:07:19"}
	 * meetingList : [{"isSignUp":0,"pictureURL":"http://syimage.zhongyouie.com//收藏.png","isRecord":"0","sort":2311111,"type":1,"urlId":"960ff8651e7245ca8c0776e28d54c3fe","seriesId":"","isToken":"0","isDefaultImg":1,"urlType":1,"pageType":0,"name":"吴宇会议","linkType":1,"isPunchCard":0,"id":"cb01dd67d5994394857fa9d888088ce3","isNeedRecord":0},{"isSignUp":0,"pictureURL":"http://syimage.zhongyouie.com//收藏.png","isRecord":"0","sort":3434,"type":1,"urlId":"960ff8651e7245ca8c0776e28d54c3fe","seriesId":"","isToken":"0","isDefaultImg":1,"urlType":1,"pageType":0,"name":"吴宇会议","linkType":1,"isPunchCard":0,"id":"dc92945260664ece81cae828b83df0ff","isNeedRecord":0},{"isSignUp":0,"pictureURL":"http://syimage.zhongyouie.com//3211586920543_.pic_hd.jpg","isRecord":"","sort":11,"type":1,"urlId":"d5d2e2e8f6f34230b8f60b6b36327d7c","seriesId":"","isToken":"","isDefaultImg":0,"urlType":1,"pageType":0,"name":"会议测试","linkType":1,"isPunchCard":0,"id":"5143aaacda954388ad4b9d08de75d6eb","isNeedRecord":0},{"isSignUp":0,"pictureURL":"http://syimage.zhongyouie.com//微信图片_20200408103535.jpg","isRecord":"","sort":32112,"type":1,"urlId":"73410a6bb4a049e6a7734f889a99cf30","seriesId":"","isToken":"","isDefaultImg":1,"urlType":1,"pageType":0,"name":"2233422","linkType":1,"isPunchCard":0,"id":"7cca9f97e6334807a8f25ae6b14c4d89","isNeedRecord":0},{"isSignUp":0,"pictureURL":"http://syimage.zhongyouie.com//3211586920543_.pic_hd.jpg","isRecord":"","sort":22,"type":1,"urlId":"","seriesId":"","isToken":"","isDefaultImg":1,"urlType":0,"pageType":0,"name":"李博士的分享会","linkType":1,"isPunchCard":0,"id":"143615fc58f345d48c1fe1371988aab8","isNeedRecord":0}]
	 */

	private ChangeLessonByDayBean changeLessonByDay;
	private List<ChuangkeListBean> chuangkeList;
	private List<MeetingAdvanceBean> meetingAdvance;
	private List<MeetingListBean> meetingList;

	public ChangeLessonByDayBean getChangeLessonByDay() {
		return changeLessonByDay;
	}

	public void setChangeLessonByDay(ChangeLessonByDayBean changeLessonByDay) {
		this.changeLessonByDay = changeLessonByDay;
	}

	public List<ChuangkeListBean> getChuangkeList() {
		return chuangkeList;
	}

	public void setChuangkeList(List<ChuangkeListBean> chuangkeList) {
		this.chuangkeList = chuangkeList;
	}

	public List<MeetingAdvanceBean> getMeetingAdvance() {
		return meetingAdvance;
	}

	public void setMeetingAdvance(List<MeetingAdvanceBean> meetingAdvance) {
		this.meetingAdvance = meetingAdvance;
	}

	public List<MeetingListBean> getMeetingList() {
		return meetingList;
	}

	public void setMeetingList(List<MeetingListBean> meetingList) {
		this.meetingList = meetingList;
	}

	public static class ChangeLessonByDayBean {
		/**
		 * delFlag : 0
		 * name : VR音频
		 * anchorName : 李老师
		 * belongTime : 2020-04-29
		 * onLineTime : 2020-04-29 15:03:25.0
		 * vioceURL : http://syvideo.zhongyouie.com//李博士直播彩排第一期录音.mp3
		 * comentURL : http://syimage.zhongyouie.com//58fd993ecaf8d.jpg
		 * totalDate : 01:07:19
		 */

		private String delFlag;
		private String name;
		private String anchorName;
		private String belongTime;
		private String onLineTime;
		private String vioceURL;
		private String comentURL;
		private String totalDate;

		private int type;//0 闲置状态 1 播放状态 2 暂停状态 3 加载中
		private long currentDuration;
		private boolean isExpanded;

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public long getCurrentDuration() {
			return currentDuration;
		}

		public void setCurrentDuration(long currentDuration) {
			this.currentDuration = currentDuration;
		}

		public boolean isExpanded() {
			return isExpanded;
		}

		public void setExpanded(boolean expanded) {
			isExpanded = expanded;
		}

		public String getDelFlag() {
			return delFlag;
		}

		public void setDelFlag(String delFlag) {
			this.delFlag = delFlag;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAnchorName() {
			return anchorName;
		}

		public void setAnchorName(String anchorName) {
			this.anchorName = anchorName;
		}

		public String getBelongTime() {
			return belongTime;
		}

		public void setBelongTime(String belongTime) {
			this.belongTime = belongTime;
		}

		public String getOnLineTime() {
			return onLineTime;
		}

		public void setOnLineTime(String onLineTime) {
			this.onLineTime = onLineTime;
		}

		public String getVioceURL() {
			return vioceURL;
		}

		public void setVioceURL(String vioceURL) {
			this.vioceURL = vioceURL;
		}

		public String getComentURL() {
			return comentURL;
		}

		public void setComentURL(String comentURL) {
			this.comentURL = comentURL;
		}

		public String getTotalDate() {
			return totalDate;
		}

		public void setTotalDate(String totalDate) {
			this.totalDate = totalDate;
		}
	}

	public static class ChuangkeListBean {
		/**
		 * isSignUp : 0
		 * pictureURL : http://syimage.zhongyouie.com//收藏.png
		 * isRecord : 0
		 * sort : 4
		 * type : 2
		 * urlId : 6db7218463744a6685eb3ab0f4684f6a
		 * seriesId :
		 * isToken : 1
		 * isDefaultImg : 0
		 * urlType : 1
		 * pageType : 0
		 * name : 受邀直播会议预告测试
		 * linkType : 1
		 * isPunchCard : 0
		 * id : dc233e9aadaf4004a6712683aedb854e
		 * isNeedRecord : 0
		 */

		private int isSignUp;
		private String pictureURL;
		private String isRecord;
		private int sort;
		private int type;
		private String urlId;
		private String seriesId;
		private String isToken;
		private int isDefaultImg;
		private int urlType;
		private int pageType;
		private String name;
		private int linkType;
		private int isPunchCard;
		private String id;
		private int isNeedRecord;

		public int getIsSignUp() {
			return isSignUp;
		}

		public void setIsSignUp(int isSignUp) {
			this.isSignUp = isSignUp;
		}

		public String getPictureURL() {
			return pictureURL;
		}

		public void setPictureURL(String pictureURL) {
			this.pictureURL = pictureURL;
		}

		public String getIsRecord() {
			return isRecord;
		}

		public void setIsRecord(String isRecord) {
			this.isRecord = isRecord;
		}

		public int getSort() {
			return sort;
		}

		public void setSort(int sort) {
			this.sort = sort;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getUrlId() {
			return urlId;
		}

		public void setUrlId(String urlId) {
			this.urlId = urlId;
		}

		public String getSeriesId() {
			return seriesId;
		}

		public void setSeriesId(String seriesId) {
			this.seriesId = seriesId;
		}

		public String getIsToken() {
			return isToken;
		}

		public void setIsToken(String isToken) {
			this.isToken = isToken;
		}

		public int getIsDefaultImg() {
			return isDefaultImg;
		}

		public void setIsDefaultImg(int isDefaultImg) {
			this.isDefaultImg = isDefaultImg;
		}

		public int getUrlType() {
			return urlType;
		}

		public void setUrlType(int urlType) {
			this.urlType = urlType;
		}

		public int getPageType() {
			return pageType;
		}

		public void setPageType(int pageType) {
			this.pageType = pageType;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getLinkType() {
			return linkType;
		}

		public void setLinkType(int linkType) {
			this.linkType = linkType;
		}

		public int getIsPunchCard() {
			return isPunchCard;
		}

		public void setIsPunchCard(int isPunchCard) {
			this.isPunchCard = isPunchCard;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public int getIsNeedRecord() {
			return isNeedRecord;
		}

		public void setIsNeedRecord(int isNeedRecord) {
			this.isNeedRecord = isNeedRecord;
		}
	}

	public static class MeetingAdvanceBean {
		/**
		 * id : da564438891c4f55997623ecfef73252
		 * createDate : 2020-05-06 09:13:06
		 * delFlag : 0
		 * comment : 受邀会议预告测试
		 * meetingId : 6db7218463744a6685eb3ab0f4684f6a
		 * sort : 3
		 * closeTime : 2020-05-13 00:00:00
		 * title : 受邀直播会议预告测试
		 */

		private String id;
		private String createDate;
		private String delFlag;
		private String comment;
		private String meetingId;
		private int sort;
		private String closeTime;
		private String title;

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

		public String getDelFlag() {
			return delFlag;
		}

		public void setDelFlag(String delFlag) {
			this.delFlag = delFlag;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public String getMeetingId() {
			return meetingId;
		}

		public void setMeetingId(String meetingId) {
			this.meetingId = meetingId;
		}

		public int getSort() {
			return sort;
		}

		public void setSort(int sort) {
			this.sort = sort;
		}

		public String getCloseTime() {
			return closeTime;
		}

		public void setCloseTime(String closeTime) {
			this.closeTime = closeTime;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}

	public static class MeetingListBean {
		/**
		 * isSignUp : 0
		 * pictureURL : http://syimage.zhongyouie.com//收藏.png
		 * isRecord : 0
		 * sort : 2311111
		 * type : 1
		 * urlId : 960ff8651e7245ca8c0776e28d54c3fe
		 * seriesId :
		 * isToken : 0
		 * isDefaultImg : 1
		 * urlType : 1
		 * pageType : 0
		 * name : 吴宇会议
		 * linkType : 1
		 * isPunchCard : 0
		 * id : cb01dd67d5994394857fa9d888088ce3
		 * isNeedRecord : 0
		 */

		private int isSignUp;
		private String pictureURL;
		private String isRecord;
		private int sort;
		private int type;
		private String urlId;
		private String seriesId;
		private String isToken;
		private int isDefaultImg;
		private int urlType;
		private int pageType;
		private String name;
		private int linkType;
		private int isPunchCard;
		private String id;
		private int isNeedRecord;

		public int getIsSignUp() {
			return isSignUp;
		}

		public void setIsSignUp(int isSignUp) {
			this.isSignUp = isSignUp;
		}

		public String getPictureURL() {
			return pictureURL;
		}

		public void setPictureURL(String pictureURL) {
			this.pictureURL = pictureURL;
		}

		public String getIsRecord() {
			return isRecord;
		}

		public void setIsRecord(String isRecord) {
			this.isRecord = isRecord;
		}

		public int getSort() {
			return sort;
		}

		public void setSort(int sort) {
			this.sort = sort;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getUrlId() {
			return urlId;
		}

		public void setUrlId(String urlId) {
			this.urlId = urlId;
		}

		public String getSeriesId() {
			return seriesId;
		}

		public void setSeriesId(String seriesId) {
			this.seriesId = seriesId;
		}

		public String getIsToken() {
			return isToken;
		}

		public void setIsToken(String isToken) {
			this.isToken = isToken;
		}

		public int getIsDefaultImg() {
			return isDefaultImg;
		}

		public void setIsDefaultImg(int isDefaultImg) {
			this.isDefaultImg = isDefaultImg;
		}

		public int getUrlType() {
			return urlType;
		}

		public void setUrlType(int urlType) {
			this.urlType = urlType;
		}

		public int getPageType() {
			return pageType;
		}

		public void setPageType(int pageType) {
			this.pageType = pageType;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getLinkType() {
			return linkType;
		}

		public void setLinkType(int linkType) {
			this.linkType = linkType;
		}

		public int getIsPunchCard() {
			return isPunchCard;
		}

		public void setIsPunchCard(int isPunchCard) {
			this.isPunchCard = isPunchCard;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public int getIsNeedRecord() {
			return isNeedRecord;
		}

		public void setIsNeedRecord(int isNeedRecord) {
			this.isNeedRecord = isNeedRecord;
		}
	}
}
