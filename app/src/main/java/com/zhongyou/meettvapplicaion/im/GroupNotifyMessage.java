package com.zhongyou.meettvapplicaion.im;

/**
 * @author luopan@centerm.com
 * @date 2020-03-12 15:48.
 */
public class GroupNotifyMessage extends IMChatMessage {


	/**
	 * content : {"data":"","destruct":false,"destructTime":0,"extra":"","jSONDestructInfo":{},"jSONUserInfo":{},"message":"加入群组","operation":"Add","operatorUserId":"44757511b9ed4b11a9b45549d82b0c29","userInfo":{"extra":"","name":"罗攀","portraitUri":"http://syimage.zhongyouie.com/osg/user/expostor/photo/255c9489ec274e4ba9add051aa424a23.jpg","userId":"44757511b9ed4b11a9b45549d82b0c29"}}
	 * conversationType : GROUP
	 * extra :
	 * messageDirection : RECEIVE
	 * messageId : 9
	 * objectName : RC:GrpNtf
	 * readReceiptInfo : {"readReceiptMessage":false,"respondUserIdList":{}}
	 * readTime : 0
	 * receivedStatus : {"download":false,"flag":1,"listened":false,"multipleReceive":false,"read":true,"retrieved":false}
	 * receivedTime : 1583975600447
	 * senderUserId : 44757511b9ed4b11a9b45549d82b0c29
	 * sentStatus : SENT
	 * sentTime : 1583975600388
	 * targetId : 8169d7bc01384c768342bed321efd1e5
	 * uId : BGPH-6P21-7MCD-GRVC
	 */

	private ContentBean content;
	private String conversationType;
	private String extra;
	private String messageDirection;
	private int messageId;
	private String objectName;
	private ReadReceiptInfoBean readReceiptInfo;
	private int readTime;
	private ReceivedStatusBean receivedStatus;
	private long receivedTime;
	private String senderUserId;
	private String sentStatus;
	private long sentTime;
	private String targetId;
	private String uId;

	public ContentBean getContent() {
		return content;
	}

	public void setContent(ContentBean content) {
		this.content = content;
	}

	public String getConversationType() {
		return conversationType;
	}

