package com.chemical.safety.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BudgetController {

    @GetMapping("/budget-variance")
    public String budgetVariance(Model model) {

        // Main summary values
        model.addAttribute("allocatedBudget", "5,00,000");
        model.addAttribute("actualAmount", "3,75,000");
        model.addAttribute("variance", "1,25,000");

        // Budget table
        List<Map<String, Object>> budgetLines = new ArrayList<>();

        budgetLines.add(createLine(
                "Chemical Purchase",
                "2,00,000",
                "1,65,000",
                "35,000",
                true
        ));

        budgetLines.add(createLine(
                "Storage & Handling",
                "1,00,000",
                "1,20,000",
                "-20,000",
                false
        ));

        budgetLines.add(createLine(
                "Safety Equipment",
                "75,000",
                "60,000",
                "15,000",
                true
        ));

        budgetLines.add(createLine(
                "Transport & Logistics",
                "50,000",
                "55,000",
                "-5,000",
                false
        ));

        budgetLines.add(createLine(
                "Maintenance",
                "25,000",
                "20,000",
                "5,000",
                true
        ));

        model.addAttribute("budgetLines", budgetLines);

        // Budget utilisation
        model.addAttribute("utilisationPct", 75);

        return "budget-variance";
    }

    private Map<String, Object> createLine(
            String account,
            String budgeted,
            String actual,
            String variance,
            boolean positive) {

        Map<String, Object> line = new HashMap<>();

        line.put("account", account);
        line.put("budgeted", budgeted);
        line.put("actual", actual);
        line.put("variance", variance);
        line.put("positive", positive);

        return line;
    }
}