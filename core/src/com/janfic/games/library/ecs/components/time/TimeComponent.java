package com.janfic.games.library.ecs.components.time;

import com.badlogic.ashley.core.Component;

public class TimeComponent implements Component {
    public float hour, minute;
    public float minutesPerRealSecond;
}
