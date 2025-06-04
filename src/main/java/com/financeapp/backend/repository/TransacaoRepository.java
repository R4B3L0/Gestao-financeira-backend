package com.financeapp.backend.repository;

import com.financeapp.backend.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByUsuarioId(Long usuarioId);

    // Método para verificar se uma transação pertence a um usuário específico
    boolean existsByIdAndUsuarioId(Long id, Long usuarioId);

    // Método para buscar dados agregados para o gráfico
    // Retorna uma lista de arrays de objetos, onde cada array tem [categoria, total]
    @Query("SELECT t.categoria as categoria, SUM(t.valor) as total FROM Transacao t WHERE t.usuario.id = :usuarioId GROUP BY t.categoria")
    List<Object[]> findTotalValorPorCategoria(@Param("usuarioId") Long usuarioId);

}

