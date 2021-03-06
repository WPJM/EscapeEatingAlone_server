package com.wpjm.escapeeatingalone.Activity

import android.app.ActivityManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wpjm.escapeeatingalone.Interface.KakaoAPI
import com.wpjm.escapeeatingalone.Model.Menu
import com.wpjm.escapeeatingalone.Model.MenuDetailModel
import com.wpjm.escapeeatingalone.Model.MenuList
import com.wpjm.escapeeatingalone.Model.ResultSearchKeyword
import com.wpjm.escapeeatingalone.PersonFragment
import com.wpjm.escapeeatingalone.R
import com.wpjm.escapeeatingalone.databinding.ActivityMenuDetailBinding
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import javax.security.auth.callback.Callback

class MenuDetailActivity : AppCompatActivity() {
    private var mBinding: ActivityMenuDetailBinding? = null
    private val binding get() = mBinding!!
    private val listItems = arrayListOf<MenuList>()
    private val listAdapter=MenuDetailAdapter(listItems)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_detail)
        mBinding = ActivityMenuDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var menuType=intent.getStringExtra("menuType")
        var x=intent.getStringExtra("x")
        var y=intent.getStringExtra("y")

        /*
        var filtedMenuDetailList = menuDetailList.filter {
            it.type.equals(intent.getStringExtra("MenuType").toString())
        }
         */

        binding.meniDetailActivityRecyclerView.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        binding.meniDetailActivityRecyclerView.setHasFixedSize(true)
        binding.meniDetailActivityRecyclerView.adapter=listAdapter

        if (menuType != null && x!=null && y!=null) {
            searchKeyword(menuType,x,y)
        }



    }
    // ????????? ?????? ??????
    private fun searchKeyword(keyword: String,x:String,y:String) {
        Log.d("searchKeyword","????????????")
        val retrofit = Retrofit.Builder()          // Retrofit ??????
            .baseUrl(CurrentMapSearchActivity.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)            // ?????? ?????????????????? ????????? ??????

        Log.d("call ???","x??? ${x} y??? ${y}")
        val call = api.getSearchMenu(CurrentMapSearchActivity.API_KEY, keyword,x,y,5000)    // ?????? ?????? ??????

        // API ????????? ??????
        call.enqueue(object: retrofit2.Callback<ResultSearchKeyword> {
            override fun onResponse(call: Call<ResultSearchKeyword>, response: Response<ResultSearchKeyword>) {
                // ?????? ??????
                var searchResult: ResultSearchKeyword?=response.body()
                var menuType:String=keyword
                if (!searchResult?.documents.isNullOrEmpty()) {
                    listItems.clear()
                    // ?????? ?????? ??????
                    Log.d("????????????","??????")
                    for (document in searchResult!!.documents) {
                        // ????????? ??????????????? ?????? ??????
                        val item = MenuList(document.place_name,
                            document.road_address_name,
                            document.address_name,
                            menuType)
                        listItems.add(item)
                        Log.d("????????????","${listItems[0].name}")
                    }
                    listAdapter.notifyDataSetChanged()
                    Log.d("???????????? ?????????"," ????????? ? ${listItems[0].name}")
                    Log.d("???????????? ?????????","${listItems[0].menuType}")

                } else {
                    // ?????? ?????? ??????
                    //Toast.makeText(this, "?????? ????????? ????????????", Toast.LENGTH_SHORT).show()
                }
                Log.d("LocalSearch", "?????? ??????")

            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // ?????? ??????
                Log.w("LocalSearch", "?????? ??????: ${t.message}")
            }
        })

    }



    override fun onBackPressed() {
        super.onBackPressed()
    }




}