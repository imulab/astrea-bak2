package io.imulab.astrea.test.func;

import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

public class ContainerFactory {

    public static GenericContainer createDiscovery(Config config, Logger logger) {
        GenericContainer container = new GenericContainer(config.getString("discovery.image"))
                .withExposedPorts(config.getInt("discovery.httpPort"))
                .waitingFor(Wait.forHttp("/health").forStatusCode(200).forStatusCode(204));
        container.start();
        container.followOutput(new Slf4jLogConsumer(logger));
        return container;
    }
}
