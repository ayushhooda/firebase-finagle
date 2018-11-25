package service

import java.io.InputStream
import com.opentok.{MediaMode, OpenTok, Session, SessionProperties, TokenOptions}
import io.circe._
import io.circe.parser._
import io.circe.generic.semiauto._
import io.circe.syntax._

case class OpentokCredentials(apiKey: Int, apiSecret: String)

case class SessionCredentials(apiKey: String, sessionId: String, token: String)


object OpenTok {

  private val credentialsJSON: String = {
    val stream: InputStream = getClass.getResourceAsStream("./serviceAccountCredentials.json")
    scala.io.Source.fromInputStream(stream).mkString
  }

  implicit val credentialsDecoder: Decoder[OpentokCredentials] = deriveDecoder[OpentokCredentials]

  private val credentials = decode[OpentokCredentials](credentialsJSON).getOrElse(null)

  private val opentok: OpenTok = new OpenTok(credentials.apiKey, credentials.apiSecret)

  def createSession: String = {
    val session: Session = opentok.createSession(new SessionProperties.Builder()
      .mediaMode(MediaMode.ROUTED)
      .build())
    session.getSessionId
  }

  def getCredentials(sessionId: String, user: User): SessionCredentials = {
    SessionCredentials(credentials.apiKey.toString, sessionId, createToken(sessionId, user))
  }

  private def createToken(sessionId: String, user: User): String = {
    val token = opentok.generateToken(sessionId, new TokenOptions.Builder().build())
    token
  }
}
