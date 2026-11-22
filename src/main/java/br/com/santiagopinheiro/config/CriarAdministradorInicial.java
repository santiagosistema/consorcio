
package br.com.santiagopinheiro.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.santiagopinheiro.model.Usuario;
import br.com.santiagopinheiro.repository.UsuarioRepository;

@Component
public class CriarAdministradorInicial implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public CriarAdministradorInicial(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        // Só cria o administrador se não houver usuários.
        if (usuarioRepository.count() > 0) {
            return;
        }

        Usuario usuario = new Usuario();

        usuario.setNome("Administrador");
        usuario.setCpf("99999999999");
        usuario.setEmail("admin@consorciopinheiros.com.br");
        usuario.setSenha(passwordEncoder.encode("8877"));
        usuario.setSituacao(true);
        usuario.setAdministrador(true);

        usuarioRepository.save(usuario);

        System.out.println("Administrador inicial criado.");
    }
}
