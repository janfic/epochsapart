package com.janfic.games.library.utils.gamebuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Json;

import java.util.*;

/**
 * An outline for a game server using this model of a state game.
 */
public class GameServer {

    private static Json json = new Json();
    private static Date date = new Date();
    private List<GameRoomGroup> groups;
    private int maxRoomsPerGroup;
    private Queue<GameMessage> outgoingMessages;
    private List<GameClient> gameClients;
    private Map<GameMessage, GameRoom> destinations;
    private ServerSocket serverSocket;
    private boolean isListening;
    private Thread connectionListener = new Thread(() -> {
        while (isListening) {
            synchronized (gameClients) {
                Socket socket = listen(serverSocket);
                if (socket != null) {
                    System.out.println("ACCEPTED");
                    int id = (int) (Math.random() * Integer.MAX_VALUE);
                    while (GameClientAPI.getSingleton().clientList.contains(id) && id != 0) {
                        id = (int) (Math.random() * Integer.MAX_VALUE);
                    }
                    GameClientAPI.getSingleton().addConnection(id, socket);
                    this.gameClients.add(new GameClient(id));
                    GameClientAPI.getSingleton().sendMessage(id, new GameMessage(GameMessage.GameMessageType.CONNECTION_INFO, "" + id, 0, id));
                }
            }
        }
    });

    public GameServer() {
        this.groups = new ArrayList<>();
        this.maxRoomsPerGroup = 2;
        this.outgoingMessages = new LinkedList<>();
        this.gameClients = new ArrayList<>();
        this.destinations = new HashMap<>();
    }

    public static void log(String message) {
        System.out.println("[SERVER]: " + message);
    }

    public void addGameClient(GameClient client) {
        gameClients.add(client);
    }

    public void addGameRoom(GameRoom gameRoom) {
        // Find Available Game Room Group ( Thread )
        for (GameRoomGroup group : groups) {
            if (group.size() < maxRoomsPerGroup) {
                group.addGameRoom(gameRoom);
                return;
            }
        }

        // Create new group ( thread ) if all full
        GameRoomGroup group = new GameRoomGroup();
        group.addGameRoom(gameRoom);
        groups.add(group);
        new Thread(group).start();
        group.setRunning(true);
    }

    // TODO: Implement game server update()
    public void update(float delta) {
        // Read Incoming Messages from clients using API
        if (!GameClientAPI.getSingleton().messages.isEmpty()) {
            GameMessage message = GameClientAPI.getSingleton().messages.poll();
            System.out.println(message.header + " " + message.message);

            GameMessage response = new GameMessage(0, message.sender);
            switch (message.header) {
                case PING:
                    ping(message, response);
                    break;
                case REQUEST_CREATE_GAME:
                    createGameRoom(message, response);
                    break;
                case REQUEST_GAMES_INFO:
                    gamesInfoResponse(message, response);
                    break;
                case REQUEST_JOIN_ROOM:
                    joinGameRoom(message, response);
                    break;
                case REQUEST_ROOMS_INFO:
                    roomsInfo(message, response);
                    break;
                case REQUEST_LEAVE_GAME:
                    leaveGame(message, response);
                    break;
                case GAME_STATE_CHANGE:
                    gameStateChange(message, response);
            }
            if (response.header != null) {
                outgoingMessages.add(response);
            }
        }

        // Send any game state changes / messages to clients using API
        if (!outgoingMessages.isEmpty()) {
            GameMessage message = outgoingMessages.poll();
            if (message != null) {
                GameClientAPI.getSingleton().sendMessage(message.destination, message);
            }
        }
    }

    private void gameStateChange(GameMessage message, GameMessage response) {
        GameClient client = getClientByID(message.sender);


        if(client != null) {
            GameRoom room = getRoomByClient(client);
            if(room != null) {
                GameStateChange change = json.fromJson(GameStateChange.class, message.message);
                room.addGameStateChange(change);
            }
        }
    }

