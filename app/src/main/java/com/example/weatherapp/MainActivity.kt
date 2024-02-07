package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.Screens.MainCard
import com.example.weatherapp.Screens.TabLayout
import com.example.weatherapp.data.WeatherModel
import com.example.weatherapp.ui.theme.WeatherAppTheme
import org.json.JSONObject
import org.json.JSONStringer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                val daysList = remember {
                    mutableStateOf(listOf<WeatherModel>())
                }
                getData("London", this, daysList)
                Image(
                    painter = painterResource(id = R.drawable.clouds),
                    contentDescription = "im1",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.4f),
                    contentScale = ContentScale.FillBounds
                )
                Column {
                    MainCard()
                    TabLayout(daysList)
                }

            }
        }
    }
}


private fun getData(city: String, context: Context, daysList: MutableState<List<WeatherModel>>) {
    val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
            "8411cdf511e54d7c808133537230810" +
            "&q=$city" +
            "&days=" +
            "4" +
            "&aqi=no&alerts=no\n"
    val queque = Volley.newRequestQueue(context)
    val sRequest = StringRequest( // Делаю запрос с помощью библеотеки
        Request.Method.GET,// прописываю все с помощью ctrl+d и все
        url,
        {
            respons ->
            val list = getWeather(respons)
            daysList.value = list
        },
        {

        }
    )
    queque.add(sRequest) // Добавляю ответ на мой запрос в очередь
}

private fun getWeather(response:String): List<WeatherModel> {
    if (response.isEmpty()) return listOf()
    val list = ArrayList<WeatherModel>()
    val mainObj = JSONObject(response)
    val city = mainObj.getJSONObject("location").getString("name")
    val days = mainObj.getJSONObject("forecast").getJSONArray("forecastday")

    for (i in 0 until days.length()) {
        val item = days[i] as JSONObject
        list.add(
            WeatherModel(
                city, item.getString("date"), "",
                item.getJSONObject("day").getJSONObject("condition").getString("text"),
                item.getJSONObject("day").getJSONObject("condition").getString("icon"),
                item.getJSONObject("day").getString("maxtemp_c"),
                item.getJSONObject("day").getString("mintemp_c"),
                item.getJSONArray("hour").toString()
            )
        )
    }
    list[0] = list[0].copy(
        time = mainObj.getJSONObject("current").getString("last_updated"),
        currentTemp = mainObj.getJSONObject("current").getString("temp_c"),
    )
    return list
}