package db

import slick.jdbc.PostgresProfile.api._ // Core Slick components
import slick.lifted.{ProvenShape, Tag} // To use table/tag
import model.FoodItem

class FoodTable(tag: Tag) extends Table[FoodItem](tag, "food_items"): // Using slick FRM mapping
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def nutrition = column[String]("nutrition")
  def calories = column[Double]("calories")
  def playerID = column[Int]("player_id")

  def * : ProvenShape[FoodItem] =
    (id, name, nutrition, calories, playerID) <> (FoodItem.apply.tupled, FoodItem.unapply)// Maps tuple (db row) into food item (object)

object FoodTable:
  val foodItem = TableQuery[FoodTable]
