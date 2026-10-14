package com.crm.BackendCrm.service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service 
public class OathStateService {
    private record StateDoc(Long userId, Long expiresAt) {}

    private final ConcurrentHashMap<String, StateDoc> store = new ConcurrentHashMap<>();
    private static final long TTL_MS = 10 * 60 * 1000;

    public String createAndSaveStateWithUserId(Long userId){
        String state = UUID.randomUUID().toString();
        StateDoc stateDoc = new StateDoc(userId, System.currentTimeMillis() + TTL_MS);
        store.put(state, stateDoc);
        return state;
    }

    public Long getUserIdFromState(String state){
        if(state == null) 
            return null;
        StateDoc sd = store.remove(state);
        if(sd == null || sd.expiresAt() < System.currentTimeMillis())
            return null;
        return sd.userId();
    }
}
