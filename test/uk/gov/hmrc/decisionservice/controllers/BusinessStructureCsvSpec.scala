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

package uk.gov.hmrc.decisionservice.controllers

import uk.gov.hmrc.decisionservice.Versions
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class BusinessStructureCsvSpec extends UnitSpec with WithFakeApplication with DecisionControllerClusterCsvSpec {
  val clusterName = "businessStructure"
  val BUSINESS_STRUCTURE_SCENARIO_LOW_0 = "/test-scenarios/single/business-structure/scenario-should-give-LOW-0.csv"
  val BUSINESS_STRUCTURE_SCENARIO_HIGH_0 = "/test-scenarios/single/business-structure/scenario-should-give-HIGH-0.csv"
  val BUSINESS_STRUCTURE_SCENARIO_MEDIUM = "/test-scenarios/single/business-structure/scenario-should-give-MEDIUM.csv"
  val BUSINESS_STRUCTURE_SCENARIO_LOW_1 = "/test-scenarios/single/business-structure/scenario-should-give-LOW-1.csv"
  val BUSINESS_STRUCTURE_SCENARIO_LOW_2 = "/test-scenarios/single/business-structure/scenario-should-give-LOW-2.csv"
  val BUSINESS_STRUCTURE_SCENARIO_NOTVALIDUSECASE = "/test-scenarios/single/business-structure/scenario-should-give-NotValidUseCase.csv"
  val BUSINESS_STRUCTURE_SCENARIO_HIGH_1 = "/test-scenarios/single/business-structure/scenario-should-give-HIGH-1.csv"

  "POST /decide" should {
    "return 200 and correct response with the expected decision for business structure scenario LOW_0" in {
      createRequestSendVerifyDecision(BUSINESS_STRUCTURE_SCENARIO_LOW_0, Versions.VERSION101_BETA)
    }
    "return 200 and correct response with the expected decision for business structure scenario HIGH_0" in {
      createRequestSendVerifyDecision(BUSINESS_STRUCTURE_SCENARIO_HIGH_0, Versions.VERSION101_BETA)
    }
    "return 200 and correct response with the expected decision for business structure scenario MEDIUM" in {
      createRequestSendVerifyDecision(BUSINESS_STRUCTURE_SCENARIO_MEDIUM, Versions.VERSION101_BETA)
    }
    "return 200 and correct response with the expected decision for business structure scenario LOW_1" in {
      createRequestSendVerifyDecision(BUSINESS_STRUCTURE_SCENARIO_LOW_1, Versions.VERSION101_BETA)
    }
    "return 200 and correct response with the expected decision for business structure scenario LOW_2" in {
      createRequestSendVerifyDecision(BUSINESS_STRUCTURE_SCENARIO_LOW_2, Versions.VERSION101_BETA)
    }
    "return 200 and correct response with the expected decision for business structure scenario NotValidUseCase" in {
      createRequestSendVerifyDecision(BUSINESS_STRUCTURE_SCENARIO_NOTVALIDUSECASE, Versions.VERSION101_BETA)
    }
    "return 200 and correct response with the expected decision for business structure scenario HIGH_1" in {
      createRequestSendVerifyDecision(BUSINESS_STRUCTURE_SCENARIO_HIGH_1, Versions.VERSION101_BETA)
    }
  }
}
