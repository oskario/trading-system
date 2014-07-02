package com.oskario.ts.models

case class OrderConfirmation(instrument: String, price: BigDecimal, volume: BigDecimal) extends OrderLike(price, volume)
