package controllers

import models._
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._


import play.api.libs.concurrent.Execution.Implicits._
// intellij always deletes this import, so I keep a copy for easy reference
//import play.api.libs.concurrent.Execution.Implicits._

/**
 * Partially Copied from Jamie Allen - https://github.com/jamie-allen/effective_akka
 */


class AccountBalanceActorService(checkingAccounts: ActorRef,
                            savingsAccounts: ActorRef,
                            moneyMarketAccounts: ActorRef) {


  implicit val timeout: Timeout = 1 seconds

  def getCustomerAccountBalances(userId: Long) = {
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

    futBalances
  }
}

