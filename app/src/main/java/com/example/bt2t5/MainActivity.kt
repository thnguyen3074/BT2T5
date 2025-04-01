package com.example.bt2t5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.bt2t5.screens.LoginScreen
import com.example.bt2t5.screens.UserProfileScreen
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var userId by remember { mutableStateOf<String?>(null) }
            var userName by remember { mutableStateOf<String?>(null) }
            var userEmail by remember { mutableStateOf<String?>(null) }
            var userPhoto by remember { mutableStateOf<String?>(null) }

            if (userId == null || userName == null || userEmail == null) {
                LoginScreen { id, name, email, photo ->
                    userId = id
                    userName = name
                    userEmail = email
                    userPhoto = photo
                }
            } else {
                UserProfileScreen(
                    userId = userId!!,
                    userName = userName!!,
                    userEmail = userEmail!!,
                    userPhoto = userPhoto,
                    onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        userId = null
                        userName = null
                        userEmail = null
                        userPhoto = null
                    }
                )
            }
        }
    }
}