package dev.gaston.schabas.take.home.zyzygy.problem2

object Problem2Solution:

  case class Promotion(code: String, notCombinableWith: Seq[String])
  case class PromotionCombo(promotionCodes: Seq[String]):
    def withSortedCodes: PromotionCombo = copy(promotionCodes = promotionCodes.sorted)

  /**
   * @param allPromotions all available promotions
   * @return all PromotionCombos with maximum number of combinable promotions in each.
   */
  def allCombinablePromotions(allPromotions: Seq[Promotion]): Seq[PromotionCombo] =
    val allValidCombos = generateAllValidCombinations(allPromotions)
    findMaximalCombinations(allValidCombos, allPromotions)

  /**
   * @param promotionCode the code of the promotion
   * @param allPromotions all available promotions
   * @return all PromotionCombos for a given Promotion from given list of Promotions.
   */
  def combinablePromotions(
    promotionCode: String,
    allPromotions: Seq[Promotion]
  ): Seq[PromotionCombo] =
    if allPromotions.exists(_.code == promotionCode) then
      val validCombosWithCode = generateValidCombinationsContaining(promotionCode, allPromotions)
      findMaximalCombinations(validCombosWithCode, allPromotions)
    else Seq.empty

  /**
   * Generate all valid combinations
   */
  private def generateAllValidCombinations(allPromotions: Seq[Promotion]): Seq[PromotionCombo] =
    val codes = allPromotions.map(_.code)
    val promotionMap = buildPromotionMap(allPromotions)

    for
      size        <- 1 to codes.length
      combination <- codes.combinations(size)
      if isValidCombination(combination, promotionMap)
    yield PromotionCombo(combination)

  /**
   * Generate all valid combinations containing the target code
   */
  private def generateValidCombinationsContaining(
    targetCode: String,
    allPromotions: Seq[Promotion]
  ): Seq[PromotionCombo] =
    val otherCodes = allPromotions.map(_.code).filterNot(_ == targetCode)
    val promotionMap = buildPromotionMap(allPromotions)

    for
      size        <- 0 to otherCodes.length
      combination <- otherCodes.combinations(size)
      fullCombo = targetCode +: combination
      if isValidCombination(fullCombo, promotionMap)
    yield PromotionCombo(fullCombo)

  /**
   * Pick the maximal from valid combinations list.
   */
  private def findMaximalCombinations(
    validCombos: Seq[PromotionCombo],
    allPromotions: Seq[Promotion]
  ): Seq[PromotionCombo] =
    val allCodes = allPromotions.map(_.code)
    val promotionMap = buildPromotionMap(allPromotions)

    validCombos.filter: combo =>
      val unusedCodes = allCodes.filterNot(combo.promotionCodes.contains)
      unusedCodes.forall: newCode =>
        val extendedCombo = combo.promotionCodes :+ newCode
        !isValidCombination(extendedCombo, promotionMap)

  /**
   * Validate whether a given set of promotion codes forms a combinable group.
   */
  private def isValidCombination(
    codes: Seq[String],
    promotionMap: Map[String, Set[String]]
  ): Boolean =
    codes.forall: code =>
      val incompatibleCodes = promotionMap.getOrElse(code, Set.empty)
      val otherCodes = codes.filterNot(_ == code)
      otherCodes.forall(!incompatibleCodes.contains(_))

  /**
   * Build a lookup map where each promotion code is associated with the list of codes it cannot be combined with. This
   * precomputation avoids repeatedly scanning the full list of promotions during validation.
   */
  private def buildPromotionMap(allPromotions: Seq[Promotion]): Map[String, Set[String]] =
    allPromotions.groupMapReduce(_.code)(_.notCombinableWith.toSet)(_ ++ _)
