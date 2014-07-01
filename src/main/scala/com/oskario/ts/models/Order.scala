package com.oskario.ts.models

case class Order(user: User, instrument: Instrument, price: BigDecimal, `type`: OrderType)
