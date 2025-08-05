package controller

import db.PlayerDAO
import model.Player
import scalafx.application.Platform
import scalafx.stage.Stage
import gui.Home
import scalafx.Includes.*
import scala.concurrent.ExecutionContext.Implicits.global // Allows Future operations to run asynchronously in the bg
import gui.Landing.getClass
import org.mindrot.jbcrypt.BCrypt
import scalafx.geometry.Insets
import scalafx.scene.control.{Alert, ButtonType, Dialog, Label, PasswordField, TextField}
import scalafx.scene.layout.GridPane

import scala.util.{Failure, Success}

object LandingController:
  
  var loggedInPlayer: Option[Player] = None
  private var appStage: Stage = _
  
  def setStage (stage: Stage): Unit =
    appStage = stage

  var guestCounter = 0 // Global counter, only increases per guest login

  def loginAsGuest(): Unit =
    guestCounter -= 1
    loggedInPlayer = Some(Player(guestCounter, "Guest", "", "", 0))
    println(s"Logged in as guest with ID $guestCounter")
    Platform.runLater {
      appStage.scene().setRoot(Home.build(loggedInPlayer.get, appStage))
    }

  // Login feature (popup)
  def showLoginPopup(): Unit =
    val dialog = new Dialog[Unit]():
      title = "Login"
      headerText = "Enter your credentials"

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
    val stylesheet = getClass.getResource("/css/styles.css").toExternalForm // Connect to global css file
    dialog.dialogPane().stylesheets.add(stylesheet) // Add styles to the dialogue

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
            if BCrypt.checkpw(password, player.passwordHash) then // Check/compare password hash
              loggedInPlayer = Some(player)
              println(s"Login successful! Welcome, ${player.name}.") // For testing
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
    val stylesheet = getClass.getResource("/css/styles.css").toExternalForm
    dialog.dialogPane().stylesheets.add(stylesheet)

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
            val player = Player(0, name, email, hashed, 0)
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