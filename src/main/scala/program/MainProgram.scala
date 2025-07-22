package program

import gui.Landing
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene

// Main method
object MainProgram extends JFXApp3:
  override def start(): Unit =
    stage = new PrimaryStage:
      title = "Nutri-Farm"
      scene = new Scene(800, 600) {
        stylesheets.add(getClass.getResource("/css/styles.css").toExternalForm)
      }

    stage.scene().setRoot(Landing.build(stage)) // Change current screen to the landing page screen
