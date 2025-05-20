package com.intelli.zhiyuan.ui.components
// TriangleShape.kt
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline

// 右上角直角三角形
val TriangleShape = GenericShape { size: Size, _ ->
    moveTo(size.width, 0f)
    lineTo(size.width, size.height)
    lineTo(0f, 0f)
    close()
}
