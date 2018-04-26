package com.weiresearch.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

// 接口响应体 POJO 类
public class ApplicationResponse {

	// 接口响应码
	public static final Integer CODE_SUCCESS = 0;
	public static final Integer CODE_FAIL = 1;
	public static final Integer CODE_LOGIN_EXPIRED = 102;

	private Integer code;

	@JsonInclude(Include.NON_NULL)
	private Object data;

	public ApplicationResponse() {
	};

	public ApplicationResponse(Integer code, Object data) {
		this.code = code;
		this.data = data;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
