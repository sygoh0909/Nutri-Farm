package gui

import controller.GardenController
import model.Player
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.VBox
import scalafx.stage.Stage

object Garden:

  def build(stage: Stage, player: Player): VBox =
    val gardenGrid = GardenController.buildGrid() // Plan to add grid planting logic in garden controller

    // Link back to home page
    val backButton = new Button("Back to Home") {
      styleClass.add("back-button") // Plan to implement in css file
      onAction = _ => stage.scene().setRoot(Home.build(player, stage))
    }

    new VBox {
      spacing = 30
      padding = Insets(40)
      alignment = Pos.Center
      prefWidth = 1200
      prefHeight = 850
      children = Seq(
        new Label("Garden - Start Farming here!") {
          styleClass.add("title-label")
        },
        gardenGrid,
        backButton
      )
    }