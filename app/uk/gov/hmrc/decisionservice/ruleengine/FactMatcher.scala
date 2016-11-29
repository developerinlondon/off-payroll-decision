/*
 * Copyright 2016 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.decisionservice.ruleengine


import cats.data.Xor
import play.api.Logger
import play.api.i18n.Messages
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.model.rules.{CarryOver, _}

import scala.annotation.tailrec


sealed trait FactMatcher {
  import FactMatcherHelper._

  def matchFacts(facts: Map[String,CarryOver], ruleSet: SectionRuleSet): Xor[DecisionServiceError,CarryOver] =
  {
    @tailrec
    def go(factValues: List[CarryOver], rules:List[SectionRule]):Xor[DecisionServiceError,CarryOver] = rules match {
      case Nil => noMatchResult(facts, ruleSet.rules)
      case rule :: xs if !factsValid(factValues, rule) => Xor.left(FactError(Messages("facts.incorrect.fact.error")))
      case rule :: xs =>
        factMatches(factValues, rule) match {
          case Some(result) => Xor.right(result)
          case None => go(factValues, xs)
        }
    }

    val factValues = ruleSet.headings.map(a => facts.getOrElse(a,EmptyCarryOver))
    go(factValues, ruleSet.rules)
  }

  def factMatches(factValues: List[CarryOver], rule:SectionRule):Option[CarryOver] = {
    factValues.zip(rule.values).filterNot(>>>.equivalent(_)) match {
      case Nil =>
        Logger.info(s"matched:\t${rule.values.map(_.value).mkString("\t,")}")
        Some(rule.result)
      case _ => None
    }
  }

  def noMatchResult(facts: Map[String,CarryOver], rules: List[SectionRule]): Xor[DecisionServiceError,CarryOver] = {
    val factSet = factsEmptySet(facts)
    val rulesSet = rulesMaxEmptySet(rules)
    if (factSet.subsetOf(rulesSet)) Xor.Right(NotValidUseCase) else Xor.Left(FactError(Messages("facts.empty.values.error")))
  }

}

object FactMatcherInstance extends FactMatcher

object FactMatcherHelper {
  def factsValid(factValues: List[CarryOver], rule:SectionRule):Boolean = factValues.size == rule.values.size
  def factsEmptySet(facts:Map[String,CarryOver]):Set[Int] = >>>.emptyPositions(facts.values)
  def rulesMaxEmptySet(rules: List[SectionRule]):Set[Int] = {
    def ruleEmptySet(rules: SectionRule):Set[Int] = >>>.emptyPositions(rules.values)
    val sets = for { r <- rules } yield { ruleEmptySet(r) }
    sets.foldLeft(Set[Int]())((a,b) => a ++ b)
  }
}
