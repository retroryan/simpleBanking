package controllers

import play.api.mvc._
import play.api.libs.json.{JsValue, Json}
import utils.Global
import models.AccountBalances
import scala.concurrent.Future
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.duration._
import play.api.libs.iteratee._
import models.GetCustomerAccountBalances
import play.api.Logger

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

        (Global.accountBalanceActor ? GetCustomerAccountBalances(userId)).mapTo[AccountBalances].map {
          accountBalances =>
            Ok(Json.toJson(accountBalances))
        }
      }
  }


  /**
   * Handles the chat websocket.
   */
  def accountBalancesWS = Action {
    implicit request =>
      Ok(views.html.accountBalancesWS())
  }

  /**
   * Handles the chat websocket.
   */
  def getAccountBalancesWS() = WebSocket.async[JsValue] {
    implicit request =>

      val (enumerator, clientChannel) = Concurrent.broadcast[JsValue]

      // Create an Iteratee to recieve the user id
      val iteratee = Iteratee.foreach[JsValue] {
        userIdJson => {
          val userId = (userIdJson \ "userId").as[String].toLong
          Global.accountBalanceWSActor ! GetCustomerAccountBalancesWS(userId, clientChannel)
        }
      }.mapDone {
        _ =>
          Logger.debug("disconnected client channel")
      }

      import play.api.libs.concurrent.Execution.Implicits._
      Future((iteratee, enumerator))


  }
}