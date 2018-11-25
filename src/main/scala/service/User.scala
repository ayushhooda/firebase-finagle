package service

import com.google.firebase.database.{DataSnapshot, DatabaseError, DatabaseReference, ValueEventListener}
import com.twitter.util.Promise
import db.Firebase
import scala.beans.BeanProperty

class UserBean() {
  @BeanProperty var id: String = null
  @BeanProperty var name: String = null
  @BeanProperty var email: String = null
  def toCase: User = {
    User(id, name, email)
  }
}

case class User(id: String, name: String, email: String) {
  def toBean = {
    val user = new UserBean()
    user.id = id
    user.name = name
    user.email = email
    user
  }
}

case class UserNotFoundException(str: String) extends Exception(str)
case class FirebaseException(str: String) extends Exception(str)

object User {

  def create(user: User): Promise[User] = {
    val ref = Firebase.ref(s"users/${user.id}")
    val userRecord = user.toBean
    val p = Promise[User]
    ref.setValue(userRecord, new DatabaseReference.CompletionListener() {
      override def onComplete(databaseError: DatabaseError, databaseReference: DatabaseReference): Unit = {
       if(databaseError != null) {
          p.setException(FirebaseException(databaseError.getMessage))
       } else {
          p.setValue(user)
       }
      }
    })
    p
  }

  def get(id: String): Promise[User] = {
    val ref = Firebase.ref(s"users/$id")
    val p = Promise[User]
    ref.addListenerForSingleValueEvent(new ValueEventListener {
      override def onDataChange(dataSnapshot: DataSnapshot): Unit = {
        val userRecord = dataSnapshot.getValue(classOf[UserBean])
        if(userRecord != null) {
          p.setValue(userRecord.toCase)
        }
        else {
          p.setException(UserNotFoundException(s"User $id not found"))
        }
      }
      override def onCancelled(databaseError: DatabaseError): Unit = {
        p.setException(FirebaseException(databaseError.getMessage))
      }
    })
    p
  }

  def existsOrCreate(user: User): Promise[User] = {
    val p = new Promise[User]
    get(user.id).map(userRecord => p.setValue(userRecord)).rescue {
      case e: UserNotFoundException => {
        create(user).map(userRecord => p.setValue(userRecord))
      }
    }
    p
  }

}
