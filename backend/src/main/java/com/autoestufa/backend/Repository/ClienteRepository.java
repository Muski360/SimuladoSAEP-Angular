package com.autoestufa.backend.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autoestufa.backend.Model.ClienteModel;

public interface ClienteRepository extends JpaRepository<ClienteModel, Long> {

    // Permite buscar clientes por nome ou documento.
    List<ClienteModel> findByNomeContainingIgnoreCaseOrCpfContaining(
            String nome,
            String cpf);

}