package com.example.duoihinhbatchu

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PlayActivity : AppCompatActivity() {

    private var lives = 3
    private var score = 0
    private lateinit var yellowButtons: Array<Button>
    private lateinit var grayButtons: Array<Button>
    private lateinit var livesFrame: TextView
    private lateinit var scoreFrame: TextView
    private lateinit var nextQuestionButton: Button
    private lateinit var imageFrame: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        livesFrame = findViewById(R.id.livesFrame)
        scoreFrame = findViewById(R.id.scoreFrame)
        nextQuestionButton = findViewById(R.id.nextQuestionButton)
        imageFrame = findViewById(R.id.imageFrame)

        val yellowLayout = findViewById<LinearLayout>(R.id.yellowButtonsLayout)
        val grayLayout = findViewById<LinearLayout>(R.id.grayButtonsLayout)

        yellowButtons = Array(16) { index ->
            yellowLayout.getChildAt(index) as Button
        }
        grayButtons = Array(16) { index ->
            grayLayout.getChildAt(index) as Button
        }

        yellowButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                handleYellowButtonClick(index)
            }
        }

        nextQuestionButton.setOnClickListener {
            loadNextQuestion()
        }
    }

    private fun handleYellowButtonClick(index: Int) {
        // Logic to handle yellow button click
        val selectedButton = yellowButtons[index]
        val character = selectedButton.text
        selectedButton.visibility = View.INVISIBLE

        // Find the first available gray button
        for (grayButton in grayButtons) {
            if (grayButton.text.isEmpty()) {
                grayButton.text = character
                break
            }
        }

        // Check if gray buttons are filled
        if (grayButtons.none { it.text.isEmpty() }) {
            checkAnswer()
        }
    }

    private fun checkAnswer() {
        // Logic to validate answer
        val isCorrect = true // Replace with actual answer checking logic

        if (isCorrect) {
            updateScore(100)
            nextQuestionButton.visibility = View.VISIBLE
        } else {
            updateLives(-1)
        }
    }

    private fun updateScore(points: Int) {
        score += points
        scoreFrame.text = "Score: $score"
    }

    private fun updateLives(delta: Int) {
        lives += delta
        livesFrame.text = "Lives: $lives"
        if (lives <= 0) {
            Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadNextQuestion() {
        // Logic to load next question, set new image, reset buttons, etc.
        // For example:
        imageFrame.setImageResource(R.drawable.new_sample_image)
        yellowButtons.forEach { it.visibility = View.VISIBLE }
        grayButtons.forEach { it.text = "" }
        nextQuestionButton.visibility = View.INVISIBLE
    }
}
