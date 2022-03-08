package com.example.bmi_calcurate

import android.content.ContentValues
import android.preference.PreferenceActivity
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlin.math.round

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
        record_list()
        //list_custom_item(bmi)
        record_btn(bmi)

    }

}

@Composable
fun list_custom_item(bmi : Double){

    val bmi_state_res = bmi_state_res(bmi)


    Card(
        elevation = 5.dp,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(24.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Text(text = "BMI : ${round(bmi*100)/100}" , fontSize = 24.sp , fontWeight = FontWeight.Bold , color = Black)

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
fun record_list(){

    ////////////////////////////////////////////////////////////////////////////////
    //db에서 bmi받아오기
    val context = LocalContext.current
    val db = DBHelper(context).readableDatabase
    var bmi_list = mutableListOf<Double>()

    // query의 매개변수
    // (테이블 명, 배열형식의 컬럼명, where뒤에 들어갈 문자열, where조건에 들어갈 데이터,
    // GroupBy절, Having절, orderBy절)
    val cursor = db.query("bmidb_member" , arrayOf("bmi"),
        null , null , null , null , null)

    while(cursor.moveToNext()){
        bmi_list.add(cursor.getDouble(0))
    }
    db.close()
    ////////////////////////////////////////////////////////////////////////////////
    println("bmi_list : ${bmi_list}")
    //val scrollState = rememberLazyListState()
    val scrollState = rememberScrollState()

//    LazyColumn(
//        state = scrollState,
//        // 리스트 전체 패딩
//        contentPadding = PaddingValues(16.dp),
//        // item 사이 간격
//        verticalArrangement = Arrangement.spacedBy(5.dp)
//    ){
//        items(5){ idx ->
//            Column(modifier = Modifier.fillMaxSize()) {
//                Text("Item : ${idx}" , color = Black , fontSize = 30.sp)
//            }
//        }
////        items(bmi_list) { idx ->
////            list_custom_item(idx)
////        }
//    }

    //Column은 잘나옴
    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()
            //.verticalScroll(scrollState)
    ) {
        for(i in 0..bmi_list.size-1){
            list_custom_item(bmi_list[i])
        }
    }


}


@Composable
fun record_btn(bmi : Double){
    val context = LocalContext.current
    val helper = DBHelper(context)
    val db = helper.writableDatabase

    Button(onClick = {
        // DB저장
        val values = ContentValues()
        values.put("bmi" , bmi)
        // insert
        db.insert("bmidb_member" , null , values)
        db.close()
    },
    ) {
        Text("기록하기" , color = White , fontSize = 24.sp , fontWeight = FontWeight.Bold)
    }

    Button(onClick = {
        // DB삭제
        db.delete("bmidb_member" , null , null)
    },
    ) {
        Text("삭제" , color = White , fontSize = 24.sp , fontWeight = FontWeight.Bold)
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
                .clickable { navController.navigate("Result") }
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

