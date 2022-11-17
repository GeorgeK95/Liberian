package com.demo.android.librarian.ui.composeUi

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.demo.android.librarian.R

@Composable
fun ActionButton(
  modifier: Modifier = Modifier,
  text: String,
  isEnabled: Boolean,
  enabledColor: Color = MaterialTheme.colors.primary,
  disabledTextColor: Color = Color.Gray,
  onClick: () -> Unit
) {
  val backgroundColor = if (isEnabled) enabledColor else Color.LightGray
  val contentColor = if (isEnabled) MaterialTheme.colors.onSecondary else disabledTextColor

  TextButton(
    shape = RoundedCornerShape(16.dp),
    enabled = isEnabled,
    colors = ButtonDefaults.textButtonColors(
      backgroundColor = backgroundColor,
      contentColor = contentColor,
      disabledContentColor = disabledTextColor
    ),
    modifier = modifier.padding(16.dp),
    content = { Text(text) },
    onClick = onClick)
}