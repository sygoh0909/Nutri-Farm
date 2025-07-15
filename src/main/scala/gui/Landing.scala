package gui

import scalafx.stage.Stage
import db.PlayerDAO
import scalafx.application.Platform
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Alert, Button, Label, TextField}
import scalafx.scene.layout.VBox
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object Landing:

  // Source: https://javadoc.io/doc/org.scalafx/scalafx_3/latest/scalafx/scene/layout/VBox.html
  def build(stage: Stage): VBox =
    val emailField = new TextField {
      promptText = "Enter your email"
    }

    new VBox {
      spacing = 15
      padding = Insets(30)
      alignment = Pos.Center
      children = Seq(
        new Label("Testing"),
        emailField,
        new Button("Find player") {
          onAction = _ =>
            val email = emailField.text.value
            PlayerDAO.findByEmail(email).onComplete {
              case Success(Some(player)) =>
                println(s"Player found: ${player.name}")
                Platform.runLater {
                  new Alert(Alert.AlertType.Information) {
                    contentText = s"Player found: ${player.name}"
                  }.showAndWait()
                }

              case Success(None) =>
                println("No player found with that email.")
                Platform.runLater {
                  new Alert(Alert.AlertType.Warning) {
                    contentText = "No player found with that email."
                  }.showAndWait()
                }

              case Failure(e) =>
                println(s"DB error: ${e.getMessage}")
                Platform.runLater {
                  new Alert(Alert.AlertType.Error) {
                    contentText = s"DB error: ${e.getMessage}"
                  }.showAndWait()
                }
            }
        }
      )
    }
