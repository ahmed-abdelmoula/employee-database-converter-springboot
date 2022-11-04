package com.project.service;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.springframework.stereotype.Service;

public interface UserService {
	 void process(List<String> filePath) throws JAXBException;

}
