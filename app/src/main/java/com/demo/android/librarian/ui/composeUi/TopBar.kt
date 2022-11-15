package com.demo.android.librarian.ui.composeUi

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.demo.android.librarian.R

@Composable
fun TopBar(
  modifier: Modifier = Modifier,
  title: String,
  actions: @Composable RowScope.() -> Unit = {},
  onBackPressed: (() -> Unit)? = null
) {
  val backButtonAction: (@Composable () -> Unit)? = if (onBackPressed != null) {
    @Composable { BackButton(onBackAction = { onBackPressed() }) }
  } else {
    null
  }

  TopAppBar(
    modifier = modifier,
    title = { Text(text = title) },
    navigationIcon = backButtonAction,
    actions = actions,
    backgroundColor = MaterialTheme.colors.primary,
    contentColor = MaterialTheme.colors.onSecondary)
}