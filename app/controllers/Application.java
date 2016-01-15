package controllers;

import models.Bar;
import play.data.Form;
import play.db.ebean.Model;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import java.util.List;

import static play.libs.Json.toJson;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("hello play from java"));
    }

    public static Result addBar() {
        Form<Bar> form = Form.form(Bar.class).bindFromRequest();
        Bar bar = form.get();
        bar.save();
        return redirect(controllers.routes.Application.index());
    }

    public static Result getBars() {
        List<Bar> bars = new Model.Finder(String.class, Bar.class).all();
        return ok(toJson(bars));
    }

}
