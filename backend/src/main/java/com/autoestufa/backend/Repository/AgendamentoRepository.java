package com.autoestufa.backend.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autoestufa.backend.Model.AgendamentoModel;
import com.autoestufa.backend.Model.EstufaModel;

public interface AgendamentoRepository extends JpaRepository<AgendamentoModel, Long> {

    // Lista os agendamentos de uma data especifica.
    List<AgendamentoModel> findByDataAgendamento(LocalDate dataAgendamento);

    // Busca agendamentos da mesma estufa que se sobrepoem ao intervalo informado.
    List<AgendamentoModel> findByDataAgendamentoAndEstufaAndHoraInicioLessThanAndHoraFimGreaterThan(
            LocalDate dataAgendamento,
            EstufaModel estufa,
            LocalTime horaFim,
            LocalTime horaInicio);

}