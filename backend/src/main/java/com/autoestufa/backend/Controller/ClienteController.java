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

import com.autoestufa.backend.Model.ClienteModel;
import com.autoestufa.backend.Repository.ClienteRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteRepository clienteRepository;

    @GetMapping
    public ResponseEntity<List<ClienteModel>> listar(
            @RequestParam(name = "busca", required = false) String busca) {
        if (busca == null || busca.isBlank()) {
            return ResponseEntity.ok(clienteRepository.findAll());
        }

        return ResponseEntity.ok(
                clienteRepository.findByNomeContainingIgnoreCaseOrCpfContaining(busca, busca));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteModel> buscarPorId(@PathVariable(name = "id") Long id) {
        return clienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ClienteModel> cadastrar(@RequestBody ClienteModel cliente) {
        return ResponseEntity.ok(clienteRepository.save(cliente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteModel> atualizar(
            @PathVariable(name = "id") Long id,
            @RequestBody ClienteModel dadosAtualizados) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    cliente.setNome(dadosAtualizados.getNome());
                    cliente.setCpf(dadosAtualizados.getCpf());
                    cliente.setTelefone(dadosAtualizados.getTelefone());
                    cliente.setEmail(dadosAtualizados.getEmail());
                    return ResponseEntity.ok(clienteRepository.save(cliente));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable(name = "id") Long id) {
        if (!clienteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        clienteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
