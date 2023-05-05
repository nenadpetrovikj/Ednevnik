package mk.ukim.finki.wp.project.ednevnik.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = {"/", "/home"})
public class Home {

    @GetMapping
    public String getHomePage() {
        return "home";
    }
}
