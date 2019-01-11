package controllers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.Auditoria;
import models.AuditoriaTerminal;
import play.libs.WS;
import play.mvc.Controller;

public class Logins extends Controller {

	public static void login() {
		render();
	}

	public static void autenticarTerminal(Long idSecao,String ipUrna) {

		try {
			String ipTerminal = InetAddress.getLocalHost().getHostAddress();
			
			System.out.println("passo -1");
			Map param = new HashMap();
			param.put("idSecao", idSecao);
			param.put("ipTerminal", ipTerminal);

			play.libs.WS.HttpResponse res = WS.url("http://tse.vps.leandrorego.com/api/setTerminal")
					.setParameters(param).post();

			if (res.success()) {
				System.out.println("passo 0"); 
				Long id = idSecao;
				Logins.autenticarUrna(id, ipTerminal,ipUrna);
			} else {

				flash.error(" TSE - Seção Invalida ou a requisição esta indisponivel!");
				Logins.login();
			}
		} catch (UnknownHostException ex) {
			Logger.getLogger(Logins.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static void autenticarUrna(Long id, String ipTerminal,String ipUrna) {
		String idSecao = Long.toString(id);
		
		Map param = new HashMap();
		param.put("idSecao", idSecao);
		param.put("ipTerminal", ipTerminal);
		param.put("ipUrna", ipUrna);
        
		play.libs.WS.HttpResponse res = WS.url("https://urna-api.herokuapp.com/api/secao")
				.setParameters(param).post();

		if (res.success()) {
			System.out.println("passo 1");
			session.put("secao", id);
			AuditoriaTerminal terminal = new AuditoriaTerminal();
			terminal.inicioLogin = new Date();
			terminal.save();
			Home.home(null);
		} else {
			System.out.println("passo 2");
			flash.error("Urna - Seção Invalida ou a requisição esta indisponivel!");
			Logins.login();
		}
	}

	public static void finalizarVotacao(String numero) {
		if (numero.equals("999999999")) {
			String finalizar = "true";
			play.libs.WS.HttpResponse res = WS.url("https://urna-api.herokuapp.com/api/finaliza-votacao")
					.setParameter("finalizar", finalizar).post();
		
			//if (res.success()) {
			
				session.clear();
				Date fim = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				AuditoriaTerminal auditoriaTerminal = AuditoriaTerminal.find("id = ?",1L).first();
				auditoriaTerminal.fimLogin = new Date();
				auditoriaTerminal.save();
				flash.success("Votação finalizada! "+sdf.format(fim));
				Logins.login();
			//}

		} else {
			flash.error("Numero incorreto");
			System.out.println("teste 3");
			Home.home(null);
		}
	}
}
