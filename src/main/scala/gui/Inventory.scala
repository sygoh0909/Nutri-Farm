package gui

import db.FoodDAO
import model.{FoodItem, Player}
import scalafx.Includes.*
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer // Dynamic collection that automatically updates UI controls when modified
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{HBox, VBox, FlowPane}
import scalafx.stage.Stage

import scala.concurrent.ExecutionContext.Implicits.global

object Inventory:

  // Utility: Returns emoji based on crop name
  def cropEmoji(name: String): String = name match
    case "Carrot"  => "🥕" // Match garden crop icons
    case "Tomato"  => "🍅"
    case "Lettuce" => "🥬"
    case _         => "🌱"

  def build(player: Player, stage: Stage): VBox =
    // Container for inventory items
    val inventoryBox = new FlowPane {
      hgap = 15
      vgap = 15
      alignment = Pos.Center
    }

    // Load crops harvested by this player from db
    FoodDAO.getByPlayerId(player.id).map { items =>
      // Group crops by name, count quantity
      val grouped = items.groupBy(_.name).toSeq.map { case (name, group) =>
        val emoji = cropEmoji(name)
        val qty = group.size
        s"$emoji $name x$qty"
      }

      // Update the UI safely
      Platform.runLater {
        inventoryBox.children.clear()
        grouped.foreach { text =>
          inventoryBox.children.add(new Label(text) {
            style =
              """-fx-font-size: 18;
                |-fx-border-color: #ccc;
                |-fx-padding: 10;
                |-fx-background-color: #f9f9f9;
                |-fx-border-radius: 10;
                |-fx-background-radius: 10;
              """.stripMargin
          })
        }
      }
    }

    val titleLabel = new Label("Inventory") {
      style = "-fx-font-size: 20;"
    }

    val backButton = new Button("⬅ Back to Garden") {
      onAction = _ => stage.scene().setRoot(Garden.build(stage, player))
    }

    // Button to meal builder page
    val mealBuilderButton = new Button("Go to Meal Builder") {
      style = "-fx-background-color: #3F51B5; -fx-text-fill: white;"
      onAction = _ =>
        // Load inventory again and pass to MealBuilder
        FoodDAO.getByPlayerId(player.id).foreach { items =>
          Platform.runLater {
            stage.scene().setRoot(MealBuilder.build(player, items, stage))
          }
        }
    }

    new VBox {
      spacing = 20
      padding = Insets(30)
      alignment = Pos.Center
      children = Seq(
        titleLabel,
        inventoryBox,
        new HBox {
          spacing = 10
          alignment = Pos.Center
          children = Seq(mealBuilderButton, backButton)
        }
      )
    }
