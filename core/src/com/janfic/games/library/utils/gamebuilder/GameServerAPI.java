package com.janfic.games.library.utils.gamebuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessNet;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.Json;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.Queue;

//TODO: Implement GameServerAPI
public class GameServerAPI {
    private  Socket gameServerSocket;
    public  Queue<GameMessage> messages;
    private  StringBuilder builder;

    private GameClient gameClient;

    private static boolean isListening;

    //TODO: Add Message Detection

    private  Thread thread = new Thread(()->{
        while(isListening) {
            try {
                if(gameServerSocket.getInputStream().available() > 0) {
                    byte[] bytes = new byte[1024];
                    int r = gameServerSocket.getInputStream().read(bytes);
                    String fragment = new String(bytes, 0 , r);
                    builder.append(fragment);
                    int[] find = GameMessage.findEndOfMessage(0, builder.toString());
                    if(find != null) {
                        GameMessage message = GameMessage.parse(builder.substring(find[0], find[1]));
                        builder.delete(0, find[1]);
                        if(message != null)
                            messages.add(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });

    private static GameServerAPI singleton;

    private GameServerAPI() {
        builder = new StringBuilder();
        messages = new LinkedList<>();
    }

    public static GameServerAPI getSingleton() {
        if(singleton == null) singleton = new GameServerAPI();
        return singleton;
    }

    public void connectToServer(String url, int port) {
        gameServerSocket = Gdx.net.newClientSocket(Net.Protocol.TCP, url, port, new SocketHints());
    }

    public static boolean isListening() {
        return isListening;
    }

    public void start() {
        thread.start();
        isListening = true;
    }

    public static boolean toggleListening() {
        isListening = !isListening;
        return isListening;
    }

    public void sendMessage(GameMessage message) {
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
}
