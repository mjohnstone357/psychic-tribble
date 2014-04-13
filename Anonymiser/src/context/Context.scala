package context

/**
 * A context in which a piece of data is to be anonymised.
 * @param tableName the name of the table in which the data resides
 * @param columnName the name of the column in which the data resides
 * @param columnIsForeignKey whether or not this column is used as a foreign key referencing data somewhere else
 * @param columnIsReferenced whether or not this column is the target for a foreign key
 */
sealed case class Context(tableName: String, columnName: String, columnIsForeignKey: Boolean, columnIsReferenced: Boolean)
