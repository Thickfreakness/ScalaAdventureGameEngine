package scalaAVGameEngine.sWorld

import scala.collection.immutable.HashMap
import akka.actor.Actor
import akka.actor.actorRef2Scala
import scala.concurrent.Awaitable
import scalaAVGameEngine.triggers.Trigger

class RoomObject(val label: String,
    val sizeX: Int, val sizeY: Int, val sizeZ: Int, trig: Trigger, onCombine: Map[String, Trigger],description: Map[String,String]) extends Actor{
	var x = 0
	var y = 0
	var z = 0
	var state = ""
     val timeout = "1"
  override def receive = {
    case GoHere(xs,ys,zs) => {
      x = xs
      y = ys
      z = zs
    }
    case WhereAreYou => sender ! IAmHere(x,y,z,label)
    case Action => trig.triggerEvent()
    case Combine(label) => for(s <- onCombine.keys ; if(s == label) ) onCombine(s).triggerEvent()
    case State(s) => state = s
    case GetState => sender ! state
    case GetSize => sender ! (sizeX,sizeY,sizeZ)
    case WhoAreYou => sender ! label
    case GetDesciption => sender ! description(state)
  }
}