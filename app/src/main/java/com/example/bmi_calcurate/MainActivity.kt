package com.example.bmi_calcurate

import android.graphics.fonts.FontStyle
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
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
        //수직
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ){
        Spacer(modifier = Modifier.height(40.dp))
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier
                .size(30.dp)
                .clickable { navController.navigate("home") }
        )
        Spacer(modifier = Modifier.width(250.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_link_24),
            contentDescription = "Bell",
            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_add_24),
            contentDescription = "Menu",
            tint = Color.White,
            modifier = Modifier.size(30.dp)
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
        topBar = {
            TopAppBar (
                title = { Text(text = "체질량 측정기 (BMI)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 23.sp,
                    color = Color.White)}
            )
        }
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

    val bmi_cal = when{
        bmi >= 35 -> "고도 비만"
        bmi >= 30 -> "비만"
        bmi >= 25 -> "과체중"
        bmi >= 20 -> "정상"
        else -> "저체중"
    }

    val bmi_state_color = when{
        bmi >= 35 -> listOf(Color.Black , Color(0xFF464545))
        bmi >= 30 -> listOf(Color.Red , Color(0xFFF76262))
        bmi >= 25 -> listOf(Color(0xFFFF5722), Color(0xFFF76638))
        bmi >= 20 -> listOf(Color.Blue , Color(0xFF7E8EEB))
        else -> listOf(Color.Green , Color(0xFF98C069))
    }

    var currrent : LocalDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH시mm분 a")
    val formatted = currrent.format(formatter)

    val imageRes = when{
        bmi >= 35 -> R.drawable.ic_superhigh_weight
        bmi >= 30 -> R.drawable.ic_high_overweight
        bmi >= 25 -> R.drawable.ic_overweight
        bmi >= 20 -> R.drawable.ic_normal
        else -> R.drawable.ic_row_weight
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                Brush.verticalGradient(
                    colors = bmi_state_color as List<Color>
                ),
                alpha = 0.5f
            ),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        TopBar(navController)
        Spacer(modifier = Modifier.height(25.dp))
        Text(text = "현재 BMI" , fontSize = 25.sp , color = Color.White)
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = "${round(bmi*100)/100}"
            , fontSize = 50.sp
            , fontWeight = FontWeight.Bold
            , color = Color.White)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "${formatted}" , fontSize = 25.sp , color = Color.White)
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.size(200.dp),
            colorFilter = ColorFilter.tint(
                color = Color.White
            )
        )
        Text(bmi_cal, fontSize = 50.sp , fontWeight = FontWeight.Bold , color = Color.White)

        // 상세정보
        Detail_content(bmi )

    }

}

@Composable
fun Detail_content(
    bmi : Double,
    modifier : Modifier = Modifier
){


        Text(text = "상세정보" , fontSize = 20.sp , fontWeight = FontWeight.Bold , color = Color.White)
        Card(elevation = 5.dp) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
                    .padding(24.dp)
                    .fillMaxSize()
                    .alpha(0.5f)
            ) {

            }
        }



}

@Preview
@Composable
fun Pre_Detail(){
    Detail_content(bmi = 10.0 )
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