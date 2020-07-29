package com.durak.controller;

import com.durak.entity.Player;
import com.durak.service.Class.PlayerImplService;
import com.durak.service.Interface.PlayerDAO;
import com.durak.service.Interface.UserDAO;
import com.durak.entity.User;
import com.durak.util.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/")
public class AuthorizeController {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private PlayerDAO playerDAO;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserValidator userValidator;

    @GetMapping("/")
    public RedirectView welcome() {
        return new RedirectView("login");
    }

    @GetMapping("login")
    public String login() {
        return "login";
    }

    @GetMapping("registration")
    public String registration() {
        return "registration";
    }

    @RequestMapping(value="register",method= RequestMethod.POST)
    public ModelAndView register(ModelMap model, @RequestParam String username, @RequestParam String password, @RequestParam String confirmPassword) {

        userValidator.validate(new User(username,password),confirmPassword);
        if (userValidator.hasErrors()){
            model.addAttribute("errors",userValidator.getErrors());
            return new ModelAndView("registration", model);
        }
        else {
            User user = new User(username, passwordEncoder.encode(password));
            userDAO.addNewUser(user);
            playerDAO.save(new Player(user));
            return new ModelAndView("login");
        }
//
    }
}
