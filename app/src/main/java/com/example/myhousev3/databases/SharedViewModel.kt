package com.example.myhousev3.databases

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedViewModel : ViewModel() {
    private val cartItemCount = MutableLiveData<Int>(0)
    private val totalPrice = MutableLiveData<Double>(0.0)

    fun getCartItemCount(): LiveData<Int> {
        return cartItemCount
    }

    fun incrementCartItemCount() {
        cartItemCount.postValue(cartItemCount.value?.plus(1))
    }
    fun decrementCartItemCount() {
        cartItemCount.value = (cartItemCount.value ?: 0) - 1
        if (cartItemCount.value!! < 0) cartItemCount.value = 0
    }

    fun setCartItemCount(count: Int) {
        if (count > 0) {
            cartItemCount.value = count
        } else {
            cartItemCount.value = 0
        }
    }
    fun resetCartItemCount() {
        cartItemCount.postValue(0)
    }

    fun getTotalPrice(): LiveData<Double> { // Добавьте этот метод
        return totalPrice
    }
    fun setTotalPrice(price: Double) { // И этот
        totalPrice.postValue(price)
    }
}