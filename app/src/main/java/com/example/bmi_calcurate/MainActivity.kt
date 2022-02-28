package com.example.bmi_calcurate

import android.graphics.fonts.FontStyle
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import kotlin.math.pow
import kotlin.math.round

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 뷰모델
            val viewModel = viewModel<BmiViewModel>()
            val navController = rememberNavController()
            val bmi = viewModel.bmi.value

            NavHost(
                navController = navController,
                startDestination = "home"
            ){
                composable(route = "home"){
                    HomeScreen(){ height, weight ->
                        viewModel.bmiCalculate(height, weight)
                        navController.navigate("result")
                    }
                }
                composable(route = "Result"){
                    ResultScreen(
                        navController,
                        bmi = bmi
                    )
                }
            }
        }
    }
}

@Composable
fun TopBar(
    navController: NavController,
    modifier : Modifier = Modifier
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ){
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "home",
            modifier = Modifier.clickable {
                navController.navigate("home")
            }
        )
        //클릭 리스너 처리
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_link_24) ,
            contentDescription = "link",
            tint = Color.Black,
            modifier = Modifier.size(24.dp)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_add_24),
            contentDescription = "add",
            tint = Color.Black,
            modifier = Modifier.size(24.dp)
        )
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

    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
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
                modifier = Modifier.align(Alignment.End)
            ){
                Text("결과")
            }
        }
    }
}

@Composable
fun ResultScreen(navController: NavController , bmi : Double){
//    저체중	20 미만
//    정상	20 - 24
//    과체중	25 - 29
//    비만	30 이상

    val bmi_cal = when{
        bmi >= 35 -> "고도 비만"
        bmi >= 30 -> "비만"
        bmi >= 25 -> "과체중"
        bmi >= 20 -> "정상"
        else -> "저체중"
    }

    // when객체로 받아오기
    val bmi_state_color = listOf<Color>(Color.Green , Color.Blue , Color(0xFFFF9800) ,
                                        Color.Red , Color.Black)

    val Icon_Tint_Color = listOf(Color.Black , Color.White)
    var date : LocalDateTime = LocalDateTime.now()

    val imageRes = when{
        bmi >= 35 -> R.drawable.ic_superhigh_weight
        bmi >= 30 -> R.drawable.ic_high_overweight
        bmi >= 25 -> R.drawable.ic_overweight
        bmi >= 20 -> R.drawable.ic_normal
        else -> R.drawable.ic_row_weight
    }

    Scaffold(
        topBar = {
            TopBar(navController)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "현재 BMI" , fontSize = 15.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "${round(bmi*10)/100}" , fontSize = 30.sp , fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "${date}" , fontSize = 15.sp)
            
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                colorFilter = ColorFilter.tint(
                    color = Color.Black
                )
            )
            Text(bmi_cal, fontSize = 30.sp)
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