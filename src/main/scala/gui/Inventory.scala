package gui

import controller.InventoryController
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.*
import scalafx.scene.layout.*
import scalafx.scene.text.Text
import scalafx.stage.Stage
import model.{CropType, Player}
import scalafx.application.Platform
import scalafx.scene.paint.Color
import scalafx.util.StringConverter
import utils.FilterUtils.*
import scala.concurrent.ExecutionContext.Implicits.global

object Inventory:

  def build(player: Player, stage: Stage): StackPane =

    val itemToggleGroup = new ToggleGroup()

    // List of food items
    val itemList = new VBox(8) {
      padding = Insets(10)
      styleClass.add("item-list")
    }

    // Info for each food item
    val infoBox = new VBox(8) {
      padding = Insets(16)
      styleClass.add("info-box")
      visible = false
    }

    // Filter dropdown for CropType
    val cropTypeOptions: Seq[Option[CropType]] = None +: CropType.values.toSeq.map(Some(_))

    // Select crop type filter
    val cropTypeFilterBox = new ComboBox[Option[CropType]](cropTypeOptions) {
      promptText = "Filter by Crop Type"
      value = None // Select "All" by default

      // Convert between CropType and String
      converter = StringConverter(
        (str: String) =>
          cropTypeOptions.find {
            case None => str == "All"
            case Some(value) => value.toString == str
          }.flatten,
        {
          case None => "All"
          case Some(value) => value.toString
        }
      )
    }

    // Search bar state
    var currentQuery: String = ""

    // Search bar
    val searchField = new TextField {
      promptText = "Search by name, nutrition, or crop type"
      prefWidth = 300
    }

    val filterControls = new HBox(12) {
      children = Seq(cropTypeFilterBox, searchField)
      alignment = Pos.CenterLeft
      padding = Insets(0, 0, 8, 0)
    }

    val backBtn = new Button("Back to Home") {
      styleClass.add("game-button")
      onAction = _ => stage.scene().setRoot(Home.build(player, stage))
    }

    // Load inventory from controller
    InventoryController.loadInventory(player).foreach { items =>
      Platform.runLater {
        InventoryController.updateItemList(itemList, infoBox, itemToggleGroup, items)

        // Filtering using controller method
        def applyFilters(): Unit =
          val filtered = InventoryController.filterItems(items, cropTypeFilterBox.value.value, searchField.text.value.trim)
          InventoryController.updateItemList(itemList, infoBox, itemToggleGroup, filtered)

        // Attach change listeners to trigger filtering after data loaded
        searchField.text.onChange((_, _, _) => applyFilters())
        cropTypeFilterBox.onAction = _ => applyFilters()
      }
    }

    // Final layout containing all inventory components
    val mainLayout = new VBox(20) {
      padding = Insets(20)
      alignment = Pos.TopCenter
      styleClass ++= Seq("bg-base", "inventory-bg")
      children = Seq(
        new Text(s"${player.name}'s Inventory") {
          styleClass.add("title")
          fill = Color.White
        },
        new HBox(40) {
          alignment = Pos.TopCenter
          children = Seq(
            // Food items on left
            new VBox(10) {
              children = Seq(
                new Label("Food Items") {
                  styleClass.add("section-title")
                },
                filterControls,
                itemList
              )
              minWidth = 380
              maxWidth = 400
            },
            // Food items info on right
            new VBox(10) {
              children = Seq(
                new Label("Item Details") {
                  styleClass.add("section-title")
                },
                infoBox
              )
              minWidth = 340
              maxWidth = 360
            }
          )
        },
        new Region {
          VBox.setVgrow(this, Priority.Always)
        },
        backBtn
      )
    }
    new StackPane {
      children = Seq(
        mainLayout,
        components.MenuButton.build(stage, player)
      )
    }
