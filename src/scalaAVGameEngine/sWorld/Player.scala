package scalaAVGameEngine.sWorld

import scala.collection.immutable.HashMap
import akka.actor.actorRef2Scala
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scalaAVGameEngine.world.Conversation
import scalaAVGameEngine.triggers.Trigger

class Player(label: String,
  sizeX: Int, sizeY: Int, sizeZ: Int, trig: Trigger, onCombine: HashMap[String, Trigger],description: Map[String,String],
  actions: Set[String], convo: Map[String, Conversation]) extends RoomActor(label, sizeX, sizeY, sizeZ, trig, onCombine,description,actions,convo) {

  var inventory:Set[ActorRef] = Set()
  
  override def receive = {
    case AddItem(item) => inventory = inventory + item
    case RemoveItem(item) => inventory = for(i <- inventory ; 
    val f = ask(sender, WhoAreYou)(Timeout(1)) ;
    val name = Await.result(f,Duration(timeout)).asInstanceOf[String] ;
    if(name != item)) yield i
    case SeeInventory => sender ! inventory.toSet
  }

}