package com.example.standardcalculatorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.standardcalculatorapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private  var canAddOperation = false
    private  var canAddDecimal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    fun allClearAction(view: View) {
        binding.etInput.text=""
        binding.etOutput.text=""
        canAddDecimal = true
        canAddOperation = false
    }
    fun backSpaceAction(view: View) {
        val length = binding.etInput.length()
        if(length>0){
            binding.etInput.text = binding.etInput.text.subSequence(0, length - 1)
            canAddOperation = true
        }
    }
    fun operationAction(view: View) {
        if(view is Button && canAddOperation){
            binding.etInput.append(view.text)
            canAddOperation=false
            canAddDecimal=true
        }
    }
    fun numberAction(view: View) {
        if(view is Button){
            if(view.text=="."){
                if(canAddDecimal)
                    binding.etInput.append(view.text)
                canAddOperation=false
                canAddDecimal=false
            }
            else
                binding.etInput.append(view.text)
            canAddOperation=true
        }
    }

    fun equalsAction(view: View) {
        val result = calculateResult()
        binding.etOutput.text = result ?: "Error"
    }

    private fun calculateResult(): String {
        val digitOperators = digitsOperators()
        if(digitOperators.isEmpty()) return ""
        val timeDivision = timeDivisionCalculate(digitOperators)
        if(timeDivision.isEmpty()) return ""
        
        val result = addSubtractCalculate(timeDivision)
        return result.toString()
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float
        for (i in passedList.indices){
            if(passedList[i] is Char && i !=passedList.lastIndex){
                val operator = passedList[i]
                val nextDigit = passedList[i+1] as Float
                when (operator) {
                    '+' -> result += nextDigit
                    '-' -> result -= nextDigit
                }
            }
        }
        return result
    }

    private fun timeDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while(list.contains('×')|| list.contains('/')){
            list = calcTimeDiv(list)
        }
        return list

    }

    private fun calcTimeDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for(i in passedList.indices){
            if(passedList[i] is Char &&i != passedList.lastIndex && i <restartIndex){
                val operator =passedList[i]
                val prevDigit = passedList[i-1] as Float
                val nextDigit = passedList[i+1] as Float
                when(operator){
                    '×'->{
                        newList.add(prevDigit* nextDigit)
                        restartIndex = i+1
                    }
                    '/'->{
                        newList.add(prevDigit/nextDigit)
                        restartIndex = i+1
                    }
                    else ->{
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }
            if (i>restartIndex)
                newList.add(passedList[i])
        }
        return newList
    }

    private fun digitsOperators(): MutableList<Any>{
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for(character in binding.etInput.text){
            if(character.isDigit() || character=='.')
                currentDigit +=character
            else{
                list.add(currentDigit.toFloat())
                currentDigit=""
                list.add(character)
            }
        }
        if (currentDigit.isNotEmpty()) {
            list.add(currentDigit.toFloat())
        }
        return list
    }
}