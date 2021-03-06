package com.rasterfoundry.granary.api.error

import cats.syntax.functor._
import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax._

sealed abstract class CrudError

object CrudError {

  implicit val decCrudErrror: Decoder[CrudError] =
    Decoder[NotFound].widen or Decoder[ValidationError].widen

  implicit val encCrudError: Encoder[CrudError] = new Encoder[CrudError] {

    def apply(thing: CrudError): Json =
      thing match {
        case t: NotFound        => t.asJson
        case t: ValidationError => t.asJson
        case t: Forbidden       => t.asJson
      }
  }
}

case class NotFound(msg: String = "Not found") extends CrudError

object NotFound {
  implicit val encNotFound: Encoder[NotFound] = deriveEncoder
  implicit val decNotFound: Decoder[NotFound] = deriveDecoder
}

case class ValidationError(msg: String) extends CrudError

object ValidationError {
  implicit val encValidationError: Encoder[ValidationError] = deriveEncoder
  implicit val decValidationError: Decoder[ValidationError] = deriveDecoder
}

case class Forbidden(msg: String = "Not authorized") extends CrudError

object Forbidden {
  implicit val encNotAuthorized: Encoder[Forbidden] = deriveEncoder
  implicit val decNotAUthorized: Decoder[Forbidden] = deriveDecoder
}
