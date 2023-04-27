package com.adspostx.adspostxdemo

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.trimmedLength
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.adspostx.sdk.AdsPostX
import me.relex.circleindicator.CircleIndicator3
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NativeUIActivity : AppCompatActivity() {
    private var offers: MutableList<Offer>? = mutableListOf()
    private var viewPager: ViewPager2? = null
    private var apiKey: String? = null
    private var attributes: MutableMap<String, String>? = null
    private var progressbar: ProgressBar? = null
    private var buttonCloseOffer: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_uiactivity)

        val bundle = intent.extras
        apiKey = bundle?.getString("APIKey")
        attributes = bundle?.getSerializable("attributes") as? MutableMap<String, String>
        viewPager = findViewById(R.id.viewPager2)
        progressbar = findViewById(R.id.progressbar2)
        buttonCloseOffer = findViewById(R.id.buttonCloseOffer)

        getOffers()

        buttonCloseOffer?.setOnClickListener {
            goBack()
        }
    }

    fun getCurrentURL() {
        val currentIndex = viewPager?.currentItem
        if (currentIndex ?: 0 < offers?.size ?: 0) {
            val currentOffer = currentIndex?.let { offers?.get(it) }
            currentOffer?.click_url?.let {
                openURL(it)
                goToNextOffer()
            }
        }
    }

    private fun openURL(url: String) {
        this.runOnUiThread {
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    fun goToNextOffer() {
        this.runOnUiThread {
            if (viewPager?.currentItem == offers?.size?.minus(1)) {
                goBack()
            }
            viewPager?.apply {
                beginFakeDrag()
                fakeDragBy(-200f)
                endFakeDrag()
            }
        }
    }

    private fun getOffers() {
        progressbar?.isVisible = true
        buttonCloseOffer?.isVisible = false

        if (apiKey != null && apiKey.toString().trimmedLength() > 0) {
            AdsPostX.getOffers(apiKey!!, attributes) { result ->
                this.runOnUiThread {
                    progressbar?.isVisible = false
                    buttonCloseOffer?.isVisible = true
                }
                result.onSuccess {
                    val gson = Gson()
                    val dataJson = it.getJSONObject("data")
                    val offersJsonArray = dataJson.getJSONArray("offers")

                    val listType = object : TypeToken<List<Offer>>() {}.type
                    offers = gson.fromJson(offersJsonArray.toString(), listType)
                    offers?.let {
                        this.runOnUiThread {
                            val adapter = ViewPagerAdapter(it, this)
                            viewPager?.adapter = adapter
                            viewPager?.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                            val indicator: CircleIndicator3 = findViewById(R.id.indicator)
                            indicator.setViewPager(viewPager)
                        }
                    }
                }
                result.onFailure {
                    goBack()
                }
            }
        } else {
            goBack()
        }
    }

    private fun goBack() {
        this.runOnUiThread {
            finish()
            overridePendingTransition(0, 0);
        }
    }
}