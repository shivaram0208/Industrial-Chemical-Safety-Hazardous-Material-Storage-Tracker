package com.hazmat.tracker.config;

import com.hazmat.tracker.entity.Account;
import com.hazmat.tracker.entity.Enums.AccountType;
import com.hazmat.tracker.entity.Enums.JournalType;
import com.hazmat.tracker.entity.Journal;
import com.hazmat.tracker.repository.AccountRepository;
import com.hazmat.tracker.repository.JournalRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Seeds the default Chart of Accounts and Journals required by the transaction flows,
 * matching section 3.3 and 3.4 of the spec. Runs once at startup; skips if data already exists.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final JournalRepository journalRepository;

    public DataSeeder(AccountRepository accountRepository, JournalRepository journalRepository) {
        this.accountRepository = accountRepository;
        this.journalRepository = journalRepository;
    }

    @Override
    public void run(String... args) {
        seedAccounts();
        seedJournals();
    }

    private void seedAccounts() {
        if (accountRepository.count() > 0) return;

        createAccount("1001", "Containment Storage Facility", AccountType.ASSET);
        createAccount("1002", "Cash/Bank", AccountType.ASSET);
        createAccount("2001", "Chemical Supplier Payables", AccountType.LIABILITY);
        createAccount("2002", "Safety Vendor Creditors", AccountType.LIABILITY);
        createAccount("4001", "Hazmat Storage Revenue", AccountType.INCOME);
        createAccount("4002", "Compliance Fee Revenue", AccountType.INCOME);
        createAccount("5001", "Chemical Disposal Expenses", AccountType.EXPENSE);
        createAccount("5002", "Facility Safety Operating Costs", AccountType.EXPENSE);
    }

    private void createAccount(String code, String name, AccountType type) {
        Account a = new Account();
        a.setCode(code);
        a.setName(name);
        a.setType(type);
        a.setBalance(BigDecimal.ZERO);
        accountRepository.save(a);
    }

    private void seedJournals() {
        if (journalRepository.count() > 0) return;

        createJournal("Sales Journal", JournalType.SALES);
        createJournal("Purchase Journal", JournalType.PURCHASE);
        createJournal("Cash Journal", JournalType.CASH);
        createJournal("Bank Journal", JournalType.BANK);
    }

    private void createJournal(String name, JournalType type) {
        Journal j = new Journal();
        j.setName(name);
        j.setType(type);
        journalRepository.save(j);
    }
}
