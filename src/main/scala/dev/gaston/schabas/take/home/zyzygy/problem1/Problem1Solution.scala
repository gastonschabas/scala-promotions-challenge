package dev.gaston.schabas.take.home.zyzygy.problem1

object Problem1Solution:

  case class Rate(rateCode: String, rateGroup: String)
  case class CabinPrice(cabinCode: String, rateCode: String, price: BigDecimal)
  case class BestGroupPrice(cabinCode: String, rateCode: String, price: BigDecimal, rateGroup: String)

  /**
   * Write a function that will take a list of rates and a list of prices and returns the best price for each rate
   * group. We’ve supplied the function and case class definitions below for you to use
   */
  def getBestGroupPrices(rates: Seq[Rate], prices: Seq[CabinPrice]): Seq[BestGroupPrice] = ???
