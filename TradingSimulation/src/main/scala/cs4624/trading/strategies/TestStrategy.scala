package cs4624.trading.strategies

import cs4624.microblog.aggregation.AggregatedOpinions
import cs4624.microblog.sentiment.{Bearish, Bullish, SentimentAnalysisModel}
import cs4624.microblog.sources.MicroblogDataSource
import cs4624.portfolio.Portfolio
import cs4624.prices.sources.StockPriceDataSource
import cs4624.trading.{TradingEvent, TradingStrategy}
import java.time.{Duration, Instant}

import cs4624.portfolio.error.TransactionError
import cs4624.trading.events._
import org.apache.log4j.LogManager

/**
  * Created by joeywatts on 3/15/17.
  */
class TestStrategy(stocks: Set[String],
                   sentimentAnalysisModel: SentimentAnalysisModel,
                   stockPriceDataSource: StockPriceDataSource,
                   microblogDataSource: MicroblogDataSource) extends TradingStrategy {

  val aggregatedOpinions = new AggregatedOpinions(sentimentAnalysisModel, stockPriceDataSource, Duration.ofDays(3))
  private val log = LogManager.getRootLogger
  var isFirst = true
  var lastTimeOption: Option[Instant] = None

  override def eventSources = Set(
    new MicroblogEventEmitter(microblogDataSource),
    new MarketEventsEmitter()
  )

  override def on(event: TradingEvent, initialPortfolio: Portfolio): Portfolio = {
    event match {
      case MicroblogPostEvent(post) =>
        aggregatedOpinions.on(post)
        initialPortfolio
      case MarketOpen(time) =>
        lastTimeOption match {
          case Some(lastTime) =>
            val opinions = stocks.map { stock =>
              (stock, aggregatedOpinions.sentimentForStock(stock, (lastTime, time)))
            }
            println(opinions)
            aggregatedOpinions.resetSentimentCounts()
            val resultPortfolio = opinions.foldLeft(initialPortfolio) {
              case (portfolio, (stock, Bullish)) =>
                if (portfolio.stocks(stock) > 0)
                  portfolio
                else
                  handleTrade(portfolio.withSharesPurchasedAtValue(time, stock, 0.1 * portfolio.initialCash))
              case (portfolio, (stock, Bearish)) =>
                handleTrade(portfolio.withSharesSold(time, stock, portfolio.stocks(stock)))
            }
            log.info(resultPortfolio)
            resultPortfolio
          case None =>
            lastTimeOption = Some(time)
            initialPortfolio
        }
      case _ => initialPortfolio
    }
  }

  private def handleTrade(retValue: Either[Portfolio, TransactionError]): Portfolio = {
    retValue match {
      case Left(portfolio) => portfolio
      case Right(transactionError) =>
        log.error(transactionError.message)
        transactionError.portfolio
    }
  }

}
