package com.janfic.games.epochsapart;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.janfic.games.epochsapart.client.EpochsApartDriver;
import com.janfic.games.library.JanFixelDriver;

public class DesktopLauncher {
    public static void main (String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setTitle("JanFixelsLibrary");
        config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode(Lwjgl3ApplicationConfiguration.getMonitors()[1]));
        config.setWindowSizeLimits(1000, 1000, 1000, 1000);
        config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL30, 3, 2);
        new Lwjgl3Application(new EpochsApartDriver(), config);
    }
}
