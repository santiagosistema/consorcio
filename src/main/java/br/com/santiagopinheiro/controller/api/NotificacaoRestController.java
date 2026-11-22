package br.com.santiagopinheiro.controller.api;

import br.com.santiagopinheiro.repository.NotificacaoRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.santiagopinheiro.model.Notificacao;
import br.com.santiagopinheiro.model.Usuario;
import br.com.santiagopinheiro.repository.UsuarioRepository;
import br.com.santiagopinheiro.service.EmailsTodosService;
import br.com.santiagopinheiro.service.NotificacaoService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoRestController {

	private final NotificacaoRepository notificacaoRepository;
	private final NotificacaoService notificacaoService;
	private final UsuarioRepository usuarioRepository;
	private final EmailsTodosService email;

	public NotificacaoRestController(NotificacaoService notificacaoService, 
			UsuarioRepository usuarioRepository, NotificacaoRepository notificacaoRepository,
			EmailsTodosService email) {

		this.notificacaoService = notificacaoService;
		this.usuarioRepository = usuarioRepository;
		this.notificacaoRepository = notificacaoRepository;
		this.email = email;
	}

	private Usuario buscarUsuarioLogado(Authentication authentication) {

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalArgumentException("Usuário não autenticado.");
		}

		String cpf = authentication.getName().replaceAll("\\D", "");

		return usuarioRepository.findByCpf(cpf)
				.orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
	}

	// Marca uma notificação como lida.
	@PostMapping("/{id}/ler")
	public ResponseEntity<?> marcarComoLida(@PathVariable Long id, Authentication authentication) {

		Usuario usuario = buscarUsuarioLogado(authentication);

		try {
			notificacaoService.marcarComoLida(id, usuario.getId());

			// Retorna o destino associado à notificação.
			String destino = notificacaoService.buscarNotificacaoPorId(id).getUrlDestino();

			if (destino == null || destino.isBlank()) {
				destino = "/home";
			}

			// Evita redirecionamentos para endereços externos.
			if (!destino.startsWith("/") || destino.startsWith("//")) {
				destino = "/home";
			}

			return ResponseEntity.ok(Map.of("urlDestino", destino));

		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("erro", e.getMessage()));
		}
	}

	// Marca todas as notificações individuais como lidas.
	@PostMapping("/marcar-todas-lidas")
	public ResponseEntity<?> marcarTodasComoLidas(Authentication authentication) {

		Usuario usuario = buscarUsuarioLogado(authentication);

		int quantidade = notificacaoService.marcarTodasComoLidas(usuario.getId());

		return ResponseEntity.ok(Map.of("quantidade", quantidade));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(@PathVariable Long id, Authentication authentication) {

		if (authentication == null || !authentication.isAuthenticated()
				|| authentication instanceof AnonymousAuthenticationToken) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String cpf = authentication.getName().replaceAll("\\D", "");

		Usuario usuario = usuarioRepository.findByCpf(cpf)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

		try {
			notificacaoService.excluirNotificacaoDoUsuario(id, usuario.getId());

			return ResponseEntity.noContent().build();

		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/notificar")
	public ResponseEntity<String> notificar(
			Authentication authentication, 
			@RequestParam("mensagem") String mensagem,
			HttpServletRequest request) {
		
		//um parametro pode ser pego no corpo da função ou com @ na chamada da função
		String titulo = request.getParameter("titulo");
		
		// Recupera o CPF do usuário autenticado
		String cpf = authentication.getName().replaceAll("\\D", "");

		Usuario usuario = usuarioRepository.findByCpf(cpf)
				.orElseThrow(() -> new RuntimeException("Falha ao localizar usuario logado."));

		
		LocalDate dataAtual = LocalDate.now();
		
		List<Usuario> usuarios = usuarioRepository.findBySituacaoTrueOrderByNomeAsc();
		
		if (usuarios.isEmpty()) {
		    return ResponseEntity.ok("Não existem usuários ativos para notificar.");
		}
		
		List<Notificacao> notificacoes = new ArrayList<>();
		
		for(Usuario u : usuarios) {
			
			Notificacao notificacao = new Notificacao();
			
			notificacao.setDataCriacao(dataAtual);
			notificacao.setLida(false);
			notificacao.setMensagem(mensagem);
			notificacao.setTitulo(titulo);
			notificacao.setUserNotificador(usuario);
			notificacao.setUsuario(u);
			
			notificacoes.add(notificacao);
		}
		
		notificacaoRepository.saveAll(notificacoes);
		
		email.emailNovaNotificacao(usuarios);

		return ResponseEntity.ok("Notificação enviada com sucesso.");
	}

}
