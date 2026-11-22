package br.com.santiagopinheiro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.santiagopinheiro.model.Notificacao;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    // Busca notificações individuais.
    List<Notificacao> findByUsuario_IdOrderByDataCriacaoDesc(
            Long usuarioId);

    // Busca notificações individuais não lidas.
    List<Notificacao> findByUsuario_IdAndLidaFalseOrderByDataCriacaoDesc(
            Long usuarioId);

    // Verifica se já existe uma notificação para determinada parcela e usuário.
    boolean existsByParcela_IdAndUsuario_Id(
            Long parcelaId, Long usuarioId);

    // Conta notificações individuais não lidas.
    long countByUsuario_IdAndLidaFalse(Long usuarioId);
    
   // Marca todas as notificações individuais do usuário como lidas.
    @Modifying
    @Query("""
        UPDATE Notificacao n
        SET n.lida = true
        WHERE n.usuario.id = :usuarioId
          AND n.lida = false
    """)
    int marcarTodasComoLidas(@Param("usuarioId") Long usuarioId);
    
    
    Optional<Notificacao> findByIdAndUsuario_Id(
    	    Long id,
    	    Long usuarioId
    	);
    
    
}