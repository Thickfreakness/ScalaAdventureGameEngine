package scalaAVGameEngine.sWorld

import scala.collection.immutable.HashMap
import akka.actor.ActorContext
import scalaAVGameEngine.world.Conversation
import scalaAVGameEngine.triggers.Trigger

class RoomActor(label: String,
  sizeX: Int, sizeY: Int, sizeZ: Int, trig: Trigger, onCombine: HashMap[String, Trigger],description: Map[String,String],
  actions: Set[String], convos: Map[String, Conversation]) extends RoomObject(
  label, sizeX, sizeY, sizeZ, trig, onCombine,description) {
  var route:List[(Int,Int,Int)] = List()
  var currentAction = ""

  override def receive = {
    case SetAction(a) => currentAction = a
    case Step => route match{
      case x :: xs => self ! GoHere(x._1,x._2,x._3) ; route = xs
      case List() => Unit
    }
    case AssignRoute(r) => route = r
    		
  }
}