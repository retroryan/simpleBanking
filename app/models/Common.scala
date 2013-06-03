package models

import play.api.libs.json._
import play.api.libs.json.Json._


/**
 * Partially Copied from Jamie Allen - https://github.com/jamie-allen/effective_akka
 */

case class GetCustomerAccountBalances(id: Long)

case class AccountBalances(checking: Option[List[(Long, BigDecimal)]],
                           savings: Option[List[(Long, BigDecimal)]],
                           moneyMarket: Option[List[(Long, BigDecimal)]]) {
}

object AccountBalances {

  implicit object ListWrites extends Writes[Option[List[(Long, BigDecimal)]]] {
    def writes(balanceList: Option[List[(Long, BigDecimal)]]) = Json.arr(
      for {
        (accountId, balance) <- balanceList.get
      } yield {
        Json.obj("accountId" -> accountId, "balance" -> balance)
      }
    )
  }

  implicit object AccountBalancesWrites extends Writes[AccountBalances] {
    def writes(accountBalance: AccountBalances) = Json.obj(
      "checking" -> accountBalance.checking,
      "savings" -> accountBalance.savings,
      "moneyMarket" -> accountBalance.moneyMarket
    )
  }

}

case class CheckingAccountBalances(balances: Option[List[(Long, BigDecimal)]])

case class SavingsAccountBalances(balances: Option[List[(Long, BigDecimal)]])

case class MoneyMarketAccountBalances(balances: Option[List[(Long, BigDecimal)]])

trait SavingsAccountsProxy

trait CheckingAccountsProxy

trait MoneyMarketAccountsProxy