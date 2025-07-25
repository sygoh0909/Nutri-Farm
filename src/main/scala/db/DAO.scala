package db

import model.{Player, FoodItem}
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

// Data access object (DAO) to map custom functions for different db
// Source: https://scala-slick.org/doc/3.5.0-M1/database.html
object PlayerDAO: // Provides functions to interact with player table
  // Modified by ChatGPT
  def insert(player: Player): Future[Int] = // Insert new player to db player table
    DBConfig.db.run(PlayerTable.players += player)

  def findByEmail(email: String): Future[Option[Player]] = // Find player by email, return some or none
    DBConfig.db.run(PlayerTable.players.filter(_.email === email).result.headOption)

  def updatePoints(id: Int, points: Int): Future[Int] =
    DBConfig.db.run(
      PlayerTable.players.filter(_.id === id).map(_.points).update(points)
    )

object FoodDAO: // Provides functions to interact with food item table
  def insert(food: FoodItem): Future[Int] =
    DBConfig.db.run(FoodTable.foodItem += food)

  def getAll(): Future[Seq[FoodItem]] = // Get all foods
    DBConfig.db.run(FoodTable.foodItem.result)
