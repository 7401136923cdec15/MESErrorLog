package com.mes.errorlog.server.serviceimpl.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.mes.errorlog.server.controller.APIResult;

@Service
public class CoreServiceUtils {

	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(CoreServiceUtils.class);

	public CoreServiceUtils() {
	}

	private static CoreServiceUtils Instance;

	public static CoreServiceUtils getInstance() {
		if (Instance == null)
			Instance = new CoreServiceUtils();
		return Instance;
	}

	public APIResult BMS_LoginEmployee(String wLoginName, String wPassword, String wToken, long wMac, int wnetJS) {
		APIResult wResult = new APIResult();

		Map<String, Object> wParms = new HashMap<String, Object>();
		wParms.put("user_id", wLoginName);
		wParms.put("passWord", wPassword);
		wParms.put("token", wToken);
		wParms.put("PhoneMac", wMac);
		wParms.put("netJS", wnetJS);

		wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI("", "MESCore/api/User/Login", wParms, HttpMethod.POST);
		return wResult;
	}
}
