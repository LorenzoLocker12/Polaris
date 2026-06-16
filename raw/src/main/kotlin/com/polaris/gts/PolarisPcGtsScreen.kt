package com.polaris.gts

import com.cobblemon.mod.common.api.storage.pc.PCPosition
import com.cobblemon.mod.common.client.storage.ClientPC
import com.cobblemon.mod.common.client.storage.ClientParty
import com.cobblemon.mod.common.pokemon.Pokemon
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text
import java.util.Locale

@Environment(EnvType.CLIENT)
class PolarisPcGtsScreen(
    private val pc: ClientPC,
    private val party: ClientParty,
    initialBox: Int,
    private val submit: (
        pc: ClientPC,
        position: PCPosition,
        pokemon: Pokemon,
        partySlot: Int,
        price: Long,
        delayMs: Long,
    ) -> Unit,
    private val cancelSession: () -> Unit,
) : Screen(Text.literal("Polaris PC GTS")) {
    private data class Rect(val x: Int, val y: Int, val width: Int, val height: Int) {
        fun contains(mouseX: Int, mouseY: Int): Boolean {
            return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height
        }
    }

    private var selectedBox = initialBox.coerceIn(0, (pc.boxes.size - 1).coerceAtLeast(0))
    private var selectedPcSlot: Int? = firstPokemonSlot(selectedBox)
    private var selectedPartySlot = firstEmptyPartySlot()
    private var priceField: TextFieldWidget? = null
    private var delayField: TextFieldWidget? = null
    private var submitted = false

    override fun init() {
        val panel = panelRect()
        priceField = TextFieldWidget(
            textRenderer,
            panel.x + 22,
            panel.y + 232,
            170,
            20,
            Text.literal("Preco"),
        ).apply {
            setMaxLength(20)
            text = "36000"
        }
        addDrawableChild(priceField)

        delayField = TextFieldWidget(
            textRenderer,
            panel.x + 210,
            panel.y + 232,
            105,
            20,
            Text.literal("Delay"),
        ).apply {
            setMaxLength(6)
            text = "10"
        }
        addDrawableChild(delayField)
    }

    override fun shouldPause(): Boolean = false

    override fun close() {
        if (!submitted) {
            cancelSession()
        }
        super.close()
    }

    override fun renderBackground(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        context.fill(0, 0, width, height, 0xD0000000.toInt())
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context, mouseX, mouseY, delta)

        val panel = panelRect()
        context.fill(panel.x, panel.y, panel.x + panel.width, panel.y + panel.height, 0xF0080D1D.toInt())
        drawBorder(context, panel, 0xFF41EBFF.toInt())
        context.drawCenteredTextWithShadow(
            textRenderer,
            Text.literal("POLARIS - PC PARA GTS"),
            width / 2,
            panel.y + 10,
            0xFFFFE288.toInt(),
        )
        context.drawCenteredTextWithShadow(
            textRenderer,
            Text.literal("Selecione o Pokemon, um slot livre da party, preco e delay"),
            width / 2,
            panel.y + 24,
            0xFF969FC4.toInt(),
        )

        drawBoxHeader(context, panel, mouseX, mouseY)
        drawPcSlots(context, panel, mouseX, mouseY)
        drawPartySlots(context, panel, mouseX, mouseY)

        context.drawTextWithShadow(textRenderer, "Preco", panel.x + 22, panel.y + 220, 0xFFEFF1FF.toInt())
        context.drawTextWithShadow(textRenderer, "Delay (segundos)", panel.x + 210, panel.y + 220, 0xFFEFF1FF.toInt())

        val selectedPokemon = selectedPokemon()
        val price = parsePrice(priceField?.text.orEmpty())
        val delaySeconds = delayField?.text?.toLongOrNull() ?: -1L
        val partyAvailable = selectedPartySlot in 0..5 && party.get(selectedPartySlot) == null
        val valid = selectedPokemon != null &&
            selectedPokemon.tradeable &&
            partyAvailable &&
            price > 0L &&
            delaySeconds in 0L..300L

        val preview = when {
            selectedPokemon == null -> "Selecione um Pokemon do PC"
            !selectedPokemon.tradeable -> "${pokemonName(selectedPokemon)} nao pode ser trocado"
            selectedPartySlot !in 0..5 -> "A party esta cheia"
            else -> "PC ${selectedBox + 1}/${(selectedPcSlot ?: 0) + 1} -> Party ${selectedPartySlot + 1} -> /gts add pokemon ${selectedPartySlot + 1} $price"
        }
        context.drawCenteredTextWithShadow(
            textRenderer,
            Text.literal(preview),
            width / 2,
            panel.y + 259,
            if (valid) 0xFFFFE288.toInt() else 0xFFFF7088.toInt(),
        )

        val cancel = cancelRect(panel)
        val start = startRect(panel)
        drawButton(context, cancel, "CANCELAR", 0xFFFF5270.toInt(), cancel.contains(mouseX, mouseY))
        drawButton(
            context,
            start,
            if (valid) "AGENDAR FLUXO" else "VERIFIQUE OS CAMPOS",
            if (valid) 0xFF5CFF91.toInt() else 0xFF545C7E.toInt(),
            valid && start.contains(mouseX, mouseY),
        )

        context.drawCenteredTextWithShadow(
            textRenderer,
            Text.literal("Durante o delay, o Pokemon permanece no PC."),
            width / 2,
            panel.y + 296,
            0xFF969FC4.toInt(),
        )

        super.render(context, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button)
        }

        val x = mouseX.toInt()
        val y = mouseY.toInt()
        val panel = panelRect()

        if (previousBoxRect(panel).contains(x, y)) {
            changeBox(-1)
            return true
        }
        if (nextBoxRect(panel).contains(x, y)) {
            changeBox(1)
            return true
        }

        for (slot in 0 until 30) {
            if (pcSlotRect(panel, slot).contains(x, y) && pokemonAt(selectedBox, slot) != null) {
                selectedPcSlot = slot
                return true
            }
        }

        for (slot in 0 until 6) {
            if (partySlotRect(panel, slot).contains(x, y) && party.get(slot) == null) {
                selectedPartySlot = slot
                return true
            }
        }

        if (cancelRect(panel).contains(x, y)) {
            close()
            return true
        }

        if (startRect(panel).contains(x, y)) {
            val slot = selectedPcSlot
            val pokemon = selectedPokemon()
            val price = parsePrice(priceField?.text.orEmpty())
            val delaySeconds = delayField?.text?.toLongOrNull() ?: -1L
            if (
                slot != null &&
                pokemon != null &&
                pokemon.tradeable &&
                selectedPartySlot in 0..5 &&
                party.get(selectedPartySlot) == null &&
                price > 0L &&
                delaySeconds in 0L..300L
            ) {
                submitted = true
                submit(
                    pc,
                    PCPosition(selectedBox, slot),
                    pokemon,
                    selectedPartySlot,
                    price,
                    delaySeconds * 1000L,
                )
                super.close()
            }
            return true
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    private fun drawBoxHeader(context: DrawContext, panel: Rect, mouseX: Int, mouseY: Int) {
        val previous = previousBoxRect(panel)
        val next = nextBoxRect(panel)
        drawButton(context, previous, "<", 0xFF41EBFF.toInt(), previous.contains(mouseX, mouseY))
        drawButton(context, next, ">", 0xFF41EBFF.toInt(), next.contains(mouseX, mouseY))

        val boxName = pc.boxes.getOrNull(selectedBox)?.name?.string?.takeIf { it.isNotBlank() }
            ?: "Caixa ${selectedBox + 1}"
        context.drawCenteredTextWithShadow(
            textRenderer,
            Text.literal("$boxName (${selectedBox + 1}/${pc.boxes.size})"),
            width / 2,
            panel.y + 45,
            0xFFEFF1FF.toInt(),
        )
    }

    private fun drawPcSlots(context: DrawContext, panel: Rect, mouseX: Int, mouseY: Int) {
        for (slot in 0 until 30) {
            val rect = pcSlotRect(panel, slot)
            val pokemon = pokemonAt(selectedBox, slot)
            val selected = selectedPcSlot == slot
            val hovered = rect.contains(mouseX, mouseY)
            val fill = when {
                selected -> 0xFF163F2A.toInt()
                hovered && pokemon != null -> 0xFF182440.toInt()
                else -> 0xFF0B1428.toInt()
            }
            val border = when {
                selected -> 0xFF5CFF91.toInt()
                pokemon != null -> 0xFF1F5F84.toInt()
                else -> 0xFF28304A.toInt()
            }
            context.fill(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, fill)
            drawBorder(context, rect, border)

            if (pokemon == null) {
                context.drawCenteredTextWithShadow(
                    textRenderer,
                    Text.literal("${slot + 1} -"),
                    rect.x + rect.width / 2,
                    rect.y + 7,
                    0xFF59617B.toInt(),
                )
            } else {
                val shiny = if (pokemon.shiny) "*" else ""
                context.drawTextWithShadow(
                    textRenderer,
                    "${slot + 1}. ${shorten(pokemonName(pokemon), 10)}$shiny",
                    rect.x + 3,
                    rect.y + 3,
                    if (pokemon.tradeable) 0xFFEFF1FF.toInt() else 0xFFFF7088.toInt(),
                )
                context.drawTextWithShadow(
                    textRenderer,
                    "Lv.${pokemon.level}",
                    rect.x + 3,
                    rect.y + 13,
                    0xFF969FC4.toInt(),
                )
            }
        }
    }

    private fun drawPartySlots(context: DrawContext, panel: Rect, mouseX: Int, mouseY: Int) {
        context.drawTextWithShadow(textRenderer, "Destino na party", panel.x + 22, panel.y + 183, 0xFFEFF1FF.toInt())
        for (slot in 0 until 6) {
            val rect = partySlotRect(panel, slot)
            val pokemon = party.get(slot)
            val selected = selectedPartySlot == slot
            val hovered = rect.contains(mouseX, mouseY)
            val fill = when {
                selected -> 0xFF163F2A.toInt()
                hovered && pokemon == null -> 0xFF182440.toInt()
                else -> 0xFF0B1428.toInt()
            }
            val border = when {
                selected -> 0xFF5CFF91.toInt()
                pokemon == null -> 0xFF1F5F84.toInt()
                else -> 0xFF545C7E.toInt()
            }
            context.fill(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, fill)
            drawBorder(context, rect, border)
            val label = if (pokemon == null) {
                "${slot + 1}: Livre"
            } else {
                "${slot + 1}: ${shorten(pokemonName(pokemon), 7)}"
            }
            context.drawCenteredTextWithShadow(
                textRenderer,
                Text.literal(label),
                rect.x + rect.width / 2,
                rect.y + 7,
                if (pokemon == null) 0xFFEFF1FF.toInt() else 0xFF777F9D.toInt(),
            )
        }
    }

    private fun changeBox(delta: Int) {
        if (pc.boxes.isEmpty()) return
        selectedBox = (selectedBox + delta + pc.boxes.size) % pc.boxes.size
        selectedPcSlot = firstPokemonSlot(selectedBox)
    }

    private fun selectedPokemon(): Pokemon? {
        val slot = selectedPcSlot ?: return null
        return pokemonAt(selectedBox, slot)
    }

    private fun firstPokemonSlot(box: Int): Int? {
        return (0 until 30).firstOrNull { pokemonAt(box, it) != null }
    }

    private fun firstEmptyPartySlot(): Int {
        return (0 until 6).firstOrNull { party.get(it) == null } ?: -1
    }

    private fun pokemonAt(box: Int, slot: Int): Pokemon? {
        return pc.boxes.getOrNull(box)?.slots?.getOrNull(slot)
    }

    private fun pokemonName(pokemon: Pokemon): String = pokemon.getDisplayName(false).string

    private fun shorten(value: String, maxLength: Int): String {
        return if (value.length <= maxLength) value else value.take(maxLength - 1) + "."
    }

    private fun panelRect(): Rect = Rect(width / 2 - 225, height / 2 - 165, 450, 330)

    private fun previousBoxRect(panel: Rect): Rect = Rect(panel.x + 22, panel.y + 39, 24, 20)

    private fun nextBoxRect(panel: Rect): Rect = Rect(panel.x + panel.width - 46, panel.y + 39, 24, 20)

    private fun pcSlotRect(panel: Rect, slot: Int): Rect {
        val column = slot % 6
        val row = slot / 6
        return Rect(panel.x + 22 + column * 68, panel.y + 66 + row * 22, 64, 20)
    }

    private fun partySlotRect(panel: Rect, slot: Int): Rect {
        return Rect(panel.x + 22 + slot * 68, panel.y + 196, 64, 20)
    }

    private fun cancelRect(panel: Rect): Rect = Rect(panel.x + 72, panel.y + 276, 128, 24)

    private fun startRect(panel: Rect): Rect = Rect(panel.x + 218, panel.y + 276, 160, 24)

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
            rect.y + 7,
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
