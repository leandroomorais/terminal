package controllers;

import java.util.Date;

import models.Eleitor;
import play.mvc.Controller;

public class Auditorias extends Controller {

    public static void salvar(Eleitor eleitor) {

        //eleitor.dataVoto = new Date();
        eleitor.save();
    }
}
