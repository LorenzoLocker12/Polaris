package com.polaris.gts

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.Locale

@Environment(EnvType.CLIENT)
class PolarisScenarioScreen(
    private val scheduleListing: (position: Int, price: Long, delayMs: Long) -> Unit,
) : Screen(Text.literal("Polaris GTS")) {
    private data class Rect(val x: Int, val y: Int, val width: Int, val height: Int) {
        fun contains(mouseX: Int, mouseY: Int): Boolean {
            return mouseX in x..(x + width) && mouseY in y..(y + height)
        }
    }

    private var selectedPosition = 1
    private var priceField: TextFieldWidget? = null
    private var delayField: TextFieldWidget? = null

    override fun init() {
        val centerX = width / 2
        priceField = TextFieldWidget(
            textRenderer,
            centerX - 90,
            height / 2 - 20,
            180,
            20,
            Text.literal("Preco"),
        ).apply {
            setMaxLength(20)
            text = "36000"
        }
        addDrawableChild(priceField)

        delayField = TextFieldWidget(
            textRenderer,
            centerX - 90,
            height / 2 + 22,
            180,
            20,
            Text.literal("Delay"),
        ).apply {
            setMaxLength(6)
            text = "10"
        }
        addDrawableChild(delayField)
    }

    override fun shouldPause(): Boolean = false

    override fun renderBackground(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        context.fill(0, 0, width, height, 0xD0000000.toInt())
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context, mouseX, mouseY, delta)

        val panel = panelRect()
        context.fill(panel.x, panel.y, panel.x + panel.width, panel.y + panel.height, 0xF0080D1D.toInt())
        drawBorder(context, panel, 0xFF41EBFF.toInt())
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("POLARIS - ANUNCIO GTS"), width / 2, panel.y + 14, 0xFFFFE288.toInt())
        context.drawCenteredTextWithShadow(
            textRenderer,
            Text.literal("Cliente standalone baseado em comandos de chat"),
            width / 2,
            panel.y + 30,
            0xFF969FC4.toInt(),
        )

        context.drawTextWithShadow(textRenderer, "Posicao do Pokemon (1 = 1, 2 = 2...)", panel.x + 28, panel.y + 55, 0xFFEFF1FF.toInt())
        for (position in 1..6) {
            val rect = positionRect(position)
            val selected = position == selectedPosition
            context.fill(
                rect.x,
                rect.y,
                rect.x + rect.width,
                rect.y + rect.height,
                if (selected) 0xFF163F2A.toInt() else 0xFF0B1428.toInt(),
            )
            drawBorder(context, rect, if (selected) 0xFF5CFF91.toInt() else 0xFF1F5F84.toInt())
            context.drawCenteredTextWithShadow(
                textRenderer,
                Text.literal(position.toString()),
                rect.x + rect.width / 2,
                rect.y + 7,
                if (selected) 0xFF5CFF91.toInt() else 0xFFEFF1FF.toInt(),
            )
        }

        context.drawTextWithShadow(textRenderer, "Preco", width / 2 - 90, height / 2 - 34, 0xFFEFF1FF.toInt())
        context.drawTextWithShadow(textRenderer, "Delay em segundos", width / 2 - 90, height / 2 + 8, 0xFFEFF1FF.toInt())

        val price = parsePrice(priceField?.text.orEmpty())
        val delaySeconds = delayField?.text?.toLongOrNull() ?: -1L
        val command = if (price > 0L) {
            "/gts add pokemon $selectedPosition $price"
        } else {
            "Preco invalido"
        }
        context.drawCenteredTextWithShadow(
            textRenderer,
            Text.literal(command),
            width / 2,
            height / 2 + 54,
            if (price > 0L) 0xFFFFE288.toInt() else 0xFFFF5270.toInt(),
        )

        val cancel = cancelRect()
        val start = startRect()
        drawButton(context, cancel, "CANCELAR", 0xFFFF5270.toInt(), cancel.contains(mouseX, mouseY))
        val valid = price > 0L && delaySeconds in 0L..300L
        drawButton(
            context,
            start,
            if (valid) "AGENDAR" else "VERIFIQUE OS CAMPOS",
            if (valid) 0xFF5CFF91.toInt() else 0xFF545C7E.toInt(),
            valid && start.contains(mouseX, mouseY),
        )

        super.render(context, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            val x = mouseX.toInt()
            val y = mouseY.toInt()
            for (position in 1..6) {
                if (positionRect(position).contains(x, y)) {
                    selectedPosition = position
                    return true
                }
            }
            if (cancelRect().contains(x, y)) {
                close()
                return true
            }
            if (startRect().contains(x, y)) {
                val price = parsePrice(priceField?.text.orEmpty())
                val delaySeconds = delayField?.text?.toLongOrNull() ?: -1L
                if (price > 0L && delaySeconds in 0L..300L) {
                    scheduleListing(selectedPosition, price, delaySeconds * 1000L)
                    close()
                } else {
                    client?.player?.sendMessage(
                        Text.literal("[POLARIS] Preco ou delay invalido.").formatted(Formatting.RED),
                        false,
                    )
                }
                return true
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    private fun panelRect(): Rect = Rect(width / 2 - 150, height / 2 - 130, 300, 260)

    private fun positionRect(position: Int): Rect {
        val width = 34
        val gap = 8
        val total = width * 6 + gap * 5
        val startX = this.width / 2 - total / 2
        return Rect(startX + (position - 1) * (width + gap), height / 2 - 70, width, 24)
    }

    private fun cancelRect(): Rect = Rect(width / 2 - 126, height / 2 + 84, 112, 24)

    private fun startRect(): Rect = Rect(width / 2 + 14, height / 2 + 84, 112, 24)

    private fun drawButton(context: DrawContext, rect: Rect, label: String, color: Int, hover: Boolean) {
        context.fill(
            rect.x,
            rect.y,
            rect.x + rect.width,
            rect.y + rect.height,
            if (hover) 0xFF182440.toInt() else 0xFF0B1020.toInt(),
        )
        drawBorder(context, rect, color)
        context.drawCenteredTextWithShadow(
            textRenderer,
            Text.literal(label),
            rect.x + rect.width / 2,
            rect.y + 8,
            color,
        )
    }

    private fun drawBorder(context: DrawContext, rect: Rect, color: Int) {
        context.fill(rect.x, rect.y, rect.x + rect.width, rect.y + 1, color)
        context.fill(rect.x, rect.y + rect.height - 1, rect.x + rect.width, rect.y + rect.height, color)
        context.fill(rect.x, rect.y, rect.x + 1, rect.y + rect.height, color)
        context.fill(rect.x + rect.width - 1, rect.y, rect.x + rect.width, rect.y + rect.height, color)
    }

    private fun parsePrice(raw: String): Long {
        val text = raw.trim().replace("_", "").uppercase(Locale.ROOT)
        if (text.isEmpty()) return 0L
        val multiplier = when {
            text.endsWith("K") -> 1_000.0
            text.endsWith("M") -> 1_000_000.0
            text.endsWith("B") -> 1_000_000_000.0
            else -> 1.0
        }
        val number = if (multiplier == 1.0) text else text.dropLast(1)
        return ((number.toDoubleOrNull() ?: 0.0) * multiplier).toLong().coerceAtLeast(0L)
    }
}
