package com.unibo.s3.main_system.communication;

import akka.actor.ActorRef;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.unibo.s3.main_system.world.actors.WorldActor;

import com.unibo.s3.main_system.communication.Messages.CreateCharacterMsg;
import com.unibo.s3.main_system.communication.Messages.ActMsg;
import com.unibo.s3.main_system.communication.Messages.GenerateMapMsg;

public class CommunicationTest extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture img;

    @Override
    public void create() {
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");

        SystemManager.getInstance().createSystem("System", null);
        SystemManager.getInstance().createActor(WorldActor.props(new World(new Vector2(0, 0), true)), "worldActor");
        SystemManager.getInstance().createActor(GraphActor.props(), "graphActor");
        ActorRef mapActor = SystemManager.getInstance().createActor(MapActor.props(), "mapActor");

        SystemManager.getInstance().createActor(QuadTreeActor.props(), "quadTreeActor");

        ActorRef masterActor = SystemManager.getInstance().createActor(MasterActor.props(), "masterActor");

        masterActor.tell(new CreateCharacterMsg(new Vector2(1,1)), ActorRef.noSender());
        masterActor.tell(new CreateCharacterMsg(new Vector2(2,2)), ActorRef.noSender());
        masterActor.tell(new CreateCharacterMsg(new Vector2(3,3)), ActorRef.noSender());
        masterActor.tell(new CreateCharacterMsg(new Vector2(24,34)), ActorRef.noSender());

        //mapActor.tell(new GenerateMapMsg(), ActorRef.noSender());

        masterActor.tell(new ActMsg(0.016f), ActorRef.noSender());

        /*
        ActorRef copOne = SystemManager.getInstance().createActor
                (CharacterActor.props(new BaseCharacter(new Vector2(1,1),1)), "cop1");
        ActorRef copTwo = SystemManager.getInstance()
                .createActor(CharacterActor.props(new BaseCharacter(new Vector2(2,2),2)), "cop2");
        ActorRef copThree = SystemManager.getInstance()
                .createActor(CharacterActor.props(new BaseCharacter(new Vector2(3,3),3)), "cop3");

        quadTree.tell(new Messages.AskNeighboursMsg(), copOne);
        quadTree.tell(new Messages.AskNeighboursMsg(), copTwo);
        quadTree.tell(new Messages.AskNeighboursMsg(), copThree);*/
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();
    }

    @Override
    public void dispose () {
        batch.dispose();
        img.dispose();
    }
}
