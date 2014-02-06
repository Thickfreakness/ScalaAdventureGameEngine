package scalaAVGameEngine.sWorld

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class World(rooms: List[ActorRef], val player: ActorRef) {
  var activeRoom = ""

  def setActiveRoom(room: String) = activeRoom = room

  def getActiveRoom: ActorRef = {
    (for(r <- rooms ; if(r.path.name == activeRoom)) yield r).head
  }
  def update(time: Float) = {
    for (r <- rooms) r ! Update(time)
  }
}