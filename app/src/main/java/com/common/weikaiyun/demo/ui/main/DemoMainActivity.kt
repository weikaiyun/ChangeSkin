package com.common.weikaiyun.demo.ui.main

import android.Manifest
import android.os.Bundle
import com.common.weikaiyun.R
import com.common.weikaiyun.demo.ui.base.BaseSupportActivity
import com.weikaiyun.fragmentation.SupportHelper
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructPermissionsRequest

class DemoMainActivity : BaseSupportActivity() {
    private lateinit var permissionsRequester: PermissionsRequester

    override fun getContentViewID(): Int = R.layout.activity_demo_main

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun initView(savedInstanceState: Bundle?) {
        var mainFragment = SupportHelper.findFragment(supportFragmentManager, DemoMainFragment::class.java)
        if (mainFragment == null) {
            mainFragment = DemoMainFragment.newInstance()
            loadRootFragment(R.id.container, mainFragment)
        }

        permissionsRequester = constructPermissionsRequest(Manifest.permission.READ_EXTERNAL_STORAGE,
            onShowRationale = ::onShowRationale,
            onPermissionDenied = ::onDenied,
            onNeverAskAgain = ::onNeverAskAgain
        ) {

        }

        permissionsRequester.launch()
    }

    private fun onDenied() {

    }

    private fun onShowRationale(request: PermissionRequest) {
        request.proceed()
    }

    private fun onNeverAskAgain() {

    }
}