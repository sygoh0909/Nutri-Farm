package gui

import model.Player
import scalafx.scene.layout.VBox
import scalafx.scene.control.{Label, Button}
import scalafx.geometry.{Insets, Pos}
import scalafx.stage.Stage

object Home:

  // Home page for testing - pass in logged in user/guest
  def build(player: Player, stage: Stage): VBox =
    new VBox {
      spacing = 20
      padding = Insets(30)
      alignment = Pos.Center
      children = Seq(
        new Label(s"Welcome, ${player.name}!"),
      )
    }
