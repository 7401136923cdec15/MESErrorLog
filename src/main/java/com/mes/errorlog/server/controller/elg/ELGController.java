package com.mes.errorlog.server.controller.elg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mes.errorlog.server.controller.BaseController;
import com.mes.errorlog.server.service.ELGService;
import com.mes.errorlog.server.service.po.ServiceResult;
import com.mes.errorlog.server.service.po.elg.ELGCatalog;
import com.mes.errorlog.server.service.utils.CloneTool;
import com.mes.errorlog.server.service.utils.StringUtils;
import com.mes.errorlog.server.utils.RetCode;

@RestController
@RequestMapping("/api/ELG")
public class ELGController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(ELGController.class);

	@Autowired
	ELGService wELGService;

	@GetMapping("/LogList")
	Object LogList(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			ServiceResult<List<ELGCatalog>> wServiceResult = wELGService.ELG_QueryCataLogList();
			wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
		} catch (Exception e) {
			wResult = GetResult(RetCode.SERVER_CODE_SUC, RetCode.SERVER_CODE_ERR_MSG);
			logger.error(e.toString());
		}
		return wResult;
	}

	@GetMapping("/LogInfo")
	Object LogInfo(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			int wID = StringUtils.parseInt(request.getParameter("ID"));
			ServiceResult<List<String>> wServiceResult = wELGService.ELG_ShowLogFileByID(wID);
			wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wServiceResult.Result);
		} catch (Exception e) {
			wResult = GetResult(RetCode.SERVER_CODE_SUC, RetCode.SERVER_CODE_ERR_MSG);
			logger.error(e.toString());
		}
		return wResult;
	}

	@GetMapping("/FileDownload")
	void FileDownload(HttpServletRequest request, HttpServletResponse response) {
		try {
			int wID = StringUtils.parseInt(request.getParameter("ID"));
			wELGService.ELG_DownloadLogFileByID(wID, response);
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	@GetMapping("/DeleteInfo")
	Object DeleteInfo(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			int wID = StringUtils.parseInt(request.getParameter("ID"));
			ServiceResult<Boolean> wServiceResult = wELGService.ELG_DeleteLogFileByID(wID);
			wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wServiceResult.Result);
		} catch (Exception e) {
			wResult = GetResult(RetCode.SERVER_CODE_SUC, RetCode.SERVER_CODE_ERR_MSG);
			logger.error(e.toString());
		}
		return wResult;
	}

	@PostMapping("/DeleteList")
	Object DeleteList(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			List<Integer> wIDList = CloneTool.CloneArray(wParam.get("data"), Integer.class);
			ServiceResult<String> wServiceResult = wELGService.ELG_DeleteLogFileList(wIDList);
			wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wServiceResult.Result);
		} catch (Exception e) {
			wResult = GetResult(RetCode.SERVER_CODE_SUC, RetCode.SERVER_CODE_ERR_MSG);
			logger.error(e.toString());
		}
		return wResult;
	}
}
