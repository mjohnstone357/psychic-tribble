package engine

import context.Context
import database.{ContextAndData, Data}

class Engine {

  private val configuration = new Configuration(Set())

  def shouldAnonymiseData(context: Context, config: Configuration) = ???

  def anonymise(data: Data): Data = ???

  def process(contextAndData: ContextAndData) = {
    if (shouldAnonymiseData(contextAndData.context, configuration)) {
      // TODO Handle null values (don't anonymise them)
      anonymise(contextAndData.data)
    } else {
      contextAndData.data
    }
  }
}
