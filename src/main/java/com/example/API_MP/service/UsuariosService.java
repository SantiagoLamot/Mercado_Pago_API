package com.example.API_MP.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.API_MP.entidades.Usuarios;
import com.example.API_MP.repository.UsuariosRepository;

@Service
public class UsuariosService {
    private final UsuariosRepository usuariosRepository;
    
    public UsuariosService(UsuariosRepository usuariosRepository){
        this.usuariosRepository=usuariosRepository;
    }

    public Optional<Usuarios> obtenerUsuariosPorId(Long id){
        return usuariosRepository.findById(id);
    }

}
