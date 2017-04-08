package cs4624.prices.splits.sources

import cs4624.prices.splits.StockSplit

import java.time.LocalDate

trait StockSplitDataSource {
  def get(symbol: String, date: LocalDate): Option[StockSplit]
}
