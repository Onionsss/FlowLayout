package com.onion.flowlayout

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.onion.flow.FlowClickListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = mutableListOf("梦幻西游","魔兽世界","京东","美团","饿了么","魔兽世界之征途","呵呵","一一一一","QQ飞车","DNF","京东"
                ,"我的世界","360应用市场","美国之心","Windows","呵呵呵")
        flow.setData(data)
        flow.start()


        flow.flowListener = object : FlowClickListener{
            override fun onClick(title: String, position: Int) {
                Toast.makeText(this@MainActivity,"$title-$position",Toast.LENGTH_LONG).show()
            }

        }
    }
}
