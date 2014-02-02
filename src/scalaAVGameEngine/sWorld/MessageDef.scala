package scalaAVGameEngine.sWorld

import akka.actor.ActorRef

case class GoHere(x: Int, y: Int, z: Int)
case class WhereAreYou
case class GetSize
case class Size(size: (Int,Int,Int))
case class IAmHere(x: Int, y: Int, z: Int, label: String)
case class Action
case class Combine(label: String)
case class GetState
case class State(state: String)
case class SetAction(act: String)
case class Step
case class AssignRoute(route: List[(Int,Int,Int)])
case class AddItem(item: ActorRef)
case class RemoveItem(item: String)
case class SeeInventory
case class Inventory(items: Set[String])
case class MoveObject(label: String, dest: (Int,Int,Int))
case class GetObjects
case class Objects(obs: Set[ActorRef])
case class AddObject(ref: ActorRef)
case class WhoAreYou
case class SendTo(label: String, dest: (Int,Int,Int))
case class GetDesciption
case class Update(time: Float)
case class ActionHere(loc: (Int,Int,Int))
case class GetAt(x: Int, y: Int)
case class WhatType
case class IAmPlayer
case class IAmActor
case class IAmObject