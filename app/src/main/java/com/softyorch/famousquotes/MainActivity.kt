package com.softyorch.famousquotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.softyorch.famousquotes.ui.theme.FamousQuotesTheme
import com.softyorch.famousquotes.ui.theme.PrimaryColor
import com.softyorch.famousquotes.ui.theme.SecondaryColor
import com.softyorch.famousquotes.ui.theme.TertiaryColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FamousQuotesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val appResourceName = BuildConfig.APP_TITLE
    val intResource = context.resources.getIdentifier(appResourceName, "string", context.packageName)
    val appTitle = stringResource(intResource)

    Column {
        Text(
            text = "Hello $appTitle!",
            modifier = modifier.background(color = PrimaryColor),
            color = Color.White
        )
        Text(
            text = "Hello $appTitle!",
            modifier = modifier.background(color = SecondaryColor),
            color = Color.White
        )
        Text(
            text = "Hello $appTitle!",
            modifier = modifier.background(color = TertiaryColor),
            color = Color.White
        )
    }


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FamousQuotesTheme {
        Greeting("Android")
    }
}