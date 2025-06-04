package com.financeapp.backend.service;

import com.financeapp.backend.dto.TransacaoDto;
import com.financeapp.backend.dto.TransacaoRequest;
import com.financeapp.backend.entity.Transacao;
import com.financeapp.backend.entity.Usuario;
import com.financeapp.backend.repository.TransacaoRepository;
import com.financeapp.backend.repository.UsuarioRepository;
import com.financeapp.backend.security.UserDetailsImpl; // Assuming UserDetailsImpl will be created later
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository; // Needed to fetch Usuario reference

    // Helper method to get current user ID
    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) principal).getId();
        } else {
            // Handle cases where principal might not be UserDetailsImpl (e.g., anonymous user)
            // Or throw an exception if authenticated user is always expected
            throw new IllegalStateException("User not authenticated properly");
        }
    }

    // Helper to convert Entity to DTO
    private TransacaoDto convertToDto(Transacao transacao) {
        TransacaoDto dto = new TransacaoDto();
        dto.setId(transacao.getId());
        dto.setUsuarioId(transacao.getUsuario().getId()); // Include if needed
        dto.setTipo(transacao.getTipo());
        dto.setCategoria(transacao.getCategoria());
        dto.setValor(transacao.getValor());
        dto.setDescricao(transacao.getDescricao());
        dto.setData(transacao.getData());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<TransacaoDto> getTransacoesByUsuario() {
        Long usuarioId = getCurrentUserId();
        List<Transacao> transacoes = transacaoRepository.findByUsuarioId(usuarioId);
        return transacoes.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public TransacaoDto createTransacao(TransacaoRequest transacaoRequest) {
        Long usuarioId = getCurrentUserId();
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado")); // Or a custom exception

        Transacao transacao = new Transacao();
        transacao.setUsuario(usuario);
        transacao.setTipo(transacaoRequest.getTipo());
        transacao.setCategoria(transacaoRequest.getCategoria());
        transacao.setValor(transacaoRequest.getValor());
        transacao.setDescricao(transacaoRequest.getDescricao());
        transacao.setData(LocalDate.now()); // Set current date as per Node.js logic

        Transacao savedTransacao = transacaoRepository.save(transacao);
        return convertToDto(savedTransacao);
    }

    @Transactional
    public TransacaoDto updateTransacao(Long id, TransacaoRequest transacaoRequest) {
        Long usuarioId = getCurrentUserId();
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada")); // Custom exception preferred

        // Verify ownership
        if (!transacao.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso negado: Transação não pertence ao usuário."); // Custom exception preferred
        }

        boolean updated = false;
        if (transacaoRequest.getTipo() != null) {
            transacao.setTipo(transacaoRequest.getTipo());
            updated = true;
        }
        if (transacaoRequest.getCategoria() != null) {
            transacao.setCategoria(transacaoRequest.getCategoria());
            updated = true;
        }
        if (transacaoRequest.getValor() != null) {
            transacao.setValor(transacaoRequest.getValor());
            updated = true;
        }
        if (transacaoRequest.getDescricao() != null) {
            transacao.setDescricao(transacaoRequest.getDescricao());
            updated = true;
        }

        if (!updated) {
             throw new RuntimeException("Nenhum campo para atualizar."); // Or return current DTO
        }

        Transacao updatedTransacao = transacaoRepository.save(transacao);
        return convertToDto(updatedTransacao);
    }

    @Transactional
    public void deleteTransacao(Long id) {
        Long usuarioId = getCurrentUserId();
        // Verify ownership before deleting
        if (!transacaoRepository.existsByIdAndUsuarioId(id, usuarioId)) {
             throw new RuntimeException("Transação não encontrada ou não pertence ao usuário."); // Custom exception preferred
        }
        transacaoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getGraficoData() {
        Long usuarioId = getCurrentUserId();
        List<Object[]> results = transacaoRepository.findTotalValorPorCategoria(usuarioId);

        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0], // Categoria
                        result -> (BigDecimal) result[1]  // Total
                ));
    }
}

