package controllers

import play.api.mvc._
import play.api.libs.json.Json
import utils.Global
import models.{GetCustomerAccountBalances, AccountBalances}
import scala.concurrent.Future
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.duration._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def getAccountBalances = Action {
    Ok(views.html.getAccountBalances())
  }

  def getAccountBalancesBlocking = Action(parse.json) {
    request =>
      val userIdJson = request.body
      val userId = (userIdJson \ "userId").as[String].toLong
      val accountBalances = AccountBalanceBlockingService.getCustomerAccountBalances(userId)
      Ok(Json.toJson(accountBalances))
  }

  def asyncOneGetAccountBalances = Action(parse.json) {
    request =>
      val userIdJson = request.body
      val userId = (userIdJson \ "userId").as[String].toLong

      Async {
        import play.api.libs.concurrent.Execution.Implicits._
        val futureBalances: Future[AccountBalances] = Global.accountBalanceActorService.getCustomerAccountBalances(userId)
        futureBalances map (accountBalances => {
          Ok(Json.toJson(accountBalances))
        })
      }
  }

  def asyncTwoGetAccountBalances = Action(parse.json) {
    request =>
      val userIdJson = request.body
      val userId = (userIdJson \ "userId").as[String].toLong

      Async {
        import play.api.libs.concurrent.Execution.Implicits._
        implicit val timeout: Timeout = 5 seconds

        (Global.accountBalanceFullActor ? GetCustomerAccountBalances(userId)).mapTo[AccountBalances].map {
          accountBalances =>
            Ok(Json.toJson(accountBalances))
        }
      }
  }
}