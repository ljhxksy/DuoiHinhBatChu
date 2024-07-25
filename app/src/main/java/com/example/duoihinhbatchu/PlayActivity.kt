package com.example.duoihinhbatchu

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible

class PlayActivity : AppCompatActivity() {

    private lateinit var imageFrame: ImageView
    private lateinit var grayButtonsLayout: GridLayout
    private lateinit var yellowButtonsLayout: GridLayout
    private lateinit var nextQuestionButton: Button
    private lateinit var scoreFrame: TextView
    private lateinit var livesFrame: TextView
    private lateinit var statusText: TextView

    private var score = 0
    private var lives = 3
    private lateinit var currQuestion: Question
    private var listData = arrayListOf<Question>()
    private val grayButtons = mutableListOf<Button>()
    private val yellowButtons = mutableListOf<Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        initQuestions()
        initializeViews()
        loadQuestion()
        setupButtons()
    }

    private fun initializeViews() {
        imageFrame = findViewById(R.id.imageFrame)
        grayButtonsLayout = findViewById(R.id.grayButtonsLayout)
        yellowButtonsLayout = findViewById(R.id.yellowButtonsLayout)
        nextQuestionButton = findViewById(R.id.nextQuestionButton)
        scoreFrame = findViewById(R.id.scoreFrame)
        livesFrame = findViewById(R.id.livesFrame)
        statusText = findViewById(R.id.statusText)
    }

    private fun setupButtons() {
        grayButtonsLayout.removeAllViews()
        yellowButtonsLayout.removeAllViews()
        grayButtons.clear()
        yellowButtons.clear()

        // Create gray buttons
        for (i in 0 until currQuestion.answer.length) {
            val button = createButton(true)
            grayButtonsLayout.addView(button)
            grayButtons.add(button)
        }

        // Create yellow buttons
        for (i in 0 until 16) {
            val button = createButton(false)
            yellowButtonsLayout.addView(button)
            yellowButtons.add(button)
            setupLetters()
        }
    }

    private fun setupLetters() {
        val letters = mutableListOf<Char>()

        // Add letters from the answer
        letters.addAll(currQuestion.answer.toList())

        // Add random letters until we have 16 letters in total
        while (letters.size < 16) {
            val randomLetter = ('A'..'Z').random()
            letters.add(randomLetter)
        }

        // Shuffle the letters
        letters.shuffle()

        // Assign letters to yellow buttons
        yellowButtons.forEachIndexed { index, button ->
            button.text = letters[index].toString()
        }
    }

    private fun createButton(isGray: Boolean): Button {
        return Button(this).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                setVisible(true)
                width = resources.getDimensionPixelSize(R.dimen.button_width)
                height = resources.getDimensionPixelSize(R.dimen.button_height)
                setMargins(2, 4, 2, 4)
            }
            // Set the background of the button based on whether it is gray or yellow
            val backgroundDrawable = if (isGray) {
                R.drawable.ic_anwser // Use gray background
            } else {
                R.drawable.ic_tile_hover // Use yellow background
            }
            background = ContextCompat.getDrawable(this@PlayActivity, backgroundDrawable)

            // Set the text color of the button to white
            val white = ContextCompat.getColor(this@PlayActivity, android.R.color.white)

            setTextColor(white)

            // Set the text size of the button
            textSize = 18f

            if (!isGray) {
                setOnClickListener { onYellowButtonClick(this) }
            } else {
                setOnClickListener { onGrayButtonClick(this) }
            }
        }
    }

    private fun setupNextQuestionButton() {
        nextQuestionButton.isVisible = true
        nextQuestionButton.setOnClickListener {
            statusText.text = ""
            loadQuestion()
            setupButtons()
        }
    }

    private fun loadQuestion() {
        if (listData.isNotEmpty()) {
            // Set next question button visibility back to false
            nextQuestionButton.visibility = View.INVISIBLE

            // Set new answer
            currQuestion = listData.random()

            // Set new image
            imageFrame.setImageResource(currQuestion.image)

            // Remove question from data list
            listData.remove(currQuestion)
        }
    }

    private fun onYellowButtonClick(button: Button) {
        // Find the first available gray button
        val emptyGrayButton = grayButtons.firstOrNull { it.text.isEmpty() }

        // If there is an empty gray button, move the letter from the yellow button to the gray button
        emptyGrayButton?.let {
            it.text = button.text
            button.isEnabled = false
            button.visibility = View.INVISIBLE

            // Check if all gray buttons are filled
            if (grayButtons.all { it.text.isNotEmpty() }) {
                checkAnswer()
            }
        }
    }

    private fun onGrayButtonClick(button: Button) {
        if (button.text.isNotEmpty()) {
            val yellowBtn = yellowButtons.firstOrNull { it.text == button.text && !it.isEnabled }
            yellowBtn?.let {
                it.isVisible = true
                it.isEnabled = true
                button.text = ""
            }
        }
        grayButtons.forEach { grayButton ->
            grayButton.background = ContextCompat.getDrawable(this@PlayActivity, R.drawable.ic_anwser)
        }
        statusText.text = ""
    }

    private fun updateScore(points: Int) {
        score += points
        scoreFrame.text = score.toString()
    }

    private fun updateLives(change: Int) {
        lives += change
        livesFrame.text = lives.toString()
        if (lives <= 0) {
            gameOver()
        }
    }

    private fun gameOver() {
        // Show a game over dialog
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Game Over! You have run out of lives.")
            .setCancelable(false)
            .setPositiveButton("Play Again") { dialog, _ ->
                // Restart the game
                resetGame()
                dialog.dismiss()
            }
            .setNegativeButton("Exit") { _ , _ ->
                // Exit the activity
                finish()
            }

        val alert = dialogBuilder.create()
        alert.setTitle("Game Over")
        alert.show()
    }

    private fun resetGame() {
        // Reset game variables
        score = 0
        lives = 3
        scoreFrame.text = score.toString()
        livesFrame.text = lives.toString()
        statusText.text = ""

        // Reload the questions and start a new game
        initQuestions()
        loadQuestion()
        setupButtons()
    }

    private fun checkAnswer() {
        // Construct the current answer from gray buttons
        val currentAnswer = grayButtons.joinToString("") { it.text.toString() }

        // Compare with the correct answer
        if (currentAnswer == currQuestion.answer) {
            // Correct answer, update score and load next question
            setCorrecAnswer()
            updateScore(10) // Assuming 10 points for correct answer
            setupNextQuestionButton()
        } else {
            Log.d(TAG, "setIncorrectAnswer: hi")
            // Incorrect answer, update lives
            setIncorrectAnswer()
            updateLives(-1)
        }
    }

    private fun setIncorrectAnswer() {
        statusText.text = "Wrong answer!!"

        grayButtons.forEach { button ->
            button.background = ContextCompat.getDrawable(this@PlayActivity, R.drawable.ic_tile_false)
        }
    }

    private fun setCorrecAnswer() {
        statusText.text = "Yass go off queen"

        grayButtons.forEach { button ->
            button.background = ContextCompat.getDrawable(this@PlayActivity, R.drawable.ic_tile_true)
        }
        setupNextQuestionButton()
    }


    private fun initQuestions() {
        listData.add(Question("aomua".uppercase(), R.drawable.aomua))
        listData.add(Question("baocao".uppercase(), R.drawable.baocao))
        listData.add(Question("canthiep".uppercase(), R.drawable.canthiep))
        listData.add(Question("cattuong".uppercase(), R.drawable.cattuong))
        listData.add(Question("chieutre".uppercase(), R.drawable.chieutre))
        listData.add(Question("danhlua".uppercase(), R.drawable.danhlua))
        listData.add(Question("danong".uppercase(), R.drawable.danong))
        listData.add(Question("giandiep".uppercase(), R.drawable.giandiep))
        listData.add(Question("giangmai".uppercase(), R.drawable.giangmai))
        listData.add(Question("hoidong".uppercase(), R.drawable.hoidong))
        listData.add(Question("hongtam".uppercase(), R.drawable.hongtam))
        listData.add(Question("khoailang".uppercase(), R.drawable.khoailang))
        listData.add(Question("kiemchuyen".uppercase(), R.drawable.kiemchuyen))
        listData.add(Question("lancan".uppercase(), R.drawable.lancan))
        listData.add(Question("masat".uppercase(), R.drawable.masat))
        listData.add(Question("nambancau".uppercase(), R.drawable.nambancau))
        listData.add(Question("oto".uppercase(), R.drawable.oto))
        listData.add(Question("quyhang".uppercase(), R.drawable.quyhang))
        listData.add(Question("songsong".uppercase(), R.drawable.songsong))
        listData.add(Question("thattinh".uppercase(), R.drawable.thattinh))
        listData.add(Question("thothe".uppercase(), R.drawable.thothe))
        listData.add(Question("tichphan".uppercase(), R.drawable.tichphan))
        listData.add(Question("totien".uppercase(), R.drawable.totien))
        listData.add(Question("tranhthu".uppercase(), R.drawable.tranhthu))
        listData.add(Question("vuaphaluoi".uppercase(), R.drawable.vuaphaluoi))
        listData.add(Question("vuonbachthu".uppercase(), R.drawable.vuonbachthu))
        listData.add(Question("xakep".uppercase(), R.drawable.xakep))
        listData.add(Question("xaphong".uppercase(), R.drawable.xaphong))
        listData.add(Question("xedapdien".uppercase(), R.drawable.xedapdien))
    }

}