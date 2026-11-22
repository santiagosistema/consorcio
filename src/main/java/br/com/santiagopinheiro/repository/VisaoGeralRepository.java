package br.com.santiagopinheiro.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.santiagopinheiro.model.Parcela;

public interface VisaoGeralRepository extends JpaRepository<Parcela, Long> {

    @Query("""
        SELECT COALESCE(SUM(p.valorDevido), 0.0)
        FROM Parcela p
        WHERE p.cotaParticipante.cota.grupo.id = :grupoId
          AND p.dtPagamento IS NOT NULL
        """)
    Double somarArrecadacaoPaga(@Param("grupoId") Long grupoId);

    
    
    @Query("""
        SELECT COUNT(DISTINCT p.id)
        FROM Parcela p
        WHERE p.cotaParticipante.cota.grupo.id = :grupoId
          AND p.dtPagamento IS NOT NULL
        """)
    Long contarParcelasPagas(@Param("grupoId") Long grupoId);
    
    
    
    @Query("""
    		SELECT p
    		FROM Parcela p
    		WHERE p.dtVencimento BETWEEN :dataInicio AND :dataFim 
    		""")
    List<Parcela> listParcelasDoMes(@Param("dataInicio") LocalDate dataInicio,
    								@Param("dataFim") LocalDate dataFim);
}
