package com.project.service;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.dao.UserRepository;
@Service

public class UserserviceImpl implements UserService{
	@Autowired
	UserRepository userep;
	
	public void process(List<String> filePath)throws JAXBException {
		userep.process(filePath);	
	}
	

}
