package components

import gui.{Garden, Home, Landing}
import model.Player
import scalafx.geometry.Side
import scalafx.scene.control.*
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.{AnchorPane, Region, VBox}
import scalafx.stage.Stage

object MenuButton:

  // Build reusable menu button
  def build(stage: Stage, player: Player): Region =
    // AnchorPane gives explicit control over child position via anchors
    new AnchorPane {
      // Ensure menu button is not blocked
      pickOnBounds = false

      // Menu shown
      val menu: ContextMenu = new ContextMenu {
        items ++= List(
          new MenuItem("Home") {
            onAction = _ => stage.scene().setRoot(Home.build(player, stage))
          },
          new MenuItem("Garden") {
            onAction = _ => stage.scene().setRoot(Garden.build(stage, player))
          },
          new MenuItem("Log Out") {
            onAction = _ =>
              if player.name.toLowerCase == "guest" then
                // Show warning about data loss (guest)
                val confirmLogout = new Alert(AlertType.Confirmation) {
                  initOwner(stage)
                  title = "Log Out Confirmation"
                  headerText = "Confirm Log out?"
                  contentText = "You're using a guest account. Logging out will result in the loss of all game progress. Proceed?"
                }

                val stylesheet = getClass.getResource("/css/global.css").toExternalForm
                confirmLogout.dialogPane().getStylesheets.add(stylesheet)
                confirmLogout.dialogPane().getStyleClass.add("dialog-pane")

                val response = confirmLogout.showAndWait()
                if response.contains(ButtonType.OK) then
                  stage.scene().setRoot(Landing.build(stage)) // Go back to login screen
              else
                // Confirm logout (normal logged-in players)
                val confirmLogout = new Alert(AlertType.Confirmation) {
                  initOwner(stage)
                  title = "Log Out Confirmation"
                  headerText = "Are you sure you want to log out?"
                  contentText = "You can log in again later with your credentials."
                }
                val response = confirmLogout.showAndWait()
                if response.contains(ButtonType.OK) then
                  stage.scene().setRoot(Landing.build(stage))
          }
        )
      }

      // Hamburger style menu
      val menuButton: Button = new Button("☰") {
        styleClass += "menu-button"
        onAction = _ => menu.show(this, Side.Top, 0, 0)
      }

      children.add(menuButton)

      // Position the button bottom right
      AnchorPane.setBottomAnchor(menuButton, 20.0)
      AnchorPane.setRightAnchor(menuButton, 20.0)
    }