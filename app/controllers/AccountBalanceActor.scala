package controllers

import models._
import akka.actor.{Actor, ActorRef, ActorLogging}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import akka.pattern.pipe


import play.api.libs.concurrent.Execution.Implicits._
//import play.api.libs.concurrent.Execution.Implicits._

/**
 * Partially Copied from Jamie Allen - https://github.com/jamie-allen/effective_akka
 */


class AccountBalanceActor(checkingAccounts: ActorRef,
                              savingsAccounts: ActorRef,
                              moneyMarketAccounts: ActorRef) extends Actor with ActorLogging {

  implicit val timeout: Timeout = 5 seconds

  def receive = {
    case GetCustomerAccountBalances(userId) =>
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

      futBalances pipeTo sender
  }
}

