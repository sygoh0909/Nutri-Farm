package gui

import db.PlayerDAO
import model.Player
import scalafx.stage.Stage
import scalafx.scene.control.*
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.geometry.{Insets, Pos}
import scalafx.Includes.*
import org.mindrot.jbcrypt.BCrypt
// Source: https://docs.scala-lang.org/overviews/core/futures.html
import scala.concurrent.ExecutionContext.Implicits.global // Allows Future operations to run asynchronously in the bg
import scala.util.{Failure, Success}
import scalafx.application.Platform

object Landing:

  var loggedInPlayer: Option[Player] = None
  private var appStage: Stage = _

  // Source: https://javadoc.io/doc/org.scalafx/scalafx_3/latest/scalafx/scene/layout/VBox.html
  // GUI layout for the landing page
  def build(stage: Stage): VBox =
    appStage = stage

    new VBox {
      spacing = 20
      padding = Insets(40)
      alignment = Pos.Center
      children = Seq(
        new Label("Welcome to Nutri Farm!") {
          styleClass += "title"
        },
        new HBox {
          spacing = 15
          alignment = Pos.Center
          children = Seq(
            new Button("🔐 Login") { // Login Option
              styleClass += "game-button"
              onAction = _ => showLoginPopup()
            },
            new Button("📝 Register") { // Register Option
              styleClass += "game-button"
              onAction = _ => showRegisterPopup()
            }
          )
        },
        new Button("🚶 Continue as Guest") { // Continue as Guest Option
          styleClass += "guest-button"
          onAction = _ =>
            loggedInPlayer = Some(Player(0, "Guest", "", "", 0.0))
            println("Logged in as guest")
            Platform.runLater {
              appStage.scene().setRoot(Home.build(loggedInPlayer.get, appStage))
            }
        }
      )
    }

  // Login feature (popup)
  def showLoginPopup(): Unit =
    // Source: https://javadoc.io/doc/org.scalafx/scalafx_3/latest/scalafx/scene/control/Dialog.html
    val dialog = new Dialog[Unit]():
      title = "Login"
      headerText = "Enter your credentials"

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)

    val emailField = new TextField() { promptText = "Email" }
    val passwordField = new PasswordField() { promptText = "Password" }

    val grid = new GridPane:
      hgap = 10
      vgap = 10
      padding = Insets(20)
      add(new Label("Email:"), 0, 0)
      add(emailField, 1, 0)
      add(new Label("Password:"), 0, 1)
      add(passwordField, 1, 1)

    dialog.dialogPane().content = grid

    // Only runs if user clicks OK
    dialog.resultConverter = dialogButton =>
      if dialogButton == ButtonType.OK then
        val email = emailField.text.value
        val password = passwordField.text.value

        // Asynchronous call (to db)
        PlayerDAO.findByEmail(email).onComplete { // on Complete is Future operations
          case Success(Some(player)) =>
            // Source: https://github.com/jeremyh/jBCrypt
            if BCrypt.checkpw(password, player.passwordHash) then // Check/compare password hash
              loggedInPlayer = Some(player)
              println(s"Login successful! Welcome, ${player.name}.") // For testing
              // Source: https://javadoc.io/doc/org.scalafx/scalafx_3/latest/scalafx/application/Platform$.html#runLater(action:=%3EUnit):Unit
              Platform.runLater { // Used to safely update the UI from a background thread
                appStage.scene().setRoot(Home.build(player, appStage)) // Bring user to home page if successful validated
              }
            else
              Platform.runLater {
                // Show alert popup with error message if unsuccessful
                new Alert(Alert.AlertType.Error) {
                  contentText = "Incorrect password."
                }.showAndWait()
              }
          case Success(None) =>
            Platform.runLater {
              new Alert(Alert.AlertType.Warning) {
                contentText = "Email not found."
              }.showAndWait()
            }
          case Failure(e) =>
            Platform.runLater {
              new Alert(Alert.AlertType.Error) {
                contentText = s"Login error: ${e.getMessage}"
              }.showAndWait()
            }
        }

    dialog.showAndWait()

  // Register feature (popup)
  def showRegisterPopup(): Unit =
    val dialog = new Dialog[Unit]():
      title = "Register"
      headerText = "Create a new account"

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)

    val nameField = new TextField() { promptText = "Name" }
    val emailField = new TextField() { promptText = "Email" }
    val passwordField = new PasswordField() { promptText = "Password" }

    val grid = new GridPane:
      hgap = 10
      vgap = 10
      padding = Insets(20)
      add(new Label("Name:"), 0, 0)
      add(nameField, 1, 0)
      add(new Label("Email:"), 0, 1)
      add(emailField, 1, 1)
      add(new Label("Password:"), 0, 2)
      add(passwordField, 1, 2)

    dialog.dialogPane().content = grid

    dialog.resultConverter = dialogButton =>
      if dialogButton == ButtonType.OK then
        val name = nameField.text.value
        val email = emailField.text.value
        val password = passwordField.text.value

        PlayerDAO.findByEmail(email).onComplete {
          case Success(Some(_)) =>
            Platform.runLater {
              new Alert(Alert.AlertType.Warning) {
                contentText = "Email already registered."
              }.showAndWait()
            }
          case Success(None) =>
            val hashed = BCrypt.hashpw(password, BCrypt.gensalt()) // Encrypt password for safety purposes
            val player = Player(0, name, email, hashed, 0.0)
            PlayerDAO.insert(player).onComplete {
              // Successful register account, and added to db
              case Success(_) =>
                loggedInPlayer = Some(player)
                Platform.runLater {
                  new Alert(Alert.AlertType.Information) {
                    contentText = "Registration successful! You can now log in."
                  }.showAndWait()
                  appStage.scene().setRoot(Home.build(player, appStage))
                }
              case Failure(e) =>
                Platform.runLater {
                  new Alert(Alert.AlertType.Error) {
                    contentText = s"Registration failed: ${e.getMessage}"
                  }.showAndWait()
                }
            }
          case Failure(e) =>
            Platform.runLater {
              new Alert(Alert.AlertType.Error) {
                contentText = s"Error checking email: ${e.getMessage}"
              }.showAndWait()
            }
        }

    dialog.showAndWait()
