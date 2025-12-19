package server.ws;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

//AI generated JAVADOC
/**
 * Spring WebSocket + STOMP configuration.
 *
 * <p>This config:
 * <ul>
 *   <li>Registers the STOMP handshake endpoint at {@code /ws}.</li>
 *   <li>Enables an in-memory (simple) message broker for {@code /topic/*} destinations.</li>
 *   <li>Sets {@code /app} as the prefix for application-level message mappings
 *       (e.g., {@code @MessageMapping}).</li>
 * </ul>
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //AI generate javadoc
    /**
    * Register STOMP endpoints that clients use to establish a WebSocket (or SockJS) connection.
    *
    * <p>Clients connect to {@code /ws}. Allowed origins are configured via
    * {@link org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration
     * #setAllowedOriginPatterns(String...)}.
    *
    * @param registry the STOMP endpoint registry
    */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }

    //AI generated javadoc
    /**
     * Configure the message broker used for routing messages between server and clients.
     *
     * <p>With this configuration:
     * <ul>
     *   <li>Server-to-client broadcasts should be sent to {@code /topic/*} destinations
     *       (subscribed to by clients).</li>
     *   <li>Client-to-server messages should be sent to {@code /app/*} destinations
     *       and handled by methods annotated with {@code @MessageMapping}.</li>
     * </ul>
     *
     * @param registry the message broker registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
