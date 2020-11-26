package com.zhongyou.meettvapplicaion.im;

import java.util.List;

/**
 * @author luopan@centerm.com
 * @date 2020-03-05 14:56.
 */
public class ImUserInfo {


	/**
	 * errcode : 0
	 * data : {"msg":"查询群组成员信息成功","groupName":"安卓测试","code":"200","groupId":"00f13f7088c74ecb8a9c56592c2ab28b","users":[{"id":"44757511b9ed4b11a9b45549d82b0c29"},{"id":"42d07174b4b24fa1bf7cc83b11824de4"},{"id":"607d687a9ec9411dab26940df0795161"}]}
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

	public static class DataBean {
		/**
		 * msg : 查询群组成员信息成功
		 * groupName : 安卓测试
		 * code : 200
		 * groupId : 00f13f7088c74ecb8a9c56592c2ab28b
		 * users : [{"id":"44757511b9ed4b11a9b45549d82b0c29"},{"id":"42d07174b4b24fa1bf7cc83b11824de4"},{"id":"607d687a9ec9411dab26940df0795161"}]
		 */

		private String msg;
		private String groupName;
		private String code;
		private String groupId;
		private List<UsersBean> users;

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		public String getGroupName() {
			return groupName;
		}

		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getGroupId() {
			return groupId;
		}

		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}

		public List<UsersBean> getUsers() {
			return users;
		}

		public void setUsers(List<UsersBean> users) {
			this.users = users;
		}

		public static class UsersBean {
			/**
			 * id : 44757511b9ed4b11a9b45549d82b0c29
			 */

			private String id;

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}
		}
	}
}
