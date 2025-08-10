package db

import slick.jdbc.PostgresProfile.api._

object DBConfig:
  // Connect to neon db (PostGreSQL) using JDBC and Slick
  val db = Database.forURL(
    url = "jdbc:postgresql://ep-cold-band-a1k2953j-pooler.ap-southeast-1.aws.neon.tech:5432/NutriFarm?sslmode=require&channelBinding=require",
    user = "neondb_owner",
    password = "npg_8qaEMoxXKH2J",
    driver = "org.postgresql.Driver"
  )
