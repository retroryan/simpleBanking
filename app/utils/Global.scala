package utils

import play.api._
import play.libs.Akka
import akka.actor.Props
import controllers._

object Global extends GlobalSettings {

  lazy val checkingAccountProxy = Akka.system().actorOf(Props(new CheckingAccountsProxyStub()), "checkingAccountProxy")
  lazy val savingsAccountProxy = Akka.system().actorOf(Props(new SavingsAccountsProxyStub()), "savingsAccountProxy")
  lazy val moneyMarketAccountProxy = Akka.system().actorOf(Props(new MoneyMarketAccountsProxyStub()), "moneyMarketAccountsProxyStub")

  lazy val accountBalanceActorService = new AccountBalanceActorService(checkingAccountProxy, savingsAccountProxy, moneyMarketAccountProxy)

  lazy val accountBalanceActor = Akka.system().actorOf(Props(new AccountBalanceActor(checkingAccountProxy, savingsAccountProxy, moneyMarketAccountProxy)), "accountBalanceActor")

  lazy val accountBalanceSecondActor = Akka.system().actorOf(Props(new AccountBalanceSecondActor(checkingAccountProxy, savingsAccountProxy, moneyMarketAccountProxy)), "accountBalanceActor")

  lazy val accountBalanceWSActor = Akka.system().actorOf(Props(new AccountBalanceWSActor(checkingAccountProxy, savingsAccountProxy, moneyMarketAccountProxy)), "accountBalanceWSActor")

  override def onStart(app: Application) {

  }
}