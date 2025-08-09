package controller

import db.FoodDAO
import scalafx.geometry.Insets
import scalafx.scene.control.*
import scalafx.scene.layout.*
import scalafx.scene.text.Text
import model.{CropRegistry, CropType, FoodItem, Player}
import utils.FilterUtils.*
import scala.concurrent.{ExecutionContext, Future}

object InventoryController:

  // Load inventory
  def loadInventory(player: Player)(using ec: ExecutionContext): Future[Seq[FoodItem]] =
    FoodDAO.getByPlayerId(player.id)

  // Updates the displayed list of items (with grouping and quantity count)
  def updateItemList(itemList: VBox, infoBox: VBox, itemToggleGroup: ToggleGroup, filteredItems: Seq[FoodItem]): Unit =
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

  def filterItems(items: Seq[FoodItem], cropType: Option[CropType], query: String): Seq[FoodItem] =
    var filtered = filterByEnumField(items, cropType)(_.cropType)
    if (query.nonEmpty) {
      val byName = filterAllByFieldContains(filtered, query)(_.name)
      val byNutrition = filterAllByFieldContains(filtered, query)(_.nutrition)
      val byCropType = filterAllByFieldContainsOpt(filtered, query)(item => Option(item.cropType))
      filtered = (byName ++ byNutrition ++ byCropType).distinct
    }
    filtered


