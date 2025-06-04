package com.financeapp.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransacaoDto {
    private Long id;
    private Long usuarioId; // Apenas para referência, pode ser omitido se não necessário no DTO
    private String tipo;
    private String categoria;
    private BigDecimal valor;
    private String descricao;
    private LocalDate data;
}

