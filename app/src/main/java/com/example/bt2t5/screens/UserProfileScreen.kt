package com.example.bt2t5.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bt2t5.R
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userId: String,
    userName: String,
    userEmail: String,
    userPhoto: String?,
    onLogout: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var birthDate by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    birthDate = snapshot.getString("birthDate") ?: ""
                } else {
                    val userData = hashMapOf(
                        "name" to userName,
                        "email" to userEmail,
                        "birthDate" to ""
                    )
                    db.collection("users").document(userId).set(userData)
                }
            }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onLogout() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF2196F3)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                if (userPhoto != null) {
                    AsyncImage(
                        model = userPhoto,
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(125.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.img_3),
                        contentDescription = "Default Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(125.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                    )
                }

                // Camera Icon
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .offset(y = 12.dp)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.img_2),
                        contentDescription = "Change Photo",
                        tint = Color(0xCF242760),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // User Info Fields
            ProfileTextField(
                label = "Name",
                value = userName,
                readOnly = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileTextField(
                label = "Email",
                value = userEmail,
                readOnly = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileTextField(
                label = "Date of Birth",
                value = birthDate,
                readOnly = false,
                onValueChange = {
                    birthDate = it
                    db.collection("users").document(userId)
                        .update("birthDate", it)
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Select Date",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onLogout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
            ) {
                Text(
                    text = "Back",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTextField(
    label: String,
    value: String,
    readOnly: Boolean = false,
    onValueChange: (String) -> Unit = {},
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = value,
            onValueChange = { if (!readOnly) onValueChange(it) },
            readOnly = readOnly,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0x24544C4C),
                unfocusedBorderColor = Color(0x24544C4C),
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            trailingIcon = trailingIcon
        )
    }
}