package com.oskario.ts.exceptions

import com.oskario.ts.models.OrderConfirmation

case class InvalidConfirmationReceived(confirmation: OrderConfirmation, reason: String)
  extends TradingSystemException(s"Invalid confirmation received: $confirmation ($reason)")
