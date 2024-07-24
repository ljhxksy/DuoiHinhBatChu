package com.example.duoihinhbatchu

import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class PlayActivity : AppCompatActivity() {

    private lateinit var imageFrame: ImageView
    private lateinit var grayButtonsLayout: GridLayout
    private lateinit var yellowButtonsLayout: GridLayout
    private lateinit var nextQuestionButton: Button
    private lateinit var scoreFrame: TextView
    private lateinit var livesFrame: TextView

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
        setupNextQuestionButton()
    }

    private fun initializeViews() {
        imageFrame = findViewById(R.id.imageFrame)
        grayButtonsLayout = findViewById(R.id.grayButtonsLayout)
        yellowButtonsLayout = findViewById(R.id.yellowButtonsLayout)
        nextQuestionButton = findViewById(R.id.nextQuestionButton)
        scoreFrame = findViewById(R.id.scoreFrame)
        livesFrame = findViewById(R.id.livesFrame)
    }

    private fun setupButtons() {
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
            background = ContextCompat.getDrawable(this@PlayActivity,
                if (isGray) R.drawable.ic_anwser else R.drawable.ic_tile_hover)
            setTextColor(ContextCompat.getColor(this@PlayActivity, android.R.color.white))
            textSize = 18f
            if (!isGray) {
                setOnClickListener { onYellowButtonClick(this) }
            }
        }
    }

    private fun setupNextQuestionButton() {
        nextQuestionButton.setOnClickListener {
            loadQuestion()
        }
    }

    private fun loadQuestion() {
        if (listData.isNotEmpty()) {
            // Set new answer
            currQuestion = listData.random()

            // Set new image
            imageFrame.setImageResource(currQuestion.image)

            // Remove question from data list
            listData.remove(currQuestion)
        }
    }

    private fun onYellowButtonClick(button: Button) {
        // TODO: Implement logic for yellow button click
        // Move letter to gray button, check answer, update score/lives
        val emptyGrayButton = grayButtons.firstOrNull { it.text.isEmpty() }

        // If there is an empty gray button, move the letter from the yellow button to the gray button
        emptyGrayButton?.let {
            it.text = button.text
            button.isEnabled = false

            // Check if all gray buttons are filled
            if (grayButtons.all { it.text.isNotEmpty() }) {
                checkAnswer()
            }
        }
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
        // TODO: Implement game over logic
        // Show toast, finish activity
    }

    private fun checkAnswer() {
        // Construct the current answer from gray buttons
        val currentAnswer = grayButtons.joinToString("") { it.text.toString() }

        // Compare with the correct answer
        if (currentAnswer == currQuestion.answer) {
            // Correct answer, update score and load next question
            updateScore(10) // Assuming 10 points for correct answer
            loadQuestion()
        } else {
            // Incorrect answer, update lives
            updateLives(-1)
            if (lives > 0) {
                // Clear the gray buttons and re-enable the yellow buttons
                setupButtons()
            }
        }
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