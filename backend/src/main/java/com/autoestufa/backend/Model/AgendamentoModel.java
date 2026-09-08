package com.autoestufa.backend.Model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Representa um agendamento de servico da oficina.
@Entity
@Getter
@Setter
// O JPA precisa de um construtor sem argumentos para instanciar a entidade.
@NoArgsConstructor
// Facilita a criacao de objetos completos em testes e services.
@AllArgsConstructor
@Table(name = "agendamento")
public class AgendamentoModel {

    // Identificador gerado automaticamente pelo banco de dados.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cliente e estufa associados ao agendamento.
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private ClienteModel cliente;

    @ManyToOne
    @JoinColumn(name = "estufa_id")
    private EstufaModel estufa;

    // Dados do horario e do servico.
    private LocalDate dataAgendamento;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private String servico;
    private String status;
}
