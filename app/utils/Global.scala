package utils

import play.api._
import play.libs.Akka
import akka.actor.Props
import controllers._

object Global extends GlobalSettings {

  lazy val accountBalanceActorService = {
    val checkingAccountProxy = Akka.system().actorOf(Props(new CheckingAccountsProxyStub()), "checkingAccountProxy")
    val savingsAccountProxy = Akka.system().actorOf(Props(new SavingsAccountsProxyStub()), "savingsAccountProxy")
    val moneyMarketAccountProxy = Akka.system().actorOf(Props(new MoneyMarketAccountsProxyStub()), "moneyMarketAccountsProxyStub")
    new AccountBalanceActorService(checkingAccountProxy, savingsAccountProxy, moneyMarketAccountProxy)
  }

  lazy val accountBalanceFullActor = {
    val checkingAccountProxy = Akka.system().actorOf(Props(new CheckingAccountsProxyStub2()), "checkingAccountProxy")
    val savingsAccountProxy = Akka.system().actorOf(Props(new SavingsAccountsProxyStub2()), "savingsAccountProxy")
    val moneyMarketAccountProxy = Akka.system().actorOf(Props(new MoneyMarketAccountsProxyStub2()), "moneyMarketAccountsProxyStub")
    Akka.system().actorOf(Props(new AccountBalanceFullActor(checkingAccountProxy, savingsAccountProxy, moneyMarketAccountProxy)), "accountBalanceService")
  }

  override def onStart(app: Application) {

  }
}