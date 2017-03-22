# Trading Stocks with Crowdsourced Aggregated Opinions

*Authors:* [Joseph Watts](https://github.com/joeywatts), [Nick Anderson](https://github.com/nicknea), Connor Asbill, Joseph Mehr

*Clients:* [Saurabh Chakravty](https://github.com/saurabhc123), [Eric Williamson](https://github.com/ewmson)

*Supervisor:* [Prof. Edward Fox](http://fox.cs.vt.edu/)

Algorithmic stock trading based on a complex price prediction engine that factors in the sentiment of microblog posts and the reliability of the posters.

## Getting Started

For more information on how to build this project and some helpful tricks for developing in this environment, see [UsingSBT.md](UsingSBT.md)

## Project Structure

### Common

The "common" subproject contains general utilities used throughout the project.

### Pricing Data

This module is responsible for providing interfaces to access stock pricing data. This data can come from various sources, so this module will hold classes that retrieve the prices from web APIs or read the prices from different file formats. Once the price data is obtained, we store this data in an HBase table.

Other modules can depend on this module to retrieve stock prices.

### Opinion Aggregation

This module implements a method of aggregating sentiment from microblog posts. This implementation is based off of the [CrowdIQ paper]() developed with faculty from Virginia Tech. The CrowdIQ paper describes a method of modeling the reliability of microblog authors based on the dependency of their opinions on other authors as well as the accuracy of their opinions.

### Trading Simulation

This subproject facilitates the simulation of a trading algorithm. It contains classes to model a virtual stock portfolio. By extending the `TradingStrategy` trait, you can define a strategy that can be used to execute stock trades in response to `TradingEvent`s.