	public void setConversationType(String conversationType) {
		this.conversationType = conversationType;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getMessageDirection() {
		return messageDirection;
	}

	public void setMessageDirection(String messageDirection) {
		this.messageDirection = messageDirection;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public ReadReceiptInfoBean getReadReceiptInfo() {
		return readReceiptInfo;
	}

	public void setReadReceiptInfo(ReadReceiptInfoBean readReceiptInfo) {
		this.readReceiptInfo = readReceiptInfo;
	}

	public int getReadTime() {
		return readTime;
	}

	public void setReadTime(int readTime) {
		this.readTime = readTime;
	}

	public ReceivedStatusBean getReceivedStatus() {
		return receivedStatus;
	}

	public void setReceivedStatus(ReceivedStatusBean receivedStatus) {
		this.receivedStatus = receivedStatus;
	}

	public long getReceivedTime() {
		return receivedTime;
	}

	public void setReceivedTime(long receivedTime) {
		this.receivedTime = receivedTime;
	}

	public String getSenderUserId() {
		return senderUserId;
	}

	public void setSenderUserId(String senderUserId) {
		this.senderUserId = senderUserId;
	}

	public String getSentStatus() {
		return sentStatus;
	}

	public void setSentStatus(String sentStatus) {
		this.sentStatus = sentStatus;
	}

	public long getSentTime() {
		return sentTime;
	}

	public void setSentTime(long sentTime) {
		this.sentTime = sentTime;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getUId() {
		return uId;
	}

	public void setUId(String uId) {
		this.uId = uId;
	}

	public static class ContentBean {
		/**
		 * data :
		 * destruct : false
		 * destructTime : 0
		 * extra :
		 * jSONDestructInfo : {}
		 * jSONUserInfo : {}
		 * message : 加入群组
		 * operation : Add
		 * operatorUserId : 44757511b9ed4b11a9b45549d82b0c29
		 * userInfo : {"extra":"","name":"罗攀","portraitUri":"http://syimage.zhongyouie.com/osg/user/expostor/photo/255c9489ec274e4ba9add051aa424a23.jpg","userId":"44757511b9ed4b11a9b45549d82b0c29"}
		 */

		private String data;
		private boolean destruct;
		private int destructTime;
		private String extra;
		private JSONDestructInfoBean jSONDestructInfo;
		private JSONUserInfoBean jSONUserInfo;
		private String message;
		private String operation;
		private String operatorUserId;
		private UserInfoBean userInfo;

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

		public boolean isDestruct() {
			return destruct;
		}

		public void setDestruct(boolean destruct) {
			this.destruct = destruct;
		}

		public int getDestructTime() {
			return destructTime;
		}

		public void setDestructTime(int destructTime) {
			this.destructTime = destructTime;
		}

		public String getExtra() {
			return extra;
		}

		public void setExtra(String extra) {
			this.extra = extra;
		}

		public JSONDestructInfoBean getJSONDestructInfo() {
			return jSONDestructInfo;
		}

		public void setJSONDestructInfo(JSONDestructInfoBean jSONDestructInfo) {
			this.jSONDestructInfo = jSONDestructInfo;
		}

		public JSONUserInfoBean getJSONUserInfo() {
			return jSONUserInfo;
		}

		public void setJSONUserInfo(JSONUserInfoBean jSONUserInfo) {
			this.jSONUserInfo = jSONUserInfo;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getOperation() {
			return operation;
		}

		public void setOperation(String operation) {
			this.operation = operation;
		}

		public String getOperatorUserId() {
			return operatorUserId;
		}

		public void setOperatorUserId(String operatorUserId) {
			this.operatorUserId = operatorUserId;
		}

		public UserInfoBean getUserInfo() {
			return userInfo;
		}

		public void setUserInfo(UserInfoBean userInfo) {
			this.userInfo = userInfo;
		}

		public static class JSONDestructInfoBean {
		}

		public static class JSONUserInfoBean {
		}

		public static class UserInfoBean {
			/**
			 * extra :
			 * name : 罗攀
			 * portraitUri : http://syimage.zhongyouie.com/osg/user/expostor/photo/255c9489ec274e4ba9add051aa424a23.jpg
			 * userId : 44757511b9ed4b11a9b45549d82b0c29
			 */

			private String extra;
			private String name;
			private String portraitUri;
			private String userId;

			public String getExtra() {
				return extra;
			}

			public void setExtra(String extra) {
				this.extra = extra;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getPortraitUri() {
				return portraitUri;
			}

			public void setPortraitUri(String portraitUri) {
				this.portraitUri = portraitUri;
			}

			public String getUserId() {
				return userId;
			}

			public void setUserId(String userId) {
				this.userId = userId;
			}
		}
	}

	public static class ReadReceiptInfoBean {
		/**
		 * readReceiptMessage : false
		 * respondUserIdList : {}
		 */

		private boolean readReceiptMessage;
		private RespondUserIdListBean respondUserIdList;

		public boolean isReadReceiptMessage() {
			return readReceiptMessage;
		}

		public void setReadReceiptMessage(boolean readReceiptMessage) {
			this.readReceiptMessage = readReceiptMessage;
		}

		public RespondUserIdListBean getRespondUserIdList() {
			return respondUserIdList;
		}

		public void setRespondUserIdList(RespondUserIdListBean respondUserIdList) {
			this.respondUserIdList = respondUserIdList;
		}

		public static class RespondUserIdListBean {
		}
	}

	public static class ReceivedStatusBean {
		/**
		 * download : false
		 * flag : 1
		 * listened : false
		 * multipleReceive : false
		 * read : true
		 * retrieved : false
		 */

		private boolean download;
		private int flag;
		private boolean listened;
		private boolean multipleReceive;
		private boolean read;
		private boolean retrieved;

		public boolean isDownload() {
			return download;
		}

		public void setDownload(boolean download) {
			this.download = download;
		}

		public int getFlag() {
			return flag;
		}

		public void setFlag(int flag) {
			this.flag = flag;
		}

		public boolean isListened() {
			return listened;
		}

		public void setListened(boolean listened) {
			this.listened = listened;
		}

		public boolean isMultipleReceive() {
			return multipleReceive;
		}

		public void setMultipleReceive(boolean multipleReceive) {
			this.multipleReceive = multipleReceive;
		}

		public boolean isRead() {
			return read;
		}

		public void setRead(boolean read) {
			this.read = read;
		}

		public boolean isRetrieved() {
			return retrieved;
		}

		public void setRetrieved(boolean retrieved) {
			this.retrieved = retrieved;
		}
	}
}
