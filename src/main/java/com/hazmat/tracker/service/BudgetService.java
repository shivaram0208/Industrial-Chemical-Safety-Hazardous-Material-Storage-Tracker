package com.hazmat.tracker.service;

import com.hazmat.tracker.entity.Budget;
import com.hazmat.tracker.repository.BudgetRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    /**
     * Records actual hazard-handling expenditure against a budget line (e.g. when a
     * vendor bill for neutralization supplies or disposal is booked against a sector).
     */
    public Budget recordActualExpense(Long budgetId, BigDecimal amount) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found: " + budgetId));
        budget.setActualAmount(budget.getActualAmount().add(amount));
        return budgetRepository.save(budget);
    }

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    public List<Budget> getBudgetsForAnalyticAccount(Long analyticAccountId) {
        return budgetRepository.findAll().stream()
                .filter(b -> b.getAnalyticAccount().getId().equals(analyticAccountId))
                .toList();
    }
}
