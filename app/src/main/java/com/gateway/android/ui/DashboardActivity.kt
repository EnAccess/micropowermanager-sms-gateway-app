package com.gateway.android.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.TelephonyManager
import android.text.Html
import com.gateway.android.BuildConfig
import com.gateway.android.utils.SharedPreferencesWrapper
import com.gateway.android.utils.Util
import kotlinx.android.synthetic.main.activity_dashboard.*


class DashboardActivity : AppCompatActivity(), SharedPreferencesWrapper.Listener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.gateway.android.R.layout.activity_dashboard)

        SharedPreferencesWrapper.getInstance().setListener(this)

        supportActionBar?.hide()

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.READ_PHONE_STATE
                ),
                REQUEST_CODE_SMS_PERMISSION
            )
        }

        tvAppInfo.text = getString(com.gateway.android.R.string.app_information, BuildConfig.VERSION_NAME)
        invalidateDynamicViews()

        tvContactDetail.text = Html.fromHtml(getString(com.gateway.android.R.string.contact_detail))

        cvContact.setOnClickListener {
            Util.sendEmail(this)
        }
    }

    override fun onSharedPreferencesValueChange() {
        invalidateDynamicViews()
    }

    private fun invalidateDynamicViews() {
        runOnUiThread {
            ivNetworkState.setImageDrawable(
                if (SharedPreferencesWrapper.getInstance().networkState == 1) ContextCompat.getDrawable(
                    this,
                    com.gateway.android.R.drawable.network_active
                ) else ContextCompat.getDrawable(this, com.gateway.android.R.drawable.network_inactive)
            )

            ivSimState.setImageDrawable(
                if (SharedPreferencesWrapper.getInstance().simState == TelephonyManager.SIM_STATE_READY) ContextCompat.getDrawable(
                    this,
                    com.gateway.android.R.drawable.sim_active
                ) else ContextCompat.getDrawable(this, com.gateway.android.R.drawable.sim_inactive)
            )

            tvSentMessageCount.text = SharedPreferencesWrapper.getInstance().sentMessageCount.toString()
            tvReceivedMessageCount.text = SharedPreferencesWrapper.getInstance().receivedMessageCount.toString()
        }
    }

    companion object {
        private const val REQUEST_CODE_SMS_PERMISSION = 1234
    }
}
