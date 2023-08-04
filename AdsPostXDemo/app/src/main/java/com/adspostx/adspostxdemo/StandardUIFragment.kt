package com.adspostx.adspostxdemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.text.trimmedLength
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.adspostx.sdk.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButtonToggleGroup

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [StandardUIFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StandardUIFragment : Fragment(),  SeekBar.OnSeekBarChangeListener {

    private var textSdkId: TextView? = null

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
    private var buttonNextScreen: Button? = null
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

    private var attr = mutableMapOf<String , Any>()

    private var style: AdsPostXPresentationStyle = AdsPostXPresentationStyle.POPUP


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onProgressChanged(bar: SeekBar, progress: Int, fromUser: Boolean) {

        if (bar == seekbarTopMargin) {
            "$progress%".also { textSeekValueTopMargin?.text = it }
            topMargin = progress.toUInt()
        } else if (bar == seekbarBottompMargin) {
            "$progress%".also { textSeekValueBottomMargin?.text = it }
            bottomMargin = progress.toUInt()
        } else if(bar == seekbarRightMargin) {
            "$progress%".also { textSeekValueRightMargin?.text = it }
            rightMargin = progress.toUInt()
        } else if(bar == seekbarLeftMargin) {
            "$progress%".also { textSeekValueLeftMargin?.text = it }
            leftMargin = progress.toUInt()
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_standard_u_i, container, false)
        textSdkId = view.findViewById(R.id.textSdkId)

        spinnerAttribute = view.findViewById(R.id.spinnerSelectAttribute)
        textValue = view.findViewById(R.id.textValue)

        buttonAddAttribute = view.findViewById(R.id.buttonAddAttribute)
        textAttributes = view.findViewById(R.id.textAttributes)

        buttonClearAllAttributes = view.findViewById(R.id.buttonClearAllAttributes)

        environmentGroup = view.findViewById(R.id.environmentGroup)
        styleGroup = view.findViewById(R.id.styleGroup)
        transparentGroup = view.findViewById(R.id.transparancyGroup)

        buttoninitSDK = view.findViewById(R.id.buttonInitsdk)
        buttonShowOffer = view.findViewById(R.id.buttonShowOffer)
        buttonLoadOffers = view.findViewById(R.id.buttonLoadOffers)
        buttonNextScreen = view.findViewById(R.id.buttonGoToNextScreen)

        seekbarTopMargin = view.findViewById(R.id.seekbarTopMargin)
        seekbarBottompMargin = view.findViewById(R.id.seekbarBottomMargin)
        seekbarLeftMargin = view.findViewById(R.id.seekbarLeftMargin)
        seekbarRightMargin = view.findViewById(R.id.seekbarRightMargin)

        textSeekValueTopMargin = view.findViewById(R.id.textSeekValueTopMargin)
        textSeekValueBottomMargin = view.findViewById(R.id.textSeekValueBottomMargin)
        textSeekValueLeftMargin = view.findViewById(R.id.textSeekValueLeftMargin)
        textSeekValueRightMargin = view.findViewById(R.id.textSeekValueRightMargin)

        progressbar = view.findViewById(R.id.progressbar)


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
                this.activity?.runOnUiThread {
                    Toast.makeText(this.context, "Enter valid attribute name/value.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        buttonClearAllAttributes?.setOnClickListener {
            attr.clear()
            textAttributes?.text = ""
        }

        if(this.context != null) {
            val adapter = ArrayAdapter.createFromResource(
                this.requireContext(),
                R.array.attributes, android.R.layout.simple_spinner_dropdown_item
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerAttribute?.adapter = adapter
        }

        environmentGroup?.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {   //only listen for check event, ignore uncheck event
                val checkedIndex = group.indexOfChild(view.findViewById(checkedId))
                if(checkedIndex == 0) {
                    AdsPostX.setEnvironment(AdsPostxEnvironment.LIVE)
                } else  {
                    AdsPostX.setEnvironment(AdsPostxEnvironment.TEST)
                }
            }
        }

        styleGroup?.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {   //only listen for check event, ignore uncheck event
                val checkedIndex = group.indexOfChild(view.findViewById(checkedId))
                if(checkedIndex == 0) {
                    style = AdsPostXPresentationStyle.POPUP
                } else  {
                    style = AdsPostXPresentationStyle.FULLSCREEN
                }
            }
        }

        transparentGroup?.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {   //only listen for check event, ignore uncheck event
                val checkedIndex = group.indexOfChild(view.findViewById(checkedId))
                isTransparent = checkedIndex == 0
//                println(isTransparent)
            }
        }

        seekbarTopMargin?.setOnSeekBarChangeListener(this)
        seekbarBottompMargin?.setOnSeekBarChangeListener(this)
        seekbarRightMargin?.setOnSeekBarChangeListener(this)
        seekbarLeftMargin?.setOnSeekBarChangeListener(this)

        buttoninitSDK?.setOnClickListener {
            var sdkId: String = ""

            textSdkId?.text.toString().let {
                sdkId = it
            }

            AdsPostX.init(sdkId) { status, error ->
                if (status) {
                    Toast.makeText(this.context, "SDK initialized successfully.", Toast.LENGTH_SHORT).show()
                    textSdkId?.isEnabled = false
                } else {
                    if (error != null) {
                        Toast.makeText(this.context,error.message,Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this.context,"Unknown Error!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        buttonLoadOffers?.setOnClickListener {
            //            println("button init sdk tapped")
            progressbar?.isVisible = true

           val applicationContext: Context? = context?.applicationContext

            applicationContext?.applicationContext?.let {
                AdsPostX.load(it, attr) { status, error ->
                    this.activity?.runOnUiThread {
                        progressbar?.isVisible = false
                    }
                    if (status) {
                        this.activity?.runOnUiThread {
                            Toast.makeText(this.context, "Offers loaded Successfully.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (error != null) {
                            this.activity?.runOnUiThread {
                                Toast.makeText(this.context, error?.message, Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            this.activity?.runOnUiThread {
                                Toast.makeText(this.context, "Unknown Error!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

    }

        buttonShowOffer?.setOnClickListener {
//            println("button show offer with attr tapped")
            AdsPostX.showOffers(
                style,
                isTransparent,
                margin = Margin(topMargin,bottomMargin,leftMargin,rightMargin),
                onShow = {
                    println("On show")
                }, onError = {
                    println(it)
                    this.activity?.runOnUiThread {
                        Toast.makeText(this.context,it.message,Toast.LENGTH_SHORT).show()
                    }
                },
                onDismiss = {
                    println("Dismiss")
                })
        }

        buttonNextScreen?.setOnClickListener {
            val intent = Intent(this.activity, NextActivity::class.java)
            this.startActivity(intent)
        }

        return  view

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StandardUIFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            StandardUIFragment().apply {
            }
    }

}