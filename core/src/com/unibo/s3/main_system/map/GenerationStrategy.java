package com.unibo.s3.main_system.map;

import java.util.List;

public interface GenerationStrategy {

    void generate(int n, int mapWidth, int mapHeight, int startX, int startY);

    List<String> getMap();
}
