package advisor.auth;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.function.Consumer;

public class AuthServerBuilder {
    private final String CODE_NOT_FOUND_MSG = "Authorization code not found. Try again.";
    private final String AUTH_SUCCESS_MSG = "Got the code. Return back to your program.";
    private Optional<Consumer<String>> action = Optional.empty();

    public AuthServerBuilder setAction(Consumer<String> action) {
        this.action = Optional.of(action);
        return this;
    }

    public HttpServer build() {
        try {
            HttpServer server = HttpServer.create();
            InetSocketAddress address = new InetSocketAddress(8080);
            server.bind(address, 0);
            AuthInfo.authPort = server.getAddress().getPort();
            configureServerEndpoint(server);
            return server;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void configureServerEndpoint(HttpServer server) {
        server.createContext("/",
                exchange -> {
                    String query = exchange.getRequestURI().getQuery();
                    if (query != null && query.startsWith("code=")) {
                        exchange.sendResponseHeaders(200, AUTH_SUCCESS_MSG.length());
                        exchange.getResponseBody().write(AUTH_SUCCESS_MSG.getBytes());
                        exchange.getResponseBody().close();
                        action.ifPresent(act -> act.accept(query));
                    } else {
                        exchange.sendResponseHeaders(400, CODE_NOT_FOUND_MSG.length());
                        exchange.getResponseBody().write(CODE_NOT_FOUND_MSG.getBytes());
                        exchange.getResponseBody().close();
                    }
                }
        );
    }
}
