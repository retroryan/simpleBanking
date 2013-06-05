package controllers

import models._
import akka.actor.{Actor, ActorRef, ActorLogging}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import play.api.libs.json._
import play.api.libs.iteratee._

import play.api.libs.concurrent.Execution.Implicits._
// intellij always deletes this import, so I keep a copy for easy reference
// import play.api.libs.concurrent.Execution.Implicits._

class AccountBalanceWSActor(checkingAccounts: ActorRef,
                            savingsAccounts: ActorRef,
                            moneyMarketAccounts: ActorRef) extends Actor with ActorLogging {

  var userChannels = Set.empty[String]
  val (chatEnumerator, chatChannel) = Concurrent.broadcast[JsValue]

  implicit val timeout: Timeout = 5 seconds

  def receive = {
    case GetCustomerAccountBalancesWS(userId, channel) =>

      val futChecking = checkingAccounts ? GetCustomerAccountBalances(userId)
      val futSavings = savingsAccounts ? GetCustomerAccountBalances(userId)
      val futMM = moneyMarketAccounts ? GetCustomerAccountBalances(userId)

      val futBalances = for {
        checking <- futChecking.mapTo[CheckingAccountBalances]
        savings <- futSavings.mapTo[SavingsAccountBalances]
        mm <- futMM.mapTo[MoneyMarketAccountBalances]
      } yield {
        AccountBalances(checking.balances, savings.balances, mm.balances)
      }

      futBalances map {
        accountBalances =>
          channel.push(Json.toJson(accountBalances))
      }
  }
}

case class GetCustomerAccountBalancesWS(userId: Long, channel: Concurrent.Channel[JsValue])