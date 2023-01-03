package com.vijay.exoplayeradsdemo.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vijay.exoplayeradsdemo.data.AdsModel
import com.vijay.exoplayeradsdemo.data.AdsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.json.JSONObject

class AdsViewModel: ViewModel() {

  private var adsList = MutableLiveData<List<AdsModel>>()

  fun getAds(){
    viewModelScope.launch(Dispatchers.IO){
      val list = mutableListOf<AdsModel>()
      val response = AdsRepository().getAds()
      if (response!=null){
        val jsonObject = JSONObject(response)
        val adsObject = jsonObject.getJSONObject("result").getJSONObject("asi")
          .getJSONObject("orf5snmy").getJSONObject("cmps").getJSONObject("dd")
          .getJSONObject("5394").getJSONObject("ads")
        val keys = adsObject.keys()
        while (keys.hasNext()){
          val key = keys.next()
          val innerObject = adsObject.getJSONObject(key)
          val ad = innerObject.getString("ad")
          val adObject = JSONObject(ad)
          val imageObject = adObject.getJSONArray("customimage").getJSONObject(0)

          val adModel = AdsModel(
            title = adObject.getString("title"),
            buttonText = adObject.getString("ctatext"),
            webHook = adObject.getString("ctafb"),
            discountPrice = adObject.getString("salePrice"),
            discountPercent = adObject.getString("disPer"),
            actualPrice = adObject.getString("price"),
            imageUrl = imageObject.getString("174x218"),
          )
          list.add(adModel)
        }
      }
      withContext(Dispatchers.Main){
        setAdsList(list)
      }
    }
  }

  private fun setAdsList(list: MutableList<AdsModel>){
    adsList.value = list
  }

  fun getAdsList(): LiveData<List<AdsModel>>{
    return adsList
  }
}