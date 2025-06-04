package com.financeapp.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransacaoRequest {
    // Não inclui id ou usuarioId, pois são definidos pelo contexto/path
    // Não inclui data, pois é definida pelo servidor no Node.js (manteremos isso)
    private String tipo;
    private String categoria;
    private BigDecimal valor;
    private String descricao;
}

