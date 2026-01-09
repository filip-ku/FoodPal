package client;

import com.google.inject.Binder;
import com.google.inject.Module;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.client.ClientConfig;

/**
 * Guice module responsible for configuring the Jersey HTTP {@link Client}.
 *
 * <p>The client is created once during application startup and
 * injected wherever needed. This centralises HTTP client configuration
 * and avoids manual instantiation throughout the codebase.</p>
 */
public class ClientModule implements Module {

    /**
     * Configures Guice bindings for HTTP communication.
     *
     * <p>Binds a single, shared {@link Client} instance that is reused
     * for all server requests.</p>
     *
     * @param binder the Guice binder used to register dependencies
     */
    @Override
    public void configure(Binder binder) {
        binder.bind(Client.class)
                .toInstance(ClientBuilder.newClient(new ClientConfig()));
    }
}
