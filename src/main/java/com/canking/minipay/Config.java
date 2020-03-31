package com.canking.minipay;


import androidx.annotation.DrawableRes;

import java.io.Serializable;

/**
 * Created by changxing on 2017/9/20.
 */
public class Config implements Serializable {
    private String wechatTip, aliTip;
    @DrawableRes
    private int wechatQaImage, aliQaImage;

    /**
     * 支付宝支付码，可从支付二维码中获取
     */
    private String aliZhiKey;
    private int defaultPayWay;

    Config(Builder builder) {
        this.wechatQaImage = builder.wechatQaImage;
        this.aliQaImage = builder.aliQaImage;
        this.wechatTip = builder.wechatTip;
        this.aliTip = builder.aliTip;
        this.aliZhiKey = builder.aliZhiKey;
        this.defaultPayWay = builder.defaultPayWay;
    }

    private Config() {
    }


    public String getWechatTip() {
        return wechatTip;
    }

    public String getAliTip() {
        return aliTip;
    }

    public int getWechatQaImage() {
        return wechatQaImage;
    }

    public int getAliQaImage() {
        return aliQaImage;
    }

    public String getAliZhiKey() {
        return aliZhiKey;
    }

    public int getDefaultPayWay() {
        return defaultPayWay;
    }

    public static class Builder {
        private String wechatTip, aliTip;
        @DrawableRes
        private int wechatQaImage, aliQaImage;
        private String aliZhiKey;
        private int defaultPayWay;


        public Builder(String aliKey, @DrawableRes int qaAli, @DrawableRes int qaWechat, int defaultPayWay) {
            this.wechatQaImage = qaWechat;
            this.aliQaImage = qaAli;
            this.aliZhiKey = aliKey;
            this.defaultPayWay = defaultPayWay;

        }

        public Builder setWechatTip(String tip) {
            this.wechatTip = tip;
            return this;
        }

        public Builder setAliTip(String tip) {
            this.aliTip = tip;
            return this;
        }

        public Config build() { // 构建，返回一个新对象
            return new Config(this);
        }
    }
}
