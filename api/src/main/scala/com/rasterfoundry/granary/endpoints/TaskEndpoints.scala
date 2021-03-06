package com.rasterfoundry.granary.api.endpoints

import com.rasterfoundry.granary.api.error._
import com.rasterfoundry.granary.datamodel._

import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.model.StatusCode

import java.util.UUID

object TaskEndpoints {

  val base = endpoint.in("tasks")

  val idLookup = base.get
    .in(header[Option[TokenHeader]]("Authorization"))
    .in(path[UUID])
    .out(jsonBody[Task])
    .errorOut(
      oneOf[CrudError](
        statusMapping(StatusCode.NotFound, jsonBody[NotFound].description("not found")),
        statusMapping(
          StatusCode.Forbidden,
          jsonBody[Forbidden].description("Invalid token")
        )
      )
    )

  val create = base.post
    .in(header[Option[TokenHeader]]("Authorization"))
    .in(
      jsonBody[Task.Create].description(
        "A name, a Json Schema and some AWS batch metadata for running this task. The Schema should specify arguments as string -> string, even if they'll be parsed as ints or bools or whatever by the eventual task execution"
      )
    )
    .out(jsonBody[Task])
    .errorOut(
      oneOf[CrudError](
        statusMapping(
          StatusCode.Forbidden,
          jsonBody[Forbidden].description("Invalid token")
        )
      )
    )

  val list = base.get
    .in(header[Option[TokenHeader]]("Authorization"))
    .in(Inputs.paginationInput)
    .out(jsonBody[PaginatedResponse[Task]])
    .errorOut(
      oneOf[CrudError](
        statusMapping(
          StatusCode.Forbidden,
          jsonBody[Forbidden].description("Invalid token")
        )
      )
    )

  val delete = base.delete
    .in(header[Option[TokenHeader]]("Authorization"))
    .in(path[UUID])
    .out(jsonBody[DeleteMessage])
    .errorOut(
      oneOf[CrudError](
        statusMapping(StatusCode.NotFound, jsonBody[NotFound]),
        statusMapping(
          StatusCode.Forbidden,
          jsonBody[Forbidden].description("Invalid token")
        )
      )
    )

  val endpoints = List(idLookup, create, list, delete)
}
