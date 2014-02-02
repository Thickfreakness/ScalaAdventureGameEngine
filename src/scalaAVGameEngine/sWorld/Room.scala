package scalaAVGameEngine.sWorld

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.actorRef2Scala
import akka.pattern.ask
import akka.util.Timeout

class Room(val id: String) extends Actor {
  var objects: Map[String, ActorRef] = Map()
  val dimensions = (214, 120, 10)
  val system = ActorSystem("World System")
  val timeout = "1"
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
        val f1 = ask(x, WhereAreYou)(Timeout(1))
        val f2 = ask(x, GetSize)(Timeout(1))

        //Rewrite to remove asinstanceof method
        val loc = Await.result(f1, Duration(timeout)).asInstanceOf[(Int, Int, Int)]
        val size = Await.result(f2, Duration(timeout)).asInstanceOf[(Int, Int, Int)]
        		 
        if ((point._1 < loc._1 || point._1 > loc._1 + size._1) && (point._2 < loc._2 || point._2 > loc._2 + size._2) &&
          (point._3 < loc._3 || point._3 > loc._3 + size._3)) clash(point, xs)
        else
          false
      }
      case List() => true
    }
  }

  def receive = {
    case AddObject(ref) =>{
      val f = ask(ref, WhoAreYou)(Timeout(1))
      val name = Await.result(f,Duration(timeout)).asInstanceOf[String]
      objects = objects + (name -> ref)
    }
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
    val fst = ask(ob,WhereAreYou)(Timeout(1))
    val start = Await.result(fst,Duration(timeout)).asInstanceOf[(Int,Int,Int)]
    
    getNext(start,dest)
  }
}