package com.hocel.chirrup.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.hocel.chirrup.R

var rewardedAd: RewardedAd? = null
private var TAG = "MainActivity"
var mInterstitialAd: InterstitialAd? = null


fun loadRewardedAd(context: Context) {
    val adRequest = AdRequest.Builder().build()
    RewardedAd.load(
        context,
        context.getString(R.string.reward_ad_id),
        adRequest,
        object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                adError.toString().let { Log.d(TAG, it) }
                rewardedAd = null
            }

            override fun onAdLoaded(ad: RewardedAd) {
                Log.d(TAG, "Ad was loaded.")
                rewardedAd = ad
            }
        }
    )
    rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
        override fun onAdClicked() {
            // Called when a click is recorded for an ad.
            Log.d(TAG, "Ad was clicked.")
        }

        override fun onAdDismissedFullScreenContent() {
            // Called when ad is dismissed.
            // Set the ad reference to null so you don't show the ad a second time.
            Log.d(TAG, "Ad dismissed fullscreen content.")
            rewardedAd = null
        }

        override fun onAdImpression() {
            // Called when an impression is recorded for an ad.
            Log.d(TAG, "Ad recorded an impression.")
        }

        override fun onAdShowedFullScreenContent() {
            // Called when ad is shown.
            Log.d(TAG, "Ad showed fullscreen content.")
        }
    }
}

fun showRewardedAd(context: Context, handleReward: () -> Unit) {
    val activity = context.findActivity()
    rewardedAd?.let { ad ->
        ad.show(activity!!) { rewardItem: RewardItem ->
            // Handle the reward.
            handleReward()
            Log.d(TAG, "User earned the reward.")
        }
    } ?: run {
        Log.d(TAG, "The rewarded ad wasn't ready yet.")
    }
}


//load the interstitial ad
fun loadInterstitial(context: Context) {
    InterstitialAd.load(
        context,
        context.getString(R.string.ad_id_interstitial),
        AdRequest.Builder().build(),
        object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
                Log.d("Admob", "onAdLoaded: Ad was loaded.")
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                mInterstitialAd = null
                Log.d("Admob", "onAdFailedToLoad: ${loadAdError.message}")
            }
        }
    )
}

fun showInterstitial(context: Context) {
    val activity = context.findActivity()

    if (mInterstitialAd != null) {
        mInterstitialAd?.show(activity!!)
    } else {
        Log.d("Admob", "showInterstitial: The interstitial ad wasn't ready yet.")
    }
}

//find the current activity from a composable
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}