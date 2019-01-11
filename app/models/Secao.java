package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import play.db.jpa.GenericModel;
import play.db.jpa.Model;

@Entity
public class Secao extends GenericModel {
	
	@Id
    public Long id;	
	
    public String nome;
    public int numSecao;
    public boolean bloqueio;
    
    @ManyToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name="idZona")
    public Zona zona;
}
