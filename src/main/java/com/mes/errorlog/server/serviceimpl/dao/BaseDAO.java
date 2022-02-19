package com.mes.errorlog.server.serviceimpl.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.mes.errorlog.server.utils.Constants;
import com.mes.errorlog.server.utils_db.DBHelper;
import com.mes.errorlog.server.utils_db.DataBaseTypes;
import com.mes.errorlog.server.utils_db.SQLTypes;

public class BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(BaseDAO.class);

	protected NamedParameterJdbcTemplate nameJdbcTemplate;

	public BaseDAO() {
		nameJdbcTemplate = DBHelper.getTemplate();
		this.SQLType = Constants.SQL_TYPE;
	}

	protected SQLTypes SQLType = Constants.SQL_TYPE;

	public BaseDAO(NamedParameterJdbcTemplate nameJdbcTemplate) {
		this.nameJdbcTemplate = nameJdbcTemplate;
		this.SQLType = Constants.SQL_TYPE;
	}

	public BaseDAO(NamedParameterJdbcTemplate nameJdbcTemplate, SQLTypes wSQLTypes) {
		this.nameJdbcTemplate = nameJdbcTemplate;
		this.SQLType = wSQLTypes;
	}

	protected Object GetMapObject(Map<String, Object> wMap, String wKey) {
		Object wResult = null;
		try {
			if (wMap == null || wMap.size() < 1 || wKey == null || wKey.isEmpty())
				return wResult;

			if (wMap.containsKey(wKey))
				wResult = wMap.get(wKey);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return wResult;
	}

	protected String GetDateBaseName(int wDataBaseFiled, SQLTypes wSQLTypeFiled) {
		String wResult = DataBaseTypes.GetDateBaseName(wDataBaseFiled);

		switch (wSQLTypeFiled) {
		case Mysql:

			break;
		case SqlServer:
			wResult = wResult + ".dbo";
			break;
		case Orecle:

			break;
		case Access:

			break;
		default:
			break;
		}

		return wResult;
	}

	protected String GetDateBaseName(int wDataBaseFiled) {
		String wResult = DataBaseTypes.GetDateBaseName(wDataBaseFiled);

		switch (Constants.SQL_TYPE) {
		case Mysql:
			break;
		case SqlServer:
			wResult = wResult + ".dbo";
			break;
		case Orecle:
			break;
		case Access:
			break;
		default:
			break;
		}
		return wResult;
	}

	protected String MysqlChangeToSqlServer(String wMySqlString) {
		String wResult = "";
		try {
			StringBuffer wStringBuffer = new StringBuffer("");

			Matcher wMatcher = Pattern
					.compile("SELECT\\s+LAST_INSERT_ID\\(\\)(\\s+as\\s+ID)?\\s*\\;", Pattern.CASE_INSENSITIVE)
					.matcher(wMySqlString);
			if (wMatcher.matches()) {
				wMySqlString = wMatcher.replaceAll("");

				wMatcher = Pattern.compile("\\)\\s*VALUES\\s*\\(", Pattern.CASE_INSENSITIVE).matcher(wMySqlString);
				if (wMatcher.matches()) {
					wMySqlString = wMatcher.replaceAll(") output inserted.* \\n VALUES (");
				}
			}
			wMySqlString = wMySqlString.replaceAll("now()", "GETDATE()");

			wMatcher = Pattern.compile("\\s*SELECT\\s+", Pattern.CASE_INSENSITIVE).matcher(wMySqlString);

			if (wMatcher.matches()) {
				wMatcher = Pattern.compile("\\s+limit\\s+(?<Num>\\d+)\\s*\\,?(?<Num2>\\d*)", Pattern.CASE_INSENSITIVE)
						.matcher(wMySqlString);
				if (wMatcher.matches()) {
					wMySqlString = wMatcher.replaceAll("");

					wMatcher = Pattern.compile("\\s*SELECT\\s+", Pattern.CASE_INSENSITIVE).matcher(wMySqlString);

					wStringBuffer.setLength(0);

					while (wMatcher.find()) {
						wMatcher.appendReplacement(wStringBuffer,
								String.format(" SELECT Top ($1) ", wMatcher.group("Num")));
					}

					wMatcher.appendTail(wStringBuffer);
					wMySqlString = wStringBuffer.toString();
				}
			}

			wMatcher = Pattern.compile("\\`(?<Column>[a-zA-Z]+[a-zA-Z0-9_]+)\\`", Pattern.CASE_INSENSITIVE)
					.matcher(wMySqlString);
			wStringBuffer.setLength(0);

			while (wMatcher.find()) {
				wMatcher.appendReplacement(wStringBuffer, wMatcher.group("Column"));
			}

			wMatcher.appendTail(wStringBuffer);
			wMySqlString = wStringBuffer.toString();

			wMatcher = Pattern.compile(
					"str_to_date\\(\\s*(?<STR>[\\']{1,2}2010\\-01\\-01[\\']{1,2})\\s*\\,\\s*[\\']{1,2}\\%Y\\-\\%m\\-\\%d\\s*\\%H[\\']{1,2}\\)",
					Pattern.CASE_INSENSITIVE).matcher(wMySqlString);
			wStringBuffer.setLength(0);

			while (wMatcher.find()) {
				wMatcher.appendReplacement(wStringBuffer,
						String.format("cast( %s  as Calendar)", wMatcher.group("STR")));
			}

			wMatcher.appendTail(wStringBuffer);
			wMySqlString = wStringBuffer.toString();

			wResult = wMySqlString;

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}

		return wResult;
	}

	protected String DMLChange(String wMySqlString, SQLTypes wSQLTypeFiled) {
		switch (wSQLTypeFiled) {
		case Mysql:

			break;
		case SqlServer:
			wMySqlString = this.MysqlChangeToSqlServer(wMySqlString);
			break;
		case Orecle:

			break;
		case Access:

			break;
		default:
			break;
		}
		return wMySqlString;
	}

	protected String DMLChange(String wMySqlString) {
		switch (SQLType) {
		case Mysql:

			break;
		case SqlServer:
			wMySqlString = this.MysqlChangeToSqlServer(wMySqlString);
			break;
		case Orecle:

			break;
		case Access:

			break;
		default:
			break;
		}
		return wMySqlString;
	}

	/**
	 * SelectAll数据量查询数据
	 * 
	 * @param wSQL      查询sql语句 用:冒号定义参数
	 * @param wParamMap sql参数集
	 * @param clazz     返回数据类型 注意sql返回的数据需与实体类型相匹配
	 * @return
	 */
	protected <T> List<T> QueryForList(String wSQL, Map<String, Object> wParamMap, Class<T> clazz) {
		List<T> wResult = new ArrayList<T>();
		try {

			List<Map<String, Object>> wQueryResultList = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			wResult = JSON.parseArray(JSON.toJSONString(wQueryResultList), clazz);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * Select数据量查询数据
	 * 
	 * @param wSQL      查询sql语句 用:冒号定义参数
	 * @param wParamMap sql参数集
	 * @param clazz     返回数据类型 注意sql返回的数据需与实体类型相匹配 不准用简单类型
	 * @return
	 */
	protected <T> T QueryForObject(String wSQL, Map<String, Object> wParamMap, Class<T> clazz) {
		T wResult = null;
		try {

			Map<String, Object> wQueryResult = nameJdbcTemplate.queryForMap(wSQL, wParamMap);

			wResult = JSON.parseObject(JSON.toJSONString(wQueryResult), clazz);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Transactional
	protected void ExecuteSqlTransaction(List<String> wSqlList) {
		try {
			String wSQL = "";
			for (String String : wSqlList) {

				wSQL = this.DMLChange(String, Constants.SQL_TYPE);
				nameJdbcTemplate.update(wSQL, new HashMap<String, Object>());
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	@Transactional
	protected void ExecuteSqlTransaction(String wSqlString) {
		try {
			String wSQL = this.DMLChange(wSqlString, Constants.SQL_TYPE);
			nameJdbcTemplate.update(wSQL, new HashMap<String, Object>());

		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

}
