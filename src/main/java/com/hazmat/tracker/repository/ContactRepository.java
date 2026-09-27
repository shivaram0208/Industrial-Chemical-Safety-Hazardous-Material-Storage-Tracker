package com.hazmat.tracker.repository;

import com.hazmat.tracker.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {
}
