# Scala Promotions Challenge

This repository contains the solutions to the problems proposed in the technical challenge.

This exercise is designed to demonstrate your understanding of basic Scala
concepts, functional programming, clean code, and problem solving techniques.
Acceptance Criteria:

1. Please submit your exercise using GitHub or other public git
   repository. Include a short readme with instructions on how to run
   your program.
2. We recommend attempting the problem in Scala even if you are not
   familiar with the language. However, we will accept the solution in
   another language, but you may have to transform the sample data to
   an appropriate data structure.
   a. If using Java, use the latest version of Java as well as leverage
   Java’s functional capabilities such as Streams and lambda
   expressions.
3. We also recommend writing testable code with enough unit tests to
   cover most or all possible scenarios.

---

## Table of Contents

- [Environment and Setup](#environment-and-setup)
    - [Stack](#stack)
    - [Running Tests](#running-tests)
    - [CI Pipeline](#ci-pipeline)
- [Problem 1](#problem-1)
    - [Background](#background-1)
    - [Solution](#solution-1)
- [Problem 2](#problem-2)
    - [Background](#background-2)
    - [Solution](#solution-2)

---

# Environment and Setup

## Stack

| Technology | Version |
|------------|---------|
| Java       | 21      |
| Scala      | 3.7.3   |
| SBT        | 1.11.6  |
| ScalaTest  | 3.2.19  |

## Running Tests

To run all unit tests, execute the following command in the project root:

```shell
sbt test
```

## CI Pipeline

This project includes a GitHub Actions workflow that runs on every pull request to the main branch. The workflow ensures
code quality and test coverage by performing the following steps:

| Step             | Description                                                               |
|------------------|---------------------------------------------------------------------------|
| Checkout code    | Uses `actions/checkout@v4` to clone the repository.                       |
| Setup JDK 21     | Uses `actions/setup-java@v5` to install Temurin JDK 21 with SBT caching.  |
| Setup SBT        | Uses `sbt/setup-sbt@v1` to configure SBT.                                 |
| Check formatting | Runs `sbt scalafmtCheckAll scalafmtSbtCheck` to ensure code is formatted. |
| Run tests        | Runs `sbt test` to execute all unit tests.                                |

---

# Problem 1

## Background 1

The application receives pricing and rate information from a third party data provider. We
make two calls to this provider to receive a list of rates and a list of cabin prices. We can use this data to
solve several problems for our customers. The problem we’ll be focusing on for this exercise will be
finding the best price for a particular rate group.

**Cabin Price**: The price for a specific cabin on a specific cruise. All cabin prices will have a single rate
attached.

**Rate**: A rate is a way to group related prices together. A rate is defined by its Rate Code and which Rate
Group it belongs to. For example. (MilAB, Military) and (Sen123, Senior)
**Rate Group**: Specific rates are grouped into a related rate group. There is a one-to-many relationship
between rate groups and rates (A rate group is made up of many rates, but a rate can only belong to a
single rate group) Some examples of rate groups are: Standard, Military, Senior, and Promotion.

1. Write a function that will take a list of rates and a list of prices and returns the best price for each rate
   group. We’ve supplied the function and case class definitions below for you to use.

```scala
case class Rate(rateCode: String, rateGroup: String)

case class CabinPrice(cabinCode: String, rateCode: String, price: BigDecimal)

case class BestGroupPrice(cabinCode: String, rateCode: String, price: BigDecimal, rateGroup: String)

/**
 * Write a function that will take a list of rates and a list of prices and returns the 
 * best price for each rate group. We’ve supplied the function and case class definitions 
 * below for you to use
 */
def getBestGroupPrices(rates: Seq[Rate], prices: Seq[CabinPrice]): Seq[BestGroupPrice] = ???
```

2. On startup, your program should run the following sample data through your function and output the
   sequence of BestGroupPrices. We included the expected output below:

Input - Rates:

```scala
Rate(M1, Military)
Rate(M2, Military)
Rate(S1, Senior)
Rate(S2, Senior)
```

Input - Cabin Prices:

```scala
CabinPrice(CA, M1, 200.00)
CabinPrice(CA, M2, 250.00)
CabinPrice(CA, S1, 225.00)
CabinPrice(CA, S2, 260.00)
CabinPrice(CB, M1, 230.00)
CabinPrice(CB, M2, 260.00)
CabinPrice(CB, S1, 245.00)
CabinPrice(CB, S2, 270.00)
```

Expected Output - Best Cabin Prices:

```scala
BestGroupPrice(CA, M1, 200.00, Military)
BestGroupPrice(CA, S1, 225.00, Senior)
BestGroupPrice(CB, M1, 230.00, Military)
BestGroupPrice(CB, S1, 245.00, Senior)
```

## Solution 1

| File                                                                                                                   | Description                                                                                                                    |
|------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------|
| [Problem1Solution.scala](./src/main/scala/dev/gaston/schabas/take/home/zyzygy/problem1/Problem1Solution.scala)         | Computes best price per rate group by grouping CabinPrice by cabin and rate group, then selecting minimal price.               |
| [Problem1SolutionTest.scala](./src/test/scala/dev/gaston/schabas/take/home/zyzygy/problem1/Problem1SolutionTest.scala) | Unit tests covering multiple cabins, rates, and rate groups, validating expected best prices.                                  |
| _Generators_                                                                                                           | ScalaCheck generators are used to produce combinations of rates and cabin prices to thoroughly test happy path and edge cases. |

---

# Problem 2

## Background 2

Cruise bookings can have one or more Promotions applied to them. But sometimes a
Promotion cannot be combined with another Promotion. Our application has to find out all possible
Promotion Combinations that can be applied together.

1. Implement a function to find all PromotionCombos with maximum number of combinable
   promotions in each. The function and case class definitions are supplied below to get you started.

```scala
case class Promotion(code: String, notCombinableWith: Seq[String])

case class PromotionCombo(promotionCodes: Seq[String])

def allCombinablePromotions(allPromotions: Seq[Promotion]): Seq[PromotionCombo] = ???
```

2. Implement a function to find all PromotionCombos for a given Promotion from given list of
   Promotions. The function definition is provided.

```scala
def combinablePromotions(
                          promotionCode: String,
                          allPromotions: Seq[Promotion]
                        ): Seq[PromotionCombo] = ???
```

3. On startup your program should run through the following sample data and output the sequence of
   PromotionCombos.

Input - Promotions:

```scala
Promotion(P1, Seq(P3)) // P1 is not combinable with P3
Promotion(P2, Seq(P4, P5)) // P2 is not combinable with P4 and P5
Promotion(P3, Seq(P1)) // P3 is not combinable with P1
Promotion(P4, Seq(P2)) // P4 is not combinable with P2
Promotion(P5, Seq(P2)) // P5 is not combinable with P2
```

Expected Output for All Promotion Combinations:

```scala
Seq(
  PromotionCombo(Seq(P1, P2)),
  PromotionCombo(Seq(P1, P4, P5)),
  PromotionCombo(Seq(P2, P3)),
  PromotionCombo(Seq(P3, P4, P5))
)
```

Expected Output for Promotion Combinations for promotionCode="P1":

```scala
Seq(
  PromotionCombo(Seq(P1, P2)),
  PromotionCombo(Seq(P1, P4, P5))
)
```

Expected Output for Promotion Combinations for promotionCode="P3":

```scala
Seq(
  PromotionCombo(Seq(P3, P2)),
  PromotionCombo(Seq(P3, P4, P5))
)
```

## Solution 2

### TODO