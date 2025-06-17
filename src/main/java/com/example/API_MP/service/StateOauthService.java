package com.example.API_MP.service;

import org.springframework.stereotype.Service;

import com.example.API_MP.entidades.StateOauth;
import com.example.API_MP.repository.StateOauthRepository;

@Service
public class StateOauthService {
    private final StateOauthRepository stateOauthRepository;

    public StateOauthService(StateOauthRepository stateOauthRepository) {
        this.stateOauthRepository = stateOauthRepository;
    }

    public void guardarStateOauth(Long idUsuarioLogueado, String state) {
        StateOauth entity = new StateOauth(idUsuarioLogueado, state);
        stateOauthRepository.save(entity);
    }

    public Long obtenerIdUsuarioPorState(String state) {
        return stateOauthRepository.findByState(state)
                .orElseThrow(() -> new RuntimeException("state no encontrado"))
                .getId();
    }
}
