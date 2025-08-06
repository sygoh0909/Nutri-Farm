package controller

import db.FoodDAO
import gui.{Home, Inventory}
import model.{FoodItem, Player}
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.stage.Stage

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalafx.scene.control.{Alert, Button, Label, ProgressBar}
import scalafx.scene.layout.StackPane // Container that stacks children nodes on top of each other

object GardenController:

  // Garden dimensions (2 rows, 6 columns)
  private val rows = 2
  private val cols = 6

  private var plantButton: Button = _

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
    case "Carrot"  => "🥕"
    case "Tomato"  => "🍅"
    case "Lettuce" => "\uD83C\uDF3F" // Finding a better one
    case _         => "🌱"

  // Utility method to get nutrition info
  private def cropNutrition(crop: String): (String, Double) = crop match
    case "Carrot"  => ("Vitamin A", 25.0)
    case "Tomato"  => ("Vitamin C", 22.0)
    case "Lettuce" => ("Folate", 15.0)
    case _         => ("", 0.0)

  // Build garden plot grid
  def buildGrid(stage: Stage, player: Player): GridPane =
    val grid = new GridPane {
      styleClass += "garden-grid"
    }

    // Create a garden tile for each plot
    for row <- 0 until rows do
      for col <- 0 until cols do
        val emojiLabel = new Label("\uD83C\uDF31") {
          styleClass ++= Seq("crop-emoji", "default-emoji")
          mouseTransparent = true
        }

        // Crop status
        val statusLabel = new Label("Empty") {
          styleClass += "status-label"
          visible = false
        }

        // Progress bar (growing status)
        val progressBar = new ProgressBar {
          styleClass += "progress-bar"
          progress = 0.0
          visible = false
        }

        // Will show when crop is grown (btn)
        val harvestBtn = new Button("Harvest") {
          visible = false
          styleClass += "harvest-button"
        }

        // Save UI elements to arrays to update later
        emojiLabels(row)(col) = emojiLabel
        statusTexts(row)(col) = statusLabel
        progressBars(row)(col) = progressBar
        harvestButtons(row)(col) = harvestBtn

        // If player harvest (press the btn) the crop, crop will be saved to their inventory and the crop will be reset to empty
        harvestButtons(row)(col).onAction = _ => {
          val crop = gardenCrop(row)(col)

          if gardenStatus(row)(col) == "Ready" then
            // Insert into inventory (DB)
            val (nutri, cal) = cropNutrition(crop)
            FoodDAO.insert(FoodItem(0, crop, nutri, cal, player.id))

            // Reset the plot data
            gardenCrop(row)(col) = "Empty"
            gardenStatus(row)(col) = "Empty"

            // Reset the UI for this plot
            emojiLabels(row)(col).text = ""
            statusTexts(row)(col).text = "Empty"
            statusTexts(row)(col).visible = false
            progressBars(row)(col).progress = 0.0
            progressBars(row)(col).visible = false
            harvestButtons(row)(col).visible = false

            emojiLabel.text = "\uD83C\uDF31"
            statusLabel.text = "Empty"
            statusLabel.visible = false
            progressBar.progress = 0.0
            progressBar.visible = false
            harvestBtn.visible = false

            // Popup alert for harvest success
            val alert = new Alert(AlertType.Information) {
              title = "Harvest Complete"
              headerText = s"You harvested $crop!"
              contentText = s"$crop added to inventory."
            }

            val stylesheet = getClass.getResource("/css/global.css").toExternalForm
            alert.dialogPane().getStylesheets.add(stylesheet)
            alert.getDialogPane.getStyleClass.add("dialog-pane")

            alert.showAndWait()
        }

        // Clickable plot area (tile)
        val stackPane = new StackPane {
          styleClass += "garden-plot"
          children = Seq(
            new Rectangle {
              width = 130
              height = 130
              arcWidth = 15
              arcHeight = 15
              fill = Color.Transparent
              stroke = Color.Transparent
            },
            emojiLabel
          )

          // Clicking plot
          onMouseClicked = _ =>
            // Reset all plots
            for r <- 0 until rows; c <- 0 until cols do
              stackPanes(r)(c).styleClass.remove("garden-plot-selected")
              progressBars(r)(c).visible = false
              statusTexts(r)(c).visible = false
              harvestButtons(r)(c).visible = false

            // Highlight selected
            selectedRow = row
            selectedCol = col
            selectedStackPane = Some(this)
            this.styleClass += "garden-plot-selected"

            gardenStatus(row)(col) match
              case "Growing" =>
                progressBars(row)(col).visible = true
                statusTexts(row)(col).visible = true

              case "Ready" =>
                // Show the harvest button
                harvestButtons(row)(col).visible = true

              case _ =>
            // Do nothing extra for Empty

            plantButton.visible = true
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

  // Planting logic
  def buildControlPanel(stage: Stage, player: Player): VBox =
    val inventoryLabel = new Label {
      styleClass += "inventory-label"
    }

    // Link to meal builder page
    val inventoryBtn = new Button("Inventory") {
      styleClass += "inventory-button"
      onAction = _ =>
        FoodDAO.getByPlayerId(player.id).foreach { items =>
          Platform.runLater {
            stage.scene().setRoot(Inventory.build(player, items, stage))
          }
        }
    }

    // Back to home page button
    val backButton = new Button("Back to Home") {
      styleClass += "game-button"
      onAction = _ => stage.scene().setRoot(Home.build(player, stage))
    }

    // Crop selection UI
    plantButton = new Button("Plant Crop") {
      styleClass += "plant-button"
      visible = false
      onAction = _ =>
        val cropOptions = List("Carrot", "Tomato", "Lettuce")

        // Show crop selection dialog
        val dialog = new scalafx.scene.control.ChoiceDialog(defaultChoice = "Carrot", choices = cropOptions) {
          title = "Select Crop"
          headerText = "Choose a crop to plant"
          contentText = "Crop:"
        }

        val stylesheet = getClass.getResource("/css/global.css").toExternalForm
        dialog.dialogPane().getStylesheets.add(stylesheet)
        dialog.dialogPane().getStyleClass.add("dialog-pane")

        // Handle selected crop
        dialog.showAndWait().foreach { selectedCrop =>
          val row = selectedRow
          val col = selectedCol

          if gardenStatus(row)(col) == "Ready" then
            // Show warning and prevent replanting
            val alert = new Alert(AlertType.Warning) {
              title = "Cannot Plant"
              headerText = "Plot is already ready to harvest!"
              contentText = "Please harvest the crop before planting a new one."
            }

            alert.dialogPane().getStylesheets.add(stylesheet)
            alert.dialogPane().getStyleClass.add("dialog-pane")

            alert.showAndWait()

          else if gardenCrop(row)(col) == "Empty" then
            gardenCrop(row)(col) = selectedCrop
            gardenStatus(row)(col) = "Growing"
            emojiLabels(row)(col).text = "\uD83C\uDF31"
            statusTexts(row)(col).text = "Growing..."
            progressBars(row)(col).progress = 0.0
            progressBars(row)(col).visible = true
            statusTexts(row)(col).visible = true
            harvestButtons(row)(col).visible = false

            // Simulate growing with background thread
            Future {
              for i <- 1 to 100 do
                Thread.sleep(30)
                val progress = i / 100.0
                Platform.runLater {
                  progressBars(row)(col).progress = progress
                }

              // When growing finishes, show only harvest button
              Platform.runLater {
                gardenStatus(row)(col) = "Ready"

                val emojiLabel = emojiLabels(row)(col)
                emojiLabel.text = cropEmoji(selectedCrop)

                // Clear old crop-specific classes (if any) and add new one
                emojiLabel.styleClass --= Seq("carrot-emoji", "tomato-emoji", "lettuce-emoji", "default-emoji")
                emojiLabel.styleClass += (selectedCrop.toLowerCase + "-emoji")

                statusTexts(row)(col).visible = false
                harvestButtons(row)(col).visible = true
              }
            }
          else
            inventoryLabel.text = s"Plot already has ${gardenCrop(row)(col)}"
        }
    }

    // Show plant button only after plot is clicked
    val cropControlBox = new HBox {
      spacing = 10
      alignment = Pos.Center
      children = Seq(plantButton)
    }

    // Locate button on same lines
    val navigationBox = new HBox {
      spacing = 10
      alignment = Pos.Center
      children = Seq(inventoryLabel, backButton, inventoryBtn)
    }

    // Final layout of the control panel
    new VBox {
      styleClass += "control-panel"
      children = Seq(
        cropControlBox,
        navigationBox
      )
    }