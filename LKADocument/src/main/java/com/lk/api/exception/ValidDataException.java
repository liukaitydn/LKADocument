package com.lk.api.exception;

import java.util.HashMap;
import java.util.Map;

public class ValidDataException extends RuntimeException{

	private static final long serialVersionUID = 3835326258670411310L;
	
	private Map<String,String> errors = new HashMap<String,String>();
	
	public ValidDataException() {
		super();
	}

	public ValidDataException(String message) {
		super(message);
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public void setErrors(Map<String, String> errors) {
		this.errors = errors;
	}
}
