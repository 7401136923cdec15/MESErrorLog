package com.mes.errorlog.server.utils_db;
 

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate; 
import com.mchange.v2.c3p0.ComboPooledDataSource; 

public class DBHelper {
	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(DBHelper.class);
  
	public static DataSource dataSource=new ComboPooledDataSource("Mysql_dataSource");
	
	public static NamedParameterJdbcTemplate getTemplate() {
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource); 
        return jdbcTemplate;
    } 
	
	
}
