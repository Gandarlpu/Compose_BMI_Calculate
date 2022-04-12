package com.example.bmi_calcurate

import android.annotation.SuppressLint
import android.content.ContentValues
import android.preference.PreferenceActivity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.time.LocalDate
import kotlin.math.round

@Composable
fun recordScreen(navController: NavController , bmi : Double , formatted : String){

    record_main_list(navController, bmi, formatted)

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun record_main_list(navController: NavController , bmi : Double , formatted : String){

    val context = LocalContext.current
    val db = DBHelper(context).writableDatabase
    var bmi_list = ArrayList<record_list_item_data>()

    // query의 매개변수
    val cursor = db.query("bmidb_member" , arrayOf("bmi" , "time"),
        null , null , null , null , null)

    if(cursor == null){
        record_btn(bmi = bmi, time = formatted, navController = navController)
    }else if(cursor != null){
        while(cursor.moveToNext()){
            val record_data = record_list_item_data(
                  cursor.getDouble(0)
                , cursor.getString(1)
            )
            bmi_list.add(record_data)
        }
        //db.close()
    }
    println("$bmi_list")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(com.example.bmi_calcurate.ui.theme.record_theme_color),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        record_Topbar(navController)
        LazyColumn{
            itemsIndexed(
                items = bmi_list,
                key = {index , item ->
                    item.hashCode() // item 하나마다 객체주소값을 부여?
                }
            ){ index, item ->
                val backgroundColor = Color(240, 240, 240)
                val dismissState = rememberDismissState(confirmStateChange = { dismissValue ->
                    when (dismissValue) {
                        DismissValue.Default -> { // dismissThresholds 만족 안한 상태
                            false
                        }
                        DismissValue.DismissedToEnd -> { // -> 방향 스와이프
                            false
                        }
                        DismissValue.DismissedToStart -> { // <- 방향 스와이프 (삭제)
                            true
                        }
                    }
                })

                SwipeToDismiss(
                    state = dismissState,
                    dismissThresholds = { FractionalThreshold(0.25f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    dismissContent = { // content
                        list_custom_item(item)
                    },
                    background = { // dismiss content
                        val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
                        val color by animateColorAsState(
                            when (dismissState.targetValue) {
                                DismissValue.Default -> backgroundColor.copy(alpha = 0.5f) // dismissThresholds 만족 안한 상태
                                DismissValue.DismissedToEnd -> Color.Green.copy(alpha = 0.4f) // -> 방향 스와이프 (수정)
                                DismissValue.DismissedToStart -> Color.Red.copy(alpha = 0.5f) // <- 방향 스와이프 (삭제)
                            }
                        )
                        val icon = when (dismissState.targetValue) {
                            DismissValue.Default -> painterResource(R.drawable.ic_baseline_add_circle_24)
                            DismissValue.DismissedToEnd -> painterResource(R.drawable.ic_baseline_add_circle_24)
                            DismissValue.DismissedToStart -> painterResource(R.drawable.ic_baseline_delete_forever_24)
                        }
                        val scale by animateFloatAsState(
                            when (dismissState.targetValue == DismissValue.Default) {
                                true -> 0.8f
                                else -> 1.5f
                            }
                        )
                        val alignment = when (direction) {
                            DismissDirection.EndToStart -> Alignment.CenterEnd
                            DismissDirection.StartToEnd -> Alignment.CenterStart
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(horizontal = 30.dp),
                            contentAlignment = alignment
                        ) {
                            Icon(
                                modifier = Modifier
                                    .scale(scale)
                                    .clickable {
                                        val values = bmi_list.get(index)
                                        println("bmi : ${values.bmi}")
                                        println("time : ${values.time}")
                                        //val sql =
                                        //    "DELETE FROM bmidb WHERE bmi=${values.bmi} AND time=${values.time};"
                                        //db.execSQL(sql)
                                        // 참고 : https://www.youtube.com/watch?v=MSuVcP0OqRg
                                        db.delete("bmidb","bmi=${values.bmi} AND time=${values.time}",null)
                                        db.close()
                                        println("삭제 후 : $bmi_list")
                                    },
                                painter = icon,
                                contentDescription = null
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
        record_btn(bmi = bmi, time = formatted, navController = navController)
    }
}

@Composable
fun record_btn(bmi : Double , time : String ,  navController: NavController){
    val context = LocalContext.current
    val helper = DBHelper(context)
    val db = helper.writableDatabase
    var isClicked = true
    // 두번 클릭했을 때 튕김이슈
    // 일단 버튼 클릭횟수 제한으로 막아두고 나중에 다른방법 사용
    // 기록하기 누르니까 기존에 있던 데이터가 한번 더 겹쳐서 입력됨,

    Spacer(modifier = Modifier.height(10.dp))
    Button(onClick = {
            if(isClicked){
                // DB저장
                val values = ContentValues()
                values.put("bmi" , bmi)
                values.put("time" , time)
                // insert
                db.insert("bmidb_member" , null , values)
                db.close()

                isClicked = false

                Toast.makeText(context , "저장 완료" , Toast.LENGTH_SHORT).show()
                navController.navigate("result")
            }else{
                Toast.makeText(context , "앱 종료 후 다시 클릭해주세요" , Toast.LENGTH_SHORT).show()
            }
        }
    ) {
        Text("기록하기" , color = White , fontSize = 23.sp , fontWeight = FontWeight.Bold)
    }

}


@Composable
fun record_Topbar(
    navController: NavController,
    modifier : Modifier = Modifier
){

    val context = LocalContext.current
    val db = DBHelper(context).writableDatabase
    var remove_state by remember { mutableStateOf(false) }

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
                .clickable { navController.navigate("Result") }
        )
        Text(text = "등록된 BMI", fontSize = 20.sp , color = White)
        Spacer(modifier = Modifier.width(150.dp))

    }
}

@Composable
fun list_custom_item(bmi : record_list_item_data) {

    val bmi_data = bmi.bmi
    val bmi_state_res = bmi_state_res(bmi_data)

    Card(
        elevation = 5.dp,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(5.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(5.dp)
            ) {
                Text(text = "BMI : ${round(bmi_data*100)/100}" , fontSize = 24.sp , fontWeight = FontWeight.Bold , color = Black)
                Text(text = "${bmi.time}" , fontSize = 15.sp , color = Gray)
            }
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
                        modifier = Modifier
                            .size(50.dp),
                        colorFilter = ColorFilter.tint(
                            color = Color.White
                        ),
                    )
                    Text(bmi_state_res.bmi_cal(), fontWeight = FontWeight.Bold
                        , color = Color.White , fontSize = 16.sp)

                }
            }
        }
    }
}