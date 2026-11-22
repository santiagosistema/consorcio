package br.com.santiagopinheiro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.santiagopinheiro.model.Sorteio;

public interface SorteioRepository extends JpaRepository<Sorteio, Long> {
    List<Sorteio> findByGrupo_IdOrderByDataSorteioDesc(Long grupoId);
}
