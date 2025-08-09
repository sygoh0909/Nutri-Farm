package utils

object FilterUtils:

  // Filter by enum field
  def filterByEnumField[T, E](items: Seq[T], selected: Option[E])(extractField: T => E): Seq[T] =
    selected match {
      case Some(enumValue) => items.filter(item => extractField(item) == enumValue)
      case None => items
    }

  // Finds the first item where the field exactly matches the value (Future use)
  def filterByField[T](items: Seq[T], value: String)(extractField: T => String): Option[T] =
    items.find(item => normalize(extractField(item)) == normalize(value))

  // Finds all items where the field exactly matches the value
  def filterAllByField[T](items: Seq[T], value: String)(extractField: T => String): Seq[T] =
    items.filter(item => normalize(extractField(item)) == normalize(value))

  // Finds all items where the field contains the search value (partial matches also)
  def filterAllByFieldContains[T](items: Seq[T], value: String)(extractField: T => String): Seq[T] =
    items.filter(item => normalize(extractField(item)).contains(normalize(value)))

  // Works on Option field
  def filterAllByFieldContainsOpt[T](items: Seq[T], value: String)(extractField: T => Option[Any]): Seq[T] =
    items.filter(item =>
      extractField(item).exists(fieldVal => normalize(fieldVal.toString).contains(normalize(value)))
    )

  // Makes a string lowercase and trims spaces
  private def normalize(s: String): String = Option(s).map(_.toLowerCase.trim).getOrElse("")
