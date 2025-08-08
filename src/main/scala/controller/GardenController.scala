package controller

import db.FoodDAO
import gui.{Home, Inventory}
import logging.GameLogger
import model.{CropRegistry, FoodItem, Player}
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
import scalafx.scene.layout.StackPane

object GardenController:

  private val logger = GameLogger.getLogger(this.getClass)

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

  // Build garden plot grid
  def buildGrid(stage: Stage, player: Player): GridPane =
    logger.info(s"[UI] Building garden grid for Player '${player.name}' (ID: ${player.id}) with $rows rows × $cols columns")
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

        // Checking for status (ensure player leave the garden page and come back still have the same status)
        if gardenStatus(row)(col) == "Ready" then
          CropRegistry.getByName(gardenCrop(row)(col)) match {
            case Some(crop) => emojiLabel.text = crop.emoji
            case None => emojiLabel.text = "🌱"
          }
          emojiLabel.styleClass.setAll(
            "crop-emoji", // keep size from original
            "emoji-label",
            gardenCrop(row)(col).toLowerCase + "-emoji"
          )

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
          logger.info(s"[ACTION] Player '${player.name}' pressed Harvest on plot [$row,$col] → crop='${gardenCrop(row)(col)}', status='${gardenStatus(row)(col)}'")

          val crop = gardenCrop(row)(col)

          if gardenStatus(row)(col) == "Ready" then
            // Insert into inventory (DB)
            CropRegistry.getByName(crop).foreach { c =>
              logger.info(s"[SUCCESS] Harvested '${c.name}' (+${c.points} points) for Player '${player.name}' (ID: ${player.id}). Plot [$row,$col] reset to Empty.")
              val food = FoodItem(0, c.name, c.nutrition, c.calories, c.cropType, player.id)
              FoodDAO.insert(food)
              player.points += c.points // Add points to player

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
              emojiLabels(row)(col).styleClass.setAll("crop-emoji", "default-emoji")
              selectedStackPane.foreach(_.styleClass.remove("garden-plot-selected"))
              selectedStackPane = None
              statusLabel.text = "Empty"
              statusLabel.visible = false
              progressBar.progress = 0.0
              progressBar.visible = false
              harvestBtn.visible = false

              // Popup alert for harvest success & points earned
              val alert = new Alert(AlertType.Information) {
                title = "Harvest Complete"
                headerText = s"You harvested ${c.name}!"
                contentText = s"${c.name} added to inventory.\n+${c.points} points earned!"
              }

              val stylesheet = getClass.getResource("/css/global.css").toExternalForm
              alert.dialogPane().getStylesheets.add(stylesheet)
              alert.getDialogPane.getStyleClass.add("dialog-pane")

              alert.showAndWait()
            }
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
            logger.info(s"Plot [$row,$col] clicked. Current status: ${gardenStatus(row)(col)} | Current crop: ${gardenCrop(row)(col)}")

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
                logger.info(s"Showing growing UI for plot [$row,$col]")
                progressBars(row)(col).visible = true
                statusTexts(row)(col).visible = true
                plantButton.visible = false

              case "Ready" =>
                logger.info(s"Showing harvest button for plot [$row,$col]")
                harvestButtons(row)(col).visible = true
                plantButton.visible = false

              case _ => // Do nothing extra for Empty
                logger.info(s"No crop in plot [$row,$col], keeping UI empty")
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

    // Link to inventory page
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
        val cropOptions: List[String] = CropRegistry.crops.map(_.name).toList

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
          logger.info(s"[INPUT] Player '${player.name}' clicked Plant on plot [$row,$col] → crop='${gardenCrop(row)(col)}', status='${gardenStatus(row)(col)}'")

          if gardenStatus(row)(col) == "Ready" then
            // Show warning and prevent replanting
            val alert = new Alert(AlertType.Warning) {
              logger.warning(s"[DENIED] Player '${player.name}' tried to plant on plot [$row,$col] with status='Ready'. Must harvest first.")
              title = "Cannot Plant"
              headerText = "Plot is already ready to harvest!"
              contentText = "Please harvest the crop before planting a new one."
            }

            alert.dialogPane().getStylesheets.add(stylesheet)
            alert.dialogPane().getStyleClass.add("dialog-pane")

            alert.showAndWait()

          else if gardenCrop(row)(col) == "Empty" then
            logger.info(s"[ACTION] Planting '$selectedCrop' in plot [$row,$col] for Player '${player.name}'. Status → Growing.")
            gardenCrop(row)(col) = selectedCrop
            gardenStatus(row)(col) = "Growing"
            emojiLabels(row)(col).text = "\uD83C\uDF31"
            statusTexts(row)(col).text = "Growing..."
            progressBars(row)(col).progress = 0.0
            progressBars(row)(col).visible = true
            statusTexts(row)(col).visible = true
            harvestButtons(row)(col).visible = false
            plantButton.visible = false

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
                logger.info(s"[EVENT] Growth complete for plot [$row,$col]. Crop='$selectedCrop' now Ready for harvest.")
                val emojiLabel = emojiLabels(row)(col)
                CropRegistry.getByName(selectedCrop) match {
                  case Some(crop) => emojiLabel.text = crop.emoji
                  case None       => emojiLabel.text = "🌱"
                }

                // Clear old crop-specific classes (if any) and add new one
                emojiLabel.styleClass --= Seq("carrot-emoji", "tomato-emoji", "corn-emoji", "eggplant-emoji", "cucumber-emoji", "wheat-emoji", "default-emoji")
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