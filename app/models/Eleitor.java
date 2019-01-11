package models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import play.db.jpa.GenericModel;
import play.db.jpa.Blob;

@Entity
public class Eleitor extends GenericModel {

    @Id
    public Long id;

    public String nome;
    public String titulo; 
    
    @ManyToOne(cascade=CascadeType.PERSIST)
   	@JoinColumn(name = "idSecao")
	public Secao secao;
    
    public String statusEle;
    public String statusVot;
    public Blob foto;
}
