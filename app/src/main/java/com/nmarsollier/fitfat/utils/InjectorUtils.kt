package com.nmarsollier.fitfat.utils

import android.view.LayoutInflater
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.reflect.Method
import kotlin.reflect.KClass

@MainThread
inline fun <reified B : ViewBinding> Fragment.viewBinding(): Lazy<B> =
    object : Lazy<B> {
        private val kClass = B::class
        private var internalValue: B? = null

        override val value: B
            get() = internalValue
                ?: kClass.getFactoryMethod()!!.let {
                    (it.invoke(null, this@viewBinding.layoutInflater) as B).also { bInstance ->
                        internalValue = bInstance
                    }
                }

        override fun isInitialized() = internalValue != null

        fun KClass<B>.getFactoryMethod(): Method? {
            return this.java.methods.firstOrNull {
                it.name == "inflate"
                        && it.parameters.size == 1
                        && it.parameters[0].type == LayoutInflater::class.java
            }
        }
    }


@MainThread
inline fun <reified B : ViewBinding> AppCompatActivity.viewBinding(): Lazy<B> =
    object : Lazy<B> {
        private val kClass = B::class
        private var internalValue: B? = null

        override val value: B
            get() = internalValue
                ?: kClass.getFactoryMethod()!!.let {
                    (it.invoke(null, this@viewBinding.layoutInflater) as B).also { bInstance ->
                        internalValue = bInstance
                    }
                }

        override fun isInitialized() = internalValue != null

        fun KClass<B>.getFactoryMethod(): Method? {
            return this.java.methods.firstOrNull {
                it.name == "inflate"
                        && it.parameters.size == 1
                        && it.parameters[0].type == LayoutInflater::class.java
            }
        }
    }




