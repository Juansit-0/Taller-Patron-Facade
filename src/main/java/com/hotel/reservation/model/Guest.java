package com.hotel.reservation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "guests")
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "document_id", nullable = false)
    private String documentId;

    @JsonIgnore
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Guest() {
    }

    public Guest(String fullName, String email, String phone, String documentId, String passwordHash) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.documentId = documentId;
        this.passwordHash = passwordHash;
        this.createdAt = LocalDateTime.now();
    }

    public void updateProfile(String fullName, String phone, String documentId) {
        this.fullName = fullName;
        this.phone = phone;
        this.documentId = documentId;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
