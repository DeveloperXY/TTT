package com.spartech.ttt.socketio;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.spartech.ttt.gameutils.Constants;

import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Mohammed Aouf ZOUAG on 04/04/2016.
 */
public class TTTApplicationTest {
    private Socket socket;

    @Before
    public void setUp() throws Exception {
        socket = IO.socket(Constants.SERVER_IP_ADDRESS);
    }

    @Test
    public void CreateNonNullSocketSuccessfully() throws URISyntaxException {
        assertNotNull(socket);
    }

    @Test
    public void ConnectSocketToServerSuccessful() throws Exception {
        socket.connect();
    }
}