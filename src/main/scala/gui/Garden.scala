package gui

import controller.GardenController
import model.Player
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Label
import scalafx.scene.layout.VBox
import scalafx.stage.Stage

object Garden:

  def build(stage: Stage, player: Player): VBox =
    val gardenGrid = GardenController.buildGrid(stage, player)
    val controlPanel = GardenController.buildControlPanel(stage, player)

    new VBox {
      spacing = 10
      padding = Insets(30, 40, 30, 40)
      alignment = Pos.Center
      styleClass ++= Seq("bg-base", "garden-bg")
      children = Seq(
        new Label("Garden - Start Farming here!") {
          styleClass.add("title")
        },
        gardenGrid,
        controlPanel,
      )
    }
