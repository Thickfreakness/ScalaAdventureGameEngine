package scalaAVGameEngine.sWorld

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.actorRef2Scala
import akka.pattern.ask
import akka.util.Timeout
import akka.dispatch.OnSuccess
import scala.concurrent.ExecutionContext.Implicits.global
import akka.dispatch.OnSuccess

class Room(val id: String) extends Actor {
  var objects: Map[String, ActorRef] = Map()
  val dimensions = (214, 120, 10)
  implicit val time = Timeout(10)
  
  def walkable(dim: (Int, Int, Int)): Boolean = {
    if (dim._1 < 0 || dim._2 < 0 || dim._3 < 0 || dim._1 > dimensions._1 || dim._2 > dimensions._2 || dim._3 > dimensions._3)
      false
    else
      clash(dim, objects.values.toList)
  }

  def clash(point: (Int, Int, Int), obs: List[ActorRef]): Boolean = {
    obs match {
      case x :: xs => {
        //Check better implementation of futures
        var loc = (0,0,0)
        var size = (0,0,0)
        x ? WhereAreYou onSuccess {
          case (x:Int,y:Int,z:Int) => loc = (x,y,z)
        }
        x ? GetSize onSuccess {
          case (x:Int,y:Int,z:Int) => size = (x,y,z)
        }

        if ((point._1 < loc._1 || point._1 > loc._1 + size._1) && (point._2 < loc._2 || point._2 > loc._2 + size._2) &&
          (point._3 < loc._3 || point._3 > loc._3 + size._3)) clash(point, xs)
        else
          false
      }
      case List() => true
    }
  }

  def receive = {
    case AddObject(ref) =>objects = objects + (ref.path.name -> ref) 
    case RemoveItem(item) => objects = objects - item
    case SeeInventory => sender ! Inventory(objects.keys.toSet)
    case MoveObject(lab, d) => objects(lab) ! GoHere(d._1, d._2, d._3)
    case SendTo(lab,dest) => objects(lab) ! AssignRoute(sendTo(objects(lab),dest))
    case WhoAreYou => sender ! id
    case ActionHere(loc) => Unit//Add Implementation later
    case GetSize => sender ! dimensions
    case GetObjects => sender ! Objects((for(k <- objects.keys) yield objects(k) ).toSet) 
  }
  
  //Temp implementation for building, will replace with A* solution
  def sendTo(ob: ActorRef, dest: (Int,Int,Int)):List[(Int,Int,Int)] = {
    def getNext(curr: (Int,Int,Int), dest: (Int,Int,Int)): List[(Int,Int,Int)] = {
      if(curr == dest) List()
      else (curr._1 + 1, curr._2 + 1, curr._3) :: getNext((curr._1 + 1, curr._2 + 1, curr._3), dest)
    }
    var start = (0,0,0)
    ob ? WhereAreYou onSuccess {
      case (x:Int, y:Int, z:Int) => start = (x, y, z)
    }
    getNext(start,dest)
  }
}