package com.mes.errorlog.server.serviceimpl.dao.elg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mes.errorlog.server.service.po.elg.ELGCatalog;
import com.mes.errorlog.server.service.utils.Configuration;
import com.mes.errorlog.server.service.utils.StringUtils;
import com.mes.errorlog.server.serviceimpl.dao.BaseDAO;

import org.apache.commons.io.FileUtils;

public class ELGCatalogDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(ELGCatalogDAO.class);

	private static ELGCatalogDAO Instance = null;

	/**
	 * 获取服务所在的本机的错误日志目录
	 * 
	 * @return
	 */
	public List<ELGCatalog> SelectList() {
		List<ELGCatalog> wResult = new ArrayList<ELGCatalog>();
		try {
			mFlag = 1;
			String wCatalogs = Configuration.readConfigString("errorlog.catalog", "config/config");
			if (wCatalogs == null || StringUtils.isEmpty(wCatalogs))
				return wResult;
			String[] wCatalogList = wCatalogs.split(";");
			if (wCatalogList == null || wCatalogList.length <= 0)
				return wResult;
			// 批量遍历删除
			for (String wItem : wCatalogList) {
				List<ELGCatalog> wCoreLogList = GetELGCatalogList(wItem);
				if (wCoreLogList != null && wCoreLogList.size() > 0)
					wResult.addAll(wCoreLogList);
			}
			// 将树形的目录展开
			List<ELGCatalog> wCollapseList = CollapseTreeList(wResult);
			mList = wCollapseList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 展开树形的目录
	 * 
	 * @param wResult
	 */
	private List<ELGCatalog> CollapseTreeList(List<ELGCatalog> wELGCatalogList) {
		List<ELGCatalog> wResult = new ArrayList<ELGCatalog>();
		try {
			if (wELGCatalogList == null || wELGCatalogList.size() <= 0)
				return wResult;

			for (ELGCatalog wItem : wELGCatalogList) {
				if (!wItem.IsCatalog)
					wResult.add(wItem);
				else {
					ELGCatalog wELGCatalog = new ELGCatalog();
					wELGCatalog.CatalogID = wItem.CatalogID;
					wELGCatalog.CatalogName = wItem.CatalogName;
					wELGCatalog.FilePath = wItem.FilePath;
					wELGCatalog.IsCatalog = wItem.IsCatalog;
					wResult.add(wELGCatalog);
					wResult.addAll(CollapseTreeList(wItem.SubCatalogList));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 全局的ID
	 */
	private int mFlag = 0;

	/**
	 * 全局的目录集合(展开结构)
	 */
	private List<ELGCatalog> mList = new ArrayList<ELGCatalog>();

	/**
	 * 根据文件目录获取所有的文件以及文件夹
	 * 
	 * @param wCatalog_MESCore
	 * @return
	 */
	private List<ELGCatalog> GetELGCatalogList(String wCatalog) {
		List<ELGCatalog> wResult = new ArrayList<ELGCatalog>();
		try {
			File wFile = new File(wCatalog);
			if (!wFile.isDirectory())
				return wResult;

			ELGCatalog wFather = new ELGCatalog();
			wFather.CatalogID = mFlag++;
			wFather.CatalogName = wFile.getName();
			wFather.IsCatalog = true;
			wFather.FilePath = wFile.getAbsolutePath();

			File[] wfss = wFile.listFiles();
			for (File wItem : wfss) {
				// 目录
				if (wItem.isDirectory()) {
					// 添加下级子目录
					wFather.SubCatalogList.addAll(GetELGCatalogList(wItem.getAbsolutePath()));
				} else {// 文件
					ELGCatalog wSubCatalog = new ELGCatalog();
					wSubCatalog.IsCatalog = false;
					wSubCatalog.CatalogID = mFlag++;
					wSubCatalog.CatalogName = wItem.getName();
					wSubCatalog.FilePath = wItem.getAbsolutePath();
					wFather.SubCatalogList.add(wSubCatalog);
				}
			}
			wResult.add(wFather);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 根据ID查询某个日志文件的文本内容
	 * 
	 * @param wID
	 * @return
	 */
	public List<String> SelectByID(int wID) {
		List<String> wResult = new ArrayList<String>();
		try {
			if (mList == null || mList.size() <= 0)
				return wResult;

			ELGCatalog wELGCatalog = null;
			Optional<ELGCatalog> wOption = mList.stream().filter(p -> p.CatalogID == wID).findFirst();
			if (wOption.isPresent())
				wELGCatalog = wOption.get();
			if (wELGCatalog == null || wELGCatalog.CatalogID <= 0)
				return wResult;
			File wFile = new File(wELGCatalog.FilePath);
			if (!wFile.exists())
				return wResult;
			FileReader wReader = new FileReader(wFile);
			BufferedReader wBR = new BufferedReader(wReader);
			String wTemp = "";
			while ((wTemp = wBR.readLine()) != null) {
				wResult.add(wTemp);
			}
			wBR.close();
			wReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 根据文件的ID下载文件
	 * 
	 * @param wID
	 * @throws IOException
	 */
	public void DownloadLogByID(int wID, HttpServletResponse wResponse) throws IOException {
		// 获取输出流
		OutputStream wOutputStream = wResponse.getOutputStream();
		try {
			if (mList == null || mList.size() <= 0)
				return;

			ELGCatalog wELGCatalog = null;
			Optional<ELGCatalog> wOption = mList.stream().filter(p -> p.CatalogID == wID).findFirst();
			if (wOption.isPresent())
				wELGCatalog = wOption.get();
			if (wELGCatalog == null || wELGCatalog.CatalogID <= 0)
				return;
			if (!new File(wELGCatalog.FilePath).exists())
				return;

			// 清空下载文件的空白行（空白行是因为有的前端代码编译后产生的）
			wResponse.reset();
			String wFileName = wELGCatalog.CatalogName;
			// 设置响应头，把文件名字设置好
			wResponse.setHeader("Content-Disposition", "attachment; filename=" + wFileName);
			// 解决编码问题
			wResponse.setContentType("application/octet-stream; charset=utf-8");
			// 创建存储的文件对象（文件存储的路径和文件名字）
			File wTargetFile = new File(wELGCatalog.FilePath);
			// 输出流开始写出文件（FileUtils是Apache下的工具类可以直接调用）
			wOutputStream.write(FileUtils.readFileToByteArray(wTargetFile));
			// 刷新流
			wOutputStream.flush();
		} catch (Exception ex) {
			logger.error(ex.toString());
		} finally {
			// 关闭流
			wOutputStream.close();
		}
	}

	/**
	 * 根据ID删除日志文件
	 * 
	 * @param wID
	 */
	public boolean DeleteByID(int wID) {
		boolean wResult = true;
		try {
			if (mList == null || mList.size() <= 0)
				return false;

			ELGCatalog wELGCatalog = null;
			Optional<ELGCatalog> wOption = mList.stream().filter(p -> p.CatalogID == wID).findFirst();
			if (wOption.isPresent())
				wELGCatalog = wOption.get();
			if (wELGCatalog == null || wELGCatalog.CatalogID <= 0 || wELGCatalog.IsCatalog)
				return false;

			File wFile = new File(wELGCatalog.FilePath);
			if (!wFile.exists())
				return false;
			wFile.delete();
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 删除文件集合
	 * 
	 * @param wIDList
	 * @return
	 */
	public String DeleteList(List<Integer> wIDList) {
		String wResult = "";
		try {
			if (wIDList == null || wIDList.size() <= 0)
				wResult = "参数错误!";
			List<ELGCatalog> wList = new ArrayList<ELGCatalog>();
			for (Integer wID : wIDList) {
				ELGCatalog wELGCatalog = null;
				Optional<ELGCatalog> wOption = mList.stream().filter(p -> p.CatalogID == wID).findFirst();
				if (wOption.isPresent())
					wELGCatalog = wOption.get();
				if (wELGCatalog != null && wELGCatalog.IsCatalog == false)
					wList.add(wELGCatalog);
				else if (wELGCatalog != null && wELGCatalog.IsCatalog)
					return "不能删除文件夹!";
			}
			for (ELGCatalog wItem : wList) {
				File wFile = new File(wItem.FilePath);
				if (wFile.exists())
					wFile.delete();
			}
		} catch (Exception e) {
			wResult = e.toString();
			e.printStackTrace();
			logger.error(e.toString());
		}
		return wResult;
	}

	private ELGCatalogDAO() {
		super();
	}

	public static ELGCatalogDAO getInstance() {
		if (Instance == null)
			Instance = new ELGCatalogDAO();
		return Instance;
	}
}
