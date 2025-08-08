package program

import gui.Landing
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene

// Documentation references used throughout the project are stated in the read me file

// Main method
object MainProgram extends JFXApp3:
  override def start(): Unit =
    stage = new PrimaryStage:
      title = "Nutri-Farm"
      scene = new Scene(1280, 720) {
        stylesheets.add(getClass.getResource("/css/global.css").toExternalForm)
        stylesheets.add(getClass.getResource("/css/home.css").toExternalForm)
        stylesheets.add(getClass.getResource("/css/garden.css").toExternalForm)
        stylesheets.add(getClass.getResource("/css/inventory.css").toExternalForm)
      }

    stage.scene().setRoot(Landing.build(stage))
