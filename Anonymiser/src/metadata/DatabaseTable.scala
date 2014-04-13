package metadata


sealed case class DatabaseTable(
  name: String,
  columns: List[Column]
)

sealed case class Column(
  name: String,
  colType: ColumnType
)

sealed case class ColumnType(
  dataType: DataType,
  width: Int
)

object DataType {
  def parse(typeName: String): DataType =
    typeName match {
      case "int" => Integer()
      case "varbinary" => VarBinary()
    }

}

abstract class DataType()

sealed case class Integer() extends DataType
sealed case class VarBinary() extends DataType
