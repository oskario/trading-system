package com.oskario.ts.actors

import akka.actor.{ActorRef, Props, Actor}
import com.typesafe.scalalogging.slf4j.LazyLogging
import com.oskario.ts.models.{Position, OrderConfirmation, Order}
import scala.collection.mutable.ArrayBuffer
import com.oskario.ts.exceptions.InvalidConfirmationReceived

object User {
  def default: Props = Props(new User())

  object Protocol {
    class UserMsg
    case class Buy(market: ActorRef, order: Order) extends UserMsg
    case class Sell(market: ActorRef, order: Order) extends UserMsg
  }
}

class User(initialMoney: BigDecimal = 0) extends Actor with LazyLogging {

  var money = initialMoney

  val positions: ArrayBuffer[Position] = ArrayBuffer()

  def receive = {
    case User.Protocol.Buy(market, order) =>
      buy(market, order)
    case User.Protocol.Sell(market, order) =>
      sell(market, order)
    case Market.Protocol.BuyAccepted(confirmation) =>
      buyAccepted(confirmation)
    case Market.Protocol.SellAccepted(confirmation) =>
      sellAccepted(confirmation)
  }

  def buy(market: ActorRef, order: Order) = market ! Market.Protocol.Buy(order)

  def sell(market: ActorRef, order: Order) = market ! Market.Protocol.Sell(order)

  def buyAccepted(confirmation: OrderConfirmation) = {
    positions.find(_.instrument == confirmation.instrument) match {
      case Some(position) =>
        position.volume = position.volume + confirmation.volume

      case None =>
        positions += Position(confirmation.instrument, confirmation.volume)
    }
    money = money - confirmation.price * confirmation.volume
  }

  def sellAccepted(confirmation: OrderConfirmation) = {
    positions.find(_.instrument == confirmation.instrument) match {
      case Some(position) =>
        if (position.volume < confirmation.volume)
          throw new InvalidConfirmationReceived(confirmation, s"user has only ${position.volume} of ${confirmation.instrument}")
        else
          position.volume = position.volume - confirmation.volume

      case None =>
        throw new InvalidConfirmationReceived(confirmation, s"user does not have any of ${confirmation.instrument}")
    }
    money = money + confirmation.price * confirmation.volume
  }
}