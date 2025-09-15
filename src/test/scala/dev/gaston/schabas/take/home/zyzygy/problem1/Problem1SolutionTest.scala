package dev.gaston.schabas.take.home.zyzygy.problem1

import dev.gaston.schabas.take.home.zyzygy.problem1.Problem1Solution.{BestGroupPrice, CabinPrice, Rate}
import dev.gaston.schabas.take.home.zyzygy.problem1.{Problem1EdgeCasesGenerators, Problem1HappyPathGenerators}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class Problem1SolutionTest
    extends AnyFunSuite
    with ScalaCheckPropertyChecks
    with Matchers
    with Problem1HappyPathGenerators
    with Problem1EdgeCasesGenerators:

  test("getBestGroupPrices should return the cheapest price per cabin per rate group"):
    val rates = Seq(
      Rate("M1", "Military"),
      Rate("M2", "Military"),
      Rate("S1", "Senior"),
      Rate("S2", "Senior")
    )

    val prices = Seq(
      CabinPrice("CA", "M1", BigDecimal(200.00)),
      CabinPrice("CA", "M2", BigDecimal(250.00)),
      CabinPrice("CA", "S1", BigDecimal(225.00)),
      CabinPrice("CA", "S2", BigDecimal(260.00)),
      CabinPrice("CB", "M1", BigDecimal(230.00)),
      CabinPrice("CB", "M2", BigDecimal(260.00)),
      CabinPrice("CB", "S1", BigDecimal(245.00)),
      CabinPrice("CB", "S2", BigDecimal(270.00))
    )

    val expected = Seq(
      BestGroupPrice("CA", "M1", BigDecimal(200.00), "Military"),
      BestGroupPrice("CA", "S1", BigDecimal(225.00), "Senior"),
      BestGroupPrice("CB", "M1", BigDecimal(230.00), "Military"),
      BestGroupPrice("CB", "S1", BigDecimal(245.00), "Senior")
    )

    Problem1Solution.getBestGroupPrices(rates, prices).toSet should be(expected.toSet)

  test("getBestGroupPrices with gen should return the cheapest price per cabin per rate group") {
    forAll(genHappyPathScenario) { scenario =>
      val result = Problem1Solution.getBestGroupPrices(scenario.rates, scenario.prices)
      result.toSet should be(scenario.expectedBestGroupPrices.toSet)
    }
  }

  test("getBestGroupPrices should handle ties deterministically") {
    forAll(genTieScenario) { scenario =>
      val result = Problem1Solution.getBestGroupPrices(scenario.rates, scenario.prices)

      result should have size 1

      val best = result.head
      best.cabinCode should be(scenario.cabinCode)
      best.price should be(scenario.price)
      best.rateGroup should be(scenario.groupCode)

      best.rateCode should (be(scenario.rateCode1) or be(scenario.rateCode2))
    }
  }

  test("getBestGroupPrices ignore unknown rate codes") {
    forAll(genUnknownRateScenario) { scenario =>
      val result = Problem1Solution.getBestGroupPrices(scenario.rates, scenario.prices)
      val rateCodesInResult = result.map(_.rateCode).toSet

      rateCodesInResult should contain(scenario.validRateCode)
      rateCodesInResult should not contain scenario.unknownRateCode
    }
  }

  test("getBestGroupPrices skip groups with no prices") {
    forAll(genMissingGroupScenario) { scenario =>
      val result = Problem1Solution.getBestGroupPrices(scenario.rates, scenario.prices)
      val groupsInResult = result.map(_.rateGroup).toSet

      groupsInResult should contain(scenario.groupWithPrice)
      groupsInResult should not contain scenario.groupWithoutPrice
    }
  }
