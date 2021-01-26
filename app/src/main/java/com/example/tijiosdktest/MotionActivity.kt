package com.example.tijiosdktest

import android.content.pm.PackageManager
import android.graphics.drawable.Animatable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tijiosdktest.camera.Camera2Help
import com.example.tijiosdktest.camera.CameraXHelp
import com.example.tijiosdktest.databinding.ActivityRvBinding
import com.facebook.common.util.UriUtil
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.imagepipeline.image.ImageInfo
import com.hi.dhl.binding.viewbind
import kotlinx.android.synthetic.main.activity_rv.*
import kotlinx.android.synthetic.main.motion_test.*
import java.io.File


/**
 * MotionLayout 动画
 * motion scene 定义动画
 */
class MotionActivity : AppCompatActivity() {

    companion object{
        var isNight = false
    }

    private val bind : ActivityRvBinding by lazy { ActivityRvBinding.inflate(layoutInflater) }

    private val activityRv by viewbind<ActivityRvBinding>()

    private val activityRvB : ActivityRvBinding by viewCustom()

    private val cameraHelp by lazy { CameraXHelp() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        ActivityRvBinding.inflate(layoutInflater)

//        Fresco.initialize(this)
        setContentView(R.layout.activity_rv)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA),100)
        }

//        PropertyValuesHolder.ofFloat()

//        ObjectAnimator


//        androidx.constraintlayout.motion.widget.MotionHelper
//        MotionHelper

//        motion.progress = 0.5f

//        ProgressDialog(this).progress = 10

//        animate_view.imageAssetsFolder = "images"
//        animate_view.enableMergePathsForKitKatAndAbove(true)
//        animate_view.setOutlineMasksAndMattes(true)
        /*val a = ValueAnimator.ofFloat(0f, 1f)
        a.duration = 10_000
        a.addUpdateListener {
            animate_view.progress = it.animatedValue as Float
        }
        a.start()*/
        /*animate_view.addValueCallback(
            KeyPath("Shape Layer", "Rectangle", "Fill"),
            LottieProperty.COLOR,
            { Color.RED }
        )*/
        /*animate_view.setImageAssetDelegate {
            Log.i("zzzzzzzzzzzzzzzz","${it.id} ,, ${it.fileName} ,, ${it.dirName}")
            return@setImageAssetDelegate null
        }*/
        /*animate_view.addAnimatorUpdateListener {
            Log.i("zzzzzzzzzzzzzzzzz",":: ${it.animatedValue}")
        }*/

//        motion.currentState

//        SmartRefreshLayout

//        NestedScrollingParent

//        RecyclerView

//        NestedScrollView

//        Log.i("zzzzzzzzzzzzzzzz", "${getDirectory("ANDROID_EXPAND","/mnt/expand")}  ,,, ${getDirectory("ANDROID_STORAGE","/storage")}")

        /*btn_compat.setOnClickListener {
//            isNight = !isNight
//            val theme = if (isNight) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
//            AppCompatDelegate.setDefaultNightMode(theme)  // 会重新创建activity

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                val videoPath = File(externalCacheDir, "input.mp4").absolutePath
                val audioPath = File(externalCacheDir, "music.mp3").absolutePath
               thread {  AudioUtils().mixVideo(
                   this,
                   videoPath,
                   audioPath,
                   15 * 1000 * 1000,
                   25 * 1000 * 1000,
                   30,
                   100
               ) }
            }
        }*/

//        Glide.with(this).load(R.drawable.ic_mic).into(iv_web)
        /*val imageRequestBuilder = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.ic_mic)
        val builder = Fresco.newDraweeControllerBuilder()
        builder.setImageRequest(imageRequestBuilder.build())
        builder.autoPlayAnimations = true
        sdv.setController(builder.build())*/
        /*var anim : Animatable? = null
        val listener = object : BaseControllerListener<ImageInfo>() {
            override fun onFinalImageSet(
                    id: String?,
                    imageInfo: ImageInfo?,
                    animatable: Animatable?
            ) {
//                super.onFinalImageSet(id, imageInfo, animatable)
                animatable?.start()
                anim = animatable

            }
        }

        val build = Fresco.newDraweeControllerBuilder()
                .setUri(UriUtil.getUriForResourceId(R.drawable.ic_mic))
                .setControllerListener(listener)
                .build()
        sdv.controller = build

        sdv.setOnClickListener {
            if (anim?.isRunning == true) {
                anim?.stop()
            } else {
                anim?.start()
            }
        }*/
//
//
//        sdv.setActualImageResource(R.drawable.ic_mic)


//        rv_test.layoutManager = CustomLayoutManager(LinearLayoutManager.VERTICAL)
//        rv_test.adapter = TestAdapter()

        /*texture.postDelayed({
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                cameraHelp.startPreview(texture,null,true)
            }
        },5500)*/
    }

    fun getDirectory(variableName: String?, defaultPath: String?): File? {
        val path = System.getenv(variableName!!)
        return if (path == null) File(defaultPath) else File(path)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                cameraHelp.start(this,texture)
            }
        }
    }


}