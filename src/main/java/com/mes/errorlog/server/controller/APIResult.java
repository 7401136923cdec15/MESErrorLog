package com.mes.errorlog.server.controller;

import java.io.Serializable;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
  
import com.mes.errorlog.server.service.utils.CloneTool;

public class APIResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public APIResult() {
	}

	private int resultCode = 1000;
	private Map<String, Object> returnObject = new HashMap<>();

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public Map<String, Object> getReturnObject() {
		return returnObject;
	}

	public void setReturnObject(Map<String, Object> returnObject) {
		this.returnObject = returnObject;
	}

	public <T> T Info(Class<T> clazz) {
		APIReturnObject wAPIReturnObject = new APIReturnObject(returnObject);
		//return TypeUtils.castToJavaBean(wAPIReturnObject.info, clazz);
		 return CloneTool.Clone(wAPIReturnObject.info, clazz);

	}

	public <T> List<T> List(Class<T> clazz) {
		APIReturnObject wAPIReturnObject = new APIReturnObject(returnObject); 
		
		return CloneTool.CloneArray(wAPIReturnObject.list, clazz); 
	}

	public <T> T Custom(String wKey, Class<T> clazz) {
		APIReturnObject wAPIReturnObject = new APIReturnObject(returnObject); 
		
		return CloneTool.Clone(wAPIReturnObject.list, clazz);
	}

	public APIReturnObject ReturnObejct() {
		return new APIReturnObject(returnObject);
	}
}
