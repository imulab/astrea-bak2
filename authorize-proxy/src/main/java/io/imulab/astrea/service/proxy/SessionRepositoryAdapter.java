package io.imulab.astrea.service.proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Component;

@Component
public class SessionRepositoryAdapter {

    @Autowired
    private SessionRepository sessionRepository;

    @SuppressWarnings("unchecked")
    public void save(Session session) {
        sessionRepository.save(session);
    }
}
