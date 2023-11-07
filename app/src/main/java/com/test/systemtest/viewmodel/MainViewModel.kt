package com.test.systemtest.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.systemtest.model.ImageData
import java.text.FieldPosition

class MainViewModel:ViewModel() {


    val  imageList = MutableLiveData<ArrayList<String>>()
    val currentList = imageList.value ?: ArrayList()

    var titleTextValue=""



    fun addImageData(imgUri: String) {
        currentList.add(imgUri)
        println("check values----"+currentList)
        imageList.value=currentList
    }

    fun removeImageData(removePosition: Int){
        currentList.removeAt(removePosition)
        imageList.value=currentList
    }

}