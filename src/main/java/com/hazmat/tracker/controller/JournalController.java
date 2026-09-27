package com.hazmat.tracker.controller;

import com.hazmat.tracker.entity.Journal;
import com.hazmat.tracker.entity.JournalEntry;
import com.hazmat.tracker.repository.JournalEntryRepository;
import com.hazmat.tracker.repository.JournalRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/journals")
public class JournalController {

    private final JournalRepository journalRepository;
    private final JournalEntryRepository journalEntryRepository;

    public JournalController(JournalRepository journalRepository, JournalEntryRepository journalEntryRepository) {
        this.journalRepository = journalRepository;
        this.journalEntryRepository = journalEntryRepository;
    }

    @GetMapping
    public List<Journal> getAll() {
        return journalRepository.findAll();
    }

    @GetMapping("/entries")
    public List<JournalEntry> getAllEntries() {
        return journalEntryRepository.findAll();
    }

    @GetMapping("/entries/{id}")
    public JournalEntry getEntry(@PathVariable Long id) {
        return journalEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Journal entry not found: " + id));
    }
}
