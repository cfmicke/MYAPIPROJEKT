package se.mikaelolausson.myapiprojekt

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import se.mikaelolausson.myapiprojekt.ui.theme.MyApiProjektTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApiProjektTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Chuckjoke()
                }
            }
        }
    }
}

@Composable
fun Chuckjoke(modifier: Modifier=Modifier) {

    val client = OkHttpClient()
    var thejoke by remember { mutableStateOf("joke") }
    var started by remember { mutableStateOf(false) }

    fun loadjoke() {
        val req = okhttp3.Request.Builder().url("http://api.chucknorris.io/jokes/random").build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.i("APIDEBUG", "HÄMTNING INTE OK")
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unspected code $response")
                    var responseString = response.body!!.string()

                    Log.i("APIDEBUG", responseString)
                    val jokedata = Json{ ignoreUnknownKeys = true}.decodeFromString<Chuckjoke>(responseString)
                    thejoke = jokedata.value
                }
            }
        } )
    }
    LaunchedEffect(started) {
        loadjoke()
    }

    Column {
        Text(
            text = thejoke,
            modifier = modifier
        )
        Button(onClick = { loadjoke() }) {
            Text(text = "Load Joke")

        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApiProjektTheme {
        Chuckjoke()
    }
}

@Serializable
data class Chuckjoke(val category: List<String>, val created_at :String, val value: String)