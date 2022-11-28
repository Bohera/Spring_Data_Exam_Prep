package com.example.football.util;

import java.nio.file.Path;

public enum Paths {
    ;

    public static final Path TOWNS_JSON_PATH =
            Path.of("src", "main", "resources", "files", "json", "towns.json");

    public static final Path TEAMS_JSON_PATH =
            Path.of("src", "main", "resources", "files", "json", "teams.json");

    public static final Path PLAYERS_XML_PATH =
            Path.of("src", "main", "resources", "files", "xml", "players.xml");

    public static final Path STATS_XML_PATH =
            Path.of("src", "main", "resources", "files", "xml", "stats.xml");

}
