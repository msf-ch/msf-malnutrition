package org.msf.android.rest;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class RestError {
	public String message;
	public String code;
	public String detail;
	
	@JsonProperty(value = "error")
	@JsonDeserialize(as = HashMap.class)
	public void setError(HashMap error) {
		this.message = error.get("message").toString();
		this.code = error.get("code").toString();
		this.detail = error.get("detail").toString();
	}
}
