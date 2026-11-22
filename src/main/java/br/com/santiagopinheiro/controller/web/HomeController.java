
package br.com.santiagopinheiro.controller.web;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.santiagopinheiro.model.Grupo;
import br.com.santiagopinheiro.model.Usuario;

@Controller
public class HomeController {

    

    
    @GetMapping("/index")
    public String index() {
        return "index";
    }
    
    @GetMapping("/home")
    public String home() {
        return "home";
    }
    
    @GetMapping("/usuarios")
    public String usuarios() {
        return "usuarios";
    }
    
    @GetMapping("/grupo")
    public String grupo(Model model) {
    	Grupo grupo = new Grupo();
    	model.addAttribute("grupo",grupo);
        return "grupo";
    }
    

}

