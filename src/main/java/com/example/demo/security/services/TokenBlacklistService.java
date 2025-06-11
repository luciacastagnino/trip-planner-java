package com.example.demo.security.services;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public void blacklist(String token){
        blacklistedTokens.add(token);
    }

    public boolean isBlacklisted(String token){
        return blacklistedTokens.contains(token);
    }
}
