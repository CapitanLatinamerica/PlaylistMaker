package com.practicum.playlistmaker.app

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val observers = HashMap<Observer<in T>, Observer<in T>>()

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        val wrapper = Observer<T> { t ->
            if (t != null) {
                observer.onChanged(t)
                value = null
            }
        }
        observers[observer] = wrapper
        super.observe(owner, wrapper)
    }

    override fun removeObserver(observer: Observer<in T>) {
        observers.remove(observer)?.let {
            super.removeObserver(it)
        }
    }
}