package gui

import model.Player
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.*
// Source: https://javadoc.io/static/org.scalafx/scalafx_3/24.0.0-R35/scalafx/scene/control/Alert$.html
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.{HBox, VBox}
import scalafx.stage.Stage
import scalafx.Includes.*

object Home:

  def build(player: Player, stage: Stage): VBox =
    new VBox {
      spacing = 30
      padding = Insets(40)
      alignment = Pos.Center
      styleClass ++= Seq("bg-base", "home-bg") // Adds multiple CSS classes to the component

      children = Seq(
        new Label("Nutri Farm") {
          style =
            """-fx-font-size: 40px;
              |-fx-font-weight: bold;
              |-fx-text-fill: white;
              |-fx-background-color: rgba(0, 0, 0, 0.5);
              |-fx-padding: 10 20;
              |-fx-background-radius: 10;""".stripMargin // Used to make multi-line strings readable and clean
        },

        new HBox {
          alignment = Pos.Center
          spacing = 10
          children = Seq(
            // Player points
            new Button("View Points") {
              styleClass += "game-button"
              onAction = _ =>
                new Alert(AlertType.Information) {
                  initOwner(stage) // Makes the alert dialog and attach to the given window
                  title = "Your Points"
                  headerText = "Points Summary"
                  contentText = s"You currently have ${player.points} points."
                }.showAndWait()
            }
          )
        },

        new VBox {
          spacing = 20
          alignment = Pos.Center
          children = Seq(
            new Button("Start Farming Now") {
              styleClass += "game-button"
              // Navigate to the planting/garden page
            },
            // Player account with settings page
            new Button("Account") {
              styleClass += "guest-button"
              onAction = _ =>
                if player.name.toLowerCase == "guest" then
                  val confirmLogin = new Alert(AlertType.Confirmation) {
                    initOwner(stage) // Makes the alert dialog and attach to the given window
                    title = "Login Required"
                    headerText = "Login Required to View Account"
                    contentText = "Would you like to login now?"
                  }
                  val response = confirmLogin.showAndWait()
                  if response.contains(ButtonType.OK) then
                    stage.scene.value.setRoot(Landing.build(stage)) // Go to login page
                else
                  new Alert(AlertType.Information) {
                    initOwner(stage)
                    title = "Account"
                    headerText = "Will navigate to profile page"
                  }.showAndWait()
              // Change to Profile page if player logged in
            }
          )
        }
      )
    }
