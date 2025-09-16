package dev.gaston.schabas.take.home.zyzygy.problem2

import dev.gaston.schabas.take.home.zyzygy.problem2.Problem2Solution.{Promotion, PromotionCombo}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class Problem2SolutionTest extends AnyFunSuite with Matchers:

  val samplePromotions: Seq[Promotion] = Seq(
    Promotion("P1", Seq("P3")),
    Promotion("P2", Seq("P4", "P5")),
    Promotion("P3", Seq("P1")),
    Promotion("P4", Seq("P2")),
    Promotion("P5", Seq("P2"))
  )

  test("allCombinablePromotions should return all max promotion combos") {
    val expected = Seq(
      PromotionCombo(Seq("P1", "P2")),
      PromotionCombo(Seq("P1", "P4", "P5")),
      PromotionCombo(Seq("P2", "P3")),
      PromotionCombo(Seq("P3", "P4", "P5"))
    )

    val result = Problem2Solution.allCombinablePromotions(samplePromotions)
    result.map(_.withSortedCodes) should contain theSameElementsAs expected.map(_.withSortedCodes)
  }

  test("combinablePromotions should return combos containing the given promotion code P1") {
    val expectedP1 = Seq(
      PromotionCombo(Seq("P1", "P2")),
      PromotionCombo(Seq("P1", "P4", "P5"))
    )

    val resultP1 = Problem2Solution.combinablePromotions("P1", samplePromotions)
    resultP1.map(_.withSortedCodes) should contain theSameElementsAs expectedP1.map(_.withSortedCodes)
  }

  test("combinablePromotions should return combos containing the given promotion code P3") {
    val expectedP3 = Seq(
      PromotionCombo(Seq("P3", "P2")),
      PromotionCombo(Seq("P3", "P4", "P5"))
    )

    val resultP3 = Problem2Solution.combinablePromotions("P3", samplePromotions)
    resultP3.map(_.withSortedCodes) should contain theSameElementsAs expectedP3.map(_.withSortedCodes)
  }

  val mutuallyIncompatiblePromotions: Seq[Promotion] = Seq(
    Promotion("P1", Seq("P2")),
    Promotion("P2", Seq("P1"))
  )

  val allIncompatiblePromotions: Seq[Promotion] = Seq(
    Promotion("P1", Seq("P2", "P3")),
    Promotion("P2", Seq("P1", "P3")),
    Promotion("P3", Seq("P1", "P2"))
  )

  val isolatedPromotions: Seq[Promotion] = Seq(
    Promotion("P1", Seq("P2", "P3")),
    Promotion("P2", Seq("P1")),
    Promotion("P3", Seq("P1"))
  )

  test("allCombinablePromotions should return individual combos when all are mutually incompatible") {
    val expected = Seq(
      PromotionCombo(Seq("P1")),
      PromotionCombo(Seq("P2"))
    )

    val result = Problem2Solution.allCombinablePromotions(mutuallyIncompatiblePromotions)
    result.map(_.withSortedCodes) should contain theSameElementsAs expected.map(_.withSortedCodes)
  }

  test("allCombinablePromotions should return individual combos when each promotion blocks all others") {
    val expected = Seq(
      PromotionCombo(Seq("P1")),
      PromotionCombo(Seq("P2")),
      PromotionCombo(Seq("P3"))
    )

    val result = Problem2Solution.allCombinablePromotions(allIncompatiblePromotions)
    result.map(_.withSortedCodes) should contain theSameElementsAs expected.map(_.withSortedCodes)
  }

  test("combinablePromotions should return only individual combo when promotion is isolated") {
    val expected = Seq(
      PromotionCombo(Seq("P1"))
    )

    val result = Problem2Solution.combinablePromotions("P1", isolatedPromotions)
    result.map(_.withSortedCodes) should contain theSameElementsAs expected.map(_.withSortedCodes)
  }

  test("combinablePromotions should return combo with others when promotion can combine") {
    val expected = Seq(
      PromotionCombo(Seq("P2", "P3"))
    )

    val result = Problem2Solution.combinablePromotions("P2", isolatedPromotions)
    result.map(_.withSortedCodes) should contain theSameElementsAs expected.map(_.withSortedCodes)
  }

  test("allCombinablePromotions should handle empty list") {
    val result = Problem2Solution.allCombinablePromotions(Seq.empty)
    result should be(empty)
  }

  test("combinablePromotions should handle non-existent promotion code") {
    val result = Problem2Solution.combinablePromotions("P99", samplePromotions)
    result should be(empty)
  }

  test("allCombinablePromotions should return the single promotion when only one exists") {
    val single = Seq(Promotion("P1", Seq.empty))
    val expected = Seq(PromotionCombo(Seq("P1")))
    val result = Problem2Solution.allCombinablePromotions(single)
    result.map(_.withSortedCodes) should contain theSameElementsAs expected.map(_.withSortedCodes)
  }

  test("allCombinablePromotions should return one combo with all when all are mutually compatible") {
    val promos = Seq(
      Promotion("P1", Seq.empty),
      Promotion("P2", Seq.empty),
      Promotion("P3", Seq.empty)
    )

    val expected = Seq(PromotionCombo(Seq("P1", "P2", "P3")))
    val result = Problem2Solution.allCombinablePromotions(promos)
    result.map(_.withSortedCodes) should contain theSameElementsAs expected.map(_.withSortedCodes)
  }

  test("allCombinablePromotions should ignore duplicated promotions by code") {
    val promos = Seq(
      Promotion("P1", Seq("P2")),
      Promotion("P1", Seq("P3")),
      Promotion("P2", Seq("P1")),
      Promotion("P3", Seq.empty)
    )

    val expected = Seq(
      PromotionCombo(Seq("P1")),
      PromotionCombo(Seq("P2", "P3"))
    )

    val result = Problem2Solution.allCombinablePromotions(promos)
    result.map(_.withSortedCodes) should contain theSameElementsAs expected.map(_.withSortedCodes)
  }
