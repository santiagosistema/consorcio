package br.com.santiagopinheiro.controller.web;

import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.santiagopinheiro.model.Usuario;
import br.com.santiagopinheiro.repository.UsuarioRepository;



@Controller
@RequestMapping({"/usuario","/user"})
public class UserController {
	
	private final UsuarioRepository usuarioRepository;

	
	public UserController(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}
	
    @GetMapping({"/",""})
    public String user(@AuthenticationPrincipal UserDetails usuarioLogado, Model model) {
    	//se não tiver logado
    	if(usuarioLogado == null) {
	    	Usuario usuario = new Usuario();
	    	model.addAttribute("usuario",usuario);
	        return "user";
    	}
    	
    	
    	// O nome de usuário no Spring Security é o CPF
    	String cpf = usuarioLogado.getUsername();
    	
    	Optional<Usuario> usuarioEncontrado = usuarioRepository.findByCpf(cpf);
    	
    	if(usuarioEncontrado.isPresent()) {
    		Usuario usuario = usuarioEncontrado.get();
    		
    		usuario.setCpf(formataCpf(usuario.getCpf()));
    		
    		usuario.setTelefone(formataTelefone(usuario.getTelefone()));
    		
    		model.addAttribute("usuario", usuario);
    		
    		return "user";
    	}
    	
    	return "redirect:/index";
    	
    }
	
	@GetMapping("/{id}")
	public String abrirUsuario(@PathVariable Long id, Model model) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		if (usuario.isPresent()) {
			Usuario user = usuario.get();
			user.setCpf(formataCpf(user.getCpf()));
			user.setTelefone(formataTelefone(user.getTelefone()));
			model.addAttribute("usuario",user);
			return "user";
		}else {
			return "redirect:/usuarios";
		}
	}
	
	
	
	public static String formataCpf(String cpf) {
		
		if(cpf == null || cpf.isBlank()) return cpf;
		
		cpf = cpf.replaceAll("\\D", "");
		
		if(cpf.length() < 11) return null;
		
		cpf = cpf.replaceAll("^(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
		
		return cpf;
	}
	
	
	public static String formataTelefone(String telefone) {
		
		if (telefone == null || telefone.isBlank()) return telefone;
		
		telefone = telefone.replaceAll("\\D", "");
		telefone = telefone.replaceAll("^(\\d{2})(\\d{5})(\\d{4})$", "($1) $2-$3");
		telefone = telefone.replaceAll("^(\\d{2})(\\d{4})(\\d{4})$", "($1) $2-$3");
		
		return telefone;
		
	}

}
