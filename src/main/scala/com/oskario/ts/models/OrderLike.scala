package com.oskario.ts.models

abstract class OrderLike(price: BigDecimal, volume: BigDecimal) {
  def value: BigDecimal = this.price * this.volume
}
