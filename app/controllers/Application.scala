package controllers

import play.api.mvc._
import play.api.libs.json.Json

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def basicGetAccountBalances = Action {
    Ok(views.html.basicGetAccountBalances())
  }

  def getAccountBalanceBasic = Action(parse.json) {
    request =>
      val userIdJson = request.body
      val userId = (userIdJson \ "userId").as[String].toLong
      val accountBalances = AccountBalanceService.getCustomerAccountBalances(userId)
      println(Json.toJson(accountBalances))
      Ok(Json.toJson(accountBalances))
  }
}