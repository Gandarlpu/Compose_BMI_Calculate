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
                composable(route = "record"){
                    recordScreen(
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
            modifier = Modifier
                .size(30.dp)
                .clickable {
                    navController.navigate("record")
                }
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

    var currrent : LocalDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH시mm분 a")
    val formatted = currrent.format(formatter)

    var bmi_state_res = bmi_state_res(bmi)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                Brush.verticalGradient(
                    //colors = bmi_state_color as List<Color>
                bmi_state_res.bmi_state_color()
                ),
                alpha = 0.5f
            ),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        TopBar(navController)
        Spacer(modifier = Modifier.height(25.dp))
        Text(text = "현재 BMI" , fontSize = 25.sp , color = Color.White)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = "${round(bmi*100)/100}"
            , fontSize = 50.sp
            , fontWeight = FontWeight.Bold
            , color = Color.White)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "${formatted}" , fontSize = 25.sp , color = Color.White)
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            painter = painterResource(id = bmi_state_res.imageRes()),
            contentDescription = null,
            modifier = Modifier.size(200.dp),
            colorFilter = ColorFilter.tint(
                color = Color.White
            )
        )
        Text(bmi_state_res.bmi_cal(), fontSize = 50.sp , fontWeight = FontWeight.Bold , color = Color.White)

        // 상세정보
        Detail_content(bmi , bmi_state_res.bmi_state_color())

    }

}

@Composable
fun Detail_content(
    bmi : Double,
    bmi_state_color : List<Color>,
    modifier : Modifier = Modifier
){

        Spacer(modifier = modifier.height(50.dp))
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = modifier.padding(24.dp)
        ) {
            Text(text = "상세정보" , fontSize = 23.sp
                , fontWeight = FontWeight.Bold
                , color = Color.White)
            Spacer(modifier = modifier.height(13.dp))
            Card(
                elevation = 15.dp,
                shape = RoundedCornerShape(30.dp),
            ) {
                Column(
                    modifier = modifier
                        .background(bmi_state_color[0].copy(alpha = 0.5f)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = "${round(bmi*100)/100}"
                        , fontSize = 50.sp
                        , fontWeight = FontWeight.Bold
                        , color = Color.White)
                    color_bar()

                }
            }
        }
}

@Composable
fun color_bar(){

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
                Text(text = "${bmi_text[it]}" , fontSize = 20.sp , color = Color.White , fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.End
                ){
                    Text(text = "${bmi_text_num[it]}", fontSize = 20.sp , color = Color.White , fontWeight = FontWeight.Bold)
                }
            }
        }

    }

}

@Preview
@Composable
fun Pre_Detail(){
    Detail_content(25.0 , listOf(Color.Red , Color.Red))
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