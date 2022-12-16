package com.janfic.games.library.utils.gamebuilder;

import com.badlogic.gdx.utils.Json;

import java.util.*;
import java.util.function.Function;

/**
 * The Game class acts as the center of all game related logic. This logic and data includes a GameState object to store
 * the current state of the game, a queue of potential GameStateChanges to be applied to this GameState,
 * and a list of GameRules to approve or deny these queued changes.
 *
 * All logic involving messages between clients, threading, and game "rooms" are dedicated to the GameServer class, a
 * class that holds this one as a member.
 *
 * How the game handles its priority queue is up to implementing classes, like TurnBasedGame and RealTimeGame. This is
 * done in the update() method.
 *
 * @param <T> the type of GameState held in this class, affects the valid game rules and changes that are stored in
 *           other members.
 */
public abstract class Game<T extends GameState> {

    /**
     * The current game state.
     */
    protected T gameState;

    /**
     * The game rules, validates queued state changes.
     */
    protected List<GameRule<T>> rules;

    /**
     * Priority Queue of game changes prioritizing time of request via timestamp.
     */
    protected PriorityQueue<GameStateChange<T>> queuedStateChanges;

    /**
     * Queue of processed game state changes to be passed to the server to send to clients. Includes rejected or
     * invalid state changes.
     */
    protected Queue<GameStateChange<T>> processedStateChanges;

    public Game(T gameState) {
        this.gameState = gameState;
        this.rules = new ArrayList<>();
        queuedStateChanges = new PriorityQueue<>(new GameStateChange.GameStateChangeComparator());
        processedStateChanges = new LinkedList<>();
    }

    public void setGameState(T gameState) {
        this.gameState = gameState;
    }
    public abstract void setup();

    public abstract void update(float delta);

    public void addRule(GameRule<T> rule) {
        rules.add(rule);
    }

    /* Getters and Setters */

    public List<GameRule<T>> getRules() {
        return rules;
    }

    public boolean validateState() {
        return false;
    }

    // Private for now
    private T getGameState() {
        return gameState;
    }

    public PriorityQueue<GameStateChange<T>> getQueuedStateChanges() {
        return queuedStateChanges;
    }

    public Queue<GameStateChange<T>> getProcessedStateChanges() {
        return processedStateChanges;
    }
}
