package com.janfic.games.library.utils.gamebuilder;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Json;

import java.io.IOException;
import java.util.*;

public class GameClientAPI {
    private static GameClientAPI singleton;
    public final List<Integer> clientList;
    public final List<Socket> socketList;
    public final Map<Integer, Socket> connections;
    public final Map<Integer, StringBuilder> socketReads;
    public Queue<GameMessage> messages;
    private Thread clientMessageListener;
    private boolean isListening;

    private GameClientAPI() {
        clientList = new ArrayList<>();
        socketList = new ArrayList<>();
        connections = new HashMap<>();
        socketReads = new HashMap<>();
        messages = new LinkedList<>();
        isListening = false;
    }

    public static GameClientAPI getSingleton() {
        if (singleton == null) singleton = new GameClientAPI();
        return singleton;
    }

    public void addConnection(Integer clientID, Socket socket) {
        clientList.add(clientID);
        socketList.add(socket);
        connections.put(clientID, socket);
        socketReads.put(clientID, new StringBuilder());
    }

    public void startAPI() {
        isListening = true;
        clientMessageListener = new Thread(() -> {
            while (isListening) {
                synchronized (clientList) {
                    for (Integer gameClient : clientList) {
                        try {
                            Socket socket = connections.get(gameClient);
                            if (socket.getInputStream().available() > 0) {
                                byte[] bytes = new byte[1024];
                                int r = socket.getInputStream().read(bytes);
                                String partialMessage = new String(bytes, 0, r);
                                socketReads.get(gameClient).append(partialMessage);
                                int[] find = GameMessage.findEndOfMessage(0, socketReads.get(gameClient).toString());
                                if (find != null) {
                                    GameMessage message = GameMessage.parse(socketReads.get(gameClient).substring(find[0], find[1]));
                                    socketReads.get(gameClient).delete(0, find[1]);
                                    if (message != null) {
                                        messages.add(message);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        clientMessageListener.start();
    }

    public boolean isListening() {
        return isListening;
    }

    public boolean toggleListening() {
        isListening = !isListening;
        return isListening;
    }

    public void sendMessage(int clientID, GameMessage message) {
        String data = new Json().toJson(message);
        Socket socket = connections.get(clientID);

        byte[] bytes = data.getBytes();
        try {
            socket.getOutputStream().write(bytes);
            socket.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        isListening = false;
        for (Integer integer : connections.keySet()) {
            Socket s = connections.get(integer);
            s.dispose();
        }
        clientList.clear();
        socketList.clear();
        singleton = null;
    }
}
