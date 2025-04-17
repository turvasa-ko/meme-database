package meme_db;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class HelpController {

    @GetMapping("/help")
    public String help() {
        return "Welcome to the Meme Database Instructions";
    }


    @GetMapping("/")
    public String hello() {
        return "Hello, Welcome to the Meme Database!\n\n"+
               "The Instructions for the database usage can be found from \"/help\"";
    }

}
