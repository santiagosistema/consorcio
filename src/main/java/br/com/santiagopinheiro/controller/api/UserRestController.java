package br.com.santiagopinheiro.controller.api;

import br.com.santiagopinheiro.service.EmailsTodosService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.santiagopinheiro.dto.UsuarioDTO;
import br.com.santiagopinheiro.dto.UsuarioOpcaoDTO;
import br.com.santiagopinheiro.model.Usuario;
import br.com.santiagopinheiro.repository.UsuarioRepository;
import br.com.santiagopinheiro.validation.CPFValidator;

@RestController
@RequestMapping("/api/usuario")
public class UserRestController {
	

	private EmailsTodosService email;

	private final UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public UserRestController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, EmailsTodosService email) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
		this.email = email;
	}

	@GetMapping("/listar")
	public ResponseEntity<Page<UsuarioDTO>> listarUsuarios(

			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		// Evita páginas negativas e tamanhos inválidos ou exagerados
		if (page < 0 || size < 1 || size > 100) {
			return ResponseEntity.badRequest().build();
		}

		Pageable pageable = PageRequest.of(
			    page,
			    size,
			    Sort.by("nome").ascending()
			);


		// Lista os usuários, excluindo o administrador padrão,
		// e converte cada Usuario em UsuarioDTO.
		Page<UsuarioDTO> resultado =
		    usuarioRepository
		        .listarUsuariosSemPadrao("99999999999", pageable)
		        .map(usuario -> new UsuarioDTO(
		            usuario.getId(),
		            usuario.getNome(),
		            formatarCpf(usuario.getCpf()),
		            usuario.getEmail(),
		            usuario.getSituacao(),
		            usuario.getAdministrador()
		        ));

		return ResponseEntity.ok(resultado);

	}

	
	@PostMapping("/salvar")
	public ResponseEntity<?> salvar(@RequestBody Usuario usuario) {

		// 1. Validar e limpar CPF
		if (usuario.getCpf() == null || usuario.getCpf().isBlank()) {
			return ResponseEntity.badRequest().body("CPF não preenchido.");
		}

		String cpfLimpo = usuario.getCpf().replaceAll("\\D", "");

		if (!CPFValidator.validaCPF(cpfLimpo)) {
			return ResponseEntity.badRequest().body("CPF inválido. Tente novamente.");
		}

		usuario.setCpf(cpfLimpo);
		
		if(usuario.getTelefone() != null) usuario.setTelefone(usuario.getTelefone().replaceAll("\\D", ""));

		// 2. Validar nome
		if (usuario.getNome() == null || usuario.getNome().isBlank()) {
			return ResponseEntity.badRequest().body("Nome não preenchido.");
		}

		String nomeLimpo = usuario.getNome().trim().replaceAll("\\s+", " ");

		usuario.setNome(nomeLimpo);

		// 3. Verificar se é uma atualização
		boolean atualizacao = usuario.getId() != null && usuario.getId() != 0;

		Usuario existente = null;

		if (atualizacao) {
			existente = usuarioRepository.findById(usuario.getId()).orElse(null);

			if (existente == null) {
				return ResponseEntity.notFound().build();
			}

			// Preservar senha se não foi informada.
			if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {

				String senhaAtual = existente.getSenha();

				// Converte senha antiga em texto normal para BCrypt.
				if (!senhaAtual.startsWith("$2a$") && !senhaAtual.startsWith("$2b$")
						&& !senhaAtual.startsWith("$2y$")) {

					existente.setSenha(passwordEncoder.encode(senhaAtual));
				}

				usuario.setSenha(existente.getSenha());

			} else {
				// Nova senha informada na edição.
				String senha = usuario.getSenha().trim();

				if (!senha.matches("\\d{4,}")) {
					return ResponseEntity.status(HttpStatus.CONFLICT)
							.body("A senha deve conter somente números. Mínimo 4 números.");
				}

				usuario.setSenha(passwordEncoder.encode(senha));
			}
			

			// Atualizar os campos da entidade existente.
			existente.setNome(usuario.getNome());
			existente.setCpf(usuario.getCpf());
			existente.setEmail(usuario.getEmail());
			existente.setSenha(usuario.getSenha());
			existente.setSituacao(usuario.getSituacao());
			existente.setAdministrador(usuario.getAdministrador());
			existente.setTelefone(usuario.getTelefone());

			// Preservar a data de cadastro original.
			
			// SALVA USUARIO JÁ EXISTENTE
			Usuario salvo = usuarioRepository.save(existente);
			
			email.emailCadastroAtualizado(usuario.getEmail());

			return ResponseEntity.ok(respostaSemSenha(salvo));
		}

		
		// 4. Cadastro de usuário novo
		if (usuarioRepository.existsByCpf(usuario.getCpf())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("CPF já cadastrado na base.");
		}

		if (usuario.getSenha() == null || !usuario.getSenha().matches("\\d{4,}")) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("A senha deve conter somente números. Mínimo 4 números.");
		}

		// Gerar hash BCrypt antes de salvar.
		usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

		// 5. Data de cadastro
		if (usuario.getDataCadastro() == null) {
			usuario.setDataCadastro(LocalDate.now());
		}

		Usuario salvo = usuarioRepository.save(usuario);
		
		email.emailCadastroNovo(usuario.getEmail());

		return ResponseEntity.status(HttpStatus.CREATED).body(respostaSemSenha(salvo));
	}

// Retornar os dados necessários para a interface,
// sem expor a senha ou seu hash.
	private Map<String, Object> respostaSemSenha(Usuario usuario) {

		Map<String, Object> resposta = new HashMap<>();

		resposta.put("id", usuario.getId());
		resposta.put("nome", usuario.getNome());
		resposta.put("cpf", usuario.getCpf());
		resposta.put("email", usuario.getEmail());
		resposta.put("situacao", usuario.getSituacao());
		resposta.put("administrador", usuario.getAdministrador());
		String telefone = usuario.getTelefone();
		if(telefone != null) {
			telefone = telefone.replaceAll("^(\\d{2})(\\d{5})(\\d{4})$", "($1)$2-$3");
			telefone = telefone.replaceAll("^(\\d{2})(\\d{4})(\\d{4})$", "($1) $2-$3");
		}
		resposta.put("telefone", telefone);

		return resposta;
	}//salvar
	
	

	@GetMapping("/ativos")
	public List<UsuarioOpcaoDTO> listarAtivos() {

		return usuarioRepository.findBySituacaoTrueOrderByNomeAsc().stream()
				.map(usuario -> new UsuarioOpcaoDTO(usuario.getId(), usuario.getNome())).toList();
	}// listarAtivos

	public static String formatarCpf(String cpf) {

		if (cpf == null || cpf.isBlank()) {
			return cpf;
		}

		cpf = cpf.replaceAll("\\D", "");

		if (cpf.length() != 11) {
			return cpf;
		}

		cpf = cpf.replaceAll("^(\\d{3})(\\d{3})(\\d{3})(\\d{2})$", "$1.$2.$3-$4");

		return cpf;
	}// FORMATARCPF
}
