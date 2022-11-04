 package com.project.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "user")
@XmlRootElement(name="user")
@XmlAccessorType(XmlAccessType.FIELD)

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private int id;
	private String nom;
	private String prenom;
	private String cin;

	public User() {
		super();
	}

	public User(String nom, String prenom, String cin) {
		super();
		this.nom = nom;
		this.prenom = prenom;
		this.cin = cin;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getCin() {
		return cin;
	}

	public void setCin(String cin) {
		this.cin = cin;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		User other = (User) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

}
