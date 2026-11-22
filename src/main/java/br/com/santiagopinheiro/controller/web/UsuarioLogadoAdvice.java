
package br.com.santiagopinheiro.controller.web;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import br.com.santiagopinheiro.repository.UsuarioRepository;

/* O @ControllerAdvice executa esse método nas requisições que passam pelos 
 * controllers e disponibiliza o usuário no modelo com o 
 * nome usuarioLogado. Assim, o fragmento do menu poderá 
 * acessar os dados em várias páginas. */

@ControllerAdvice
public class UsuarioLogadoAdvice {

    private final UsuarioRepository usuarioRepository;

    public UsuarioLogadoAdvice(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute
    public void adicionarUsuarioLogado(
            Authentication authentication,
            Model model) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return;
        }

        String cpf = authentication.getName();

        usuarioRepository.findByCpf(cpf)
                .ifPresent(usuario ->
                    model.addAttribute("usuarioLogado", usuario)
                );
    }
}
