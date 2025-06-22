package com.example.API_MP.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.API_MP.entidades.Usuarios;
import com.example.API_MP.repository.UsuariosRepository;

@Service
public class UsuariosService {
    
    private final UsuariosRepository usuariosRepository;
    private final StateOauthService stateOauthService;
    
    public UsuariosService(UsuariosRepository usuariosRepository, StateOauthService stateOauthService){
        this.usuariosRepository=usuariosRepository;
        this.stateOauthService = stateOauthService;
    }

    public Optional<Usuarios> obtenerUsuariosPorId(Long id){
        return usuariosRepository.findById(id);
    }

    public Usuarios obtenerUsuarioPorState(String state){
        Long id = stateOauthService.obtenerIdUsuarioPorState(state);
        return usuariosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("usuario no encontrado"));
    }

}
