package io.imulab.astrea.service.proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Component;

/**
 * Adapter for {@link SessionRepository} to circumvent the java-kotlin type interoperability issue imposed by
 * the design of {@link org.springframework.session.data.redis.RedisOperationsSessionRepository}.
 *
 * {@link org.springframework.session.data.redis.RedisOperationsSessionRepository.RedisSession} is implemented
 * as an inner final class which cannot be explicitly declared. Instead, session repository has to be declared
 * as a raw type. In kotlin, a raw type is declared using the {@code *} (star) projection, which infers the
 * {@code Nothing} type. However, this type inference prevents us from calling {@link SessionRepository#save(Session)}
 * as saving a {@code Nothing} type will never succeed.
 *
 * As a solution, we create a java adapter here to circumvent the issue using Java's raw type.
 */
@Component
public class SessionRepositoryAdapter {

    @Autowired
    private SessionRepository sessionRepository;

    @SuppressWarnings("unchecked")
    public void save(Session session) {
        sessionRepository.save(session);
    }

    @SuppressWarnings("unchecked")
    public Session createSession() {
        return sessionRepository.createSession();
    }

    public Session findById(String id) {
        return sessionRepository.findById(id);
    }

    public void deleteById(String id) {
        sessionRepository.deleteById(id);
    }
}
