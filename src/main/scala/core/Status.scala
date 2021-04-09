package core

object Status extends Enumeration {
  type Status = Value

  val Unpaid, Paid, Canceled, Returned = Value
}
