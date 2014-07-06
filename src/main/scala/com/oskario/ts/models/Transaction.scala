package com.oskario.ts.models

import akka.actor.ActorRef

case class Transaction(order: Order, placer: ActorRef, receiver: Option[ActorRef]) {
  def withdraw(value: BigDecimal): Transaction = {
    new Transaction(order.withdraw(value), placer, receiver)
  }
}