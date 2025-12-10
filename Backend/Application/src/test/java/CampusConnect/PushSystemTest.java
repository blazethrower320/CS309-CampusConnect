package CampusConnect;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PushSystemTest {

    @LocalServerPort
    int port;

    @Test
    void testWebSocketEndpoint() throws Exception {

        BlockingQueue<String> messages = new ArrayBlockingQueue<>(10);

        StandardWebSocketClient client = new StandardWebSocketClient();

        var session = client.doHandshake(
                new AbstractWebSocketHandler() {
                    @Override
                    public void handleTextMessage(org.springframework.web.socket.WebSocketSession session,
                                                  TextMessage message) {
                        messages.add(message.getPayload());
                    }
                },
                new WebSocketHttpHeaders(),
                URI.create("ws://localhost:" + port + "/push/2")
        ).get();

        // Send message
        session.sendMessage(new TextMessage("hello"));
        session.sendMessage(new TextMessage("hello"));

        session.sendMessage(new TextMessage("hello"));

        session.sendMessage(new TextMessage("hello"));


        // Wait up to 2 seconds for server response
        String response = messages.poll(2, java.util.concurrent.TimeUnit.SECONDS);

        // Assert response
        assertThat(response).isEqualTo("CamdenKlicker joined your study session: Development");

        // Close session
        session.close();
    }
}
