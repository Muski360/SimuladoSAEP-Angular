package com.autoestufa.backend.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.autoestufa.backend.Model.AgendamentoModel;
import com.autoestufa.backend.Model.ClienteModel;
import com.autoestufa.backend.Model.EstufaModel;
import com.autoestufa.backend.Repository.AgendamentoRepository;
import com.autoestufa.backend.Repository.ClienteRepository;
import com.autoestufa.backend.Repository.EstufaRepository;

import lombok.RequiredArgsConstructor;

// Disponibiliza o cadastro e o controle dos agendamentos da oficina.
@RestController
@RequestMapping("/api/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoRepository agendamentoRepository;
    private final ClienteRepository clienteRepository;
    private final EstufaRepository estufaRepository;

    @GetMapping
    public ResponseEntity<List<AgendamentoModel>> listar(
            @RequestParam(name = "data", required = false) LocalDate data) {
        if (data == null) {
            return ResponseEntity.ok(agendamentoRepository.findAll());
        }

        return ResponseEntity.ok(agendamentoRepository.findByDataAgendamento(data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoModel> buscarPorId(@PathVariable(name = "id") Long id) {
        return agendamentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody AgendamentoModel dados) {
        ResponseEntity<?> validacao = validarDados(dados);
        if (validacao != null) {
            return validacao;
        }

        ClienteModel cliente = clienteRepository.findById(dados.getCliente().getId()).orElse(null);
        EstufaModel estufa = estufaRepository.findById(dados.getEstufa().getId()).orElse(null);
        if (cliente == null || estufa == null) {
            return resposta(HttpStatus.BAD_REQUEST, "Cliente ou estufa nao encontrada.");
        }

        dados.setCliente(cliente);
        dados.setEstufa(estufa);
        if (possuiConflito(dados)) {
            return resposta(HttpStatus.CONFLICT, "A estufa ja esta ocupada nesse horario.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(agendamentoRepository.save(dados));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable(name = "id") Long id,
            @RequestBody AgendamentoModel dadosAtualizados) {
        AgendamentoModel agendamento = agendamentoRepository.findById(id).orElse(null);
        if (agendamento == null) {
            return ResponseEntity.notFound().build();
        }

        ResponseEntity<?> validacao = validarDados(dadosAtualizados);
        if (validacao != null) {
            return validacao;
        }

        ClienteModel cliente = clienteRepository.findById(dadosAtualizados.getCliente().getId()).orElse(null);
        EstufaModel estufa = estufaRepository.findById(dadosAtualizados.getEstufa().getId()).orElse(null);
        if (cliente == null || estufa == null) {
            return resposta(HttpStatus.BAD_REQUEST, "Cliente ou estufa nao encontrada.");
        }

        agendamento.setCliente(cliente);
        agendamento.setEstufa(estufa);
        agendamento.setDataAgendamento(dadosAtualizados.getDataAgendamento());
        agendamento.setHoraInicio(dadosAtualizados.getHoraInicio());
        agendamento.setHoraFim(dadosAtualizados.getHoraFim());
        agendamento.setServico(dadosAtualizados.getServico());
        agendamento.setStatus(dadosAtualizados.getStatus());

        if (possuiConflito(agendamento)) {
            return resposta(HttpStatus.CONFLICT, "A estufa ja esta ocupada nesse horario.");
        }

        return ResponseEntity.ok(agendamentoRepository.save(agendamento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable(name = "id") Long id) {
        if (!agendamentoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        agendamentoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<?> validarDados(AgendamentoModel dados) {
        if (dados.getCliente() == null || dados.getCliente().getId() == null
                || dados.getEstufa() == null || dados.getEstufa().getId() == null
                || dados.getDataAgendamento() == null || dados.getHoraInicio() == null
                || dados.getHoraFim() == null) {
            return resposta(HttpStatus.BAD_REQUEST, "Cliente, estufa, data e horarios sao obrigatorios.");
        }

        if (!dados.getHoraInicio().isBefore(dados.getHoraFim())) {
            return resposta(HttpStatus.BAD_REQUEST, "A hora de inicio deve ser anterior a hora de fim.");
        }

        return null;
    }

    private boolean possuiConflito(AgendamentoModel agendamento) {
        return agendamentoRepository
                .findByDataAgendamentoAndEstufaAndHoraInicioLessThanAndHoraFimGreaterThan(
                        agendamento.getDataAgendamento(),
                        agendamento.getEstufa(),
                        agendamento.getHoraFim(),
                        agendamento.getHoraInicio())
                .stream()
                .anyMatch(outro -> !outro.getId().equals(agendamento.getId()));
    }

    private ResponseEntity<Map<String, String>> resposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("mensagem", mensagem));
    }
}
