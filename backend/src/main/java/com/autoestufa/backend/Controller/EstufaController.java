package com.autoestufa.backend.Controller;

import java.util.List;

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

import com.autoestufa.backend.Model.EstufaModel;
import com.autoestufa.backend.Repository.EstufaRepository;

import lombok.RequiredArgsConstructor;

// Disponibiliza o cadastro e a consulta das estufas de pintura.
@RestController
@RequestMapping("/api/estufas")
@RequiredArgsConstructor
public class EstufaController {

    private final EstufaRepository estufaRepository;

    @GetMapping
    public ResponseEntity<List<EstufaModel>> listar(
            @RequestParam(name = "busca", required = false) String busca) {
        if (busca == null || busca.isBlank()) {
            return ResponseEntity.ok(estufaRepository.findAll());
        }

        return ResponseEntity.ok(estufaRepository.findByNomeContainingIgnoreCase(busca));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstufaModel> buscarPorId(@PathVariable(name = "id") Long id) {
        return estufaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EstufaModel> cadastrar(@RequestBody EstufaModel estufa) {
        return ResponseEntity.ok(estufaRepository.save(estufa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstufaModel> atualizar(
            @PathVariable(name = "id") Long id,
            @RequestBody EstufaModel dadosAtualizados) {
        return estufaRepository.findById(id)
                .map(estufa -> {
                    estufa.setNome(dadosAtualizados.getNome());
                    estufa.setAtiva(dadosAtualizados.getAtiva());
                    return ResponseEntity.ok(estufaRepository.save(estufa));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable(name = "id") Long id) {
        if (!estufaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        estufaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
