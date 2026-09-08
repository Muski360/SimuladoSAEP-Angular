package com.autoestufa.backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autoestufa.backend.Model.AdminModel;

public interface AdminRepository extends JpaRepository<AdminModel, Long> {

    // Procura o administrador pela credencial utilizada no login.
    Optional<AdminModel> findByEmail(String email);
}