package com.google.firebase.ml.md.kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.ml.md.R
import kotlinx.android.synthetic.main.activity_rewards.*
import com.google.firebase.ml.md.kotlin.models.itempost
import org.json.JSONArray

import org.json.JSONObject

import java.io.IOException

import java.io.InputStream

class Rewardsactivity : AppCompatActivity() {
    private lateinit var itemAdapter: ItemRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewards)


        initRecyclerView()

        addDataSet()

    }





    private fun addDataSet(){

        val data = DataSource.createDataSet()

        itemAdapter.submitList(data)

    }








    private fun initRecyclerView(){



        recycler_view.apply {

            layoutManager = LinearLayoutManager(this@Rewardsactivity)

            val topSpacingDecorator = TopSpacingItemDecoration(30)

            addItemDecoration(topSpacingDecorator)

            itemAdapter = ItemRecyclerAdapter()

            adapter = itemAdapter

        }

    }





}

