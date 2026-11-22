package br.com.santiagopinheiro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.santiagopinheiro.model.Cota;

public interface CotaRepository extends JpaRepository<Cota, Long> {

    List<Cota> findByGrupo_IdOrderByNumeroCotaAsc(Long grupoId);

    boolean existsByGrupo_IdAndNumeroCota(
            Long grupoId,
            Integer numeroCota);
    
    boolean existsByGrupo_Id(Long grupoId);
    
    
}