package db

import slick.jdbc.PostgresProfile.api.*
import slick.lifted.{ProvenShape, Tag}
import model.{CropType, FoodItem}
import slick.ast.BaseTypedType
import slick.jdbc.JdbcType

implicit val cropTypeMapper: JdbcType[CropType] with BaseTypedType[CropType] =
  MappedColumnType.base[CropType, String](
    e => e.toString, // Save as String
    s => CropType.valueOf(s) // Convert String back to enum
  )

class FoodTable(tag: Tag) extends Table[FoodItem](tag, "food_items"): // Using slick FRM mapping
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def nutrition = column[String]("nutrition")
  def calories = column[Double]("calories")
  def cropType = column[CropType]("crop_type")
  def playerID = column[Int]("player_id")

  def * : ProvenShape[FoodItem] =
    (id, name, nutrition, calories, cropType, playerID) <> (FoodItem.apply.tupled, FoodItem.unapply)// Maps tuple (db row) into food item (object)

object FoodTable:
  val foodItem = TableQuery[FoodTable]
