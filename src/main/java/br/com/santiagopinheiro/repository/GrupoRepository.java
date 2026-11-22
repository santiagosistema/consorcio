package br.com.santiagopinheiro.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.santiagopinheiro.model.Grupo;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {
	
	List<Grupo> findBySituacaoIgnoreCase(String situacao, Sort sort);
	

}
