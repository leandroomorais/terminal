package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import play.db.jpa.GenericModel;
import play.db.jpa.Model;

@Entity
public class Zona extends GenericModel {
	
	@Id
    public Long id;	
	
	public String nome;
	public int numZona;
}
