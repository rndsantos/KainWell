package com.example.kainwell

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KainWellActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Firebase.database.setPersistenceEnabled(true)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KainWellApp()
        }
    }
}