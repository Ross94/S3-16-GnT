package com.unibo.s3.main_system.communication

import akka.actor.{Props, UntypedAbstractActor}
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.unibo.s3.main_system.communication.Messages.{GraphGenerationMsg, MapMsg}


class GraphActor extends  UntypedAbstractActor {

  val FILEPATH = "outputGraphActor.txt" //ci va il percorso del file dove salvare la mappa(Sara)

  val file: FileHandle = Gdx.files.local(FILEPATH)
  file.writeString("", false)

  override def onReceive(message: Any): Unit = message match {
    case msg: MapMsg =>
      val verifyClose = msg.line.split(":").map(_.toFloat) //prova con un forAll
      def writeFunction(verifyClose: Array[Float]): Unit = verifyClose match {
        case _ if verifyClose(0) == 0.0 && verifyClose(1) == 0.0 && verifyClose(2) == 0.0 && verifyClose(3) == 0.0 =>
          getSelf().tell(GraphGenerationMsg(), getSender())
        case _ => file.writeString(msg.line + "\n", true)
      }
      writeFunction(verifyClose)
    case _: GraphGenerationMsg =>
      //qui ho il file con la mappa, bisogna generare il grafo(Sara)
      println("graph created!")
    case _ => println("(graph actor) message unknown: " + message)
  }
}

object GraphActor {
  def props() : Props = Props(new GraphActor())
}
