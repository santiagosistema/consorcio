package br.com.santiagopinheiro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.santiagopinheiro.model.CotaParticipante;
import br.com.santiagopinheiro.model.Usuario;

public interface CotaParticipanteRepository
        extends JpaRepository<CotaParticipante, Long> {
	
	List<CotaParticipante> findByCota_IdOrderByUsuario_NomeAsc(
	        Long cotaId);
	
	List<CotaParticipante> findByCota_Grupo_Id(Long grupoId);
	
	@Query("""
    		SELECT c.usuario
    		FROM CotaParticipante c
    		WHERE c.grupo.id = :grupoId
    		AND c.usuario.situacao = true
    		""")
    List<Usuario> findUserbyGrupoId(@Param("grupoId") Long grupoId);
}