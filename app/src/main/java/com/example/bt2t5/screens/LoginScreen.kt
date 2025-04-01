package com.example.bt2t5.screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bt2t5.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(onLoginSuccess: (String, String, String, String?) -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var loginError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val googleSignInClient = remember { Identity.getSignInClient(context) }
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        isLoading = true
        loginError = null
        try {
            val credential = googleSignInClient.getSignInCredentialFromIntent(result.data)
            val googleToken = credential.googleIdToken

            googleToken?.let {
                val firebaseCredential = GoogleAuthProvider.getCredential(it, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            if (user != null) {
                                onLoginSuccess(
                                    user.uid,
                                    user.displayName ?: "Unknown",
                                    user.email ?: "No email",
                                    user.photoUrl?.toString()
                                )
                            } else {
                                loginError = "Authentication failed. User is null."
                            }
                        } else {
                            // Log the full exception for debugging
                            task.exception?.let { exception ->
                                Log.e("LoginScreen", "Sign-in error", exception)
                                loginError = when (exception) {
                                    is ApiException -> "Google Sign-In Failed: ${exception.statusCode}"
                                    else -> "Authentication failed: ${exception.message}"
                                }
                            } ?: run {
                                loginError = "Unknown authentication error"
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        isLoading = false
                        Log.e("LoginScreen", "Sign-in failure", e)
                        loginError = "Sign-in failed: ${e.message}"
                    }
            } ?: run {
                isLoading = false
                loginError = "Google token is null"
            }
        } catch (e: Exception) {
            isLoading = false
            Log.e("LoginScreen", "Sign-in error", e)
            loginError = "Google Sign-In Error: ${e.message}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        // Logo
        Box(
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFD5EDFF)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_1),
                contentDescription = "UTH Logo",
                modifier = Modifier.size(120.dp)
            )
        }

        // App title and description
        Text(
            text = "SmartTasks",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3991D8)
        )
        Text(
            text = "A simple and efficient to-do app",
            fontSize = 12.sp,
            color = Color(0xFF3991D8)
        )

        Spacer(modifier = Modifier.height(100.dp))

        // Welcome text
        Text(
            text = "Welcome",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Ready to explore? Log in to get started.",
            fontSize = 14.sp,
            color = Color(0xFF4A4646)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sign in button
        Button(
            onClick = {
                // Reset error state before attempting sign-in
                loginError = null
                isLoading = true

                val signInRequest = BeginSignInRequest.builder()
                    .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            .setServerClientId("687603544398-vq775ng4hb0bvhtfebgg3bo0ho0qql5i.apps.googleusercontent.com")
                            .setFilterByAuthorizedAccounts(false)
                            .build()
                    )
                    .build()

                googleSignInClient.beginSignIn(signInRequest)
                    .addOnSuccessListener { result ->
                        googleSignInLauncher.launch(
                            IntentSenderRequest.Builder(result.pendingIntent).build()
                        )
                    }
                    .addOnFailureListener { e ->
                        isLoading = false
                        Log.e("LoginScreen", "Begin sign-in failed", e)
                        loginError = "Google Sign-In Failed: ${e.message}"
                    }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD5EDFF)),
            shape = RoundedCornerShape(6.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color(0xFF130160),
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.img),
                    contentDescription = "Google Icon",
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign in with Google",
                    fontSize = 16.sp,
                    color = Color(0xFF130160)
                )
            }
        }

        // Error message
        loginError?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Text(
            text = "© UTHSmartTasks",
            fontSize = 12.sp,
            color = Color(0xFF4A4646)
        )
        Spacer(modifier = Modifier.height(35.dp))
    }
}