package com.schibsted.spain.fullscreenkallery

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.test.InstrumentationRegistry
import com.schibsted.spain.barista.BaristaAssertions.*
import com.schibsted.spain.barista.BaristaClickActions.click
import com.schibsted.spain.barista.BaristaRule
import com.schibsted.spain.barista.BaristaViewPagerActions.swipeViewPagerForward
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FullscreenGalleryActivityShould {

  @get:Rule
  val activityRule: BaristaRule<FullscreenGalleryActivity> = BaristaRule.create(FullscreenGalleryActivity::class.java)

  @Before
  fun setUp() {
    activityRule.activityTestRule.allowFlakyAttemptsByDefault(1)
  }

  @Test
  fun create_a_5_element_gallery() {
    activityRule.launchActivity(givenAnEmpty5imageUrlsIntent())

    assertRecyclerViewItemCount(R.id.galleryViewPager, 5)
  }

  @Test
  fun pager_indicator_should_have_5_pages_for_5_image_urls() {
    activityRule.launchActivity(givenAnEmpty5imageUrlsIntent())

    assertDisplayed("1/5")
  }

  @Test
  fun be_able_to_paginate_as_many_times_as_elements() {
    activityRule.launchActivity(givenAnEmpty5imageUrlsIntent())

    for (i in 1..5) {
      swipeViewPagerForward(R.id.galleryViewPager)
    }

    assertDisplayed("5/5")
  }

  @Test
  fun finish_when_close_button_is_clicked() {
    activityRule.launchActivity(givenAnEmpty5imageUrlsIntent())

    click(R.id.galleryCloseIcon)

    assertThatActivityHasBeenClosed()
  }

  @Test
  fun finish_when_back_button_is_clicked() {
    activityRule.launchActivity(givenAnEmpty5imageUrlsIntent())

    assertThatBackButtonClosesTheApp()
  }

  private fun givenAnEmpty5imageUrlsIntent(): Intent? {
    val intent = Intent()

    val imageUrls = arrayListOf("", "", "", "", "")
    intent.putStringArrayListExtra(FullscreenGalleryActivity.EXTRA_LIST_ITEMS, imageUrls)

    return intent
  }

  private fun assertThatActivityHasBeenClosed() {
    val activityManager = InstrumentationRegistry.getTargetContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    var numActivities = 1

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      numActivities = activityManager.appTasks[0].taskInfo.numActivities
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      if (activityManager.appTasks[0].taskInfo.topActivity == null) {
        numActivities = 0
      }
    } else {
      numActivities = activityManager.getRunningTasks(2).size
    }

    assertEquals(0, numActivities)
  }
}
