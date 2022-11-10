package com.adspostx.adspostxdemo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.trimmedLength
import androidx.core.view.isVisible
import com.adspostx.sdk.*
import com.google.android.material.button.MaterialButtonToggleGroup


enum class PresentationStyle {
    POPUP, FULLSCREEN
}

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {
    private var textAccountId: TextView? = null

    private var spinnerAttribute: Spinner? = null
    private var textValue: TextView? = null

    private var buttonAddAttribute: Button? = null
    private var textAttributes: TextView? = null

    private var buttonClearAllAttributes: Button? = null

    private var environmentGroup: MaterialButtonToggleGroup? = null
    private var styleGroup: MaterialButtonToggleGroup? = null
    private var transparentGroup: MaterialButtonToggleGroup? = null

    private var buttoninitSDK: Button? = null
    private var buttonLoadOffers: Button? = null
    private var buttonShowOffer: Button? = null
    private var isTransparent = true

    private var seekbarTopMargin: SeekBar? = null
    private var seekbarBottompMargin: SeekBar? = null
    private var seekbarLeftMargin: SeekBar? = null
    private var seekbarRightMargin: SeekBar? = null

    private var textSeekValueTopMargin: TextView? = null
    private var textSeekValueBottomMargin: TextView? = null
    private var textSeekValueLeftMargin: TextView? = null
    private var textSeekValueRightMargin: TextView? = null

    private var progressbar: ProgressBar? = null

    private var topMargin = 5u
    private var bottomMargin = 5u
    private var leftMargin = 5u
    private var rightMargin = 5u

    var attr = mutableMapOf<String , Any>()

    private var style: AdsPostXPresentationStyle = AdsPostXPresentationStyle.POPUP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.adspostx.adspostxdemo.R.layout.activity_main)
        title = "AdsPostX Demo App"

        textAccountId = findViewById(R.id.textAccountId)

        spinnerAttribute = findViewById(R.id.spinnerSelectAttribute)
        textValue = findViewById(R.id.textValue)

        buttonAddAttribute = findViewById(R.id.buttonAddAttribute)
        textAttributes = findViewById(R.id.textAttributes)

        buttonClearAllAttributes = findViewById(R.id.buttonClearAllAttributes)

        environmentGroup = findViewById(R.id.environmentGroup)
        styleGroup = findViewById(R.id.styleGroup)
        transparentGroup = findViewById(R.id.transparancyGroup)

        buttoninitSDK = findViewById(R.id.buttonInitsdk)
        buttonShowOffer = findViewById(R.id.buttonShowOffer)
        buttonLoadOffers = findViewById(R.id.buttonLoadOffers)

        seekbarTopMargin = findViewById(R.id.seekbarTopMargin)
        seekbarBottompMargin = findViewById(R.id.seekbarBottomMargin)
        seekbarLeftMargin = findViewById(R.id.seekbarLeftMargin)
        seekbarRightMargin = findViewById(R.id.seekbarRightMargin)

        textSeekValueTopMargin = findViewById(R.id.textSeekValueTopMargin)
        textSeekValueBottomMargin = findViewById(R.id.textSeekValueBottomMargin)
        textSeekValueLeftMargin = findViewById(R.id.textSeekValueLeftMargin)
        textSeekValueRightMargin = findViewById(R.id.textSeekValueRightMargin)

        progressbar = findViewById(R.id.progressbar)

        buttonLoadOffers?.setOnClickListener {
            //            println("button init sdk tapped")
            progressbar?.isVisible = true


            AdsPostX.load(this, attr) { status, error ->
                this.runOnUiThread {
                    progressbar?.isVisible = false
                }
                if (status) {
                    this.runOnUiThread {
                        Toast.makeText(this, "Offers loaded Successfully.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (error != null) {
                        this.runOnUiThread {
                            Toast.makeText(this, error?.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        this.runOnUiThread {
                            Toast.makeText(this, "Unknown Error!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        buttoninitSDK?.setOnClickListener {
            var accountid: String = ""

            textAccountId?.text.toString().let {
                accountid = it
            }

            AdsPostX.init(accountid) { status, error ->
                if (status) {
                    Toast.makeText(this, "SDK initialized successfully.", Toast.LENGTH_SHORT).show()
                    textAccountId?.isEnabled = false
                } else {
                    if (error != null) {
                        Toast.makeText(this,error.message,Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this,"Unknown Error!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        buttonShowOffer?.setOnClickListener {
//            println("button show offer with attr tapped")
            AdsPostX.showOffers(this,
                style,
                isTransparent,
                margin = Margin(topMargin,bottomMargin,leftMargin,rightMargin),
                onLoad = {
                    println("On load")
                }, onError = {
                    println(it)
                    this.runOnUiThread {
                        Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                    }
                },
                onDismiss = {
                    println("Dismiss")
                })
        }

        buttonAddAttribute?.setOnClickListener {
            val selectedItem = spinnerAttribute?.selectedItem as? String
            val length = textValue?.text?.trimmedLength() ?: 0
            if (length > 0 && !selectedItem.isNullOrEmpty() && !selectedItem.isNullOrBlank()) {
//                println("Selected attribute is ${selectedItem})")
//                println("selected attribute value is : ${textValue?.text}")
                attr["${selectedItem}"] =  "${textValue?.text}"

                textAttributes?.text = attr.toString()
                textValue?.text = ""
            } else {
                this.runOnUiThread {
                    Toast.makeText(this, "Enter valid attribute name/value.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        buttonClearAllAttributes?.setOnClickListener {
            attr.clear()
            textAttributes?.text = ""
        }

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.attributes, android.R.layout.simple_spinner_dropdown_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAttribute?.adapter = adapter

        environmentGroup?.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {   //only listen for check event, ignore uncheck event
                val checkedIndex = group.indexOfChild(findViewById(checkedId))
                if(checkedIndex == 0) {
                    AdsPostX.setEnvironment(AdsPostxEnvironment.LIVE)
                } else  {
                    AdsPostX.setEnvironment(AdsPostxEnvironment.TEST)
                }
            }
        }

        styleGroup?.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {   //only listen for check event, ignore uncheck event
                val checkedIndex = group.indexOfChild(findViewById(checkedId))
                if(checkedIndex == 0) {
                    style = AdsPostXPresentationStyle.POPUP
                } else  {
                    style = AdsPostXPresentationStyle.FULLSCREEN
                }
            }
        }

        transparentGroup?.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {   //only listen for check event, ignore uncheck event
                val checkedIndex = group.indexOfChild(findViewById(checkedId))
                isTransparent = checkedIndex == 0
//                println(isTransparent)
            }
        }

        seekbarTopMargin?.setOnSeekBarChangeListener(this);
        seekbarBottompMargin?.setOnSeekBarChangeListener(this)
        seekbarRightMargin?.setOnSeekBarChangeListener(this)
        seekbarLeftMargin?.setOnSeekBarChangeListener(this)
    }

    override fun onProgressChanged(bar: SeekBar, progress: Int, fromUser: Boolean) {

        if (bar == seekbarTopMargin) {
            textSeekValueTopMargin?.text = "$progress%"
            topMargin = progress.toUInt()
        } else if (bar == seekbarBottompMargin) {
            textSeekValueBottomMargin?.text = "$progress%"
            bottomMargin = progress.toUInt()
        } else if(bar == seekbarRightMargin) {
            textSeekValueRightMargin?.text = "$progress%"
            rightMargin = progress.toUInt()
        } else if(bar == seekbarLeftMargin) {
            textSeekValueLeftMargin?.text = "$progress%"
            leftMargin = progress.toUInt()
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }

}