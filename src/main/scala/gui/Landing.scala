package gui

import controller.LandingController
import scalafx.scene.control.*
import scalafx.scene.layout.{HBox, VBox}
import scalafx.geometry.{Insets, Pos}
import scalafx.Includes.*
import scalafx.stage.Stage

object Landing:

  // Source: https://javadoc.io/doc/org.scalafx/scalafx_3/latest/scalafx/scene/layout/VBox.html
  // GUI layout for the landing page
  def build(stage: Stage): VBox =
    LandingController.setStage(stage) // Call the controller to process

    new VBox {
      spacing = 20
      padding = Insets(40)
      alignment = Pos.Center
      style = "-fx-background-image: url('" + getClass.getResource("/bg.jpg").toExternalForm + "');" +
        "-fx-background-size: cover;" +
        "-fx-background-repeat: no-repeat;" +
        "-fx-background-position: center center;"
      children = Seq(
        new Label("Welcome to Nutri Farm!") {
          styleClass += "title"
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
