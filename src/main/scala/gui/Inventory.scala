package gui

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.*
import scalafx.scene.layout.*
import scalafx.scene.text.Text
import scalafx.stage.Stage
import model.{CropRegistry, CropType, FoodItem, Player}
import scalafx.util.StringConverter
import utils.FilterUtils.*

object Inventory {

  def build(player: Player, items: Seq[FoodItem], stage: Stage): StackPane = {
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

    // Updates the displayed list of items (with grouping and quantity count)
    def updateItemList(filteredItems: Seq[FoodItem]): Unit = {
      itemList.children.clear()

      // Group same items by name and crop type, then count quantity
      val groupedItems = filteredItems.groupBy(item => (item.name, item.cropType)).map {
        case ((name, cropType), group) => (group.head, group.size)
      }

      // Toggleable btn for each item/grouped item
      groupedItems.toSeq.sortBy(_._1.name).foreach { case (item, count) =>
        // Extra details shown when an item is selected
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
              mouseTransparent = true
              focusTraversable = false
            }
          )
        }

        // Toggle button that controls visibility of item info/details
        val itemContainer = new VBox(0) {
          children = Seq(
            new ToggleButton(s"${item.name} • ${count}x • ${item.calories} cal") {
              toggleGroup = itemToggleGroup
              styleClass.add("item-button")
              maxWidth = Double.MaxValue
              onAction = _ => {
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

    searchField.text.onChange { (_, _, _) => applyFilters() }
    cropTypeFilterBox.onAction = _ => applyFilters()

    // Attach change listeners to trigger filtering
    def applyFilters(): Unit = {
      val query = searchField.text.value.trim
      val selectedCropType = cropTypeFilterBox.value.value

      // Start with the full list
      var filtered = items

      // Apply crop type filter if selected
      filtered = filterByEnumField(filtered, selectedCropType)(_.cropType)

      // Apply search filter if query is not empty
      if (query.nonEmpty) {
        val byName = filterAllByFieldContains(filtered, query)(_.name)
        val byNutrition = filterAllByFieldContains(filtered, query)(_.nutrition)
        val byCropType = filterAllByFieldContainsOpt(filtered, query)(item => Option(item.cropType))
        filtered = (byName ++ byNutrition ++ byCropType).distinct
      }
      // Refresh item list
      updateItemList(filtered)
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

    updateItemList(items) // Initial list (none filter)

    // Final layout containing all inventory components
    val mainLayout = new VBox(20) {
      padding = Insets(20)
      alignment = Pos.TopCenter
      styleClass ++= Seq("bg-base", "inventory-bg")
      children = Seq(
        new Text(s"${player.name}'s Inventory") {
          styleClass.add("title")
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
  }
}
