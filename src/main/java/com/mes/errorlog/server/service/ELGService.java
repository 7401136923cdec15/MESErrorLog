package com.mes.errorlog.server.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.mes.errorlog.server.service.po.ServiceResult;
import com.mes.errorlog.server.service.po.elg.ELGCatalog;

public interface ELGService {

	/**
	 * 获取日志目录集合
	 * 
	 * @return
	 */
	ServiceResult<List<ELGCatalog>> ELG_QueryCataLogList();

	/**
	 * 查看日志文件内容
	 * 
	 * @param wID
	 * @return
	 */
	ServiceResult<List<String>> ELG_ShowLogFileByID(int wID);

	/**
	 * 下载日志文件
	 * 
	 * @param wID
	 */
	void ELG_DownloadLogFileByID(int wID,HttpServletResponse wResponse);
	
	/**
	 * 删除日志文件
	 * @param wID
	 */
	ServiceResult<Boolean> ELG_DeleteLogFileByID(int wID);
	
	/**
	 * 删除文件集合
	 * @param wIDList
	 * @return
	 */
	ServiceResult<String> ELG_DeleteLogFileList(List<Integer> wIDList);
}
