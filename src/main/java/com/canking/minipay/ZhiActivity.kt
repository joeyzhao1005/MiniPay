package com.canking.minipay

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.zhao.withu.app.AppData

/**
 *
 * @author changxing
 * @date 2017/9/8
 */
class ZhiActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mTitleTv: TextView
    private lateinit var mSummeryTv: TextView
    private lateinit var mTip: TextView
    private var mZhiWay = 0
    private lateinit var mZhiBg: ViewGroup
    private lateinit var mQaImage: ImageView

    /*******config */
    private var wechatTip: String? = null
    private var aliTip: String? = null

    @DrawableRes
    private var wechatQaImage = 0

    @DrawableRes
    private var aliQaImage = 0

    /**
     * 支付宝支付码，可从支付二维码中获取
     */
    private var aliZhiKey: String? = null

    /*******config */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.zhi_activity)
        initView()
        initData()
    }

    private fun initView() {
        mTitleTv = findViewById<View>(R.id.zhi_title) as TextView
        mSummeryTv = findViewById<View>(R.id.zhi_summery) as TextView
        mZhiBg = findViewById<View>(R.id.zhi_bg) as ViewGroup
        mQaImage = findViewById<View>(R.id.qa_image_view) as ImageView
        mTip = findViewById<View>(R.id.tip) as TextView
        mZhiBg.setOnClickListener(this)

        findViewById<ImageButton>(R.id.back).apply {
            setOnClickListener(this@ZhiActivity)
            if (AppData.data()?.runMode() == 0) {
                mTitleTv.setTextColor(ResourcesCompat.getColor(resources,R.color.black, null))
                mSummeryTv.setTextColor(ResourcesCompat.getColor(resources,R.color.black, null))
                mTip.setTextColor(ResourcesCompat.getColor(resources,R.color.black, null))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    findViewById<ImageButton>(R.id.back).imageTintList = (ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.black, null)))
                }
            }
        }


    }

    private fun initData() {
        val config = intent.getSerializableExtra(MiniPayUtils.EXTRA_KEY_PAY_CONFIG) as Config?
        if (config == null) {
            finish()
            return
        }
        wechatQaImage = config.wechatQaImage
        aliQaImage = config.aliQaImage
        wechatTip = config.wechatTip
        aliTip = config.aliTip
        aliZhiKey = config.aliZhiKey
        mZhiWay = config.defaultPayWay
        check(checkLegal()) { "MiniPay Config illegal!!!" }
        if (TextUtils.isEmpty(wechatTip)) {
            wechatTip = getString(R.string.wei_zhi_tip)
        }
        if (TextUtils.isEmpty(aliTip)) {
            aliTip = getString(R.string.ali_zhi_tip)
        }
        changeViews(mZhiWay)
        val animator = ObjectAnimator.ofFloat(mTip, "alpha", 0f, 0.66f, 1.0f, 0f)
        animator.duration = 2888
        animator.repeatCount = 6
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.repeatMode = ValueAnimator.REVERSE
        animator.start()
    }

    private fun checkLegal(): Boolean {
        return !(wechatQaImage == 0 || aliQaImage == 0 || TextUtils.isEmpty(aliZhiKey))
    }

    /**
     * 转换ui
     *
     * @param payWay 要转换的支付方式
     */
    private fun changeViews(payWay: Int) {
        if (payWay == PayWay.ZHI_WAY_ALIPAY) {
            if (AppData.data()?.runMode() == 0) {
                mZhiBg.setBackgroundResource(R.color.white)
            } else {
                mZhiBg.setBackgroundResource(R.color.alipay_blue)
            }
            mTitleTv.setText(R.string.ali_zhi_title)
            mSummeryTv.text = aliTip
            mQaImage.setImageResource(aliQaImage)
        } else {
            if (AppData.data()?.runMode() == 0) {
                mZhiBg.setBackgroundResource(R.color.white)
            } else {
                mZhiBg.setBackgroundResource(R.drawable.bg_wechat_pay)
            }
            mTitleTv.setText(R.string.wei_zhi_title)
            mSummeryTv.text = wechatTip
            mQaImage.setImageResource(wechatQaImage)
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.zhi_btn) {
            if (mZhiWay == PayWay.ZHI_WAY_WECHAT) {
                WeZhi.startWeZhi(this, mQaImage)
            } else {
                AliZhi.startAlipayClient(this, aliZhiKey)
            }
        } else if (v.id == R.id.back) {
            finish()
        } else if (v === mZhiBg) {
            mZhiWay = ++mZhiWay % 2
            changeViews(mZhiWay)
        }
    }
}