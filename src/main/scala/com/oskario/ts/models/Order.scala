package com.oskario.ts.models

case class Order(instrument: String, price: BigDecimal, volume: BigDecimal) extends OrderLike(price, volume) {
  def withdraw(value: BigDecimal): Order = {
    Order(instrument, price, volume - value)
  }
}
