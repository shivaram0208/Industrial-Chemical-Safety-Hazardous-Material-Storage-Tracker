package com.hazmat.tracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ModulePageController {

    @GetMapping("/storage")
    public String storage() {
        return "storage";
    }

    @GetMapping("/intake")
    public String intake() {
        return "intake";
    }

    @GetMapping("/inspection")
    public String inspection() {
        return "inspection";
    }

    @GetMapping("/procurement")
    public String procurement() {
        return "procurement";
    }

    @GetMapping("/sales")
    public String sales() {
        return "sales";
    }

    @GetMapping("/budget-variance")
    public String budgetVariance() {
        return "budget-variance";
    }
}