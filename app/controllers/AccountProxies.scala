package controllers

import models._
import akka.actor.ActorLogging

/**
 * Partially Copied from Jamie Allen - https://github.com/jamie-allen/effective_akka
 */

class CheckingAccountsProxyStub extends CheckingAccountsProxy with ActorLogging {

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

class SavingsAccountsProxyStub extends SavingsAccountsProxy with ActorLogging {

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

class MoneyMarketAccountsProxyStub extends MoneyMarketAccountsProxy with ActorLogging {

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
