package model

case class FoodItem(
                     id: Int,
                     name: String,
                     nutrition: String,
                     calories: Double,
                     cropType: CropType,
                     playerID: Int
                   )