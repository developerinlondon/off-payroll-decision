package uk.gov.hmrc.decisionservice.ruleengine

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model.RulesFileError

/**
  * Created by habeeb on 11/11/2016.
  * Modified by Mimu on 25/11/2016.
  */
sealed trait RulesFileLineValidator {

  val allowedCarryOverValues:List[String]
  val allowedValues:List[String]
  val allowedDecisionValues:List[String]

  def validateValue(value: String, errorMessage:String): Xor[RulesFileError, Unit] =
    Xor.fromOption(allowedValues.find(_ == value.trim.toLowerCase).map(_ => ()),RulesFileError(errorMessage))

  def validateResultCells(resultCells: List[String], rulesFileMetaData: RulesFileMetaData, row:Int): Xor[RulesFileError, Unit] =
    resultCells match {
      case Nil => Xor.left(RulesFileError(s"missing carry over in row $row in file ${rulesFileMetaData.path}"))
      case x::xs if !allowedCarryOverValues.contains(x.trim.toLowerCase) && !x.isEmpty => Xor.left(RulesFileError(s"invalid carry over value $x in row $row in file ${rulesFileMetaData.path}"))
      case x::Nil => Xor.right(())
      case x::exit::xs if !exit.isEmpty && exit.trim.toLowerCase() != "true" && exit.trim.toLowerCase() != "false" => Xor.left(RulesFileError(s"invalid exit value in row $row in file ${rulesFileMetaData.path}"))
      case _ => Xor.right(())
    }

  def validateRowSize(row:List[String], rulesFileMetaData: RulesFileMetaData, rowNumber:Int) : Xor[RulesFileError, Unit] =
    if (row.size > rulesFileMetaData.valueCols) Xor.right(())
    else Xor.left(RulesFileError(s"row size is ${row.size}, expected greater than ${rulesFileMetaData.valueCols} in row $rowNumber in file ${rulesFileMetaData.path}"))

  def validateColumnHeaders(row: List[String], rulesFileMetaData: RulesFileMetaData): Xor[RulesFileError, Unit] =
    if (row.size >= rulesFileMetaData.valueCols) Xor.right(())
    else Xor.left(RulesFileError(s"column header size is ${row.size}, should be ${rulesFileMetaData.numCols} in file ${rulesFileMetaData.path}"))

  def validateLine(row:List[String], rulesFileMetaData: RulesFileMetaData, rowNumber:Int): Xor[RulesFileError, Unit] = {
    for {
      _ <- validateRowSize(row, rulesFileMetaData, rowNumber)
      (valueCells, resultCells) = row.splitAt(rulesFileMetaData.valueCols)
      validationErrors = valueCells.map(cell => validateValue(cell.trim, s"invalid value in row $rowNumber in file ${rulesFileMetaData.path}")).collect { case Xor.Left(e) => e }
      _ <- Xor.fromOption(validationErrors.headOption, ()).swap
      _ <- validateResultCells(resultCells, rulesFileMetaData, rowNumber)
    }
    yield {
      ()
    }
  }

}

object RulesFileLineValidatorInstance extends RulesFileLineValidator {
  val allowedDecisionValues = List("inir35", "outofir35", "employed", "self-employed", "unknown")
  val allowedCarryOverValues = List("low", "medium", "high") ::: allowedDecisionValues
  val allowedValues = List("yes", "no", "") ::: allowedCarryOverValues
}
