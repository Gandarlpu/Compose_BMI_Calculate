package com.example.bmi_calcurate

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.math.round

@Composable
fun ResultScreen(navController: NavController, bmi : Double, formatted : String){

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
        Text(text = "${round(bmi*100) /100}"
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
                Text(text = "${round(bmi*100) /100}"
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
