package com.demo.android.librarian.ui.reviews

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.demo.android.librarian.ui.addReview.AddBookReviewActivity

class AddBookReviewContract : ActivityResultContract<Int, Boolean>() {

  override fun createIntent(context: Context, input: Int): Intent {
    return AddBookReviewActivity.getIntent(context)
  }

  override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
    return resultCode == Activity.RESULT_OK
  }
}