package gui

import model.Player
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.*
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.{HBox, VBox}
import scalafx.stage.Stage
import scalafx.Includes.*

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
          // Will design own logo if enough time
          new Label("🌾 Nutri Farm") {
            styleClass += "app-title"
          },
          // Points + Account Button (top-right)
          new HBox {
            alignment = Pos.CenterRight
            hgrow = scalafx.scene.layout.Priority.Always
            spacing = 10
            children = Seq(
              new Label(s"⭐ ${player.points}") {
                styleClass += "points-label"
              },
              // Account btn which will link to either profile page or prompt user to login
              new Button("Account") {
                styleClass += "account-button"
                onAction = _ =>
                  if player.name.toLowerCase == "guest" then
                    // Show alert if guest account (required login)
                    val confirmLogin = new Alert(AlertType.Confirmation) {
                      initOwner(stage)
                      title = "Login Required"
                      headerText = "Login Required to View Account"
                      contentText = "Would you like to login now?"
                    }
                    val response = confirmLogin.showAndWait()
                    if response.contains(ButtonType.OK) then
                      stage.scene().setRoot(Landing.build(stage))
                  else
                    // Logged-in players go to Profile page
                    stage.scene().setRoot(Profile.build(player, stage))
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
              }
            )
          },
          new Button("Start Farming Now") {
            styleClass += "game-button"
            onAction = _ =>
              stage.scene().setRoot(Garden.build(stage, player)) // Link to garden page
          }
        )
      }

      children = Seq(topBar, centerContent) // Top bar first, then the content
    }
