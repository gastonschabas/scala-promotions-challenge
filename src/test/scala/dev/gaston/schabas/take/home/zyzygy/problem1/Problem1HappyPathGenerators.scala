package dev.gaston.schabas.take.home.zyzygy.problem1

import dev.gaston.schabas.take.home.zyzygy.problem1.Problem1Solution.{BestGroupPrice, CabinPrice, Rate}
import org.scalacheck.Gen

trait Problem1HappyPathGenerators extends Problem1CommonGenerators:

  val genCabinCodes: Gen[Seq[String]] =
    for
      n      <- Gen.choose(2, 50)
      cabins <- Gen.listOfN(n, genNonEmptyAlphaStr)
    yield cabins.distinct

  val genGroupCodes: Gen[Seq[String]] =
    for
      n      <- Gen.choose(2, 50)
      groups <- Gen.listOfN(n, genNonEmptyAlphaStr)
    yield groups.distinct

  /**
   * Generates a Rate for the given group with a unique rateCode.
   *
   * The `rateCode` is composed of a random non-empty string, the group code, and an index to guarantee uniqueness.
   */
  def genRateWithUniqueCode(groupCode: String, index: Int): Gen[Rate] =
    genNonEmptyAlphaStr.map(rc => Rate(s"$rc-$groupCode-$index", groupCode))

  /**
   * Generates a sequence of Gen[Rate] for a specific rate group.
   *
   * Each Rate generated has a unique rateCode ensured by the index.
   */
  def genRatesForGroup(group: String, n: Int): Seq[Gen[Rate]] =
    (1 to n).map { idx => genRateWithUniqueCode(group, idx) }

  val genRates: Gen[Seq[Rate]] =
    for
      groupCodes     <- genGroupCodes
      nRatesPerGroup <- Gen.choose(2, 25)
      rates          <- Gen.sequence[Seq[Rate], Rate](groupCodes.flatMap(gc => genRatesForGroup(gc, nRatesPerGroup)))
    yield rates

  /**
   * Group a list of cabins with same cabin code and group code with different rate codes having their best price
   *
   * @param cabinPrices list of cabins for the same specific cabin code and rate group
   * @param bestPrice the best price for that rate group and cabin code
   */
  case class CabinGroupPrices(
    cabinPrices: Seq[CabinPrice],
    bestPrice: BestGroupPrice
  )

  /**
   * Generates a list of unique positive prices for each Rate in a given group. Ensures that the minimum price is
   * deterministic and prices differ by a fixed step.
   */
  def genUniquePricesForRates(rates: Seq[Rate]): Gen[Seq[(String, BigDecimal)]] = {
    val step = BigDecimal(0.05)

    for minPrice <- Gen.choose(BigDecimal(10), BigDecimal(300)).map(_.setScale(2, BigDecimal.RoundingMode.HALF_UP))
    yield rates.zipWithIndex.map { case (r, idx) =>
      r.rateCode -> (minPrice + step * idx)
    }
  }

  /**
   * @param ratePricePairs a sequence of tuples where each tuple contains a rate code and its associated price
   * @return
   *   a `CabinGroupPrices` containing:
   *   - a list of `CabinPrice` for each rate code in the group
   *   - the `BestGroupPrice` representing the lowest price among the provided rate codes for this cabin and group
   */
  def makeCabinGroupPrices(
    cabinCode: String,
    groupCode: String,
    ratePricePairs: Seq[(String, BigDecimal)]
  ): CabinGroupPrices = {
    val (minRateCode, minPrice) = ratePricePairs.minBy(_._2)
    val cabinPrices = ratePricePairs.map { case (rc, p) => CabinPrice(cabinCode, rc, p) }
    CabinGroupPrices(cabinPrices, BestGroupPrice(cabinCode, minRateCode, minPrice, groupCode))
  }

  /**
   * Generates cabins and prices for each group, computing best price per cabin per group
   */
  def genCabinGroupPrices(
    rates: Seq[Rate],
    cabinCodes: Seq[String]
  ): Gen[Seq[CabinGroupPrices]] =
    val groupedRates = rates.groupBy(_.rateGroup)

    Gen.sequence[Seq[CabinGroupPrices], CabinGroupPrices](
      cabinCodes.flatMap { cabinCode =>
        groupedRates.toSeq.map { case (group, groupRates) =>
          val pricesGen: Gen[Seq[(String, BigDecimal)]] = genUniquePricesForRates(groupRates)
          pricesGen.map(ratePricePairs => makeCabinGroupPrices(cabinCode, group, ratePricePairs))
        }
      }
    )

  case class HappyPathScenario(
    rates: Seq[Rate],
    prices: Seq[CabinPrice],
    expectedBestGroupPrices: Seq[BestGroupPrice]
  )

  val genHappyPathScenario: Gen[HappyPathScenario] =
    for
      cabinCodes       <- genCabinCodes
      rates            <- genRates
      cabinsWithPrices <- genCabinGroupPrices(rates, cabinCodes)
    yield
      val dups = cabinsWithPrices
        .flatMap(_.cabinPrices)
        .groupBy(cp => (cp.cabinCode, cp.rateCode))
        .collect { case (k, vs) if vs.size > 1 => (k, vs.map(_.price)) }

      val allPrices = cabinsWithPrices.flatMap(_.cabinPrices)
      val allBest = cabinsWithPrices.map(_.bestPrice)
      HappyPathScenario(rates, allPrices, allBest)
