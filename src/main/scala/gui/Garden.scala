package gui

import components.MenuButton
import controller.GardenController
import model.Player
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Label
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.stage.Stage

object Garden:

  def build(stage: Stage, player: Player): StackPane =
    val gardenGrid = GardenController.buildGrid(stage, player)
    val controlPanel = GardenController.buildControlPanel(stage, player)

    val mainVBox = new VBox {
      spacing = 10
      padding = Insets(30, 40, 30, 40)
      alignment = Pos.Center
      styleClass ++= Seq("bg-base", "garden-bg")
      children = Seq(
        new Label("Garden - Start Farming here!") {
          styleClass.add("title")
        },
        gardenGrid,
        controlPanel
      )
    }

    new StackPane {
      children = Seq(
        mainVBox,
        MenuButton.build(stage, player)
      )
    }
