package com.oskario.ts.actors

import akka.actor.{ActorRef, Props, Actor}
import com.oskario.ts.models.{OrderConfirmation, Instrument, Order}
import com.typesafe.scalalogging.slf4j.LazyLogging

object Market {
  def default(instruments: Seq[Instrument]): Props = Props(new Market(instruments))

  object Protocol {
    class MarketMsg
    case class Buy(order: Order) extends MarketMsg
    case class Sell(order: Order) extends MarketMsg
    case class BuyAccepted(confirmation: OrderConfirmation)
    case class SellAccepted(confirmation: OrderConfirmation)
  }
}

class Market(instruments: Seq[Instrument]) extends Actor with LazyLogging {
  import Market.Protocol._

  logger.debug(s"Market initialized")
  logger.debug(s"Instruments (${instruments.size}):")
  instruments.foreach(i => logger.debug(i.toString))

  def receive = {
    case Buy(order) =>
      buyPlaced(order, sender())
    case Sell(order) =>
      sellPlaced(order, sender())
  }

  def buyPlaced(order: Order, placer: ActorRef) = {
    logger.info(s"Received new buy order: $order from $placer")
    // Temporary accept all orders
    placer ! BuyAccepted(OrderConfirmation(order.instrument, order.price, order.volume))
  }

  def sellPlaced(order: Order, placer: ActorRef) = {
    logger.info(s"Received new sell order: $order from $placer")
    // Temporary accept all orders
    placer ! SellAccepted(OrderConfirmation(order.instrument, order.price, order.volume))
  }
}
