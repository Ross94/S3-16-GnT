package com.unibo.s3.main_system.communication

import akka.actor.{ActorRef, Props, Stash, UntypedAbstractActor}
import com.badlogic.gdx.ai.utils.RaycastCollisionDetector
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.unibo.s3.main_system.characters.EntitiesSystemImpl
import com.unibo.s3.main_system.characters.steer.collisions.Box2dProxyDetectorsFactory
import com.unibo.s3.main_system.communication.Messages._

class MasterActor extends UntypedAbstractActor with Stash {

  private[this] var charactersList = List[ActorRef]()
  private[this] val entitiesSystem = new EntitiesSystemImpl()
  private[this] var collisionDetector: RaycastCollisionDetector[Vector2] = _
  private[this] var copID = 0

  override def onReceive(message: Any): Unit = message match {

    case msg: ActMsg =>
      SystemManager.getInstance().getLocalActor("worldActor").tell(msg, getSelf())
      SystemManager.getInstance().getLocalActor("quadTreeActor").tell(RebuildQuadTreeMsg(), getSelf())
      charactersList.foreach(cop => cop.tell(msg, getSelf()))
      //manca il ladro o i ladri

    case msg: CreateCharacterMsg =>
      copID = copID + 1
      val newCharacter = entitiesSystem.spawnEntityAt(msg.position, copID)
      newCharacter.setColor(Color.ORANGE)

      if (collisionDetector == null) {
        val worldActorRef = SystemManager.getInstance().getLocalActor("worldActor")
        collisionDetector = new Box2dProxyDetectorsFactory(worldActorRef).newRaycastCollisionDetector()
      }

      newCharacter.setCollisionDetector(collisionDetector)
      newCharacter
        .setComplexSteeringBehavior()
        .avoidCollisionsWithWorld()
        .wander()
        .buildPriority(true)

      val characterRef = SystemManager.getInstance().createActor(CharacterActor.props(newCharacter), "cop"+copID)
      charactersList :+= characterRef
      SystemManager.getInstance().getLocalActor("quadTreeActor")
        .tell(InitialSavingCharacterMsg(newCharacter, characterRef), getSelf())
      SystemManager.getInstance().getLocalActor("graphActor")
        .tell(InitialSavingCharacterMsg(newCharacter, characterRef), getSelf())

    case _ => println("(worldActor) message unknown: " + message)
  }
}

object MasterActor {
  def props(): Props = Props(new MasterActor())
}