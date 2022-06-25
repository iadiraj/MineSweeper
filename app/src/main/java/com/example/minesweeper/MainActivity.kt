package com.example.minesweeper

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
const val KEY_DATA = "KEY_DATA"
class MainActivity : AppCompatActivity() {
//    Variable declaration starts
    private var level : String = ""
    private var highScore : String = ""
    private lateinit var customBoard : Button
    private lateinit var startBtn : Button
    private lateinit var numRows : EditText
    private lateinit var numCols : EditText
    private lateinit var numMines : EditText
    private lateinit var easyBtn : RadioButton
    private lateinit var mediumBtn : RadioButton
    private lateinit var hardBtn : RadioButton
    private lateinit var selectLevel : RadioGroup
    private lateinit var infoBtn : ImageButton
    private lateinit var lastGameTime : TextView
    private lateinit var bestGameTime : TextView
    private lateinit var sharedPref : SharedPreferences
    private var lasttime : String = "Last Game Time : NA"
    private var besttime : String = "Best Time : NA"
//    Variables declaration ends

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()

        sharedPref = getSharedPreferences(KEY_DATA, Context.MODE_PRIVATE)
        val a= sharedPref.getString("bt", besttime)
        bestGameTime.text = a
        Toast.makeText(this@MainActivity, "$a",Toast.LENGTH_SHORT ).show()
        val b = sharedPref.getString("lt", lasttime)
        Toast.makeText(this@MainActivity, "$b",Toast.LENGTH_SHORT ).show()
        lastGameTime.text = b

        bindListener()

    }
//   To start the Game.
    private fun startGame(level : String){
        if(level == ""){
            if(TextUtils.isEmpty(numRows.text.toString()) || TextUtils.isEmpty(numCols.text.toString()) || TextUtils.isEmpty(numMines.text.toString())){
                Toast.makeText(this@MainActivity, "Fields cannot be Empty!", Toast.LENGTH_SHORT).show()
            }else{
                var row = Integer.parseInt(numRows.text.toString())
                var col = Integer.parseInt(numCols.text.toString())
                var mine = Integer.parseInt(numMines.text.toString())
                if(row > 15 || col > 15 || row < 5 || col < 5){
                    Toast.makeText(this@MainActivity, "Number of Rows and Colums should be from 5 to 15.", Toast.LENGTH_SHORT).show()
                }else if(mine < 5){
                    Toast.makeText(this@MainActivity, "Number of Mines should be greater than 5.", Toast.LENGTH_SHORT).show()
                }else if(mine > (row*col/4)){
                    Toast.makeText(this@MainActivity, "Number of Mines should be less to avoid overcrowding", Toast.LENGTH_SHORT).show()
                }else{
                    startActivity(Intent(this@MainActivity, GameActivity::class.java).apply {
                        putExtra("numOfRows", row)
                        putExtra("numOfCols", col)
                        putExtra("numOfMines", mine)
                        putExtra("flag", 0)
                    })
                }
            }
        }else{
            startActivity(Intent(this@MainActivity, GameActivity::class.java).apply {
                putExtra("level", level)
                putExtra("flag", 1)
            })
        }
    }

//   To bind the variable to view layouts.
    private fun bindViews(){
        customBoard = findViewById(R.id.customBoard_btn)
        numRows = findViewById(R.id.num_rows_input)
        numCols = findViewById(R.id.num_cols_input)
        numMines = findViewById(R.id.num_mines_input)
        easyBtn = findViewById(R.id.easy_radiobtn)
        mediumBtn = findViewById(R.id.medium_radiobtn)
        hardBtn = findViewById(R.id.hard_radiobtn)
        startBtn = findViewById(R.id.start_btn)
        infoBtn = findViewById(R.id.imageButton)
        selectLevel = findViewById(R.id.radioGroup)
        lastGameTime = findViewById(R.id.last_game_time)
        bestGameTime = findViewById(R.id.best_time)
    }

//   To bind the views.
    private fun bindListener(){
//        On checking easyBtn RadioButton.
        easyBtn.setOnClickListener {
            level = "easy"
            customVisibility(easyBtn)
        }
//        On checking mediumBtn RadioButton.
        mediumBtn.setOnClickListener {
            level = "medium"
            customVisibility(mediumBtn)
        }
//        On checking hardBtn RadioButton.
        hardBtn.setOnClickListener {
            level = "hard"
            customVisibility(hardBtn)
        }
//        On clicking customBoard Button.
        customBoard.setOnClickListener {
            level = ""
            customVisibility(customBoard)
        }
//        On clicking startBtn Button.
        startBtn.setOnClickListener {
            startGame(level)
        }
//        On clicking infoBtn ImageButton.
        infoBtn.setOnClickListener{
            showInfo()
        }
    }

//   To show basic information.
    private fun showInfo(){
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("INSTRUCTIONS")
        builder.setMessage("I hope you are familiar with the game rules. Here are some app functionalities that will help you get through\n" +
                "1. You can either select from the given levels or can create a custom board according to your requirements\n" +
                "2. You can use the share button to share your score with friends\n" +
                "3. Start button will start the game and the timer will get started on first click\n" +
                "4. You can keep a track of marked mines using mine count\n" +
                "5. You can toggle between flag/mine using the button on top to either flag or open the mine respectively\n" +
                "6. Smile icon button can be used to refresh the game\n" +
                "Have Fun!")
        builder.setCancelable(false)
        builder.setPositiveButton("OK"){
            dialog, which ->
        }
        builder.create().show()
    }

    override fun onResume() {
        super.onResume()

        // Clearing Radio Buttons
        selectLevel.clearCheck()

        val intent = intent
        if(intent.getStringExtra("lastTime") != null || intent.getStringExtra("highScore") != null ) {
            val bt = "Best Time : " + intent.getStringExtra("highScore")
            bestGameTime.text = bt
            val dt = "Last Game Time : " + intent.getStringExtra("lastTime")
            lastGameTime.text = dt
            highScore = intent.getStringExtra("highScore")!!
            with(sharedPref.edit()){
                putString("bt", bt)
                putString("lt", dt)
                commit()
            }
        }

//        We don't need this Else condition because after calling the onResume, onCreate will also be called due with overlapping of the textView occurs.
        //        else{
//            lastGameTime.text = lasttime
////            lastGameTime.text = "Last Game Time : NA"
//            bestGameTime.text = besttime
////            bestGameTime.text = "Best Time : NA"
//        }

    }

//    Setting visibility of textViews on the basis of Level/Custom Board Selection.
    private fun customVisibility(view : View){
//        When view is RadioButton
        if(view is RadioButton){
//            Get the status of the RadioButton.
            val checked = view.isChecked
//            If RadioButton status returns True then make the Custom-Layout Invisible.
            if(checked){
                numRows.visibility = View.INVISIBLE
                numCols.visibility = View.INVISIBLE
                numMines.visibility = View.INVISIBLE
            }
        }else{
//            If RadioButton status returns True then make the Custom-Layout visible.
            selectLevel.clearCheck()
            numRows.visibility = View.VISIBLE
            numCols.visibility = View.VISIBLE
            numMines.visibility = View.VISIBLE
        }
    }

}

