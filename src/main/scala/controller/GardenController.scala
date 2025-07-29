package controller

import scalafx.scene.control.{Button, Label, ProgressBar}
import scalafx.scene.layout.{StackPane}

object GardenController:

  // Garden dimensions (2 rows, 6 columns)
  private val rows = 2
  private val cols = 6

  // Track planted crops their growth status
  private val gardenCrop = Array.fill(rows, cols)("Empty")
  private val gardenStatus = Array.fill(rows, cols)("Empty")

  private val emojiLabels = Array.ofDim[Label](rows, cols)
  private val statusTexts = Array.ofDim[Label](rows, cols)
  private val progressBars = Array.ofDim[ProgressBar](rows, cols)
  private val harvestButtons = Array.ofDim[Button](rows, cols)
  private val stackPanes = Array.ofDim[StackPane](rows, cols)

  // Track selected column (current selected)
  private var selectedRow = 0
  private var selectedCol = 0
  private var selectedStackPane: Option[StackPane] = None

  // Show different crops as emoji (for now)
  private def cropEmoji(crop: String): String = crop match
    case "Carrot"  => "🥕" // Maybe will change to picture afterwards
    case "Tomato"  => "🍅"
    case "Lettuce" => "🥬"
    case _         => "🌱"

  // Utility method to get nutrition info
  private def cropNutrition(crop: String): (String, Double) = crop match
    case "Carrot"  => ("Vitamin A", 25.0)
    case "Tomato"  => ("Vitamin C", 22.0)
    case "Lettuce" => ("Folate", 15.0)
    case _         => ("", 0.0)
