package controllers

import models._
import models.AccountBalances
import scala.Some
import models.MoneyMarketAccountBalances
import akka.actor.{Actor, ActorRef, ActorLogging}
import models.GetCustomerAccountBalances
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import akka.pattern.pipe

import play.api.libs.concurrent.Execution.Implicits._
//import play.api.libs.concurrent.Execution.Implicits._

/**
 * Partially Copied from Jamie Allen - https://github.com/jamie-allen/effective_akka
 */


class AccountBalanceFullActor(checkingAccounts: ActorRef,
                            savingsAccounts: ActorRef,
                            moneyMarketAccounts: ActorRef) extends Actor with ActorLogging {


  implicit val timeout: Timeout = 5 seconds

  def receive = {
    case GetCustomerAccountBalances(userId: Long) =>
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


class CheckingAccountsProxyStub2 extends CheckingAccountsProxy with ActorLogging {

  val accountData = Map[Long, List[(Long, BigDecimal)]](
    1L -> List((3, 15000)),
    2L -> List((6, 640000), (7, 1125000), (8, 40000)))

  def receive = {
    case GetCustomerAccountBalances(userId: Long) =>
      log.debug(s"Received GetCustomerAccountBalances for ID: $userId")
      Thread.sleep(500)
      accountData.get(userId) match {
        case Some(data) => sender ! CheckingAccountBalances(Some(data))
        case None => sender ! CheckingAccountBalances(Some(List()))
      }
    case _ => log.debug("Unhandled message")
  }
}

class SavingsAccountsProxyStub2 extends SavingsAccountsProxy with ActorLogging {

  val accountData = Map[Long, List[(Long, BigDecimal)]](
    1L -> (List((1, 150000), (2, 29000))),
    2L -> (List((5, 80000))))

  def receive = {
    case GetCustomerAccountBalances(userId: Long) =>
      log.debug(s"Received GetCustomerAccountBalances for ID: $userId")
      Thread.sleep(500)
      accountData.get(userId) match {
        case Some(data) => sender ! SavingsAccountBalances(Some(data))
        case None => sender ! SavingsAccountBalances(Some(List()))
      }
  }
}

class MoneyMarketAccountsProxyStub2 extends MoneyMarketAccountsProxy with ActorLogging {

  val accountData = Map[Long, List[(Long, BigDecimal)]](
    2L -> List((9, 640000), (10, 1125000), (11, 40000)))

  def receive = {
    case GetCustomerAccountBalances(userId: Long) =>
      log.debug(s"Received GetCustomerAccountBalances for ID: $userId")
      Thread.sleep(500)
      accountData.get(userId) match {
        case Some(data) => sender ! MoneyMarketAccountBalances(Some(data))
        case None => sender ! MoneyMarketAccountBalances(Some(List()))
      }
  }
}
