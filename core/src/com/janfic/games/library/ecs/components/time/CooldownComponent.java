package com.janfic.games.library.ecs.components.time;

import com.badlogic.ashley.core.Component;

public class CooldownComponent implements Component {
    public float cooldownAmount;
    public float currentCooldown;
}
