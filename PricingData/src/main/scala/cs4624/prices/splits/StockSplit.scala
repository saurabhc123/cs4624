package cs4624.prices.splits

import java.time.LocalDate

case class StockSplit(symbol: String, date: LocalDate, afterAmount: Int, beforeAmount: Int = 1)
