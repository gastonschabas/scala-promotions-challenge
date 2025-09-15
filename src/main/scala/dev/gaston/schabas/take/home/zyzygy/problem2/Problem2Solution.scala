package dev.gaston.schabas.take.home.zyzygy.problem2

object Problem2Solution:

  case class Promotion(code: String, notCombinableWith: Seq[String])
  case class PromotionCombo(promotionCodes: Seq[String])

  /**
   * Implement a function to find all PromotionCombos with maximum number of combinable promotions in each.
   */
  def allCombinablePromotions(allPromotions: Seq[Promotion]): Seq[PromotionCombo] = ???

  /**
   * Implement a function to find all PromotionCombos for a given Promotion from given list of Promotions.
   */
  def combinablePromotions(promotionCode: String, allPromotions: Seq[Promotion]): Seq[PromotionCombo] = ???
