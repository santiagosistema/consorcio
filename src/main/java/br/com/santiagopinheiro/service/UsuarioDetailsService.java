
package br.com.santiagopinheiro.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.santiagopinheiro.model.Usuario;
import br.com.santiagopinheiro.repository.UsuarioRepository;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String cpf)
            throws UsernameNotFoundException {

        String cpfLimpo = cpf.replaceAll("\\D", "");

        Usuario usuario = usuarioRepository.findByCpf(cpfLimpo)
                .orElseThrow(() ->
                    new UsernameNotFoundException("Usuário não encontrado."));

        if (!usuario.getSituacao()) {
            throw new UsernameNotFoundException("Usuário inativo.");
        }

        return User.withUsername(usuario.getCpf())
                .password(usuario.getSenha())
                .roles(usuario.getAdministrador() ? "ADMIN" : "USER")
                .build();
    }
}
