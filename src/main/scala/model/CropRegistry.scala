package model

object CropRegistry:
  val crops: Seq[Crop] = Seq(Carrot, Tomato, Corn, Eggplant, Kiwi, Wheat)
  def getByName(name: String): Option[Crop] = crops.find(_.name == name)

case object Carrot extends Crop {
  val name: String = "Carrot"
  val emoji: String = "🥕"
  val nutrition: String = "Vitamin A"
  val calories: Double = 25.0
  val points: Int = 5
  val cropType: CropType = CropType.Vegetable
  val recipes: Seq[String] = Seq("Carrot Soup", "Carrot Cake")
}

case object Tomato extends Crop {
  val name: String = "Tomato"
  val emoji: String = "🍅"
  val nutrition: String = "Vitamin C"
  val calories: Double = 22.0
  val points: Int = 6
  val cropType: CropType = CropType.Vegetable
  val recipes: Seq[String] = Seq("Tomato Pasta", "Tomato Soup")
}

case object Corn extends Crop {
  val name: String = "Corn"
  val emoji: String = "🌽"
  val nutrition: String = "Carbohydrate"
  val calories: Double = 35.0
  val points: Int = 4
  val cropType: CropType = CropType.Vegetable
  val recipes: Seq[String] = Seq("Corn Tacos", "Corn Chowder")
}

case object Eggplant extends Crop {
  val name: String = "Eggplant"
  val emoji: String = "🍆"
  val nutrition: String = "Antioxidants"
  val calories: Double = 28.0
  val points: Int = 6
  val cropType: CropType = CropType.Vegetable
  val recipes: Seq[String] = Seq("Eggplant Parmesan", "Eggplant Curry")
}

case object Kiwi extends Crop {
  val name: String = "Kiwi"
  val emoji: String = "🥝"
  val nutrition: String = "Vitamin C"
  val calories: Double = 42.0
  val points: Int = 5
  val cropType: CropType = CropType.Fruit
  val recipes: Seq[String] = Seq("Kiwi Smoothie", "Kiwi Tart")
}

case object Wheat extends Crop {
  val name: String = "Wheat"
  val emoji: String = "🌾"
  val nutrition: String = "Fiber"
  val calories: Double = 40.0
  val points: Int = 6
  val cropType: CropType = CropType.Grain
  val recipes: Seq[String] = Seq("Whole Wheat Bread", "Wheat Pancakes")
}