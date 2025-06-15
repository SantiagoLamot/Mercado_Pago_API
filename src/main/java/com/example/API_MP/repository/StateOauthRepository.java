package com.example.API_MP.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.API_MP.entidades.StateOauth; 

public interface StateOauthRepository extends JpaRepository<StateOauth, Long>{
    Optional<StateOauth> findByState(String state);
}