    private void roomsInfo(GameMessage message, GameMessage response) {
        List<GameRoom> rooms = new ArrayList<>();
        for (GameRoomGroup group : groups) {
            rooms.addAll(group.getGameRooms());
        }
        response.header = GameMessage.GameMessageType.ROOMS_INFO;
        response.message = json.toJson(rooms);
    }

    private void leaveGame(GameMessage message, GameMessage response) {
        GameClient client = getClientByID(message.sender);
        GameRoom room = null;
        response.setHeader(GameMessage.GameMessageType.DENIED);
        response.setMessage("Not in game room");
        if (client != null) {
            for (GameRoomGroup group : groups) {
                for (GameRoom gameRoom : group.getGameRooms()) {
                    if(gameRoom.getGameClients().contains(client)) {
                        room = gameRoom;
                    }
                }
            }
            if(room != null) {
                room.getGameClients().remove(client);
                response.setHeader(GameMessage.GameMessageType.ACCEPT);
                response.setMessage("Removed from room: " + room.getRoomID());
            }
        }
    }

    private void joinGameRoom(GameMessage message, GameMessage response) {
        String[] params = message.message.split(" ");
        GameClient client = getClientByID(message.sender);
        GameRoom room = null;
        response.setHeader(GameMessage.GameMessageType.DENIED);
        response.setMessage("No such room");
        if (client != null) {
            int roomID = Integer.parseInt(params[0]);
            for (GameRoomGroup group : groups) {
                for (GameRoom gameRoom : group.getGameRooms()) {
                    if (gameRoom.getGameClients().contains(client)) {
                        response.setHeader(GameMessage.GameMessageType.DENIED);
                        response.setMessage("Already in room");
                        return;
                    }
                    if (gameRoom.getRoomID() == roomID) {
                        room = gameRoom;
                    }
                }
            }
            if (room != null) {
                room.addClient(client);
                response.setHeader(GameMessage.GameMessageType.ACCEPT);
                response.setMessage("" + room.getRoomID());
            }
        }
    }

    private void gamesInfoResponse(GameMessage message, GameMessage response) {

    }

    private void ping(GameMessage message, GameMessage response) {
        response.setHeader(GameMessage.GameMessageType.ACCEPT);
        response.setMessage("pinged");
    }

    private void createGameRoom(GameMessage message, GameMessage response) {
        try {
            Class gameType = Class.forName(message.message);
            if (!Game.class.isAssignableFrom(gameType)) throw new Exception();
            log("Attempting to create room for " + gameType.getSimpleName() + " game...");
            Game game = (Game) gameType.getConstructor().newInstance();
            GameRoom chatRoom = new GameRoom(game, this, new ArrayList<>());
            addGameRoom(chatRoom);
            response.setHeader(GameMessage.GameMessageType.ACCEPT);
            response.setMessage("Created new GameRoom with " + gameType.getSimpleName());
        } catch (Exception e) {
            log("Failed Request to start " + message.message + " game. Invalid game name.");
            response.setHeader(GameMessage.GameMessageType.DENIED);
            response.setMessage("Invalid Game Name");
        }
    }

    private GameClient getClientByID(int id) {
        for (GameClient gameClient : gameClients) {
            if (gameClient.getID() == id) return gameClient;
        }
        return null;
    }

    private GameRoom getRoomByClient(GameClient client) {
        for (GameRoomGroup group : groups) {
            for (GameRoom gameRoom : group.getGameRooms()) {
                if (gameRoom.getGameClients().contains(client)) {
                    return gameRoom;
                }
            }
        }
        return null;
    }

    public void startServer(int port) {
        log("Starting server on port: " + port + "...");
        ServerSocketHints ssh = new ServerSocketHints();
        ssh.acceptTimeout = 5000;
        serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, port, ssh);
        GameClientAPI.getSingleton().startAPI();
        connectionListener.start();
        isListening = true;
    }

    public void stopServer() {
        isListening = false;
    }

    public void addOutgoingMessage(GameMessage message, GameRoom room) {
        this.outgoingMessages.add(message);
        this.destinations.put(message, room);
    }

    public Socket listen(ServerSocket ss) {
        try {
            Socket s = ss.accept(null);
            return s;
        } catch (Exception e) {
            return null;
        }
    }
}
