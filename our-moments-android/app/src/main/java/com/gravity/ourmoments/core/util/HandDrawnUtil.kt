package com.gravity.ourmoments.core.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import kotlin.random.Random

object HandDrawnUtil {

    /**
     * 生成手绘风格的路径
     * @param size 大小
     * @param roughness 粗糙度
     * @param strokeWidth 线条宽度
     * @return 手绘路径
     */
    fun generateHandDrawnPath(size: Size, roughness: Float, strokeWidth: Float): Path {
        val path = Path()

        // 添加一些随机性来模拟手绘效果
        val irregularity = 0.15f

        // 起始点
        val startX = Random.nextFloat() * size.width * irregularity
        val startY = Random.nextFloat() * size.height * irregularity
        path.moveTo(startX, startY)

        // 绘制不规则矩形
        // 右上角
        val topRightX = size.width - Random.nextFloat() * size.width * irregularity
        val topRightY = Random.nextFloat() * size.height * irregularity
        path.lineTo(topRightX, topRightY)

        // 右下角
        val bottomRightX = size.width - Random.nextFloat() * size.width * irregularity
        val bottomRightY = size.height - Random.nextFloat() * size.height * irregularity
        path.lineTo(bottomRightX, bottomRightY)

        // 左下角
        val bottomLeftX = Random.nextFloat() * size.width * irregularity
        val bottomLeftY = size.height - Random.nextFloat() * size.height * irregularity
        path.lineTo(bottomLeftX, bottomLeftY)

        // 回到起始点附近
        path.lineTo(startX + Random.nextFloat() * strokeWidth, startY + Random.nextFloat() * strokeWidth)
        path.close()

        return path
    }

    /**
     * 生成随机旋转角度
     * @return 旋转角度（-3度到3度）
     */
    fun generateRandomRotation(): Float {
        return (-3.0..3.0).random().toFloat()
    }

    /**
     * 获取随机胶带变体
     * @return 胶带变体
     */
    fun getRandomTapeVariant(): Int {
        return (0..4).random()
    }

    /**
     * 获取随机胶带位置
     * @return 胶带位置
     */
    fun getRandomTapePosition(): Int {
        return (0..3).random()
    }
}