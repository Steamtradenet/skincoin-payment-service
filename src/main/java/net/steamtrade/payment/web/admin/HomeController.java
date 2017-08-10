package net.steamtrade.payment.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by sasha on 01.08.17.
 */
@Controller
public class HomeController {

    @RequestMapping({"/",
            "/login"
    })
    public String index() {
        return "index.html";
    }

}
