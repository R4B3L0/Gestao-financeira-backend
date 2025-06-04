package com.financeapp.backend.controller;

import com.financeapp.backend.dto.TransacaoDto;
import com.financeapp.backend.dto.TransacaoRequest;
import com.financeapp.backend.service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transacoes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @GetMapping("/listar")
    public ResponseEntity<List<TransacaoDto>> getTransacoes() {
        try {
            List<TransacaoDto> transacoes = transacaoService.getTransacoesByUsuario();
            return ResponseEntity.ok(transacoes);
        } catch (Exception e) {
            // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<TransacaoDto> createTransacao(@RequestBody TransacaoRequest transacaoRequest) {
        System.out.println("Recebendo requisição cadastrarTransacao");
        try {
            TransacaoDto createdTransacao = transacaoService.createTransacao(transacaoRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTransacao);
        } catch (Exception e) {
            // Log the exception
            // Consider specific exceptions for bad requests (400)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransacaoDto> updateTransacao(@PathVariable Long id, @RequestBody TransacaoRequest transacaoRequest) {
        try {
            TransacaoDto updatedTransacao = transacaoService.updateTransacao(id, transacaoRequest);
            return ResponseEntity.ok(updatedTransacao);
        } catch (RuntimeException e) {
             // Handle specific exceptions like not found, access denied, no fields to update
             if (e.getMessage().contains("não encontrada") || e.getMessage().contains("não pertence")) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
             } else if (e.getMessage().contains("Nenhum campo")) {
                 // Maybe return 200 OK with the original DTO or 304 Not Modified?
                 // Or 400 Bad Request as per Node.js logic
                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); 
             }
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // General update error
        } catch (Exception e) {
            // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("excluir/{id}")
    public ResponseEntity<?> deleteTransacao(@PathVariable Long id) {
        try {
            transacaoService.deleteTransacao(id);
            // Node.js returned { mensagem: 'Transação deletada!' }
            return ResponseEntity.ok(Map.of("mensagem", "Transação deletada!"));
        } catch (RuntimeException e) {
            // Handle specific exceptions like not found, access denied
            if (e.getMessage().contains("não encontrada") || e.getMessage().contains("não pertence")) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
             }
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // General delete error
        } catch (Exception e) {
            // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/grafico")
    public ResponseEntity<Map<String, BigDecimal>> getGraficoData() {
        try {
            Map<String, BigDecimal> graficoData = transacaoService.getGraficoData();
            return ResponseEntity.ok(graficoData);
        } catch (Exception e) {
            // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
