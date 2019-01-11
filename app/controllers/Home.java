package controllers;

import models.Eleitor;
import play.mvc.Controller;

public class Home extends Controller {

    public static void home(Eleitor eleitor) {
        render(eleitor);
    }

}
