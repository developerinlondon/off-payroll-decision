/*
 * Copyright 2017 HM Revenue & Customs
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

package uk.gov.hmrc.decisionservice.services

import uk.gov.hmrc.decisionservice.model.rules.{>>>, Facts}
import uk.gov.hmrc.decisionservice.ruleengine.RulesFileMetaData
import uk.gov.hmrc.play.test.UnitSpec

class TablesNonFinalDecisionSpec extends UnitSpec {

  object DecisionServiceTestInstance extends DecisionService {
    lazy val maybeSectionRules = loadSectionRules()
    val csvSectionMetadata = List(
      (5, "/tables/1.0.1-beta/control.csv", "Control")
    ).collect{case (q,f,n) => RulesFileMetaData(q,f,n)}
  }

  "decision service" should {
    "produce correct decision for control and financial risk" in {
      val facts =
      Facts(
        Map(
          "toldWhatToDo" -> >>>("yes"),
          "engagerMovingWorker" -> >>>("no"),
          "workerDecidingHowWorkIsDone" -> >>>("workingSetInstructions"),
          "whenWorkHasToBeDone" -> >>>("workingPatternAgreed"),
          "workerDecideWhere" -> >>>("cannotFixWorkerLocation")
      ))

      val maybeDecision = facts ==>: DecisionServiceTestInstance
      maybeDecision.isValid shouldBe true
      maybeDecision.map { decision =>
        val maybeCarryOver = decision.facts.get("Control")
        maybeCarryOver.isDefined shouldBe true
        maybeCarryOver.map { carryOver =>
          carryOver.value shouldBe "OutOfIR35"
        }
      }
    }
  }
}
