package com.project.utils;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.project.model.User;
import com.project.model.Users;

public class CommonUtils {
	public static List<User> readXml(String filePath)throws JAXBException{
		JAXBContext jaxbContext=JAXBContext.newInstance(Users.class);
	Unmarshaller unmarshaller=jaxbContext.createUnmarshaller();
	Users user=(Users)unmarshaller.unmarshal(new File(filePath));
	return user.getUsers();
	}

}
