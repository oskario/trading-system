package actors

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe, TestActorRef}
import com.oskario.ts.actors.{Market, User}
import com.oskario.ts.exceptions.InvalidConfirmationReceived
import com.oskario.ts.models.{Position, OrderConfirmation, Order}
import org.scalatest.{WordSpecLike, Matchers}
import scala.concurrent.duration._

class UserSpec(_system: ActorSystem) extends TestKit(_system) with WordSpecLike with Matchers {

  def this() = this(ActorSystem("UserSpec"))
  
  val testSymbol = "EURUSD"
  val testOrder = Order(testSymbol, 1.0, 100.0)
  val testConfirmation = OrderConfirmation(testSymbol, 1.1, 50.0)
  val timeout = 5.millis

  "User" should {
    "send orders to market" in {
      val userActor = TestActorRef(new User())
      val user = userActor.underlyingActor
      val marketActor = TestProbe()

      user.buy(marketActor.ref, testOrder)
      marketActor.expectMsg(timeout, Market.Protocol.Buy(testOrder))

      userActor ! User.Protocol.Buy(marketActor.ref, testOrder)
      marketActor.expectMsg(timeout, Market.Protocol.Buy(testOrder))

      user.sell(marketActor.ref, testOrder)
      marketActor.expectMsg(timeout, Market.Protocol.Sell(testOrder))

      userActor ! User.Protocol.Sell(marketActor.ref, testOrder)
      marketActor.expectMsg(timeout, Market.Protocol.Sell(testOrder))
    }

    "save information about accepted buy order" in {
      val userActor = TestActorRef(new User())
      val user = userActor.underlyingActor

      user.receive(Market.Protocol.BuyAccepted(testConfirmation))
      user.positions.size should equal(1)
      user.positions.head.volume should equal(testConfirmation.volume)
    }

    "save information about accepted sell order" in {
      val userActor = TestActorRef(new User())
      val user = userActor.underlyingActor

      user.positions += Position(testConfirmation.instrument, 100.0)
      user.receive(Market.Protocol.SellAccepted(testConfirmation))
      user.positions.head.volume should equal(100 - testConfirmation.volume)
    }

    "inform about invalid confirmation receipt" in {
      val userActor = TestActorRef(new User())
      val user = userActor.underlyingActor

      intercept[InvalidConfirmationReceived] {
        user.receive(Market.Protocol.SellAccepted(testConfirmation))
      }

      user.positions += Position(testConfirmation.instrument, testConfirmation.volume/2)
      intercept[InvalidConfirmationReceived] {
        user.receive(Market.Protocol.SellAccepted(testConfirmation))
      }
    }

    "calculate money correctly" in {
      val initialMoney = 1000
      val transaction1 = OrderConfirmation(testSymbol, 1, 500)
      val moneyAfterTransaction1 = initialMoney - transaction1.value
      val transaction2 = OrderConfirmation(testSymbol, 2, 250)
      val moneyAfterTransaction2 = moneyAfterTransaction1 + transaction2.value
      val transaction3 = OrderConfirmation(testSymbol, 4, 250)
      val moneyAfterTransaction3 = moneyAfterTransaction2 + transaction3.value

      val userActor = TestActorRef(new User(initialMoney))
      val user = userActor.underlyingActor

      user.receive(Market.Protocol.BuyAccepted(transaction1))
      user.money should equal(moneyAfterTransaction1)
      user.receive(Market.Protocol.SellAccepted(transaction2))
      user.money should equal(moneyAfterTransaction2)
      user.receive(Market.Protocol.SellAccepted(transaction3))
      user.money should equal(moneyAfterTransaction3)
    }
  }
}
