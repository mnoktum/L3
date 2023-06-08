package com.example.l3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlin.text.substring

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private var canAddOperation = false
    private var canAddDecimal = true
    private var isReset = true

    fun interactNum(view: View)
    {
        var output = findViewById<TextView>(R.id.output)

        if(view is Button)
        {
            if(view.text == ".") {
                if(canAddDecimal) output.append(view.text)
                canAddDecimal = false
            }
            else {
                if (isReset) {
                    output.text = view.text
                }
                else output.append(view.text)
            }
            canAddOperation = true
            isReset = false
        }
    }

    fun interactOp(view: View)
    {
        var output = findViewById<TextView>(R.id.output)

        if(view is Button && canAddOperation)
        {
            output.append(view.text)
            canAddOperation = false
            canAddDecimal = true
            isReset = false
        }
        if(view is Button && !canAddOperation)
        {
            output.text = output.text.substring(0, output.text.length-1)
            output.append(view.text)
            canAddOperation = false
            canAddDecimal = true
            isReset = false
        }
    }

    fun allClearAction(view: View)
    {
        var output = findViewById<TextView>(R.id.output)
        output.text = "0"
        isReset = true
    }

    fun invertAction(view: View)
    {
        var output = findViewById<TextView>(R.id.output)
        if (output.text.substring(0,1) == "-") output.text = output.text.substring(1, output.text.length)
        else output.text = "-" + output.text
    }

    fun percentAction(view: View)
    {
        var output = findViewById<TextView>(R.id.output)
        var num = 0.0
        if (canAddOperation) {
            num = output.text.toString().toDouble() / 100
        }
        output.text = num.toString()
    }

    fun equalsAction(view: View)
    {
        var output = findViewById<TextView>(R.id.output)
        if("/0" in output.text) {
            output.text = "Ошибка"
            isReset = true
        }
        else output.text = calculateResults()
        if('.' in output.text) canAddDecimal = false
    }

    private fun calculateResults(): String
    {
        val digitsOperators = digitsOperators()
        if(digitsOperators.isEmpty()) return ""

        val timesDivision = timesDivisionCalculate(digitsOperators)
        if(timesDivision.isEmpty()) return ""

        val result = addSubtractCalculate(timesDivision)
        if (result != null) return result.toString().trimEnd('0').trimEnd('.')
        else return "Ошибка"
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Double
    {
        var result = passedList[0].toString().toDouble()

        for(i in passedList.indices)
        {
            if(passedList[i] is Char && i != passedList.lastIndex)
            {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Double
                if (operator == '+')
                    result += nextDigit
                if (operator == '-')
                    result -= nextDigit
            }
        }

        return result
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any>
    {
        var list = passedList
        while (list.contains('x') || list.contains('/'))
        {
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any>
    {
        var output = findViewById<TextView>(R.id.output)
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for(i in passedList.indices)
        {
            if(passedList[i] is Char && i != passedList.lastIndex && i < restartIndex)
            {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Double
                val nextDigit = passedList[i + 1] as Double
                when(operator)
                {
                    'x' ->
                    {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '/' ->
                    {
                        if (nextDigit == 0.toDouble()) {
                            output.text = "Ошибка"
                            isReset = true
                        }
                        else {
                            newList.add(prevDigit / nextDigit)
                            restartIndex = i + 1
                        }
                    }
                    else ->
                    {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }

            if(i > restartIndex)
                newList.add(passedList[i])
        }

        return newList
    }

    private fun digitsOperators(): MutableList<Any>
    {
        var output = findViewById<TextView>(R.id.output)
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for(character in output.text)
        {
            if(character.isDigit() || character == '.')
                currentDigit += character
            else
            {
                if(currentDigit != "") list.add(currentDigit.toDouble())
                currentDigit = ""
                list.add(character)
            }
        }

        if(currentDigit != "")
            list.add(currentDigit.toDouble())
        else list.add(0.0)

        if(list[0] == '-') list[0] = list.removeFirst().toString() + list[0].toString()
        return list
    }
}