package db

import model.{Player, FoodItem}
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

// Data access object (DAO) to map custom functions for different db
object PlayerDAO: // Provides functions to interact with player table
  def insert(player: Player): Future[Int] = // Insert new player to db player table
    DBConfig.db.run((PlayerTable.players returning PlayerTable.players.map(_.id)) += player)

  def findByEmail(email: String): Future[Option[Player]] = // Find player by email, return some or none
    DBConfig.db.run(PlayerTable.players.filter(_.email === email).result.headOption)

object FoodDAO: // Provides functions to interact with food item table
  def insert(food: FoodItem): Future[Int] =
    DBConfig.db.run(FoodTable.foodItem += food)

  def getByPlayerId(playerID: Int): Future[Seq[FoodItem]] =
    DBConfig.db.run(FoodTable.foodItem.filter(_.playerID === playerID).result)
