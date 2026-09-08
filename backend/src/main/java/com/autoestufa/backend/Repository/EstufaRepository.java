package com.autoestufa.backend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autoestufa.backend.Model.EstufaModel;

public interface EstufaRepository extends JpaRepository<EstufaModel, Long> {

    // Método para procurar por nome
    Optional<EstufaModel> findByNome(String nome);

    // Busca estufas pelo nome informado, sem diferenciar letras maiusculas e
    // minusculas.
    List<EstufaModel> findByNomeContainingIgnoreCase(String nome);

}