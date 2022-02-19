package com.mes.errorlog.server.filter;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;

import com.mes.errorlog.server.controller.BaseController;
import com.mes.errorlog.server.service.po.bms.BMSEmployee;
import com.mes.errorlog.server.service.utils.DesUtil;
import com.mes.errorlog.server.service.utils.StringUtils;
import com.mes.errorlog.server.serviceimpl.utils.CoreServiceUtils;
import com.mes.errorlog.server.shristool.LoggerTool;
import com.mes.errorlog.server.utils.SessionContants;

@Component
@ServletComponentScan
@WebFilter(urlPatterns = { "/api/*" }, filterName = "securityRequestFilter")
public class IsUserFilter implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(IsUserFilter.class);
//	private static IsUserFilter Instance;

	@PostConstruct
	public void init() {
//		Instance = this;
//		Instance.wCoreService = this.wCoreService;
		// 初使化时将已静态化的testService实例化
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse res = (HttpServletResponse) response;
		HttpServletRequest req = (HttpServletRequest) request;

		req.setCharacterEncoding("UTF-8");

		try {
			// 没有判断参数中是否有user字段
			HttpSession session = req.getSession();
			BMSEmployee wBMSEmployee = new BMSEmployee();
			if (session.getAttribute(SessionContants.SessionUser) != null) {
				wBMSEmployee = (BMSEmployee) session.getAttribute(SessionContants.SessionUser);
				if (wBMSEmployee == null)
					wBMSEmployee = new BMSEmployee();
			}

			if (wBMSEmployee.getID() <= 0 && wBMSEmployee.getID() != -100) {

				String user_info = req.getParameter(SessionContants.USER_INFO);
				String user_password = req.getParameter(SessionContants.USER_PASSWORD);

				if (StringUtils.isNotEmpty(user_info)) {
					user_info = user_info.replaceAll(" ", "+");
					user_password = user_password.replaceAll(" ", "+");

					user_info = DesUtil.decrypt(user_info, SessionContants.appSecret);
					user_password = DesUtil.decrypt(user_password, SessionContants.appSecret);

					wBMSEmployee = CoreServiceUtils.getInstance().BMS_LoginEmployee(user_info, user_password, "", 0, 0)
							.Info(BMSEmployee.class);

					if (wBMSEmployee != null && wBMSEmployee.getID() > 0) {
						BaseController.SetSession(req, wBMSEmployee);
					}
				}
			}
			
			
			String user_info = BaseController.getCookieValue(SessionContants.CookieUser, req);
			if (StringUtils.isNotEmpty(user_info) && !user_info.equalsIgnoreCase(wBMSEmployee.getLoginName())) {
				String wToken = BaseController.CreateToke(user_info);
				wBMSEmployee = CoreServiceUtils.getInstance().BMS_LoginEmployee(user_info, "", wToken, 0, 0)
						.Info(BMSEmployee.class);

				if (wBMSEmployee != null && wBMSEmployee.getID() > 0) {
					BaseController.SetSession(req, wBMSEmployee);
				}
			}
			// 生成Token

		} catch (Exception e) {
			logger.error("Error Session: " + e.toString());
		}

		// iframe引起的内部cookie丢失
		res.setHeader("P3P", "CP=CAO PSA OUR");
		res.setHeader("Access-Control-Allow-Origin", "*");

		res.setHeader("Access-Control-Allow-Credentials", "true");
		res.setHeader("Access-Control-Allow-Methods", "*");
		res.setHeader("Access-Control-Allow-Headers", "Content-Type,Access-Token");
		res.setHeader("Access-Control-Expose-Headers", "*");

		if (chain != null)

		{
			String wURI = req.getRequestURI();

			long wStartMillis = System.currentTimeMillis();
			try {
				chain.doFilter(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// String wURL = req.getRequestURL().toString();
			if (wURI.startsWith("/api/")) {
				if (wURI.startsWith("/"))
					wURI = wURI.substring(1);

				int wKeyIndex = wURI.lastIndexOf("/");

				String wModuleName = "";
				String wFuncName = "";
				if (wKeyIndex >= 0) {
					wModuleName = wURI.substring(0, wKeyIndex);
					wFuncName = wURI.substring(wKeyIndex);
					long wEndMillis = System.currentTimeMillis();
					int wCallMS = (int) (wEndMillis - wStartMillis);
					System.out.println(StringUtils.Format("ModuleName：{0},FuncName:{1}", wModuleName, wFuncName));

					LoggerTool.MonitorFunction(wModuleName, wFuncName, wCallMS);
				} else {
					logger.error("Error URI: " + wURI);
				}
			}
		}

	}

	protected void SetSession(HttpServletRequest wRequest, BMSEmployee wBMSEmployee) {
		if (wBMSEmployee == null)
			return;
		if (wRequest.getSession().getAttribute(SessionContants.SessionUser) != null) {
			wRequest.getSession().removeAttribute(SessionContants.SessionUser);
		}
		wRequest.getSession().setAttribute(SessionContants.SessionUser, wBMSEmployee);
		wRequest.getSession().setMaxInactiveInterval(-1);
	}

	@Override
	public void destroy() {

	}
}
