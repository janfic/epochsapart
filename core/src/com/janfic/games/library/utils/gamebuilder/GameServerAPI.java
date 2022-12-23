package com.janfic.games.library.utils.gamebuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.Json;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

//TODO: Implement GameServerAPI
public class GameServerAPI {
    private static boolean isListening;
    private static GameServerAPI singleton;
    public Queue<GameMessage> messages;
    private Socket gameServerSocket;
    private StringBuilder builder;

    //TODO: Add Message Detection
    private GameClient gameClient;
    private Thread thread = new Thread(() -> {
        while (isListening) {
            try {
                if (gameServerSocket.getInputStream().available() > 0) {
                    byte[] bytes = new byte[1024];
                    int r = gameServerSocket.getInputStream().read(bytes);
                    String fragment = new String(bytes, 0, r);
                    builder.append(fragment);
                    int[] find = GameMessage.findEndOfMessage(0, builder.toString());
                    if (find != null) {
                        GameMessage message = GameMessage.parse(builder.substring(find[0], find[1]));
                        builder.delete(0, find[1]);
                        if (message != null) {
                            messages.add(message);
                            System.out.println("[CLIENT]: Received message: " + message.header + " " + message.message);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });

    private GameServerAPI() {
        builder = new StringBuilder();
        messages = new LinkedList<>();
    }

    public static GameServerAPI getSingleton() {
        if (singleton == null) singleton = new GameServerAPI();
        return singleton;
    }

    public static boolean isListening() {
        return isListening;
    }

    public static boolean toggleListening() {
        isListening = !isListening;
        return isListening;
    }

    public void connectToServer(String url, int port) {
        gameServerSocket = Gdx.net.newClientSocket(Net.Protocol.TCP, url, port, new SocketHints());
    }

    public void start() {
        thread.start();
        isListening = true;
    }

    public void sendMessage(GameMessage.GameMessageType type, String message) {
        sendMessage(new GameMessage(type, message, gameClient.getID(), 0));
    }

    public void sendMessage(GameMessage message) {
        System.out.println("[CLIENT]: Sending message: " + message.header);

        String data = new Json().toJson(message);

        byte[] bytes = data.getBytes();
        try {
            gameServerSocket.getOutputStream().write(bytes);
            gameServerSocket.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setClient(GameClient client) {
        gameClient = client;
    }

    public int getClientID() {
        return gameClient.getID();
    }

    public void waitForResponse() {
        synchronized (messages) {
            while(messages.isEmpty()) {
            }
        }
    }

    public void stopProcesses() {
        isListening = false;
        gameServerSocket.dispose();
    }

    public GameClient getGameClient() {
        return gameClient;
    }
}
