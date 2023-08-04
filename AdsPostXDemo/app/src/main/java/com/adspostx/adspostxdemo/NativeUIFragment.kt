package com.adspostx.adspostxdemo

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.trimmedLength
import androidx.core.view.isVisible
import com.adspostx.sdk.AdsPostX
import com.adspostx.sdk.AdsPostxEnvironment
import com.google.android.material.button.MaterialButtonToggleGroup

/**
 * A simple [Fragment] subclass.
 * Use the [NativeUIFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NativeUIFragment : Fragment() {
    private var textAccountId: TextView? = null
    private var spinnerAttribute: Spinner? = null
    private var textValue: TextView? = null
    private var buttonAddAttribute: Button? = null
    private var textAttributes: TextView? = null
    private var buttonClearAllAttributes: Button? = null
    private var environmentGroup: MaterialButtonToggleGroup? = null

    private var buttonGetOffers: Button? = null
    private var buttonClearAPIResponse: Button? = null
    private var textAPIResponse: TextView? = null
    private var buttonShowOffersUI: Button? = null
    private var progressBar: ProgressBar? = null

    private var attr = mutableMapOf<String , String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_native_u_i, container, false)

        textAccountId =  view.findViewById(R.id.textAccountId)
        spinnerAttribute = view.findViewById(R.id.spinnerSelectAttribute)
        textValue = view.findViewById(R.id.textValue)
        buttonAddAttribute = view.findViewById(R.id.buttonAddAttribute)
        textAttributes = view.findViewById(R.id.textAttributes)
        buttonClearAllAttributes = view.findViewById(R.id.buttonClearAllAttributes)
        environmentGroup = view.findViewById(R.id.environmentGroup)
        buttonGetOffers = view.findViewById(R.id.buttonGetOffers)
        buttonClearAPIResponse = view.findViewById(R.id.buttonClearAPIResponse)
        textAPIResponse = view.findViewById(R.id.textAPIResponse)
        buttonShowOffersUI = view.findViewById(R.id.buttonShowOffersUI)
        progressBar = view.findViewById(R.id.progressbar)

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

        buttonGetOffers?.setOnClickListener {
            val accountId = textAccountId?.text.toString()

            if (accountId.trimmedLength() > 0) {
                this.context?.let { context ->
                    progressBar?.isVisible = true
                    AdsPostX.getOffers(accountId,attr, context) { result ->
                        this.activity?.runOnUiThread {
                            progressBar?.isVisible = false
                        }

                        result.onSuccess {
                            this.activity?.runOnUiThread {
                                val formattedResponse = it.toString(4)
                                textAPIResponse?.text = formattedResponse
                            }
                        }
                        result.onFailure {
                            this.activity?.runOnUiThread {
                                textAPIResponse?.text = it.toString()
                            }
                        }
                    }
                }
            } else {
                this.activity?.runOnUiThread {
                    Toast.makeText(this.context, "Enter valid AccountId.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        buttonClearAPIResponse?.setOnClickListener {
            textAPIResponse?.text = ""
        }

        buttonShowOffersUI?.setOnClickListener {
            val accountId = textAccountId?.text.toString()

            if (accountId.trimmedLength() > 0) {
                val bundle = Bundle()
                bundle.putString("AccountId", accountId)

                val attributes = attr as? java.io.Serializable
                if(attributes != null) {
                    bundle.putSerializable("attributes", attributes)
                }

                val intent = Intent(this.activity, NativeUIActivity::class.java)
                intent.putExtras(bundle)
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                this.startActivity(intent)
            } else {
                this.activity?.runOnUiThread {
                    Toast.makeText(this.context, "Enter valid AccountId.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NativeUIFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            NativeUIFragment().apply {
            }
    }
}