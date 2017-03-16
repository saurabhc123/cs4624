# Trading Stocks with Crowdsourced Aggregated Opinions

*Authors:* [Joseph Watts](https://github.com/joeywatts), [Nick Anderson](https://github.com/nicknea), Connor Asbill, Joseph Mehr

*Clients:* [Saurabh Chakravty](https://github.com/saurabhc123), [Eric Williamson](https://github.com/ewmson)

*Supervisor:* [Prof. Edward Fox](http://fox.cs.vt.edu/)

Algorithmic stock trading based on a complex price prediction engine that factors in the sentiment of microblog posts and the reliability of the posters.

## Getting Started

For more information on how to build this project and some helpful tricks for developing in this environment, see [UsingSBT.md](UsingSBT.md)

## Project Structure

### Common

The "common" subproject contains general purpose code that shouldn't be tied to any one module. For example, Spark configuration code and HBase data access helpers.

### Prediction Engine

The Prediction Engine will house the base code for the implementation of our financial modelling application.

This will utilize machine learning as well as sophisticated modelling of judge reliability.

### Pricing Data

This module is responsible for providing interfaces to access stock pricing data. This data can come from various sources, so this module will hold classes that retrieve the prices from web APIs or read the prices from different file formats. Once the price data is obtained, we store this data in an HBase table.

Other modules can depend on this module to query pricing data from HBase.

### Virtual Portfolio

This module maintains a virtual portfolio. It provides interfaces that help simulate an environment to buy/sell stocks.

### Trading Simulation

This module is the glue the brings the other modules together to run trading simulations.
