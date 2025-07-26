package gui

import controller.GardenController
import model.Player
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.VBox
import scalafx.stage.Stage

object Garden:

  def build(stage: Stage, player: Player): VBox =
    val gardenGrid = GardenController.buildGrid()
    val controlPanel = GardenController.buildControlPanel(stage, player)

    val backButton = new Button("Back to Home") {
      styleClass.add("game-button")
      onAction = _ => stage.scene().setRoot(Home.build(player, stage))
    }

    new VBox {
      spacing = 30
      padding = Insets(40)
      alignment = Pos.Center
      prefWidth = 1200
      prefHeight = 850
      styleClass ++= Seq("bg-base", "garden-bg")
      children = Seq(
        new Label("Garden - Start Farming here!") {
          styleClass.add("title")
        },
        gardenGrid,
        controlPanel,
        backButton
      )
    }