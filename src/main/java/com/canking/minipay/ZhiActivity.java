package com.canking.minipay;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by changxing on 2017/9/8.
 */
public class ZhiActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mTitleTv, mSummeryTv, mTip;
    //weixin
    private int mZhiWay;
    private ViewGroup mQaView, mZhiBg;
    private ImageView mQaImage;

    /*******config***********/
    private String wechatTip, aliTip;
    @DrawableRes
    private int wechatQaImage, aliQaImage;

    //支付宝支付码，可从支付二维码中获取
    private String aliZhiKey;

    /*******config***********/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhi_activity);
        initView();
        initData();
    }

    private void initView() {
        mTitleTv = (TextView) findViewById(R.id.zhi_title);
        mSummeryTv = (TextView) findViewById(R.id.zhi_summery);
        mQaView = (ViewGroup) findViewById(R.id.qa_layout);
        mZhiBg = (ViewGroup) findViewById(R.id.zhi_bg);
        mQaImage = (ImageView) findViewById(R.id.qa_image_view);
        mTip = (TextView) findViewById(R.id.tip);
        mZhiBg.setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }

    private void initData() {
        Config config = (Config) getIntent().getSerializableExtra(MiniPayUtils.EXTRA_KEY_PAY_CONFIG);
        if (config == null) {
            finish();
            return;
        }
        this.wechatQaImage = config.getWechatQaImage();
        this.aliQaImage = config.getAliQaImage();
        this.wechatTip = config.getWechatTip();
        this.aliTip = config.getAliTip();
        this.aliZhiKey = config.getAliZhiKey();
        this.mZhiWay = config.getDefaultPayWay();

        if (!checkLegal()) {
            throw new IllegalStateException("MiniPay Config illegal!!!");
        } else {
            if (TextUtils.isEmpty(wechatTip)) {
                wechatTip = getString(R.string.wei_zhi_tip);
            }
            if (TextUtils.isEmpty(aliTip)) {
                aliTip = getString(R.string.ali_zhi_tip);
            }

            changeViews(mZhiWay);
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(mTip, "alpha", 0, 0.66f, 1.0f, 0);
        animator.setDuration(2888);
        animator.setRepeatCount(6);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();
    }


    private boolean checkLegal() {
        if (wechatQaImage == 0 || aliQaImage == 0 || TextUtils.isEmpty(aliZhiKey)) {
            return false;
        }

        return true;
    }

    /**
     * 转换ui
     *
     * @param payWay 要转换的支付方式
     */
    private void changeViews(int payWay) {
        if (payWay == PayWay.ZHI_WAY_ALIPAY) {
            mZhiBg.setBackgroundResource(R.color.alipay_blue);
            mTitleTv.setText(R.string.ali_zhi_title);
            mSummeryTv.setText(aliTip);
            mQaImage.setImageResource(aliQaImage);
        } else {
            mZhiBg.setBackgroundResource(R.drawable.bg_wechat_pay);
            mTitleTv.setText(R.string.wei_zhi_title);
            mSummeryTv.setText(wechatTip);
            mQaImage.setImageResource(wechatQaImage);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.zhi_btn) {
            if (mZhiWay == PayWay.ZHI_WAY_WECHAT) {
                WeZhi.startWeZhi(this, mQaView);
            } else {
                AliZhi.startAlipayClient(this, aliZhiKey);
            }
        } else if (v.getId() == R.id.back) {
            finish();
        } else if (v == mZhiBg) {
            mZhiWay = ++mZhiWay % 2;
            changeViews(mZhiWay);
        }

    }
}
