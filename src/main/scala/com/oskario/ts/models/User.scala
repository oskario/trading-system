package com.oskario.ts.models

case class User(name: String, money: BigDecimal, orders: Seq[Order])
