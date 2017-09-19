package com.cho.immersivedetailtest

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cho.immersivedetail.util.LollipopCompatSingleton
import kotlinx.android.synthetic.main.fragment_immersive_detail.*
import kotlinx.android.synthetic.main.toolbar_default.*

/**
 * Created by chonamdoo on 2017. 9. 19..
 */

class MainFragment : Fragment(){
    private lateinit var activity : MainActivity
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?) =
            inflater?.inflate(R.layout.fragment_immersive_detail, null, false)
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LollipopCompatSingleton.fitStatusBarTranslucentPadding(toolbar, activity)

        toolbarTitle.text = getString(R.string.app_name)
        toolbar?.let {
            activity.setSupportActionBar(it)
            activity.supportActionBar?.setDisplayShowTitleEnabled(false)
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            activity.supportActionBar?.setHomeButtonEnabled(true)
            it.setNavigationOnClickListener {
                activity.onBackPressed()
            }
        }
        val toolbarColor = ContextCompat.getColor(activity, R.color.colorPrimary)
        scrollView.setupImmersiveEffect(activity, img_photo, toolbar, toolbarColor, toolbarTitle)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = context as MainActivity
    }
    companion object {
        val INSTANCE : MainFragment by lazy {
            MainFragment()
        }
    }
}