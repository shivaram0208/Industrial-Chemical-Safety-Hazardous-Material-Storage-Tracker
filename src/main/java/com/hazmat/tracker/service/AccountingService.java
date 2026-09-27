package com.hazmat.tracker.service;

import com.hazmat.tracker.entity.*;
import com.hazmat.tracker.repository.AccountRepository;
import com.hazmat.tracker.repository.JournalEntryRepository;
import com.hazmat.tracker.repository.JournalRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class AccountingService {

    private final JournalRepository journalRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final AccountRepository accountRepository;

    public AccountingService(JournalRepository journalRepository,
                              JournalEntryRepository journalEntryRepository,
                              AccountRepository accountRepository) {
        this.journalRepository = journalRepository;
        this.journalEntryRepository = journalEntryRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * Posts a simple two-line double-entry journal entry: debit one account, credit another,
     * for the same amount. Updates running account balances.
     */
    public JournalEntry postEntry(Enums.JournalType journalType, LocalDate date, String reference,
                                   String narration, Account debitAccount, Account creditAccount,
                                   BigDecimal amount) {

        Journal journal = journalRepository.findAll().stream()
                .filter(j -> j.getType() == journalType)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Journal of type " + journalType + " not configured. Seed master data first."));

        JournalEntry entry = new JournalEntry();
        entry.setJournal(journal);
        entry.setEntryDate(date);
        entry.setReference(reference);
        entry.setNarration(narration);

        JournalEntryLine debitLine = new JournalEntryLine();
        debitLine.setJournalEntry(entry);
        debitLine.setAccount(debitAccount);
        debitLine.setDebit(amount);
        debitLine.setCredit(BigDecimal.ZERO);

        JournalEntryLine creditLine = new JournalEntryLine();
        creditLine.setJournalEntry(entry);
        creditLine.setAccount(creditAccount);
        creditLine.setDebit(BigDecimal.ZERO);
        creditLine.setCredit(amount);

        entry.getLines().add(debitLine);
        entry.getLines().add(creditLine);

        // Update running balances (simple model: Asset/Expense increase on debit; Liability/Income/Equity increase on credit)
        applyToBalance(debitAccount, amount, true);
        applyToBalance(creditAccount, amount, false);
        accountRepository.save(debitAccount);
        accountRepository.save(creditAccount);

        return journalEntryRepository.save(entry);
    }

    private void applyToBalance(Account account, BigDecimal amount, boolean isDebit) {
        boolean increasesOnDebit = account.getType() == Enums.AccountType.ASSET || account.getType() == Enums.AccountType.EXPENSE;
        BigDecimal delta = (isDebit == increasesOnDebit) ? amount : amount.negate();
        account.setBalance(account.getBalance().add(delta));
    }
}
