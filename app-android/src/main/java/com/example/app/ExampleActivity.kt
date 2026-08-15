/**
 * EN Thin Android entry: hosts the common KMP [SdpDemoScreen] demo.
 * PT Entry Android fino: hospeda o demo [SdpDemoScreen] KMP comum.
 */
package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.appdimens.kmp.core.AppDimensProvider
import com.example.app.compose.SdpDemoScreen

class ExampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppDimensProvider {
                SdpDemoScreen()
            }
        }
    }
}
