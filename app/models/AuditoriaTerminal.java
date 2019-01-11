package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import play.db.jpa.Model;
@Entity
public class AuditoriaTerminal extends Model {
  public Date inicioLogin;
  public Date fimLogin;
  
  @OneToMany
  public List<Auditoria> listAuditoria;
}
