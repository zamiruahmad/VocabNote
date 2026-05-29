package com.example.ui.components
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.material3.LocalContentColor

@Composable
fun SidebarOutlinedIcon(
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Canvas(
        modifier = modifier
            .size(24.dp)
            .padding(2.dp)
            .semantics {
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                    this.role = Role.Image
                }
            }
    ) {
        // Outer rounded rect
        drawRoundRect(
            color = tint,
            topLeft = Offset(0f, 0f),
            size = Size(20.dp.toPx(), 20.dp.toPx()),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = Stroke(width = 2.dp.toPx())
        )
        // 3 holes (using background color since we can't easily punch holes in Canvas without Compose Path operations, 
        // OR we can just draw them in transparent/surface color)
        // Actually, let's just make the background of dots transparent by using BlendMode.Clear 
        // Wait, BlendMode.Clear requires a layer.
        // Easiest is to draw the path manually for the sidebar pill to punch holes.
        val path = Path().apply {
            fillType = PathFillType.EvenOdd
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    left = 2.dp.toPx(), 
                    top = 2.dp.toPx(), 
                    right = (2 + 6).dp.toPx(), 
                    bottom = (2 + 16).dp.toPx(),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
            )
            // 3 dots
            val r = 1.dp.toPx()
            val cx = 5.dp.toPx()
            val cy1 = 5.5.dp.toPx()
            val cy2 = 10.dp.toPx()
            val cy3 = 14.5.dp.toPx()
            addOval(androidx.compose.ui.geometry.Rect(cx - r, cy1 - r, cx + r, cy1 + r))
            addOval(androidx.compose.ui.geometry.Rect(cx - r, cy2 - r, cx + r, cy2 + r))
            addOval(androidx.compose.ui.geometry.Rect(cx - r, cy3 - r, cx + r, cy3 + r))
        }
        drawPath(path, color = tint, style = Fill)
    }
}

