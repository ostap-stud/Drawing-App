package com.example.esp_p2p.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.esp_p2p.R
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CredentialManagerAuth (
    private val context: Context,
    private val auth: FirebaseAuth = Firebase.auth
) {

    private val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(context.getString(R.string.firebase_server_client_id))
        .setAutoSelectEnabled(true)
        .build()

    private val credentialManager by lazy {
        CredentialManager.create(context)
    }

    private fun getCredentialGoogleRequest(): GetCredentialRequest{
        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

//    @Throws(GetCredentialCancellationException::class)
    suspend fun googleSignIn(onSignInComplete: (Task<AuthResult>) -> Unit){
        val response = credentialManager.getCredential(context, getCredentialGoogleRequest())
        val credential = response.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
            auth.signInWithCredential(authCredential).addOnCompleteListener{
                onSignInComplete(it)
            }
        }else{
            throw RuntimeException("Received an invalid credential type")
        }
    }

}