package marina.calculator

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import marina.calculator.databinding.ActivityMainBinding
import java.lang.ArithmeticException

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    // Flags
    private var lastNumeric: Boolean = false
    private var lastDot: Boolean = false

    private var firstNum: Float = 0.0F
    private var secondNum: Float = 0.0F
    private var inputOperator: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.switchModeButton.setOnClickListener { setMode() }

        setSwitchModeButtonIcon()

        binding.button0.setOnClickListener { onDigit(0) }
        binding.button1.setOnClickListener { onDigit(1) }
        binding.button2.setOnClickListener { onDigit(2) }
        binding.button3.setOnClickListener { onDigit(3) }
        binding.button4.setOnClickListener { onDigit(4) }
        binding.button5.setOnClickListener { onDigit(5) }
        binding.button6.setOnClickListener { onDigit(6) }
        binding.button7.setOnClickListener { onDigit(7) }
        binding.button8.setOnClickListener { onDigit(8) }
        binding.button9.setOnClickListener { onDigit(9) }

        binding.buttonDivide.setOnClickListener { onOperator("÷") }
        binding.buttonMultiply.setOnClickListener { onOperator("×") }
        binding.buttonSubtract.setOnClickListener { onOperator("-") }
        binding.buttonAdd.setOnClickListener { onOperator("+") }

        binding.buttonResult.setOnClickListener { onCalculate() }

        binding.buttonClear.setOnClickListener { onReset() }

        binding.buttonDot.setOnClickListener { onDecimalPoint() }
    }

    private fun onDigit(num: Int) {
        binding.calcResult.append("$num")
        lastNumeric = true
        lastDot = false
    }

    private fun onReset() {
        binding.calcResult.text = ""
        firstNum = 0.0F
        secondNum = 0.0F
        inputOperator = ""
        lastNumeric = false
        lastDot = false
    }

    private fun onDecimalPoint() {
        if (!lastNumeric || lastDot) return

        val parts = binding.calcResult.text.split(Regex("(\\+|×|-|÷)"))

        if (parts.size == 1 && parts[0].contains(".")) {
            return
        }

        if (parts.size == 2 && parts[1].contains(".")) {
            return
        }

        binding.calcResult.append(".")
        lastNumeric = false
        lastDot = true
    }

    private fun onOperator(operator: String) {
        val inputValue = binding.calcResult.text

        if (inputValue.isEmpty() && operator == "-") {
            binding.calcResult.append(operator)

            return
        }

        if (lastNumeric && !isOperatorAdded("$inputValue")) {
            inputOperator = operator
            firstNum = "$inputValue".toFloat()
            binding.calcResult.append(operator)
            lastNumeric = false
            lastDot = false
        }
    }

    private fun isOperatorAdded(value: String): Boolean {
        // Ignore leading minus sign
        if (value.startsWith("-")) return false

        return value.contains(Regex("(\\+|×|-|÷)"))
    }

    private fun onCalculate() {
        if (!lastNumeric || inputOperator.isEmpty()) return

        val lastIndexOfFirstNum = removeZeroAfterDot("$firstNum").lastIndex
        val inputValue = binding.calcResult.text

        secondNum = "$inputValue".substring(lastIndexOfFirstNum + 2, inputValue.length).toFloat()

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

            binding.calcResult.text = removeZeroAfterDot("$result")
        } catch (e: ArithmeticException) {
            e.printStackTrace()
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
                binding.switchModeButton.setImageResource(R.drawable.ic_sun)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                binding.switchModeButton.setImageResource(R.drawable.ic_moon)
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