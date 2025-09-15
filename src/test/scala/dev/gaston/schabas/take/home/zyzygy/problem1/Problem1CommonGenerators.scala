package dev.gaston.schabas.take.home.zyzygy.problem1

import org.scalacheck.Gen
import scala.math.BigDecimal.RoundingMode

trait Problem1CommonGenerators:

  val genNonEmptyAlphaStr: Gen[String] =
    Gen.alphaStr.suchThat(_.nonEmpty)

  val genPositivePrice: Gen[BigDecimal] =
    Gen.posNum[Double].map(d => BigDecimal(d).setScale(2, RoundingMode.HALF_UP))
