package com.example.tijiosdktest

import android.annotation.SuppressLint
import android.app.StatusBarManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.RenderScript
import android.service.notification.StatusBarNotification
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.ActionBarContextView
import androidx.appcompat.widget.ViewStubCompat
import androidx.constraintlayout.motion.widget.MotionHelper
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.ViewPager2
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar
import com.netease.nimlib.sdk.auth.AuthService
import com.tijio.TijioSmartHome.sdk.TijioSmartHomeClient
import com.tijio.TijioSmartHome.sdk.services.account.AccountService
import com.tijio.TijioSmartHome.sdk.services.common.CommonAuthService
import com.tijio.TijioSmartHome.sdk.services.device.DeviceEventType
import com.tijio.TijioSmartHome.sdk.services.device.DeviceService
import com.tijio.TijioSmartHome.sdk.services.scene.SceneRuleAttributeEnum
import com.tijio.TijioSmartHome.sdk.services.scene.SceneRuleTypeEnum
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.flow.*
import java.io.File
import java.lang.Runnable
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.DurationUnit

class MainActivity : AppCompatActivity() {

    val mLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {

    }


    private val run = Runnable {
        Log.i("zzzzzzzzzzzzzzzzzzzzz","${sb_temperature.scale}")
        val value = percent2Value(sb_temperature.scale)
        val progress = value2Percent(value)
        sb_temperature.progress = progress
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        TijioSmartHomeClient.getService(DeviceService::class.java).pushAccept()

//        supportFragmentManager.beginTransaction().setMaxLifecycle()
//        ViewPager2
//        TijioSmartHomeClient.init(this)

//        TijioSmartHomeClient.getService(DeviceService::class.java).pushAccept()

        /*TijioSmartHomeClient.getService(DeviceService::class.java).obser
//        TijioSmartHomeClient.getService(CommonAuthService::class.java)
veDeviceEvent({
            it.type == DeviceEventType.PUSH_ACCEPT
        },true)*/
//        window.decorView

//        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)  // 设置状态栏透明  相当于style  windowTranslucentStatus
//        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION) // 设置导航栏透明 相当于 windowTranslucentNavigation
        // 设置此属性 content的布局 会全屏显示 注意设置状态栏导航栏占位view
        // 同时可以给view 设置属性 android:fitsSystemWindows=true   会为view添加一个paddingTop 预留系统状态栏空间
        // 上述方法适用于4.4-5.0系统的沉浸式状态

//        logViewTree("ROOT",window.decorView as ViewGroup)

//        val view = window.decorView.findViewById<ViewStubCompat>(R.id.action_mode_bar_stub)
//        val vr = view.layoutResource
//        val inflate = LayoutInflater.from(this).inflate(vr, null)
//        view.setBackgroundColor(Color.WHITE)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)  // 设置允许绘制状态栏
            window.statusBarColor = Color.TRANSPARENT  // 设置状态栏颜色
            var uiFlags: Int = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            uiFlags = uiFlags or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR  // 亮色背景  状态栏字体黑色
            window.decorView.systemUiVisibility = uiFlags
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        val iv = ImageView(this)
        iv.setImageResource(R.mipmap.ic_launcher)
        val lp = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            leftMargin = 60
            topMargin = 60
        }
//        iv.layoutParams = lp
//        window.addContentView(iv,lp)




//        VerticalSeekBar
//        window.colorMode
//        window.volumeControlStream
//        val stub: ViewStub
//        stub.layoutResource

//        val wlp = WindowManager.LayoutParams()

//        windowManager.addView(iv,wlp)
//        SeekBar

        sb_temperature.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i("zzzzzzzzzzzzzz","$progress")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.postDelayed(run,1000)
            }

        })

        fsb.setProgress(50)

        fsb.seekChange = {
            tv_hw.text = "$it"
        }

//        SeekBar

        // DataSource
        // flow
        // scope
//        EmptyCoroutineContext
        GlobalScope.launch(Dispatchers.IO) {
            delay(5000)
            (1..3).asFlow()
                    .map {
                        it
                    }
                    .collect {
                        println(it)
                    }

            val i = withContext(Dispatchers.Main) { 0 }
            val wait = async(Dispatchers.IO){ 1 }
            val await = wait.await()

            launch(Dispatchers.Default + CoroutineExceptionHandler { coroutineContext, throwable ->
                throwable.message
            }) {
                throw error("")
            }

            withTimeout(1000) {
                delay(2000)
            }
        }

        supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(0,TestFragment(),null)
                .setMaxLifecycle(TestFragment(),Lifecycle.State.STARTED)
                .commit()

        mLauncher.launch(null)

        registerForActivityResult(ActivityResultContracts.GetContent()){

        }.launch("image/*")

        registerForActivityResult(ActivityResultContracts.TakePicture()) {

        }.launch(Uri.fromFile(File("")))  // 输出uri
    }

    fun percent2Value(percent: Float) : Int {
        return (percent * 255).toInt()
    }

    fun value2Percent(value: Int) : Int {
        return (value.toFloat() / 255 * 100).toInt()
    }
}