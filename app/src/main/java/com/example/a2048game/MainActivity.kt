package com.example.a2048game

import AiPathFinder
import Game
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
//import androidx.compose.ui.graphics.Color
import com.example.a2048game.presentation.GameVisualisation
import com.example.a2048game.presentation.MenuActivity
import java.lang.Exception


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val handler = Handler(Looper.getMainLooper())

        val size = intent.getIntExtra("size", 4)
        val delayMilis: Long = intent.getLongExtra("delay", 350)

        val game = Game(size)
        val ai = AiPathFinder(4, game.getSeeds())
        val gameVisualisation = GameVisualisation(game)
        val startStep = game.getStep()

        val gridLayout: GridLayout = findViewById(R.id.cells)

        val button: Button = findViewById(R.id.button)
        val settingButton: Button = findViewById(R.id.buttonSettings)

        gridLayout.rowCount = size
        gridLayout.columnCount = size

        for (i in 0 until size) {
            for (j in 0 until size) {
                val textView = TextView(this)
                val params = GridLayout.LayoutParams()
                params.width = 0
                params.height = 0
                params.columnSpec = GridLayout.spec(j, 1f)
                params.rowSpec = GridLayout.spec(i, 1f)

                // Установите внутренние отступы между клеточками
                val paddingInDp = 8 // Размер отступа в dp
                val scale = resources.displayMetrics.density
                val paddingInPx = (paddingInDp * scale + 0.5f).toInt() // Перевод dp в пиксели
                textView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)
                textView.text = "" // Задайте текст клеточки по вашему выбору
                //textView.textSize = 14f // Установите размер текста по вашему выбору
                textView.gravity = Gravity.CENTER // Выравнивание текста по центру
                textView.setBackgroundColor(Color.parseColor("#FFCBBFB3")) // Задайте цвет фона по вашему выбору
                textView.setTextColor(Color.parseColor("#000000")) // Задайте цвет текста по вашему выбору

                textView.setAutoSizeTextTypeUniformWithConfiguration(1, 14, 1, TypedValue.COMPLEX_UNIT_DIP)
                textView.layoutParams = params

                // Добавьте textView в gridLayout
                gridLayout.addView(textView)
            }
        }

        settingButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("size", size)
            intent.putExtra("delay", delayMilis)
            startActivity(intent)
            finish()
        }

        button.setOnClickListener {
            gameVisualisation.changeGameState()

            if (gameVisualisation.gameState && !game.isLose()){//start
                button.alpha = 0f
                val bestmoves = ai.bestMoves(game.getBoard(), game.getStep())

                for(i in bestmoves){
                    val runnable = object : Runnable {
                        override fun run() {
                            game.move(i.toString())
                            updateUI(game.getBoard(), gridLayout)

                            handler.postDelayed(this, delayMilis)
                        }
                    }
                    handler.post(runnable)
                }
            } else{
                handler.removeCallbacksAndMessages(null)
                if (game.getStep()> startStep){
                    button.text = getString(R.string.Continue)
                    //button.text = game.getBoard().map { it.joinToString(" ") }.joinToString("\n")
                }
                if (game.isLose()){
                    val text = "${getString(R.string.Restart)}\nSteps:${game.getStep()}\nMax Cell:${game.getMaxCell()}\nRecord:${game.getRecord()}"
                    button.text = text
                    game.clearBoard()
                }
                button.alpha = 0.5f
            }
        }
    }

    fun updateUI(board: Array<Array<Int>>, gridLayout: GridLayout){
        for(i in board.indices){
            for(j in board.indices){
                val textView = gridLayout.getChildAt(i * board.size + j) as? TextView
                if (board[i][j] == 0) {
                    textView?.text = ""
                } else {
                    textView?.text = board[i][j].toString()
                }
                textView?.setBackgroundColor(when(board[i][j]){
                    0 -> Color.parseColor("#FFCBBFB3")
                    2 -> Color.parseColor("#FFEEE4DA")
                    4 -> Color.parseColor("#FFEDE0C8")
                    8 -> Color.parseColor("#FFF2B179")
                    16 -> Color.parseColor("#FFEB9563")
                    32 -> Color.parseColor("#FFF67C5F")
                    64 -> Color.parseColor("#FFF65E3B")
                    128 -> Color.parseColor("#FFEDCF72")
                    256 -> Color.parseColor("#FFEDCC61")
                    512 -> Color.parseColor("#FFEDC850")
                    1024 -> Color.parseColor("#FFEDC53F")
                    2048 -> Color.parseColor("#FFEDC501")
                    else -> Color.parseColor("#FF5EDA92")
                })
            }

        }

    }
}