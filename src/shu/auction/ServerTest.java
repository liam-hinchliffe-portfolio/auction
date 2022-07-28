package shu.auction;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServerTest {
    private Integer serverPort = 8888;

    @Test
    void testServerSocketGetsCreated () throws IOException {
        assertNotNull(Server.createServerSocket(serverPort));
    }
    @Test
    public void testServerSocketWithSpecificPortGetsCreated() throws IOException {
        ServerSocket serverSocket = Server.createServerSocket(serverPort);
        assertEquals(serverSocket.getLocalPort(), serverPort);
        serverSocket.close();
    }

    @Test
    public void testClientSocketGetsCreated() throws IOException {
        ServerSocket mockServerSocket = mock(ServerSocket.class);
        when(mockServerSocket.accept()).thenReturn(new Socket());
        assertNotNull(Server.createClientSocket(mockServerSocket));
    }
}