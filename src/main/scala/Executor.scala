import service.User
import io.finch._
import com.twitter.finagle.http.filter.Cors
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.{Await, Future, Promise}
import io.finch.syntax._
import io.finch.circe._
import io.circe.generic.auto._
import shapeless.Generic
object Executor extends App {
//  import shapeless._

  implicit val aux: Generic.Aux[User, Nothing] = Generic[User]
  val validateUser: Endpoint[User] = post("user" :: body.as[User]) { user: User =>
    User.existsOrCreate(user).map(Ok)
  } handle {
    case e: Exception => InternalServerError(e)
  }

  val getUser: Endpoint[User] = get("user") {
    User.get("1").map(Ok)
  } handle {
    case e: Exception => InternalServerError(e)
  }

  // Create a User
  /*val ayush = User("1", "Ayush", "ayush.hooda@knolus.in")
  User.create(ayush).map(user => println(s"Created ${user.name}"))

  // Retrieve a User
  val user = User.get("1").map(user => println(s"Retrieved ${user.name}"))

  val x = OpenTok.getCredentials("1", ayush)*/


/*
  val service: Service[Request, Response] = (
    validateUser :+:
      getClassroom :+:
      getAllClassrooms :+:
      getInstructorClassrooms :+:
      createClassroom :+:
      removeClassroom
    ).toServiceAs[Application.Json]



  // Listen
*/

  val service: Service[Request, Response] = (validateUser :+: getUser).toServiceAs[Application.Json]


val policy: Cors.Policy = Cors.Policy(
  allowsOrigin = _ => Some("*"),
  allowsMethods = _ => Some(Seq("GET", "POST", "DELETE")),
  allowsHeaders = _ => Some(Seq("Accept", "Content-Type"))
)
  val api: Service[Request, Response] = new Cors.HttpFilter(policy).andThen(service)
  Await.ready(Http.server.serve(":3030", api))


}
