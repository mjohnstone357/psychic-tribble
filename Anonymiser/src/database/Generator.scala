package database

import context.Context


class Generator {

  def loadData(): Iterable[ContextAndData] = ???

}

sealed case class ContextAndData(context: Context, data: Data)


