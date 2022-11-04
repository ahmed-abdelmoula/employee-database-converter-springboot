package com.project.config;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.dao.support.DaoSupport;

import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class BeanWrapperRowMapper<T> extends BeanWrapperFieldSetMapper<T> implements RowMapper<T> {
    @Autowired
	private JdbcTemplate jdbcTemplate;
	@Override
	public T mapRow(ResultSet rs, final int rowNum) throws SQLException {
		final FieldSet fs = getFieldSet(rs);
		try {
			return super.mapFieldSet(fs);
		} catch (final BindException | org.springframework.validation.BindException e) {
			throw new IllegalArgumentException("Could not bind bean to FieldSet", e);
		}
	}

	private FieldSet getFieldSet(final ResultSet rs) throws SQLException {
		final ResultSetMetaData metaData = rs.getMetaData();
		final int columnCount = metaData.getColumnCount();

		final List<String> tokens = new ArrayList<>();
		final List<String> names = new ArrayList<>();

		for (int i = 1; i <= columnCount; i++) {
			tokens.add(rs.getString(i));
			names.add(metaData.getColumnName(i));
		}

		return new DefaultFieldSet(tokens.toArray(new String[0]), names.toArray(new String[0]));
	}
	public List<Map<String, Object>> getUserData(int userID)
	{

	  String sql = "select firstname, lastname, dept from users where userID = ? ";

	  ColumnMapRowMapper rowMapper = new ColumnMapRowMapper();
	  List<Map<String, Object>> userDataList = jdbcTemplate.query(sql, rowMapper, userID);
	  
	  for(Map<String, Object> map: userDataList){

	      System.out.println("FirstName = " + map.get("firstname"));
	      System.out.println("LastName = " + map.get("lastname"));
	      System.out.println("Department = " + map.get("dept"));

	  }

	  return userDataList;

	}
	

}
