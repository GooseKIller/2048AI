package com.example.a2048game.presentation

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.a2048game.MainActivity
import com.example.a2048game.R

class MenuActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        //var size:Int = intent.getIntExtra("size", 4)
        var size = intent.getIntExtra("size", 4)
        var delayMilis = intent.getLongExtra("delay", 350)

        val minusButton: Button = findViewById(R.id.button_minus)
        val plusButton: Button = findViewById(R.id.button_plus)
        val backButton: Button = findViewById(R.id.back_button)
        val changeButton: Button = findViewById(R.id.change_button)

        var changed = false

        val sizeText: TextView = findViewById(R.id.size)
        sizeText.text = "Size:$size"

        minusButton.setOnClickListener {
            if (!changed){
                sizeText.text = "Size:${--size}"
            } else {
                delayMilis -= 50
                sizeText.text = "Delay:${delayMilis}"
            }
        }
        plusButton.setOnClickListener {
            if (!changed){
                sizeText.text = "Size:${++size}"
            } else {
                delayMilis += 50
                sizeText.text = "Delay:${delayMilis}"
            }
        }
        changeButton.setOnClickListener {
            changed = !changed
            if (changed) {
                sizeText.text = "Delay:${delayMilis}"
            } else {
                sizeText.text = "Size:${size}"
            }
        }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("size", size)
            intent.putExtra("delay", delayMilis)
            startActivity(intent)
            finish()

        }

    }
}