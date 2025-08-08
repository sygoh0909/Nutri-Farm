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

  def loginAsGuest(): Unit = {
    val guestId = System.currentTimeMillis().toInt // Using current timestamp (include date, time)
    val guestPlayer = Player(guestId, "Guest", "", "", 0)
    loggedInPlayer = Some(guestPlayer)
    println(s"Logged in as Guest with ID $guestId")

    Platform.runLater {
      appStage.scene().setRoot(Home.build(guestPlayer, appStage))
    }
  }

  // Login feature (popup)
  def showLoginPopup(): Unit =
    val dialog = new Dialog[Unit]():
      title = "Login"
      headerText = "Enter your credentials"

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
    val stylesheet = getClass.getResource("/css/global.css").toExternalForm // Connect to global css file
    dialog.dialogPane().stylesheets.add(stylesheet) // Add styles to the dialogue

    val emailField = new TextField() { promptText = "Email" }
    val passwordField = new PasswordField() { promptText = "Password" }

    // Validation labels (initially hidden)
    val emailError = new Label("") {
      style = "-fx-text-fill: red; -fx-font-size: 12px"
      visible = false
    }

    val passwordError = new Label("") {
      style = "-fx-text-fill: red; -fx-font-size: 12px"
      visible = false
    }

    val grid = new GridPane:
      hgap = 10
      vgap = 6
      padding = Insets(20)
      add(new Label("Email:"), 0, 0)
      add(emailField, 1, 0)
      add(emailError, 1, 1)
      add(new Label("Password:"), 0, 2)
      add(passwordField, 1, 2)
      add(passwordError, 1, 3)

    dialog.dialogPane().content = grid

    dialog.resultConverter = dialogButton =>
      if dialogButton == ButtonType.OK then
        val email = emailField.text.value.trim
        val password = passwordField.text.value

        var valid = true

        if email.isEmpty then
          emailError.text = "Email is required"
          emailError.visible = true
          valid = false
        else emailError.visible = false

        if password.isEmpty then
          passwordError.text = "Password is required"
          passwordError.visible = true
          valid = false
        else passwordError.visible = false

        if valid then () else null

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
                // Show alert message if unsuccessful
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
    val stylesheet = getClass.getResource("/css/global.css").toExternalForm
    dialog.dialogPane().stylesheets.add(stylesheet)

    val nameField = new TextField() { promptText = "Name" }
    val emailField = new TextField() { promptText = "Email" }
    val passwordField = new PasswordField() { promptText = "Password" }

    val nameError = new Label("") {
      style = "-fx-text-fill: red; -fx-font-size: 12px"
      visible = false
    }
    val emailError = new Label("") {
      style = "-fx-text-fill: red; -fx-font-size: 12px"
      visible = false
    }
    val passwordError = new Label("") {
      style = "-fx-text-fill: red; -fx-font-size: 12px"
      visible = false
    }

    val grid = new GridPane:
      hgap = 10
      vgap = 6
      padding = Insets(20)
      add(new Label("Name:"), 0, 0)
      add(nameField, 1, 0)
      add(nameError, 1, 1)
      add(new Label("Email:"), 0, 2)
      add(emailField, 1, 2)
      add(emailError, 1, 3)
      add(new Label("Password:"), 0, 4)
      add(passwordField, 1, 4)
      add(passwordError, 1, 5)

    dialog.dialogPane().content = grid

    dialog.resultConverter = dialogButton =>
      if dialogButton == ButtonType.OK then
        val name = nameField.text.value.trim
        val email = emailField.text.value.trim
        val password = passwordField.text.value

        var valid = true

        if name.isEmpty then
          nameError.text = "Name is required"
          nameError.visible = true
          valid = false
        else nameError.visible = false

        if email.isEmpty then
          emailError.text = "Email is required"
          emailError.visible = true
          valid = false
        else emailError.visible = false

        if password.length < 6 then
          passwordError.text = "Password must be at least 6 characters"
          passwordError.visible = true
          valid = false
        else passwordError.visible = false

        if valid then () else null

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
            // Successful register account, and added to db
            PlayerDAO.insert(player).onComplete {
              case Success(newId) =>
                val playerWithId = player.copy(id = newId) // now has the actual DB ID
                loggedInPlayer = Some(playerWithId)

                Platform.runLater {
                  new Alert(Alert.AlertType.Information) {
                    contentText = "Registration successful!"
                  }.showAndWait()
                  appStage.scene().setRoot(Home.build(playerWithId, appStage))
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