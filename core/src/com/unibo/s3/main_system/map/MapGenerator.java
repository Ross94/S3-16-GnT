package com.unibo.s3.main_system.map;

import java.util.List;

public class MapGenerator {

    private GenerationStrategy strategy;

    public void generateMap(int width, int height) { strategy.generate(8, width/AbstractMapGenerator.BASE_UNIT, height/AbstractMapGenerator.BASE_UNIT, 0, 0); }/**todo eliminare primo parametro**/

    public List<String> getMap() { return strategy.getMap(); }

    public void setStrategy(GenerationStrategy strategy){ this.strategy = strategy; }

}
