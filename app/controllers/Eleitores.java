package controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.File;
import java.text.SimpleDateFormat; 

import models.Auditoria;
import models.Eleitor;
import models.ErroVoto;
import play.Play;
import play.libs.WS;
import play.mvc.Controller;

public class Eleitores extends Controller {
	
    public static void eleitor(Eleitor eleitor, boolean liberou){	
    	
    	boolean tempo = false;
    	
    	if (liberou == false){
            if (confirmarVoto()){ // verifica no WebService Terminal se o cliente Urna terminou a votação atual
            	String idSecao =  session.get("secao");
            	String titulo = eleitor.titulo;
            	setStatusVoto(idSecao, eleitor); // Seta o Status do voto do eleitor no TSE  
            }
            if(tempoUrna()){ // verifica no WebService Terminal se o cliente Urna pedio tempo 
            	render(eleitor, true);
            }else  
            	render(eleitor, false); 
    	}
    	render(eleitor,false);
    }  
  

    public static void getEleitor(String titulo) {
        Gson gson = new Gson();
        play.libs.WS.HttpResponse res = WS.url("http://tse.vps.leandrorego.com/api/getEleitor").setParameter("titulo", titulo).get();

        JsonElement type = res.getJson();
        Eleitor eleitor = gson.fromJson(type, Eleitor.class);
        
        String idSecao =  session.get("secao"); 
        
       if(eleitor != null){
    	   
    	   if (eleitor.secao.id  == Long.parseLong(idSecao) && eleitor.statusVot.equals("NAO_VOTOU") && eleitor.statusEle.equals("APTO") ) {
               eleitor(eleitor,true);
           }else  if (eleitor.secao.id  != Long.parseLong(idSecao)){
        	   flash.error("Eleitor não pertece a esta seção. Dirija-se"
        	   		+ " para a Seção: "+eleitor.secao.id+" : "+eleitor.secao.zona.nome);
        	   Home.home(eleitor);
           }
    	   else{
        	   flash.error("Eleito já votou ou não pertence essa seção ou esta inapto!");
        	   Home.home(eleitor);
           }
       }else{
    	   flash.error("Titulo informado não existe");
    	   Home.home(eleitor);
       }
    }

    public static void foto(Long id) {
        Eleitor eleitor = Eleitor.findById(id);
        notFoundIfNull(eleitor);
        response.setContentTypeIfNotSet(eleitor.foto.type());
        renderBinary(eleitor.foto.get());
    }

    public static void fotoPadrao() {
        File file = Play.getFile("/public/images/user.png");
        renderBinary(file);
    }
 
    // ------------------------------ PRIMEIRO -->  Liberação da Urna  ----------------------------------------------------
    public static void bloquearDesbloquearUrna(Eleitor eleitor, String status) {
    	play.libs.WS.HttpResponse res = WS.url("http://10.112.5.0:9002/api/terminal").setParameter("status",status).post();
    	if(res.success()){
    		Auditoria auditoria = new Auditoria();
    		
    		auditoria.titulo = eleitor.titulo;
    		auditoria.numSecao = eleitor.secao.numSecao;
    		auditoria.numZona = eleitor.secao.zona.numZona;
    		auditoria.inicioVotacao = new Date();
    		auditoria.save();
    		eleitor(eleitor,false);  
    	}else{
    		eleitor(eleitor,false); 
    	}
    }
   
 // ------------------------------ SEGUNDO --> fica aguardado o eleitor terminar de votar  -------------------------------
    public static boolean confirmarVoto(){
    	Gson gson = new Gson();
    	play.libs.WS.HttpResponse res = WS.url("http://localhost:9005/confirmarVotacaoAtual").get();

        JsonElement type = res.getJson();
        boolean liberou = gson.fromJson(type, boolean.class);
    	
        return liberou;
    }

    // ----------------------------------- SEGUNDO --> fica aguardado o eleitor pedir mais tempo ---------------------------
    public static boolean tempoUrna() {
        Gson gson = new Gson();
        play.libs.WS.HttpResponse res = WS.url("http://localhost:9000/addTempo").get();

        JsonElement type = res.getJson();
        boolean resp = gson.fromJson(type, boolean.class); 
        
        // auditoria ********************************************************

        return resp;
    }
    
    public static void cancelarVotacaoAtual(Eleitor eleitor, String status) {
    	String idSecao =  session.get("secao");
    	play.libs.WS.HttpResponse res = WS.url("http://urna-api.herokuapp.com/api/cancela-votacao").setParameter("status",status).post();
    	
    	if(res.success()) {
    		setStatusVoto(idSecao, eleitor);
    		Home.home(null);
    	}
    }

    public static void justificar(String idSecao, String titulo) {
        Gson gson = new Gson();
        Map param = new HashMap();
        param.put("idSecao", idSecao);
        param.put("titulo", titulo);

        play.libs.WS.HttpResponse res = WS.url("http://tse.vps.leandrorego.com/api/justificar").setParameters(param).post();
        JsonElement type = res.getJson();

        Eleitor eleitor = gson.fromJson(type, Eleitor.class);
      
        if (eleitor.titulo == null) {
            ErroVoto erro = gson.fromJson(type, ErroVoto.class);
            flash.success(erro.value);
            Home.home(eleitor);
        } else {
        	
        	Auditoria auditoria = new Auditoria();;
        	auditoria.inicioVotacao = new Date();
        	auditoria.titulo = eleitor.titulo;
        	auditoria.numSecao = eleitor.secao.numSecao;
        	auditoria.numZona = eleitor.secao.zona.numZona;
        	auditoria.confirmacao = "Justificou";
        	auditoria.save();
        	
        	render(eleitor);
        }
    }
    
    public static void setStatusVoto(String idSecao, Eleitor eleitor) {
    	
        Gson gson = new Gson();
        Map param = new HashMap();
        param.put("idSecao", idSecao); 
        param.put("titulo",eleitor.titulo);
           
        play.libs.WS.HttpResponse res = WS.url("http://tse.vps.leandrorego.com/api/setstatuseleitor").setParameters(param).post();
              
        	Auditoria auditoria = Auditoria.find("titulo = ?",eleitor.titulo).first();
        	auditoria.fimVotacao = new Date();
        	SimpleDateFormat sdf = new SimpleDateFormat("mm.ss");
        	double votoFim = Double.parseDouble(sdf.format(auditoria.fimVotacao));
        	double votoInicio = Double.parseDouble(sdf.format(auditoria.inicioVotacao));
		
        	auditoria.tempoDevotacao = (votoFim - votoInicio);
        	if(res.success()) {
        		auditoria.confirmacao = "Votou";
      
        		auditoria.save();
        		flash.success("Voto confirmado do eleitor: "+eleitor.nome);
        		Home.home(null);
        	}else {
        		auditoria.confirmacao = "Concelado";
        	
        		auditoria.save();
        		flash.success("Voto não enviado para o TSE");
        		Home.home(null);
        	}	
    }
}
