package db

import slick.jdbc.PostgresProfile.api._ // Core Slick components
import slick.lifted.{ProvenShape, Tag} // To use table/tag
import model.Player // Connect to player case class 

// Source: https://scala-slick.org/doc/3.5.0-M1/schemas.html
class PlayerTable(tag: Tag) extends Table[Player](tag, "players"): // Using slick FRM mapping
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def email = column[String]("email")
  def passwordHash = column[String]("password_hash")
  def points = column[Double]("points")
  def * : ProvenShape[Player] =
    (id, name, email, passwordHash, points) <> (Player.apply.tupled, Player.unapply) // Maps tuple (db row) into food item (object)

object PlayerTable:
  val players = TableQuery[PlayerTable]