package com.example.nft

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.UnknownHostException

class MainViewModel : ViewModel() {
    
    private val repository = Repository()
    
    val collections = MutableLiveData<List<Collection>>()
    val progressing = MutableLiveData(false)
    val reachEnd = MutableLiveData(false)
    val toastMessage = MutableLiveData("")

    fun getCollections(offset: Int) {
        progressing.value = true
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val query = repository.query(offset)
                progressing.postValue(false)
                reachEnd.postValue(query.isEmpty())
                collections.postValue(query)
            } catch (e: UnknownHostException) {
                progressing.postValue(false)
                toastMessage.postValue(TOAST_MESSAGE_NETWORK_ERROR)
            } catch (e: Exception) {
                progressing.postValue(false)
                toastMessage.postValue(TOAST_MESSAGE_OTHERS)
            }
        }
    }


    companion object {
        const val TOAST_MESSAGE_NETWORK_ERROR = "network"
        const val TOAST_MESSAGE_OTHERS = "others"
    }
}