package br.com.santiagopinheiro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.santiagopinheiro.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	// método que retorna usuário por parte do nome
	@Query(value = "select u from Usuario u where upper(trim(u.nome)) like %?1%")
	List<Usuario> buscarPorNome(String nome);

	// método que checa o cpf
	boolean existsByCpf(String cpf);

	// busca usuários ativos
	List<Usuario> findBySituacaoTrueOrderByNomeAsc();
	

	Optional<Usuario> findByCpf(String cpf);

	@Query("SELECT u FROM Usuario u WHERE u.cpf <> :cpfExcluido")
	Page<Usuario> listarUsuariosSemPadrao(
	    @Param("cpfExcluido") String cpfExcluido,
	    Pageable pageable
	);
}
