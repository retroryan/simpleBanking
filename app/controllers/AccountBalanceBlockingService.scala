package controllers

import models._

object AccountBalanceBlockingService {

  def getCustomerAccountBalances(userId: Long) = {
    val checkingBalances: CheckingAccountBalances = CheckingAccountsProxy.getCustomerAccountBalances(userId)
    val savingsBalances: SavingsAccountBalances = SavingsAccountsProxy.getCustomerAccountBalances(userId)
    val mmBalances: MoneyMarketAccountBalances = MoneyMarketAccountsProxy.getCustomerAccountBalances(userId)

    AccountBalances(checkingBalances.balances, savingsBalances.balances, mmBalances.balances)
  }

}

/**
 * Partially Copied from Jamie Allen - https://github.com/jamie-allen/effective_akka
 */

object CheckingAccountsProxy  {

  val accountData = Map[Long, List[(Long, BigDecimal)]](
    1L -> List((3, 15000)),
    2L -> List((6, 640000), (7, 1125000), (8, 40000)))

  def getCustomerAccountBalances(userId: Long) = {
    Thread.sleep(400)
    accountData.get(userId) match {
      case Some(data) => CheckingAccountBalances(Some(data))
      case None => CheckingAccountBalances(Some(List()))
    }
  }
}

object SavingsAccountsProxy  {

  val accountData = Map[Long, List[(Long, BigDecimal)]](
    1L -> (List((1, 150000), (2, 29000))),
    2L -> (List((5, 80000))))

  def getCustomerAccountBalances(userId: Long) = {
    Thread.sleep(400)
    accountData.get(userId) match {
      case Some(data) => SavingsAccountBalances(Some(data))
      case None => SavingsAccountBalances(Some(List()))
    }
  }
}

object MoneyMarketAccountsProxy  {

  val accountData = Map[Long, List[(Long, BigDecimal)]](
    2L -> List((9, 640000), (10, 1125000), (11, 40000)))

  def getCustomerAccountBalances(userId: Long) = {
    Thread.sleep(400)
    accountData.get(userId) match {
      case Some(data) => MoneyMarketAccountBalances(Some(data))
      case None => MoneyMarketAccountBalances(Some(List()))
    }
  }
}

