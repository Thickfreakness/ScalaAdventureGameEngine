package scalaAVGameEngine.starter

import scala.xml.NodeSeq
import scala.xml.XML

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import scalaAVGameEngine.sWorld.AddObject
import scalaAVGameEngine.sWorld.Player
import scalaAVGameEngine.sWorld.Room
import scalaAVGameEngine.sWorld.RoomActor
import scalaAVGameEngine.sWorld.RoomObject
import scalaAVGameEngine.sWorld.World

object ScWorldFactory{
  //val system = ActorSystem("World System")

  def createWorld(path: String, system: ActorSystem): World = {
    val config = XML.loadFile(path) // Add fail logic
    val rooms = List(createRoom(XML.loadFile((config \ "Room").text),system)) // requires fixing
    val player = createRoomActor(config \ "Player", true,system)
    new World(rooms, player)
  }

  def createRoom(info: NodeSeq, system: ActorSystem): ActorRef = {
    val actors = (info \ "Actor").map(a => createRoomActor(a, false,system))
    val objects = (info \ "Object").map(o => createRoomObj(XML.loadFile(o.text),system))

    val walkable = (info \ "walkableArea").text.split(",")
    val walkStart = (info \ "walkableStart").text.split(",")
    val roomName = (info \ "roomName").text
    val vpoint = (info \ "v_point").text

    val room = system.actorOf(Props(new Room(roomName)),roomName)
    for (obj <- objects) room ! AddObject(obj)
    for (act <- actors) room ! AddObject(act)

    room
  }

  //Add on combine + triggers
  def createRoomObj(info: NodeSeq, system: ActorSystem): ActorRef = {
    val id = (info \ "id").text
    val size = (info \ "size").text.split(",")
    val descriptions = (info \ "state").map { n =>
      ((n \ "stateId").text, (n \ "description").text)
    }

    system.actorOf(Props(new RoomObject(id, size(0).toInt, size(1).toInt, size(1).toInt, null, null, descriptions.toMap[String, String])), id)
  }

  def createRoomActor(info: NodeSeq, isPlayer: Boolean, system: ActorSystem): ActorRef = {
    val id = (info \ "id").text
    val descriptions = (info \ "state").map { n =>
      ((n \ "stateId").text, (n \ "description").text)
    }
    val size = (info \ "size").text.split(",")

    if (isPlayer)
      system.actorOf(Props(new RoomActor(id, size(0).toInt, size(1).toInt, size(2).toInt, null, null, descriptions.toMap[String, String], null, null)),id)
    else
      system.actorOf(Props(new Player(id, size(0).toInt, size(1).toInt, size(2).toInt, null, null, descriptions.toMap[String, String], null, null)),id)
  }

}