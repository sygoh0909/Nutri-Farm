package db

import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, Tag}
import model.FoodItem

// Source: https://scala-slick.org/doc/3.5.0-M1/schemas.html
class FoodTable(tag: Tag) extends Table[FoodItem](tag, "food_items"):
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def nutrition = column[String]("nutrition")
  def calories = column[Double]("calories")

  def * : ProvenShape[FoodItem] =
    (id, name, nutrition, calories) <> (FoodItem.apply.tupled, FoodItem.unapply)// Maps tuple (db row) into food item (object)

object FoodTable:
  val foodItem = TableQuery[FoodTable]
