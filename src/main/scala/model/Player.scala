package model

case class Player(
                   id: Int,
                   name: String,
                   email: String,
                   passwordHash: String,
                   var points: Int
                 )