package com.starbank.recommendation_service.service;

import com.starbank.recommendation_service.model.UserH2;
import com.starbank.recommendation_service.repository.UserH2Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserH2Service {

    private final UserH2Repository userH2Repository;

    public UserH2Service(UserH2Repository userH2Repository) {
        this.userH2Repository = userH2Repository;
    }

    public Optional<UserH2> findByUsername(String username) {
        return userH2Repository.findByUsername(username);
    }

    public Optional<UserH2> findByUserId(String userId) {
        return userH2Repository.findByUserId(userId);
    }
}