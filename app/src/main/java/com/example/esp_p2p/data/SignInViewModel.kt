package com.example.esp_p2p.data

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.esp_p2p.auth.CredentialManagerAuth
import com.example.esp_p2p.data.firestore.UserModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val drawingRepository: DrawingRepository
): ViewModel() {

    val auth = Firebase.auth

    var isSignedIn by mutableStateOf(auth.currentUser != null)

    private var credentialManagerAuth: CredentialManagerAuth? = null

    init {
        auth.addAuthStateListener {
            isSignedIn = it.currentUser != null
        }
    }

    private fun initCredentialManager(context: Context){
        credentialManagerAuth = CredentialManagerAuth(context, auth)
    }

    fun signInWithGoogle(localContext: Context){
        if (credentialManagerAuth == null) initCredentialManager(localContext)
        viewModelScope.launch {
            try {
                credentialManagerAuth?.googleSignIn { task ->
                    if (task.isSuccessful){
                        Toast.makeText(localContext, "Signed In", Toast.LENGTH_SHORT).show()
                        saveUserToFirestore(task.result.user!!)
                    }else{
                        Toast.makeText(localContext, "Error occurred!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: GetCredentialCancellationException) {
                Toast.makeText(localContext, "Canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserToFirestore(user: FirebaseUser){
        drawingRepository.insertUserFirestore(
            UserModel(
                uid = user.uid,
                name = user.displayName ?: "",
                email = user.email ?: "",
                photoUrl = user.photoUrl.toString()
            )
        )
    }

    fun signOut(localContext: Context){
        auth.signOut()
        if (auth.currentUser == null) Toast.makeText(localContext, "Signed Out", Toast.LENGTH_SHORT).show()
    }

}