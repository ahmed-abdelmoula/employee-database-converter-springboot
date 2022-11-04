package com.project.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.model.User;
@Repository
public interface UserRep extends JpaRepository<User,Integer>{

}
