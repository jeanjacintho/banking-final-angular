package com.devstoblu.banking_system.services;

import com.devstoblu.banking_system.models.Usuario;
import com.devstoblu.banking_system.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> buscarTodos(){
        return usuarioRepository.findAll();
    }


    public Usuario criarUsuario(Usuario usuario){
        String senhaHash = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaHash);

        if (usuarioRepository.existsByCpf((usuario.getCpf()))) {
            throw new IllegalArgumentException("CPF já cadastrado no sistema!");
        }

        if (usuarioRepository.existsByEmail((usuario.getEmail()))) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema!");
        }

        return usuarioRepository.save(usuario);
    }

    public boolean validarSenha(String senhaDigitada, String SenhaHash){
        return passwordEncoder.matches(senhaDigitada, SenhaHash);
    }


}
