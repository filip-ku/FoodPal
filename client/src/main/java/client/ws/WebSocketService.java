package client.ws;

import commons.ws.IngredientListEvent;
import commons.ws.RecipeChangedEvent;
import commons.ws.RecipeContentChangedEvent;
import commons.ws.RecipeListEvent;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.net.URI;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Lightweight WebSocket/STOMP client for receiving server push events.
 */
public class WebSocketService {

    private static final String WS_ENDPOINT = "ws://localhost:8080/ws";

    private final WebSocketStompClient stompClient;
    private StompSession session;

    /**
     * Creates a STOMP client with Jackson payload conversion.
     */
    public WebSocketService() {
        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    /**
     * Lazily establishes a STOMP session if none is active.
     *
     * @throws RuntimeException when the connection cannot be established within the timeout
     */
    private synchronized void ensureConnected() {
        if (session != null && session.isConnected()) {
            return;
        }
        try {
            session = stompClient.connectAsync(
                    URI.create(WS_ENDPOINT),
                    new WebSocketHttpHeaders(),
                    new StompHeaders(),
                    new StompSessionHandlerAdapter() {
                    }
            ).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to WebSocket endpoint", e);
        }
    }

    /**
     * Subscribes to recipe list changes (add/delete) topic.
     *
     * @param handler callback invoked for each list change
     * @return the active subscription
     */
    public StompSession.Subscription subscribeRecipeList(Consumer<RecipeListEvent> handler) {
        ensureConnected();
        return session.subscribe("/topic/recipe-list",
                new TypedFrameHandler<>(RecipeListEvent.class, handler));
    }

    /**
     * Subscribes to recipe title changes topic.
     *
     * @param handler callback invoked for each title change
     * @return the active subscription
     */
    public StompSession.Subscription subscribeRecipeChanged(Consumer<RecipeChangedEvent> handler) {
        ensureConnected();
        return session.subscribe("/topic/recipe-changed",
                new TypedFrameHandler<>(RecipeChangedEvent.class, handler));
    }

    /**
     * Subscribes to content changes for a specific recipe (ingredients/steps).
     *
     * @param recipeId recipe identifier
     * @param handler  callback invoked for each content change
     * @return the active subscription
     */
    public StompSession.Subscription subscribeRecipeContent(
            Long recipeId,
            Consumer<RecipeContentChangedEvent> handler) {
        ensureConnected();
        String destination = "/topic/recipe-content/" + recipeId;
        return session.subscribe(destination,
                new TypedFrameHandler<>(RecipeContentChangedEvent.class, handler));
    }

    /**
     * Simple frame handler that maps payloads to a given type and passes them to a consumer.
     *
     * @param <T> payload type
     */
    private static class TypedFrameHandler<T> implements StompFrameHandler {
        private final Class<T> payloadType;
        private final Consumer<T> consumer;

        private TypedFrameHandler(Class<T> payloadType, Consumer<T> consumer) {
            this.payloadType = payloadType;
            this.consumer = consumer;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return payloadType;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handleFrame(StompHeaders headers, Object payload) {
            consumer.accept((T) payload);
        }
    }

    public StompSession.Subscription subscribeIngredientList(
            Consumer<IngredientListEvent> handler) {

        ensureConnected();
        return session.subscribe("/topic/ingredient-list",
                new TypedFrameHandler<>(IngredientListEvent.class, handler)
        );
    }
}

