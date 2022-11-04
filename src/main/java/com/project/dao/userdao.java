package com.project.dao;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Component;

import com.project.model.User;
import com.project.utils.CommonUtils;

@Component
public class userdao implements UserRepository {

	NamedParameterJdbcTemplate namedParamerterJdbcTemplate;

	@Autowired
	public void setNamedParamerterJdbcTemplate(NamedParameterJdbcTemplate hd) {
		this.namedParamerterJdbcTemplate = hd;
	}

	public void process(List<String> filesPath) throws JAXBException {
		List<User> list = new ArrayList<User>();
		// read data
		for (String filePath : filesPath) {
			list.addAll(CommonUtils.readXml(filePath));
		}
		importData(list);
	}

	public void importData(List<User> list) {
		String sql = "INSERT INTO user(id,nom,prenom,cin) VALUES (:id,:nom,:prenom,:cin)";
		SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(list.toArray());
		namedParamerterJdbcTemplate.batchUpdate(sql, batch);

	}

}