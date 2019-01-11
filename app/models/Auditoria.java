package models;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;
@Entity
public class Auditoria extends Model{

	 public String titulo; 
	 public int numSecao;
	 public int numZona;
	 public Date inicioVotacao;
	 public Date fimVotacao;
     public double tempoDevotacao;
     public String confirmacao;
}
