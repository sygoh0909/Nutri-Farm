package controller

import db.FoodDAO
import model.{FoodItem}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{ChoiceBox}
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
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

  // Build garden plot grid
  def buildGrid(): GridPane =
    val grid = new GridPane {
      hgap = 30
      vgap = 30
      padding = Insets(20)
      alignment = Pos.Center
    }

    // Create a garden tile for each plot
    for row <- 0 until rows do
      for col <- 0 until cols do
        val emojiLabel = new Label("\uD83C\uDF31") { // Will update this later for better display
          style = "-fx-font-size: 28px;"
          mouseTransparent = true
        }

        // Crop status
        val statusLabel = new Label("Empty") {
          style = "-fx-font-size: 13px;"
          visible = false
        }

        // Progress bar (growing status)
        val progressBar = new ProgressBar {
          prefWidth = 100
          progress = 0.0
          visible = false
        }

        // Will show when crop is grown (btn)
        val harvestBtn = new Button("Harvest") {
          visible = false
        }

        // Save UI elements to arrays to update later
        emojiLabels(row)(col) = emojiLabel
        statusTexts(row)(col) = statusLabel
        progressBars(row)(col) = progressBar
        harvestButtons(row)(col) = harvestBtn

        // If player harvest (press the btn) the crop, crop will be saved to their inventory and the crop will be reset to empty
        harvestBtn.onAction = _ =>
          val crop = gardenCrop(row)(col)
          val (nutri, cal) = cropNutrition(crop)

          FoodDAO.insert(FoodItem(0, crop, nutri, cal))
          gardenCrop(row)(col) = "Empty"
          gardenStatus(row)(col) = "Empty"

          emojiLabel.text = "\uD83C\uDF31"
          statusLabel.text = "Empty"
          progressBar.progress = 0.0
          harvestBtn.visible = false
          progressBar.visible = false
          statusLabel.visible = false

        // Clickable plot area (tile)
        val stackPane = new StackPane {
          children = Seq(
            new Rectangle {
              width = 130
              height = 130
              arcWidth = 15
              arcHeight = 15
              fill = Color.LightGreen
              stroke = Color.ForestGreen
            },
            emojiLabel
          )

          // Clicking plot
          onMouseClicked = _ =>
            // Reset all plots
            for r <- 0 until rows; c <- 0 until cols do
              stackPanes(r)(c).style = ""
              progressBars(r)(c).visible = false
              statusTexts(r)(c).visible = false

              if gardenStatus(r)(c) != "Ready" then
                harvestButtons(r)(c).visible = false

            // Highlight selected
            selectedRow = row
            selectedCol = col
            selectedStackPane = Some(this)
            this.style = "-fx-border-color: gold; -fx-border-width: 4; -fx-border-radius: 10;"
            progressBars(row)(col).visible = true
            statusTexts(row)(col).visible = true
            if gardenStatus(row)(col) == "Ready" then
              harvestButtons(row)(col).visible = true
        }

        // Store the tile to reset later
        stackPanes(row)(col) = stackPane

        val plotBox = new VBox {
          spacing = 6
          alignment = Pos.Center
          children = Seq(stackPane, statusLabel, progressBar, harvestBtn)
        }

        grid.add(plotBox, col, row)

    grid

