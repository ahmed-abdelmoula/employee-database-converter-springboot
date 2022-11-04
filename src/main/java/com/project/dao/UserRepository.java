package com.project.dao;


import java.util.List;

import javax.xml.bind.JAXBException;
public interface UserRepository  {
	
	public void process(List<String> filesPath)throws JAXBException;
}
