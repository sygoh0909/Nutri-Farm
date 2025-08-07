package model

enum CropType:
  case Vegetable, Grain, Fruit // Can be expanded in future 

trait Crop:
  def name: String
  def emoji: String
  def nutrition: String
  def calories: Double
  def points: Int
  def cropType: CropType
  def recipes: Seq[String]
