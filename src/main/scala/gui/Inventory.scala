package gui

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.*
import scalafx.scene.layout.*
import scalafx.scene.text.Text
import scalafx.stage.Stage
import model.{CropRegistry, FoodItem, Player}

object Inventory {
  
  def build(player: Player, items: Seq[FoodItem], stage: Stage): VBox = {
    // Create toggle group for item selection (only one can be selected at a time)
    val itemToggleGroup = new ToggleGroup()

    val itemList = new VBox(8) {
      padding = Insets(10)
      styleClass.add("item-list")
    }

    val infoBox = new VBox(8) {
      padding = Insets(16)
      styleClass.add("info-box")
      visible = false // Initially hidden
    }

    items.foreach { item =>
      // Show when food item is clicked
      val expandableContent = new VBox(8) {
        styleClass.add("expandable-content")
        padding = Insets(8, 0, 0, 16)
        children = Seq(
          new Text(s"Nutrition: ${item.nutrition}") {
            styleClass.add("detail-text")
          },
          new Text(s"Calories: ${item.calories}") {
            styleClass.add("detail-text")
          },
          new Label("Suggested Recipes:") {
            styleClass.add("detail-title")
          },
          new ListView[String](
            CropRegistry.getByName(item.name).map(_.recipes).getOrElse(Seq("No recipes found"))
          ) {
            styleClass.add("recipe-list")
            prefHeight = 100
            mouseTransparent = true  // Disable mouse interaction
            focusTraversable = false  // Prevent keyboard focus
          }
        )
      }

      val itemContainer = new VBox(0) {
        children = Seq(
          // Toggle btn for each item
          new ToggleButton(s"${item.name} • ${item.calories} cal") {
            this.toggleGroup = itemToggleGroup
            styleClass.add("item-button")
            maxWidth = Double.MaxValue
            onAction = _ => {
              // Show or hide info on click
              if (selected.value) {
                infoBox.children = Seq(
                  new Text(item.name) {
                    styleClass.add("detail-title")
                  },
                  expandableContent
                )
                infoBox.visible = true
              } else {
                infoBox.visible = false
              }
            }
          }
        )
      }
      itemList.children.add(itemContainer)
    }

    // Return to home page
    val backBtn = new Button("Back to Home") {
      styleClass.add("game-button")
      onAction = _ => stage.scene().setRoot(Home.build(player, stage))
    }

    // Final layout containing all inventory components
    new VBox(20) {
      padding = Insets(20)
      alignment = Pos.TopCenter
      styleClass ++= Seq("bg-base", "inventory-bg")

      children = Seq(
        new Text(s"${player.name}'s Inventory") {
          styleClass.add("title")
        },
        new HBox(20) {
          alignment = Pos.TopCenter
          children = Seq(
            // Food items on left
            new VBox {
              children = Seq(
                new Label("Food Items") {
                  styleClass.add("section-title")
                },
                itemList
              )
              minWidth = 280
              maxWidth = 280
            },
            // Food items info on right
            new VBox {
              children = Seq(
                new Label("Item Details") {
                  styleClass.add("section-title")
                },
                infoBox
              )
              minWidth = 320
              maxWidth = 320
            }
          )
        },
        // Creates an invisible empty area that pushes the Back button to the bottom of the screen
        new Region {
          VBox.setVgrow(this, Priority.Always)
        },
        backBtn
      )
    }
  }
}