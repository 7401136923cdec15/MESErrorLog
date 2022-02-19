package com.mes.errorlog.server.serviceimpl;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import com.mes.errorlog.server.service.ELGService;
import com.mes.errorlog.server.service.po.ServiceResult;
import com.mes.errorlog.server.service.po.elg.ELGCatalog;
import com.mes.errorlog.server.serviceimpl.dao.elg.ELGCatalogDAO;

@Service
public class ELGServiceImpl implements ELGService {

	private static Logger logger = LoggerFactory.getLogger(ELGServiceImpl.class);

	public ELGServiceImpl() {
	}

	@Override
	public ServiceResult<List<ELGCatalog>> ELG_QueryCataLogList() {
		ServiceResult<List<ELGCatalog>> wResult = new ServiceResult<List<ELGCatalog>>();
		try {
			wResult.Result = ELGCatalogDAO.getInstance().SelectList();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<String>> ELG_ShowLogFileByID(int wID) {
		ServiceResult<List<String>> wResult = new ServiceResult<List<String>>();
		try {
			wResult.Result = ELGCatalogDAO.getInstance().SelectByID(wID);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public void ELG_DownloadLogFileByID(int wID, HttpServletResponse wResponse) {
		try {
			ELGCatalogDAO.getInstance().DownloadLogByID(wID, wResponse);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<Boolean> ELG_DeleteLogFileByID(int wID) {
		ServiceResult<Boolean> wResult = new ServiceResult<Boolean>();
		try {
			wResult.Result = ELGCatalogDAO.getInstance().DeleteByID(wID);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> ELG_DeleteLogFileList(List<Integer> wIDList) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		try {
			wResult.Result = ELGCatalogDAO.getInstance().DeleteList(wIDList);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return wResult;
	}
}
