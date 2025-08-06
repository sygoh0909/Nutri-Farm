package gui

import controller.LandingController
import scalafx.scene.control.*
import scalafx.scene.layout.{HBox, VBox}
import scalafx.geometry.{Insets, Pos}
import scalafx.stage.Stage

object Landing:

  // GUI layout for the landing page
  def build(stage: Stage): VBox =
    LandingController.setStage(stage) // Call the controller to process

    new VBox {
      spacing = 20
      padding = Insets(40)
      alignment = Pos.Center
      styleClass ++= Seq("bg-base", "landing-bg") // Adds multiple CSS class names to a node's style class list in one line.

      children = Seq(
        new Label("Welcome to Nutri Farm!") {
          styleClass += "welcome-title"
        },
        new HBox {
          spacing = 15
          alignment = Pos.Center
          children = Seq(
            new Button("Login") { // Login Option
              styleClass += "game-button"
              onAction = _ => LandingController.showLoginPopup()
            },
            new Button("Register") { // Register Option
              styleClass += "game-button"
              onAction = _ => LandingController.showRegisterPopup()
            }
          )
        },
        new Button("Continue as Guest") { // Continue as Guest Option
          styleClass += "guest-button"
          onAction = _ => LandingController.loginAsGuest()
        }
      )
    }
