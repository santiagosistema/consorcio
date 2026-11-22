package br.com.santiagopinheiro.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FragmentController {

    @GetMapping("/fragments/menu")
    public String menu() {
        return "fragments/menu";
    }

    @GetMapping("/fragments/topnavbar")
    public String topnavbar() {
        return "fragments/topnavbar";
    }
}