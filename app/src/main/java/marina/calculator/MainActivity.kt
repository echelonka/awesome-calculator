package marina.calculator

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import java.lang.ArithmeticException

class MainActivity : AppCompatActivity() {

    private var switchModeButton: ImageButton? = null

    private var calcResult: TextView? = null

    // Digit buttons
    private var digit0: Button? = null
    private var digit1: Button? = null
    private var digit2: Button? = null
    private var digit3: Button? = null
    private var digit4: Button? = null
    private var digit5: Button? = null
    private var digit6: Button? = null
    private var digit7: Button? = null
    private var digit8: Button? = null
    private var digit9: Button? = null

    // Operator buttons
    private var divisionButton: Button? = null
    private var multiplyButton: Button? = null
    private var minusButton: Button? = null
    private var plusButton: Button? = null
    private var equalButton: Button? = null

    // Other buttons
    private var clearButton: Button? = null
    private var dotButton: Button? = null

    // Flags
    private var lastNumeric: Boolean = false
    private var lastDot: Boolean = false

    private var firstNum: Float = 0.0F
    private var secondNum: Float = 0.0F
    private var inputOperator: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        switchModeButton = findViewById(R.id.switchModeButton)
        switchModeButton?.setOnClickListener { setMode() }

        setSwitchModeButtonIcon()

        calcResult = findViewById(R.id.calcResult)

        digit0 = findViewById(R.id.button0)
        digit0?.setOnClickListener { onDigit(0) }

        digit1 = findViewById(R.id.button1)
        digit1?.setOnClickListener { onDigit(1) }

        digit2 = findViewById(R.id.button2)
        digit2?.setOnClickListener { onDigit(2) }

        digit3 = findViewById(R.id.button3)
        digit3?.setOnClickListener { onDigit(3) }

        digit4 = findViewById(R.id.button4)
        digit4?.setOnClickListener { onDigit(4) }

        digit5 = findViewById(R.id.button5)
        digit5?.setOnClickListener { onDigit(5) }

        digit6 = findViewById(R.id.button6)
        digit6?.setOnClickListener { onDigit(6) }

        digit7 = findViewById(R.id.button7)
        digit7?.setOnClickListener { onDigit(7) }

        digit8 = findViewById(R.id.button8)
        digit8?.setOnClickListener { onDigit(8) }

        digit9 = findViewById(R.id.button9)
        digit9?.setOnClickListener { onDigit(9) }

        divisionButton = findViewById(R.id.buttonDivide)
        divisionButton?.setOnClickListener { onOperator("÷") }

        multiplyButton = findViewById(R.id.buttonMultiply)
        multiplyButton?.setOnClickListener { onOperator("×") }

        minusButton = findViewById(R.id.buttonSubtract)
        minusButton?.setOnClickListener { onOperator("-") }

        plusButton = findViewById(R.id.buttonAdd)
        plusButton?.setOnClickListener { onOperator("+") }

        equalButton = findViewById(R.id.buttonResult)
        equalButton?.setOnClickListener { onCalculate() }

        clearButton = findViewById(R.id.buttonClear)
        clearButton?.setOnClickListener { onReset() }

        dotButton = findViewById(R.id.buttonDot)
        dotButton?.setOnClickListener { onDecimalPoint() }
    }

    private fun onDigit(num: Int) {
        calcResult?.append("$num")
        lastNumeric = true
        lastDot = false
    }

    private fun onReset() {
        calcResult?.text = ""
        firstNum = 0.0F
        secondNum = 0.0F
        inputOperator = ""
        lastNumeric = false
        lastDot = false
    }

    private fun onDecimalPoint() {
        if (!lastNumeric || lastDot) return

        calcResult?.text?.let {
            val parts = it.split(Regex("(\\+|×|-|÷)"))

            if (parts.size == 1 && parts[0].contains(".")) {
                return
            }

            if (parts.size == 2 && parts[1].contains(".")) {
                return
            }

            calcResult?.append(".")
            lastNumeric = false
            lastDot = true
        }
    }

    private fun onOperator(operator: String) {
        calcResult?.text?.let {
            if (it.isEmpty() && operator == "-") {
                calcResult?.append(operator)

                return
            }

            if (lastNumeric && !isOperatorAdded("$it")) {
                inputOperator = operator
                firstNum = "$it".toFloat()
                calcResult?.append(operator)
                lastNumeric = false
                lastDot = false
            }
        }
    }

    private fun isOperatorAdded(value: String): Boolean {
        // Ignore leading minus sign
        if (value.startsWith("-")) return false

        return value.contains(Regex("(\\+|×|-|÷)"))
    }

    private fun onCalculate() {
        if (!lastNumeric || inputOperator.isEmpty()) return

        calcResult?.text?.let { value ->
            val lastIndexOfFirstNum = removeZeroAfterDot("$firstNum").lastIndex

            secondNum = "$value".substring(lastIndexOfFirstNum + 2, value.length).toFloat()

            if (secondNum == 0.0F && inputOperator == "÷") {
                Toast.makeText(this, "Impossible to divide by zero!", Toast.LENGTH_SHORT).show()

                return
            }

            try {
                var result = 0.0F

                when (inputOperator) {
                    "-" -> result = firstNum - secondNum
                    "+" -> result = firstNum + secondNum
                    "÷" -> result = firstNum / secondNum
                    "×" -> result = firstNum * secondNum
                }

                onReset()
                firstNum = result
                lastNumeric = true

                calcResult?.text = removeZeroAfterDot("$result")
            } catch (e: ArithmeticException) {
                e.printStackTrace()
            }
        }
    }

    private fun removeZeroAfterDot(value: String): String {
        if (value.endsWith(".0")) return value.substringBefore(".0")

        return value
    }

    private fun getCurrentMode(): Int {
        return this.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
    }

    private fun setSwitchModeButtonIcon() {
        when (getCurrentMode()) {
            Configuration.UI_MODE_NIGHT_YES -> {
                switchModeButton?.setImageResource(R.drawable.ic_sun)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                switchModeButton?.setImageResource(R.drawable.ic_moon)
            }
        }
    }

    private fun setMode() {
        val currentMode = getCurrentMode()

        if (currentMode == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}