package models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ErroVoto {
	
	@Id
	public Long id;	
	
	public String key;
	public String value;

}
