package com.janfic.games.dddserver;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessNativesLoader;
import com.badlogic.gdx.backends.headless.HeadlessNet;
import com.janfic.games.library.utils.gamebuilder.GameClientAPI;
import com.janfic.games.library.utils.gamebuilder.GameServer;
import com.janfic.games.library.utils.gamebuilder.GameServerAPI;

public class DDDServerDriver {
    public static void main(String[] args) {
        Gdx.net = new HeadlessNet(new HeadlessApplicationConfiguration());
        GameServer server = new GameServer();
        server.startServer(7272);
    }
}
