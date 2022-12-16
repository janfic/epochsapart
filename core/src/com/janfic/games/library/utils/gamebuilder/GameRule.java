package com.janfic.games.library.utils.gamebuilder;

import java.util.function.BiFunction;

/**
 * The Game Rule Class defines a way to validate or invalidate specific state changes that occur. In other words,
 * they are simply rules of the game, as not every possible state change is valid in a typical game between players.
 *
 * A Game Rule includes the name and description of the rule, as well as a message as to why the game state change
 * cannot be applied.
 *
 * @param <T> the type of game state that this rule uses when looking at GameStateChanges.
 */
public class GameRule<T extends GameState> {
    private BiFunction<GameStateChange<T>, T, Boolean> stateChangeValidator;
    private final String ruleName, ruleDescription, invalidMessage;

    public GameRule(String ruleName, String ruleDescription, String invalidMessage, BiFunction<GameStateChange<T>, T, Boolean> stateChangeValidator) {
        this.ruleName = ruleName;
        this.ruleDescription = ruleDescription;
        this.stateChangeValidator = stateChangeValidator;
        this.invalidMessage = invalidMessage;
    }

    public boolean isStateChangeValid(GameStateChange<T> stateChange, T state) {
        return stateChangeValidator.apply(stateChange, state);
    }

    public String getInvalidMessage() {
        return invalidMessage;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public String getRuleName() {
        return ruleName;
    }
}
