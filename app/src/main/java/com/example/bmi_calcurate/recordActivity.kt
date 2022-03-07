package com.example.bmi_calcurate

import android.widget.Space
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun recordScreen(navController: NavController , bmi : Double){

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .background(
            color = com.example.bmi_calcurate.ui.theme.record_theme_color
        ),
            horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        record_Topbar(navController)
        record_list(bmi)
        record_btn()

    }

}

@Composable
fun list_custom_item(bmi: Double){

    val bmi_state_res = bmi_state_res(bmi)

    Card(
        elevation = 5.dp,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Text(text = "BMI : ${bmi}" , fontSize = 30.sp , fontWeight = FontWeight.Bold , color = Black)

            Card(
                elevation = 5.dp,
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Brush.verticalGradient(
                                bmi_state_res.bmi_state_color()
                            ),
                            alpha = 0.5f
                        )
                ) {
                Image(
                    painter = painterResource(id = bmi_state_res.imageRes()),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    colorFilter = ColorFilter.tint(
                        color = Color.White
                    )
                )
                Text(bmi_state_res.bmi_cal(), fontWeight = FontWeight.Bold
                    , color = Color.White , fontSize = 20.sp)

            }

        }
    }


    }
}

@Composable
fun record_list(bmi: Double){
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        //item간격
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){
        item {
            // 동적추가하기 mutableListOf? DB?
            list_custom_item(bmi)
        }
    }
}

@Composable
fun record_btn(){
    Button(onClick = {

    },
    ) {
        Text("기록하기" , color = White , fontSize = 24.sp , fontWeight = FontWeight.Bold)
    }
}


@Composable
fun record_Topbar(
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
            .background(com.example.bmi_calcurate.ui.theme.record_theme_color)
    ){
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = White,
            modifier = Modifier
                .size(30.dp)
                .clickable { navController.navigate("home") }
        )
        Text(text = "등록된 BMI", fontSize = 20.sp , color = White)
        Spacer(modifier = Modifier.width(150.dp))
        Button(onClick = { /*TODO*/ },
                modifier = Modifier
                    .layoutId("record_btn")
        ){
            Text(text = "수정" , color = White , fontSize = 18.sp)
        }
    }
}

