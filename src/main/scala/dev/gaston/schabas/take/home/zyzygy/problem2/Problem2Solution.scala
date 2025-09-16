package dev.gaston.schabas.take.home.zyzygy.problem2

object Problem2Solution:

  case class Promotion(code: String, notCombinableWith: Seq[String])
  case class PromotionCombo(promotionCodes: Seq[String]):
    def withSortedCodes: PromotionCombo = copy(promotionCodes = promotionCodes.sorted)

  /**
   * Compute all maximal combinable promotion sets.
   *
   * Pipeline:
   *   - Canonicalize input by merging duplicate codes and symmetrizing incompatibilities (via promotion map).
   *   - Build the compatibility graph (undirected).
   *   - Use Bron–Kerbosch with pivoting to enumerate maximal cliques (each clique is a combinable set).
   *
   * Notes:
   *   - Returns empty when no promotions are provided.
   *   - Result order is not guaranteed; sort externally for deterministic comparisons.
   *   - Codes inside PromotionCombo are returned as provided; tests may sort via `withSortedCodes`.
   *
   * @param allPromotions all available promotions (duplicates by code are allowed)
   * @return all PromotionCombos with maximum number of combinable promotions in each (order not guaranteed)
   */
  def allCombinablePromotions(allPromotions: Seq[Promotion]): Seq[PromotionCombo] =
    val promotionMap = buildPromotionMap(allPromotions)
    if promotionMap.isEmpty then Seq.empty
    else
      val compatibilityGraph = buildCompatibilityGraph(promotionMap)
      val cliques = bronKerboschPivot(Set.empty, compatibilityGraph.keySet, Set.empty, compatibilityGraph)
      cliques
        .filter(_.nonEmpty)
        .map(c => PromotionCombo(c.toSeq))
        .toSeq

  /**
   * Compute all maximal combinable promotion sets that include a given promotion code.
   *
   * Implementation seeds Bron–Kerbosch with R={promotionCode}, P=neighbors(promotionCode), X=∅ to enumerate only
   * maximal cliques containing the code.
   *
   * Notes:
   *   - If the code is not present after canonicalization (merge + symmetry), returns an empty result.
   *   - Result order is not guaranteed; sort externally for deterministic comparisons.
   *
   * @param promotionCode the code of the promotion to include
   * @param allPromotions all available promotions (duplicates by code are allowed)
   * @return all maximal PromotionCombos containing the given code (order not guaranteed)
   */
  def combinablePromotions(
    promotionCode: String,
    allPromotions: Seq[Promotion]
  ): Seq[PromotionCombo] =
    val promotionMap = buildPromotionMap(allPromotions)
    if !promotionMap.contains(promotionCode) then Seq.empty
    else
      val compatibilityGraph = buildCompatibilityGraph(promotionMap)
      val r = Set(promotionCode)
      val p = compatibilityGraph.getOrElse(promotionCode, Set.empty)
      val x = Set.empty[String]
      val cliques = bronKerboschPivot(r, p, x, compatibilityGraph)
      cliques.map(c => PromotionCombo(c.toSeq)).toSeq

  /**
   * Build a canonical lookup map code -> incompatible codes.
   *
   *   - Duplicates by code are merged by unioning their `notCombinableWith` sets.
   *   - Incompatibilities are symmetrized so the relation can be treated as undirected.
   *   - Complexity: O(n + m), where n is number of codes and m the number of declared incompatibilities.
   *   - Output map does not guarantee key order; sort keys externally if a stable order is needed.
   *
   * This precomputation avoids repeatedly scanning the full list of promotions during validation.
   */
  private def buildPromotionMap(allPromotions: Seq[Promotion]): Map[String, Set[String]] =
    val merged: Map[String, Set[String]] =
      allPromotions.groupMapReduce(_.code)(_.notCombinableWith.toSet)(_ ++ _)
    val withSymmetry: Map[String, Set[String]] =
      merged.foldLeft(merged) { case (acc, (code, notWith)) =>
        notWith.foldLeft(acc) { (innerAcc, other) =>
          val existing = innerAcc.getOrElse(other, Set.empty)
          innerAcc.updated(other, existing + code)
        }
      }
    withSymmetry

  /**
   * Build the undirected compatibility graph from the incompatibility map. Two codes are compatible iff neither lists
   * the other as incompatible.
   *
   * Complexity: worst-case O(n&#94;2) to consider all pairs; typically less if neighbor sets are sparse.
   */
  private def buildCompatibilityGraph(promotionMap: Map[String, Set[String]]): Map[String, Set[String]] =
    val codes = promotionMap.keySet
    codes
      .map: a =>
        val neighbors = codes - a filter: b =>
          val aIncompat = promotionMap.getOrElse(a, Set.empty)
          val bIncompat = promotionMap.getOrElse(b, Set.empty)
          !aIncompat.contains(b) && !bIncompat.contains(a)
        a -> neighbors
      .toMap

  /**
   * Bron–Kerbosch algorithm with pivoting to enumerate all maximal cliques. R: current clique, P: prospective vertices
   * to expand, X: already processed (to avoid duplicates).
   */
  private def bronKerboschPivot(
    r: Set[String],
    p: Set[String],
    x: Set[String],
    graph: Map[String, Set[String]]
  ): Set[Set[String]] =
    if p.isEmpty && x.isEmpty then Set(r)
    else
      // Choose a pivot u from P ∪ X to reduce branching; iterate over P \ N(u)
      val px = p union x
      val u = px.headOption.getOrElse("")
      val nOfU = graph.getOrElse(u, Set.empty)
      val candidates = p diff nOfU
      candidates
        .foldLeft((Set.empty[Set[String]], p, x)) { case ((acc, pAcc, xAcc), v) =>
          val nOfV = graph.getOrElse(v, Set.empty)
          val nextR = r + v
          val nextP = pAcc intersect nOfV
          val nextX = xAcc intersect nOfV
          val found = bronKerboschPivot(nextR, nextP, nextX, graph)
          (acc union found, pAcc - v, xAcc + v)
        }
        ._1
