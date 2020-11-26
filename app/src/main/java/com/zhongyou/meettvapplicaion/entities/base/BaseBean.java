package com.zhongyou.meettvapplicaion.entities.base;

/**
 * @author wufan
 * @desc  data为对象的bean基类
 */
public class BaseBean<T> extends BaseErrorBean{
	private T data;


	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}