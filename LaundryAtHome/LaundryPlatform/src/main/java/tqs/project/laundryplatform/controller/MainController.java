package tqs.project.laundryplatform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import tqs.project.laundryplatform.model.User;

@RestController
public class MainController {

    @GetMapping("/")
    public ModelAndView mainPage() {
        return new ModelAndView("login_form");
    }

    @GetMapping("/index")
    public ModelAndView showIndex() {
        System.err.println("index");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        return modelAndView;
    }


    @GetMapping("/login")
    public ModelAndView showLoginForm(User user) {
        System.err.println("get login");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login_form");
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView showRegisterForm(User user) {
        System.err.println("register");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register_form");
        return modelAndView;
    }

    @GetMapping("/services")
    public ModelAndView showServices() {
        System.err.println("service");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("service");
        return modelAndView;
    }

    @GetMapping("/pricing")
    public ModelAndView showPricing() {
        System.err.println("pricing");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pricing");
        return modelAndView;
    }

    @GetMapping("/orders")
    public ModelAndView showOrders() {
        System.err.println("orders");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("orders");
        return modelAndView;
    }

    @GetMapping("/neworder")
    public ModelAndView showNewOrder() {
        System.err.println("neworder");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("neworder");
        return modelAndView;
    }
}
