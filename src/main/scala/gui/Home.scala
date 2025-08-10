package gui

import model.Player
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.*
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.{HBox, VBox}
import scalafx.stage.Stage
import scalafx.Includes.*
import components.MenuButton

object Home:

  def build(player: Player, stage: Stage): VBox =
    new VBox {
      spacing = 20
      padding = Insets(30)
      styleClass ++= Seq("bg-base", "home-bg")

      // Top bar with main logo, points, and account btn
      val topBar: HBox = new HBox {
        padding = Insets(10)
        spacing = 15
        alignment = Pos.Center
        styleClass += "top-bar"
        hgrow = scalafx.scene.layout.Priority.Always // Allows the element to expand and fill extra horizontal space
        children = Seq(
          // App title/logo
          new Label("🌾 Nutri Farm") {
            styleClass += "app-title"
          },
          // Points + Log Out Button (top-right)
          new HBox {
            alignment = Pos.CenterRight
            hgrow = scalafx.scene.layout.Priority.Always
            spacing = 10
            children = Seq(
              new Label(s"⭐ ${player.points}") {
                styleClass += "points-label"
              },
              // Log out button
              new Button("Log Out") {
                styleClass += "logout-button"
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
                      stage.scene().setRoot(Landing.build(stage))  // Go back to login screen

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
                      stage.scene().setRoot(Landing.build(stage))  // Go back to login screen
              }
            )
          }
        )
      }
      // Main content
      val centerContent: VBox = new VBox {
        spacing = 30
        alignment = Pos.Center
        styleClass += "welcome-container"
        children = Seq(
          new VBox {
            spacing = 10
            alignment = Pos.Center
            children = Seq(
              new Label("Welcome to") {
                styleClass += "welcome-text"
              },
              new Label("Nutri Farm") {
                styleClass += "welcome-title"
              },
              new Label(s"${player.name}!") {
                styleClass += "welcome-text"
              },
            )
          },
          new Button("Start Farming Now") {
            styleClass += "game-button"
            onAction = _ =>
              stage.scene().setRoot(Garden.build(stage, player)) // Link to garden page
          }
        )
      }

      children = Seq(topBar, centerContent, MenuButton.build(stage, player)) // Top bar first, then the content, then only menu nav btn
    }
