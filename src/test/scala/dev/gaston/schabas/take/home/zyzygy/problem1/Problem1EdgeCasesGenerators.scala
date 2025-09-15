package dev.gaston.schabas.take.home.zyzygy.problem1

import dev.gaston.schabas.take.home.zyzygy.problem1.Problem1Solution.{CabinPrice, Rate}
import org.scalacheck.Gen

trait Problem1EdgeCasesGenerators extends Problem1CommonGenerators:

  case class TieScenario(
    rates: Seq[Rate],
    prices: Seq[CabinPrice],
    groupCode: String,
    cabinCode: String,
    rateCode1: String,
    rateCode2: String,
    price: BigDecimal
  )

  val genTieScenario: Gen[TieScenario] =
    for
      groupCode <- genNonEmptyAlphaStr
      cabinCode <- genNonEmptyAlphaStr
      rateCode1 <- genNonEmptyAlphaStr
      rateCode2 <- Gen.alphaStr.suchThat(code => code.nonEmpty && code != rateCode1)
      price     <- genPositivePrice
    yield
      val rate1 = Rate(rateCode1, groupCode)
      val rate2 = Rate(rateCode2, groupCode)
      val rates = Seq(rate1, rate2)
      val prices = Seq(
        CabinPrice(cabinCode, rate1.rateCode, price),
        CabinPrice(cabinCode, rate2.rateCode, price)
      )
      TieScenario(rates, prices, groupCode, cabinCode, rateCode1, rateCode2, price)

  case class UnknownRateScenario(
    rates: Seq[Rate],
    prices: Seq[CabinPrice],
    groupCode: String,
    cabinCode: String,
    validRateCode: String,
    unknownRateCode: String,
    validPrice: BigDecimal,
    unknownPrice: BigDecimal
  )

  val genUnknownRateScenario: Gen[UnknownRateScenario] =
    for
      groupCode       <- genNonEmptyAlphaStr
      cabinCode       <- genNonEmptyAlphaStr
      validRateCode   <- genNonEmptyAlphaStr
      unknownRateCode <- Gen.alphaStr.suchThat(code => code.nonEmpty && code != validRateCode)
      validPrice      <- genPositivePrice
      unknownPrice    <- genPositivePrice
    yield
      val rate = Rate(validRateCode, groupCode)
      val rates = Seq(rate)
      val prices = Seq(
        CabinPrice(cabinCode, rate.rateCode, validPrice),
        CabinPrice(cabinCode, unknownRateCode, unknownPrice)
      )
      UnknownRateScenario(rates, prices, groupCode, cabinCode, validRateCode, unknownRateCode, validPrice, unknownPrice)

  case class MissingGroupScenario(
    rates: Seq[Rate],
    prices: Seq[CabinPrice],
    cabinCode: String,
    rateWithPrice: String,
    rateWithoutPrice: String,
    groupWithPrice: String,
    groupWithoutPrice: String,
    price: BigDecimal
  )

  val genMissingGroupScenario: Gen[MissingGroupScenario] =
    for
      groupCode1 <- genNonEmptyAlphaStr
      groupCode2 <- Gen.alphaStr.suchThat(code => code.nonEmpty && code != groupCode1)
      cabinCode  <- genNonEmptyAlphaStr
      rateCode1  <- genNonEmptyAlphaStr
      rateCode2  <- genNonEmptyAlphaStr
      price      <- genPositivePrice
    yield
      val rate1 = Rate(rateCode1, groupCode1)
      val rate2 = Rate(rateCode2, groupCode2)
      val rates = Seq(rate1, rate2)
      val prices = Seq(
        CabinPrice(cabinCode, rate1.rateCode, price)
      )
      MissingGroupScenario(rates, prices, cabinCode, rateCode1, rateCode2, groupCode1, groupCode2, price)
