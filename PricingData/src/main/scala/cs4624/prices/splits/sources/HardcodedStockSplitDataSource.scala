package cs4624.prices.splits.sources

import cs4624.prices.splits.StockSplit
import java.time.LocalDate

class HardcodedStockSplitDataSource(data: Map[(String, LocalDate), StockSplit]) extends StockSplitDataSource {
  override def get(symbol: String, date: LocalDate): Option[StockSplit] = {
    data.get((symbol, date))
  }
}
