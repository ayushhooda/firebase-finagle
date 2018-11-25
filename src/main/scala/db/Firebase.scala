package db

import java.io.InputStream

import com.google.firebase.database.{DatabaseReference, FirebaseDatabase}
import com.google.firebase.{FirebaseApp, FirebaseOptions}

object Firebase {

  private val credentials : InputStream = getClass.getResourceAsStream("./serviceAccountCredentials.json")

  private val options = new FirebaseOptions.Builder()
    .setDatabaseUrl("https://scalageek-demo.firebaseio.com")
    .setServiceAccount(credentials)
    .build()

  FirebaseApp.initializeApp(options)

  private val database = FirebaseDatabase.getInstance()

  def ref(path: String): DatabaseReference = database.getReference(path)

}
