package br.com.santiagopinheiro.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.santiagopinheiro.model.Parcela;
import br.com.santiagopinheiro.model.Usuario;

public interface ParcelaRepository extends JpaRepository<Parcela, Long> {

	// Consulta as parcelas do grupo em determinado período
	List<Parcela> findByGrupo_IdAndDtVencimentoBetweenOrderByUsuario_NomeAscNumeroParcelaAsc(Long grupoId,
			LocalDate inicio, LocalDate fim);


	
	// Consulta todas as parcelas de um usuário dentro do grupo
	List<Parcela> findByGrupo_IdAndUsuario_IdOrderByNumeroParcelaAsc(Long grupoId, Long usuarioId);


	// busca os usuarios do grupo
	@Query("""
			SELECT DISTINCT p.usuario
			FROM Parcela p
			WHERE p.grupo.id = :grupoId
			ORDER BY p.usuario.nome
			""")
	List<Usuario> buscarUsuariosDoGrupo(@Param("grupoId") Long grupoId);
	
	
	List<Parcela>
	findByCotaParticipante_IdOrderByNumeroParcelaAsc(
	        Long cotaParticipanteId);
	
	
	@Modifying
	@Query("""
	    UPDATE Parcela p
	    SET p.situacao = 'atraso'
	    WHERE p.dtVencimento < :data
	      AND p.dtPagamento IS NULL
	      AND p.situacao <> 'atraso'
	    """)
	int atualizarParcelasVencidas(@Param("data") LocalDate hoje);
	
	//Consulta as parcelas vencidas
    List<Parcela> findByDtVencimentoBeforeAndDtPagamentoIsNullOrderByDtVencimentoAsc(LocalDate hoje);
    
    
    @Query(""" 
    		SELECT p 
    		from Parcela p
    		WHERE p.dtVencimento BETWEEN :inicioMes AND :fimMes
    		AND p.dtPagamento IS NULL
    		ORDER BY p.usuario.nome 
    		""")
    List<Parcela> findParcelasDoMes(@Param("inicioMes") LocalDate inicioMes,
    							    @Param("fimMes") LocalDate fimMes);
    
}