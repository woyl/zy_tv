package com.zhongyou.meettvapplicaion;

public class BaseException extends Exception {

	private static final long serialVersionUID = 1L;

	private int statusCode = -1;

	public BaseException(String msg) {
		super(msg);
	}

	public BaseException(Exception cause) {
		super(cause);
	}

	public BaseException(String msg, int statusCode) {
		super(msg);
		this.statusCode = statusCode;

	}

	public BaseException(String msg, Exception cause) {
		super(msg, cause);
	}

	public BaseException(String msg, Exception cause, int statusCode) {
		super(msg, cause);
		this.statusCode = statusCode;

	}

	public BaseException(int code, String msg, Throwable throwable) {
		super(msg, throwable);
		this.statusCode = code;
	}

	public BaseException(int code, Throwable throwable) {
		super(throwable);
		this.statusCode = code;
	}

	public int getStatusCode() {
		return this.statusCode == 0 ? 404 : this.statusCode;
	}

}