package com.example.TestApi.dao;

import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserStructureDAO {
	
	
	@Autowired
	protected JdbcTemplate jdbc;

	private static Log logger = LogFactory.getLog(UserStructureDAO.class);

	public Object getRegion(String regionID, String prefix, int records, int pindex, boolean countFlag) {

		String condition = "";
		if (!(regionID == null || regionID.equalsIgnoreCase(""))) {
			condition += "`region_id` = " + regionID;
		}
		if (!(prefix == null || prefix.equalsIgnoreCase(""))) {
			condition = condition != "" ? " and " : "";
			condition += " region_name like '%" + prefix + "%'";
		}
		String whereClause = "";
		if (!"".equalsIgnoreCase(condition))
			whereClause = " where " + condition;
		String sql = "";
		if (countFlag) {
			sql = "select count(distinct region_id) from region_m " + whereClause;
			return jdbc.queryForObject(sql, Integer.class);
		} else {
			if (pindex != 0) {
				sql = sql + " limit " + pindex * records + "," + records;
			}
			try {
				sql = "Select distinct `region_id` regionID, `region_name` regionName from region_m " + whereClause;
				return jdbc.queryForList(sql);
			} catch (Exception e) {
				logger.debug("Inside get region exception");
				logger.error("error", e);
				return Collections.emptyList();
			}
		}
	}
}