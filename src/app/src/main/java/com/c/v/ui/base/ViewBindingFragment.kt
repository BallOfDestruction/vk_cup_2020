package com.c.v.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import javax.inject.Inject

abstract class ViewBindingFragment<TBinding : ViewBinding> : Fragment() {
    private var _viewBinding: TBinding? = null
    protected val viewBinding get() = _viewBinding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _viewBinding = getViewBindingInflater()(inflater, container, false)
        return viewBinding.root
    }

    abstract fun getViewBindingInflater(): (LayoutInflater, ViewGroup?, Boolean) -> TBinding

    override fun onDestroyView() {
        _viewBinding = null
        super.onDestroyView()
    }
}

