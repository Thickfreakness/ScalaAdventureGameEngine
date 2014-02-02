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
    (for (
      r <- rooms;
      val f = ask(r, WhoAreYou)(Timeout(1));
      val id = Await.result(f, Duration("1")).asInstanceOf[String];
      if (id == activeRoom)
    ) yield r).head //Bit of a hack
  }
  def update(time: Float) = {
    for (r <- rooms) r ! Update(time)
  }
}