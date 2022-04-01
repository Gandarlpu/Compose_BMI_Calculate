package com.example.bmi_calcurate

import android.graphics.fonts.FontStyle
import android.os.Bundle
import android.window.SplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.pow
import kotlin.math.round

var bmi_color_bar = listOf(Color.Green , Color.Blue , Color(0xFFFF5722) ,
    Color.Red , Color.Black)
var bmi_text = listOf("저체중" , "정상" , "과체중" , "비만" , "고도비만")
var bmi_text_num = listOf("< 19" , ">= 20" , ">= 25 " , ">= 30" , ">= 35")

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 뷰모델
            val viewModel = viewModel<BmiViewModel>()
            val navController = rememberNavController()
            val bmi = viewModel.bmi.value
            // 해당 시간 받기
            var currrent : LocalDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH시mm분 a")
            val formatted = currrent.format(formatter)

            NavHost(
                navController = navController,
                startDestination = "splash"
            ){
                composable(route = "splash"){
                    SplashScreen(navController)
                }
                composable(route = "home"){
                    HomeScreen(){ height, weight ->
                        viewModel.bmiCalculate(height, weight)
                        navController.navigate("result")
                    }
                }
                composable(route = "Result"){
                    ResultScreen(
                        navController,
                        bmi = bmi,
                        formatted
                    )
                }
                composable(route = "record"){
                    recordScreen(
                        navController,
                        bmi = bmi,
                        formatted
                    )
                }
            }
        }
    }
}



@Composable
fun HomeScreen(
    onResultClicked : (Double , Double) -> Unit
){

    val (height , setheight) = rememberSaveable{
        mutableStateOf("")
    }
    val (weight , setweight) = rememberSaveable{
        mutableStateOf("")
    }


    Scaffold(
        topBar = {
            TopAppBar (
                title = {},
                Modifier
                    .background(Color.Blue)
                    .height(50.dp)
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = "체질량 측정기 (BMI) " ,)
            OutlinedTextField(
                value = height,
                onValueChange = setheight,
                label = { Text("키") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            OutlinedTextField(
                value = weight,
                onValueChange = setweight,
                label = { Text("몸무게") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if(height.isNotEmpty() && weight.isNotEmpty()){
                        onResultClicked(height.toDouble() , weight.toDouble())
                    }

                },
                modifier = Modifier
                    .align(Alignment.End)
                    .background(Color.Black)
            ){
                Text("결과" , color = Color.White)
            }
            Bmi_info(Modifier)
        }
    }

}


@Composable
fun Bmi_info(modifier : Modifier){

    Spacer(modifier = modifier.height(50.dp))
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(24.dp)
    ) {
        Text(text = "평균 BMI수치" , fontSize = 23.sp
            , fontWeight = FontWeight.Bold
            , color = Color.Black)
        Spacer(modifier = modifier.height(13.dp))
        Card(
            elevation = 15.dp,
            shape = RoundedCornerShape(30.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                bmi_info_color_bar()
            }
        }
    }
}

@Composable
fun bmi_info_color_bar(){

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(5){
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier
                    .clip(shape = RectangleShape)
                    .background(bmi_color_bar[it])
                    .size(24.dp),
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(text = "${bmi_text[it]}" , fontSize = 20.sp , color = Color.Black , fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.End
                ){
                    Text(text = "${bmi_text_num[it]}", fontSize = 20.sp , color = Color.Black , fontWeight = FontWeight.Bold)
                }
            }
        }

    }
}



class BmiViewModel : ViewModel() {
    private val _bmi = mutableStateOf(0.0)
    val bmi : State<Double> = _bmi

    // bmi계산 함수
    fun bmiCalculate(
        height : Double , weight : Double
    ){
        _bmi.value = weight / (height / 100.0).pow(2.0)
    }
}