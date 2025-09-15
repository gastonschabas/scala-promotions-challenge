package dev.gaston.schabas.take.home.zyzygy.problem1

object Problem1Solution:

  case class Rate(rateCode: String, rateGroup: String)
  case class CabinPrice(cabinCode: String, rateCode: String, price: BigDecimal)
  case class BestGroupPrice(cabinCode: String, rateCode: String, price: BigDecimal, rateGroup: String)

  /**
   * @param rates list of rates
   * @param prices list of prices
   * @return the best price for each rate group
   */
  def getBestGroupPrices(
    rates: Seq[Rate],
    prices: Seq[CabinPrice]
  ): Seq[BestGroupPrice] =
    val rateCodeGroupMap: Map[String, String] =
      rates.map(r => r.rateCode -> r.rateGroup).toMap

    val bestPriceForEachRateGroupMap: Map[(String, String), BestGroupPrice] =
      prices.foldLeft(Map.empty[(String, String), BestGroupPrice]) { (acc, nextCabinPrice) =>
        rateCodeGroupMap.get(nextCabinPrice.rateCode) match {
          case Some(rateGroup) =>
            val key = (nextCabinPrice.cabinCode, rateGroup)
            acc.get(key) match {
              case Some(existing) if existing.price <= nextCabinPrice.price =>
                acc
              case None | Some(_) =>
                acc + (key -> BestGroupPrice(
                  nextCabinPrice.cabinCode,
                  nextCabinPrice.rateCode,
                  nextCabinPrice.price,
                  rateGroup
                ))
            }
          case None => acc
        }
      }

    bestPriceForEachRateGroupMap.values.toSeq
