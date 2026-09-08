package com.autoestufa.backend.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Representa um usuario persistido na tabela correspondente do banco de dados.
@Entity
@Getter
@Setter
// O JPA precisa de um construtor sem argumentos para instanciar a entidade.
@NoArgsConstructor
// Facilita a criacao de objetos completos em testes e services.
@AllArgsConstructor

@Table(name = "estufa")
public class EstufaModel {

    // Identificador gerado automaticamente pelo banco de dados.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Dados
    private String nome;
    private Boolean ativa;
}
