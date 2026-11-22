package br.com.santiagopinheiro.controller.web;

import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import br.com.santiagopinheiro.model.Notificacao;
import br.com.santiagopinheiro.model.Usuario;
import br.com.santiagopinheiro.repository.UsuarioRepository;
import br.com.santiagopinheiro.service.NotificacaoService;

@ControllerAdvice
public class NotificacaoModelAdvice {

    private final NotificacaoService notificacaoService;
    private final UsuarioRepository usuarioRepository;

    public NotificacaoModelAdvice(
            NotificacaoService notificacaoService,
            UsuarioRepository usuarioRepository) {

        this.notificacaoService = notificacaoService;
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute
    public void carregarNotificacoes(
            Model model,
            Authentication authentication) {

        // Valores padrão
        model.addAttribute("notificacoes", Collections.emptyList());
        model.addAttribute("notificacoesNaoLidas", 0L);

        // Verifica se existe usuário autenticado
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return;
        }

        // Recupera o CPF do usuário autenticado
        String cpf = authentication.getName().replaceAll("\\D", "");

        // Busca o usuário no banco de dados
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElse(null);

        if (usuario == null || usuario.getId() == null) {
            return;
        }

        Long usuarioId = usuario.getId();

        // Busca as notificações
        List<Notificacao> notificacoes =
                notificacaoService.buscarNotificacoesDoUsuario(usuarioId);

        // Disponibiliza a lista para o Thymeleaf
        model.addAttribute("notificacoes", notificacoes);

        // Conta as notificações não lidas
        long naoLidas = notificacaoService.contarNaoLidas(usuarioId);

        model.addAttribute("notificacoesNaoLidas", naoLidas);
    }
}