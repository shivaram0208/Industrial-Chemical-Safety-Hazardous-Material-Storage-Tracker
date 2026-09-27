package com.hazmat.tracker.service;

import com.hazmat.tracker.entity.Account;
import com.hazmat.tracker.entity.Budget;
import com.hazmat.tracker.entity.Enums.AccountType;
import com.hazmat.tracker.repository.AccountRepository;
import com.hazmat.tracker.repository.BudgetRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final AccountRepository accountRepository;
    private final BudgetRepository budgetRepository;

    public ReportService(AccountRepository accountRepository, BudgetRepository budgetRepository) {
        this.accountRepository = accountRepository;
        this.budgetRepository = budgetRepository;
    }

    /**
     * Balance Sheet: Assets (Storage Facility + Cash/Bank) vs Liabilities (Payables) + Equity.
     */
    public Map<String, Object> getBalanceSheet() {
        List<Account> assets = accountRepository.findByType(AccountType.ASSET);
        List<Account> liabilities = accountRepository.findByType(AccountType.LIABILITY);
        List<Account> equity = accountRepository.findByType(AccountType.EQUITY);

        BigDecimal totalAssets = sum(assets);
        BigDecimal totalLiabilities = sum(liabilities);
        BigDecimal totalEquity = sum(equity);

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("assets", accountSummary(assets));
        report.put("totalAssets", totalAssets);
        report.put("liabilities", accountSummary(liabilities));
        report.put("totalLiabilities", totalLiabilities);
        report.put("equity", accountSummary(equity));
        report.put("totalEquity", totalEquity);
        report.put("totalLiabilitiesAndEquity", totalLiabilities.add(totalEquity));
        report.put("balanced", totalAssets.compareTo(totalLiabilities.add(totalEquity)) == 0);
        return report;
    }

    /**
     * Profit & Loss: Income (Storage + Compliance Revenue) minus Expenses (Disposal + Safety Ops).
     */
    public Map<String, Object> getProfitAndLoss() {
        List<Account> income = accountRepository.findByType(AccountType.INCOME);
        List<Account> expenses = accountRepository.findByType(AccountType.EXPENSE);

        BigDecimal totalIncome = sum(income);
        BigDecimal totalExpenses = sum(expenses);

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("income", accountSummary(income));
        report.put("totalIncome", totalIncome);
        report.put("expenses", accountSummary(expenses));
        report.put("totalExpenses", totalExpenses);
        report.put("netProfit", totalIncome.subtract(totalExpenses));
        return report;
    }

    /**
     * Budget variance report per Analytic Account (Containment Sector).
     */
    public List<Map<String, Object>> getBudgetVarianceReport() {
        List<Budget> budgets = budgetRepository.findAll();
        return budgets.stream().map(b -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("budgetId", b.getId());
            row.put("budgetName", b.getName());
            row.put("analyticAccount", b.getAnalyticAccount().getName());
            row.put("periodStart", b.getPeriodStart());
            row.put("periodEnd", b.getPeriodEnd());
            row.put("allocatedAmount", b.getAllocatedAmount());
            row.put("actualAmount", b.getActualAmount());
            row.put("variance", b.getVariance());
            row.put("status", b.getVariance().compareTo(BigDecimal.ZERO) >= 0 ? "WITHIN_BUDGET" : "OVER_BUDGET");
            return row;
        }).toList();
    }

    private BigDecimal sum(List<Account> accounts) {
        return accounts.stream().map(Account::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<Map<String, Object>> accountSummary(List<Account> accounts) {
        return accounts.stream().map(a -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("code", a.getCode());
            m.put("name", a.getName());
            m.put("balance", a.getBalance());
            return m;
        }).toList();
    }
}
