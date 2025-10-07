package com.devstoblu.banking_system.services;

import com.devstoblu.banking_system.enums.Status;
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

        if (usuarioRepository.existsByCpf((usuario.getCpf()))) {
            throw new IllegalArgumentException("CPF já cadastrado no sistema!");
        }

        if (usuarioRepository.existsByEmail((usuario.getEmail()))) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema!");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        return usuarioRepository.save(usuario);
    }

    public Usuario atualizarUsuario(Long id, Usuario dadosAtualizados){
        Usuario usuario = buscarPorId(id);

        if (usuarioRepository.existsByEmail(dadosAtualizados.getEmail()) && !dadosAtualizados.getEmail().equals(usuario.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema!");
        }

        usuario.setNomeCompleto(dadosAtualizados.getNomeCompleto());
        usuario.setEmail(dadosAtualizados.getEmail());
        usuario.setTelefone(dadosAtualizados.getTelefone());
        usuario.setEnderecoCompleto(dadosAtualizados.getEnderecoCompleto());
        usuario.setStatus(dadosAtualizados.getStatus());
        usuario.setUserRole(dadosAtualizados.getUserRole());

        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorId(Long id){
        return usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
    }

    public void inativarUsuario(Long id){
        Usuario usuario = buscarPorId(id);
        usuario.setStatus(Status.INATIVO);
        usuarioRepository.save(usuario);
    }

    public Usuario bloquearUsuario(Long id){
        Usuario usuario = buscarPorId(id);
        usuario.setStatus(Status.BLOQUEADO);
        return usuarioRepository.save(usuario);
    }

    public Usuario reativarUsuario(Long id){
        Usuario usuario = buscarPorId(id);

        if (usuario.getStatus() == Status.ATIVO) {
            throw new IllegalArgumentException("Usuario já está ativo!");
        }

        usuario.setStatus(Status.ATIVO);
        return usuarioRepository.save(usuario);
    }

    public boolean validarSenha(String senhaDigitada, String senhaHash){
        return passwordEncoder.matches(senhaDigitada, senhaHash);
    }


}
