package com.polaris.gts

import com.cobblemon.mod.common.api.storage.party.PartyPosition
import com.cobblemon.mod.common.api.storage.pc.PCPosition
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.gui.pc.PCGUI
import com.cobblemon.mod.common.client.storage.ClientPC
import com.cobblemon.mod.common.net.messages.server.storage.pc.MovePCPokemonToPartyPacket
import com.cobblemon.mod.common.net.messages.server.storage.pc.UnlinkPlayerFromPCPacket
import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.lwjgl.glfw.GLFW
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Environment(EnvType.CLIENT)
class PolarisClient : ClientModInitializer {
    private val scheduler = Executors.newSingleThreadScheduledExecutor { runnable ->
        Thread(runnable, "PolarisScheduler").apply { isDaemon = true }
    }
    private var pendingPcScreenTicks = -1
    private var pcSessionActive = false
    private var pendingPcMove: PendingPcMove? = null

    private data class PendingPcMove(
        val pc: ClientPC,
        val pcPosition: PCPosition,
        val pokemonId: java.util.UUID,
        val pokemonName: String,
        val requestedPartySlot: Int,
        val price: Long,
        var moveSent: Boolean = false,
        var ticksRemaining: Int = 100,
    )

    override fun onInitializeClient() {
        val pcGtsKey = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.polaris.pc_gts",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,
                "category.polaris",
            )
        )
        val gtsKey = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.polaris.gts_list",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                "category.polaris",
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            while (pcGtsKey.wasPressed()) {
                requestPcGtsScreen(client)
            }
            while (gtsKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(
                        PolarisScenarioScreen { position, price, delayMs ->
                            schedulePokemonListing(position, price, delayMs)
                        }
                    )
                }
            }
            tickPcIntegration(client)
        }

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                literal("polaris")
                    .then(literal("help").executes {
                        printHelp()
                        1
                    })
                    .then(
                        literal("gts")
                            .then(
                                literal("list")
                                    .then(
                                        argument("position", IntegerArgumentType.integer(1, 6))
                                            .then(
                                                argument("price", LongArgumentType.longArg(1))
                                                    .executes { ctx ->
                                                        schedulePokemonListing(
                                                            IntegerArgumentType.getInteger(ctx, "position"),
                                                            LongArgumentType.getLong(ctx, "price"),
                                                            0L,
                                                        )
                                                        1
                                                    }
                                                    .then(
                                                        argument("delay_ms", IntegerArgumentType.integer(0, 300_000))
                                                            .executes { ctx ->
                                                                schedulePokemonListing(
                                                                    IntegerArgumentType.getInteger(ctx, "position"),
                                                                    LongArgumentType.getLong(ctx, "price"),
                                                                    IntegerArgumentType.getInteger(ctx, "delay_ms").toLong(),
                                                                )
                                                                1
                                                            }
                                                    )
                                            )
                                    )
                            )
                            .then(
                                literal("item_drop_race")
                                    .then(
                                        argument("position", IntegerArgumentType.integer(1, 9))
                                            .then(
                                                argument("price", LongArgumentType.longArg(1))
                                                    .then(
                                                        argument("quantity", IntegerArgumentType.integer(1, 64))
                                                            .executes { ctx ->
                                                                runItemDropRace(
                                                                    IntegerArgumentType.getInteger(ctx, "position"),
                                                                    LongArgumentType.getLong(ctx, "price"),
                                                                    IntegerArgumentType.getInteger(ctx, "quantity"),
                                                                    0L,
                                                                    "add_first",
                                                                )
                                                                1
                                                            }
                                                            .then(
                                                                argument("gap_ms", IntegerArgumentType.integer(0, 1000))
                                                                    .executes { ctx ->
                                                                        runItemDropRace(
                                                                            IntegerArgumentType.getInteger(ctx, "position"),
                                                                            LongArgumentType.getLong(ctx, "price"),
                                                                            IntegerArgumentType.getInteger(ctx, "quantity"),
                                                                            IntegerArgumentType.getInteger(ctx, "gap_ms").toLong(),
                                                                            "add_first",
                                                                        )
                                                                        1
                                                                    }
                                                                    .then(
                                                                        argument("order", StringArgumentType.word())
                                                                            .executes { ctx ->
                                                                                runItemDropRace(
                                                                                    IntegerArgumentType.getInteger(ctx, "position"),
                                                                                    LongArgumentType.getLong(ctx, "price"),
                                                                                    IntegerArgumentType.getInteger(ctx, "quantity"),
                                                                                    IntegerArgumentType.getInteger(ctx, "gap_ms").toLong(),
                                                                                    StringArgumentType.getString(ctx, "order"),
                                                                                )
                                                                                1
                                                                            }
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                            .then(
                                literal("item_add_burst")
                                    .then(
                                        argument("position", IntegerArgumentType.integer(1, 9))
                                            .then(
                                                argument("price", LongArgumentType.longArg(1))
                                                    .then(
                                                        argument("quantity", IntegerArgumentType.integer(1, 64))
                                                            .then(
                                                                argument("attempts", IntegerArgumentType.integer(2, 200))
                                                                    .executes { ctx ->
                                                                        runItemAddBurst(
                                                                            IntegerArgumentType.getInteger(ctx, "position"),
                                                                            LongArgumentType.getLong(ctx, "price"),
                                                                            IntegerArgumentType.getInteger(ctx, "quantity"),
                                                                            IntegerArgumentType.getInteger(ctx, "attempts"),
                                                                            0L,
                                                                        )
                                                                        1
                                                                    }
                                                                    .then(
                                                                        argument("gap_ms", IntegerArgumentType.integer(0, 1000))
                                                                            .executes { ctx ->
                                                                                runItemAddBurst(
                                                                                    IntegerArgumentType.getInteger(ctx, "position"),
                                                                                    LongArgumentType.getLong(ctx, "price"),
                                                                                    IntegerArgumentType.getInteger(ctx, "quantity"),
                                                                                    IntegerArgumentType.getInteger(ctx, "attempts"),
                                                                                    IntegerArgumentType.getInteger(ctx, "gap_ms").toLong(),
                                                                                )
                                                                                1
                                                                            }
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                            .then(
                                literal("item_price_race")
                                    .then(
                                        argument("position", IntegerArgumentType.integer(1, 9))
                                            .then(
                                                argument("price_a", LongArgumentType.longArg(1))
                                                    .then(
                                                        argument("price_b", LongArgumentType.longArg(1))
                                                            .then(
                                                                argument("quantity", IntegerArgumentType.integer(1, 64))
                                                                    .then(
                                                                        argument("cycles", IntegerArgumentType.integer(1, 100))
                                                                            .executes { ctx ->
                                                                                runItemPriceRace(
                                                                                    IntegerArgumentType.getInteger(ctx, "position"),
                                                                                    LongArgumentType.getLong(ctx, "price_a"),
                                                                                    LongArgumentType.getLong(ctx, "price_b"),
                                                                                    IntegerArgumentType.getInteger(ctx, "quantity"),
                                                                                    IntegerArgumentType.getInteger(ctx, "cycles"),
                                                                                    0L,
                                                                                )
                                                                                1
                                                                            }
                                                                            .then(
                                                                                argument("gap_ms", IntegerArgumentType.integer(0, 1000))
                                                                                    .executes { ctx ->
                                                                                        runItemPriceRace(
                                                                                            IntegerArgumentType.getInteger(ctx, "position"),
                                                                                            LongArgumentType.getLong(ctx, "price_a"),
                                                                                            LongArgumentType.getLong(ctx, "price_b"),
                                                                                            IntegerArgumentType.getInteger(ctx, "quantity"),
                                                                                            IntegerArgumentType.getInteger(ctx, "cycles"),
                                                                                            IntegerArgumentType.getInteger(ctx, "gap_ms").toLong(),
                                                                                        )
                                                                                        1
                                                                                    }
                                                                            )
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                            .then(
                                literal("item_quantity_race")
                                    .then(
                                        argument("position", IntegerArgumentType.integer(1, 9))
                                            .then(
                                                argument("price", LongArgumentType.longArg(1))
                                                    .then(
                                                        argument("quantity_a", IntegerArgumentType.integer(1, 64))
                                                            .then(
                                                                argument("quantity_b", IntegerArgumentType.integer(1, 64))
                                                                    .then(
                                                                        argument("cycles", IntegerArgumentType.integer(1, 100))
                                                                            .executes { ctx ->
                                                                                runItemQuantityRace(
                                                                                    IntegerArgumentType.getInteger(ctx, "position"),
                                                                                    LongArgumentType.getLong(ctx, "price"),
                                                                                    IntegerArgumentType.getInteger(ctx, "quantity_a"),
                                                                                    IntegerArgumentType.getInteger(ctx, "quantity_b"),
                                                                                    IntegerArgumentType.getInteger(ctx, "cycles"),
                                                                                    0L,
                                                                                )
                                                                                1
                                                                            }
                                                                            .then(
                                                                                argument("gap_ms", IntegerArgumentType.integer(0, 1000))
                                                                                    .executes { ctx ->
                                                                                        runItemQuantityRace(
                                                                                            IntegerArgumentType.getInteger(ctx, "position"),
                                                                                            LongArgumentType.getLong(ctx, "price"),
                                                                                            IntegerArgumentType.getInteger(ctx, "quantity_a"),
                                                                                            IntegerArgumentType.getInteger(ctx, "quantity_b"),
                                                                                            IntegerArgumentType.getInteger(ctx, "cycles"),
                                                                                            IntegerArgumentType.getInteger(ctx, "gap_ms").toLong(),
                                                                                        )
                                                                                        1
                                                                                    }
                                                                            )
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
            )
        }
    }

    private fun requestPcGtsScreen(client: MinecraftClient) {
        if (pendingPcMove != null) {
            chat("Ja existe um fluxo PC -> party -> GTS agendado.", Formatting.RED)
            return
        }
        val currentScreen = client.currentScreen
        if (currentScreen is PCGUI) {
            openPcGtsScreen(client, currentScreen)
            return
        }
        if (currentScreen is PolarisPcGtsScreen) {
            return
        }
        if (currentScreen != null) {
            chat("Feche a tela atual ou abra /pc e pressione F8.", Formatting.RED)
            return
        }
        if (client.networkHandler == null) {
            chat("Entre em um servidor antes de acessar o PC.", Formatting.RED)
            return
        }

        pendingPcScreenTicks = 100
        client.networkHandler?.sendChatCommand("pc")
        chat("Abrindo /pc e aguardando a sincronizacao do Cobblemon...", Formatting.AQUA)
    }

    private fun tickPcIntegration(client: MinecraftClient) {
        if (client.player == null || client.networkHandler == null) {
            pendingPcScreenTicks = -1
            pendingPcMove = null
            pcSessionActive = false
            return
        }

        if (pendingPcScreenTicks >= 0) {
            val pcScreen = client.currentScreen as? PCGUI
            if (pcScreen != null) {
                pendingPcScreenTicks = -1
                openPcGtsScreen(client, pcScreen)
            } else {
                pendingPcScreenTicks--
                if (pendingPcScreenTicks == 0) {
                    pendingPcScreenTicks = -1
                    chat("O /pc nao abriu a tempo. Confirme se o comando existe no servidor.", Formatting.RED)
                }
            }
        }

        val pending = pendingPcMove ?: return
        if (!pending.moveSent) return

        val party = CobblemonClient.storage.party
        val actualSlot = (0 until 6).firstOrNull { party.get(it)?.uuid == pending.pokemonId }
        if (actualSlot != null) {
            pendingPcMove = null
            if (actualSlot != pending.requestedPartySlot) {
                chat(
                    "O servidor moveu o Pokemon para a party ${actualSlot + 1}; o anuncio foi ajustado.",
                    Formatting.YELLOW,
                )
            } else {
                chat("Pokemon sincronizado na party ${actualSlot + 1}.", Formatting.GREEN)
            }
            sendCommand("gts add pokemon ${actualSlot + 1} ${pending.price}")
            unlinkPcSession()
            return
        }

        pending.ticksRemaining--
        if (pending.ticksRemaining <= 0) {
            pendingPcMove = null
            chat("O movimento PC -> party nao sincronizou; o anuncio foi cancelado.", Formatting.RED)
            unlinkPcSession()
        }
    }

    private fun openPcGtsScreen(client: MinecraftClient, pcScreen: PCGUI) {
        pcSessionActive = true
        client.setScreen(
            PolarisPcGtsScreen(
                pc = pcScreen.pc,
                party = pcScreen.party,
                initialBox = CobblemonClient.lastPcBoxViewed,
                submit = ::movePcPokemonAndSchedule,
                cancelSession = ::unlinkPcSession,
            )
        )
    }

    private fun movePcPokemonAndSchedule(
        pc: ClientPC,
        position: PCPosition,
        pokemon: Pokemon,
        partySlot: Int,
        price: Long,
        delayMs: Long,
    ) {
        if (!pcSessionActive) {
            chat("A sessao do PC nao esta ativa. Abra novamente com F8.", Formatting.RED)
            return
        }
        if (pendingPcMove != null) {
            chat("Ja existe um movimento PC -> party em andamento.", Formatting.RED)
            return
        }
        if (pc.get(position)?.uuid != pokemon.uuid) {
            chat("O slot do PC mudou antes do envio. Abra o F8 novamente.", Formatting.RED)
            unlinkPcSession()
            return
        }

        val party = CobblemonClient.storage.party
        if (partySlot !in 0..5 || party.get(partySlot) != null) {
            chat("O slot ${partySlot + 1} da party nao esta mais livre.", Formatting.RED)
            unlinkPcSession()
            return
        }

        pendingPcMove = PendingPcMove(
            pc = pc,
            pcPosition = position,
            pokemonId = pokemon.uuid,
            pokemonName = pokemon.getDisplayName(false).string,
            requestedPartySlot = partySlot,
            price = price,
        )
        val delay = delayMs.coerceIn(0L, 300_000L)
        chat(
            "Agendado em ${delay}ms: ${pokemon.getDisplayName(false).string} permanecera no PC ate o delay terminar.",
            Formatting.AQUA,
        )
        scheduler.schedule({
            MinecraftClient.getInstance().execute {
                executePendingPcMove(pokemon.uuid)
            }
        }, delay, TimeUnit.MILLISECONDS)
    }

    private fun executePendingPcMove(pokemonId: java.util.UUID) {
        val pending = pendingPcMove
        if (pending == null || pending.pokemonId != pokemonId || pending.moveSent) return

        if (!pcSessionActive) {
            pendingPcMove = null
            chat("A sessao do PC foi encerrada durante o delay; fluxo cancelado.", Formatting.RED)
            return
        }
        if (pending.pc.get(pending.pcPosition)?.uuid != pending.pokemonId) {
            pendingPcMove = null
            chat("O Pokemon nao esta mais no slot selecionado; fluxo cancelado.", Formatting.RED)
            unlinkPcSession()
            return
        }

        val party = CobblemonClient.storage.party
        if (pending.requestedPartySlot !in 0..5 || party.get(pending.requestedPartySlot) != null) {
            pendingPcMove = null
            chat("O slot ${pending.requestedPartySlot + 1} da party foi ocupado durante o delay.", Formatting.RED)
            unlinkPcSession()
            return
        }

        pending.moveSent = true
        pending.ticksRemaining = 100
        MovePCPokemonToPartyPacket(
            pending.pokemonId,
            pending.pcPosition,
            PartyPosition(pending.requestedPartySlot),
        ).sendToServer()
        chat(
            "Delay concluido: movendo ${pending.pokemonName} para a party ${pending.requestedPartySlot + 1}.",
            Formatting.AQUA,
        )
    }

    private fun unlinkPcSession() {
        if (!pcSessionActive) return
        pcSessionActive = false
        runCatching {
            UnlinkPlayerFromPCPacket().sendToServer()
        }
    }

    private fun schedulePokemonListing(
        position: Int,
        price: Long,
        delayMs: Long,
        afterSend: (() -> Unit)? = null,
    ) {
        val command = "gts add pokemon $position $price"
        val delay = delayMs.coerceIn(0L, 300_000L)
        chat("Agendado: /$command em ${delay}ms.", Formatting.AQUA)
        scheduler.schedule({
            MinecraftClient.getInstance().execute {
                sendCommand(command)
                afterSend?.invoke()
            }
        }, delay, TimeUnit.MILLISECONDS)
    }

    private fun runItemDropRace(
        position: Int,
        price: Long,
        quantity: Int,
        gapMs: Long,
        order: String,
    ) {
        val normalizedOrder = order.lowercase(Locale.ROOT)
        if (normalizedOrder != "add_first" && normalizedOrder != "drop_first") {
            chat("Ordem invalida. Use add_first ou drop_first.", Formatting.RED)
            return
        }

        val client = MinecraftClient.getInstance()
        val player = client.player
        val handler = client.networkHandler
        if (player == null || handler == null) {
            chat("Entre em um servidor antes de iniciar o teste.", Formatting.RED)
            return
        }

        val hotbarSlot = position - 1
        val stack = player.inventory.getStack(hotbarSlot)
        if (stack.isEmpty || stack.count < quantity) {
            chat("A posicao $position nao possui $quantity item(ns).", Formatting.RED)
            return
        }

        val command = "gts add item $position $price $quantity"
        val gap = gapMs.coerceIn(0L, 1000L)
        player.inventory.selectedSlot = hotbarSlot
        handler.sendPacket(UpdateSelectedSlotC2SPacket(hotbarSlot))

        chat(
            "Race: /$command + drop de $quantity item(ns), gap=${gap}ms, order=$normalizedOrder.",
            Formatting.AQUA,
        )

        fun announce() {
            sendCommand(command)
        }

        fun drop() {
            var dropped = 0
            while (dropped < quantity && player.dropSelectedItem(false)) {
                dropped++
            }
            chat("Drop enviado: $dropped/$quantity.", if (dropped == quantity) Formatting.YELLOW else Formatting.RED)
        }

        fun second(action: () -> Unit) {
            if (gap == 0L) {
                action()
                return
            }
            scheduler.schedule({
                client.execute {
                    runCatching(action).onFailure {
                        chat("Segunda acao falhou: ${it.message}", Formatting.RED)
                    }
                }
            }, gap, TimeUnit.MILLISECONDS)
        }

        client.execute {
            runCatching {
                if (normalizedOrder == "drop_first") {
                    drop()
                    second(::announce)
                } else {
                    announce()
                    second(::drop)
                }
            }.onFailure {
                chat("Race falhou: ${it.message}", Formatting.RED)
            }
        }
    }

    private fun runItemAddBurst(
        position: Int,
        price: Long,
        quantity: Int,
        attempts: Int,
        gapMs: Long,
    ) {
        val item = inspectHotbarItem(position, quantity) ?: return
        val requested = quantity.toLong() * attempts
        val command = "gts add item $position $price $quantity"
        chat(
            "Mesmo slot: $attempts x /$command; stack=${item.count}; solicitado=$requested.",
            Formatting.AQUA,
        )
        if (requested > item.count) {
            chat("A soma solicitada excede o stack. O servidor deve aceitar no maximo o estoque real.", Formatting.GOLD)
        }
        dispatchItemCommands(
            label = "item_add_burst",
            commands = List(attempts) { command },
            gapMs = gapMs,
        )
    }

    private fun runItemPriceRace(
        position: Int,
        priceA: Long,
        priceB: Long,
        quantity: Int,
        cycles: Int,
        gapMs: Long,
    ) {
        if (priceA == priceB) {
            chat("Use dois precos diferentes para item_price_race.", Formatting.RED)
            return
        }
        val item = inspectHotbarItem(position, quantity) ?: return
        val commands = buildList(cycles * 2) {
            repeat(cycles) {
                add("gts add item $position $priceA $quantity")
                add("gts add item $position $priceB $quantity")
            }
        }
        val requested = quantity.toLong() * commands.size
        chat(
            "Preco concorrente: ${commands.size} anuncios, $priceA vs $priceB; stack=${item.count}; solicitado=$requested.",
            Formatting.AQUA,
        )
        chat("Seguro: o mesmo item nao pode gerar anuncios ativos com precos conflitantes.", Formatting.GOLD)
        dispatchItemCommands("item_price_race", commands, gapMs)
    }

    private fun runItemQuantityRace(
        position: Int,
        price: Long,
        quantityA: Int,
        quantityB: Int,
        cycles: Int,
        gapMs: Long,
    ) {
        if (quantityA == quantityB) {
            chat("Use duas quantidades diferentes para item_quantity_race.", Formatting.RED)
            return
        }
        val item = inspectHotbarItem(position, minOf(quantityA, quantityB)) ?: return
        val commands = buildList(cycles * 2) {
            repeat(cycles) {
                add("gts add item $position $price $quantityA")
                add("gts add item $position $price $quantityB")
            }
        }
        val requested = cycles.toLong() * (quantityA + quantityB)
        chat(
            "Quantidade concorrente: ${commands.size} anuncios, qty=$quantityA vs $quantityB; stack=${item.count}; solicitado=$requested.",
            Formatting.AQUA,
        )
        chat("Seguro: reservas concorrentes nunca podem superar a quantidade existente.", Formatting.GOLD)
        dispatchItemCommands("item_quantity_race", commands, gapMs)
    }

    private data class HotbarItem(val name: String, val count: Int)

    private fun inspectHotbarItem(position: Int, minimumQuantity: Int): HotbarItem? {
        val player = MinecraftClient.getInstance().player
        if (player == null) {
            chat("Entre em um servidor antes de iniciar o teste.", Formatting.RED)
            return null
        }
        val stack = player.inventory.getStack(position - 1)
        if (stack.isEmpty || stack.count < minimumQuantity) {
            chat("A posicao $position nao possui $minimumQuantity item(ns).", Formatting.RED)
            return null
        }
        return HotbarItem(stack.name.string, stack.count)
    }

    private fun dispatchItemCommands(label: String, commands: List<String>, gapMs: Long) {
        val client = MinecraftClient.getInstance()
        if (client.networkHandler == null) {
            chat("Sem conexao com o servidor.", Formatting.RED)
            return
        }
        val gap = gapMs.coerceIn(0L, 1000L)
        chat("$label: ${commands.size} comandos, gap=${gap}ms.", Formatting.GRAY)

        if (gap == 0L) {
            client.execute {
                val handler = client.networkHandler
                if (handler == null) {
                    chat("$label cancelado: conexao encerrada.", Formatting.RED)
                    return@execute
                }
                commands.forEach(handler::sendChatCommand)
                chat("$label disparado no mesmo tick. Confira anuncios, inventario e logs.", Formatting.RED)
            }
            return
        }

        commands.forEachIndexed { index, command ->
            scheduler.schedule({
                client.execute {
                    client.networkHandler?.sendChatCommand(command)
                }
            }, index * gap, TimeUnit.MILLISECONDS)
        }
        scheduler.schedule({
            client.execute {
                chat("$label concluido. Confira anuncios, inventario e logs.", Formatting.RED)
            }
        }, commands.size * gap + 100L, TimeUnit.MILLISECONDS)
    }

    private fun sendCommand(command: String) {
        val handler = MinecraftClient.getInstance().networkHandler
        if (handler == null) {
            chat("Nao foi possivel enviar /$command: sem conexao.", Formatting.RED)
            return
        }
        handler.sendChatCommand(command)
        chat("/$command enviado.", Formatting.GREEN)
    }

    private fun printHelp() {
        chat("F8: abre /pc, move um Pokemon para a party e agenda o anuncio.", Formatting.GOLD)
        chat("F9: abre a tela de anuncio de Pokemon com delay.", Formatting.GOLD)
        chat("/polaris gts list <posicao 1-6> <preco> [delay_ms]", Formatting.AQUA)
        chat(
            "/polaris gts item_drop_race <posicao 1-9> <preco> <quantidade> [gap_ms] [add_first|drop_first]",
            Formatting.AQUA,
        )
        chat(
            "/polaris gts item_add_burst <posicao> <preco> <quantidade> <tentativas> [gap_ms]",
            Formatting.AQUA,
        )
        chat(
            "/polaris gts item_price_race <posicao> <preco_a> <preco_b> <quantidade> <ciclos> [gap_ms]",
            Formatting.AQUA,
        )
        chat(
            "/polaris gts item_quantity_race <posicao> <preco> <qty_a> <qty_b> <ciclos> [gap_ms]",
            Formatting.AQUA,
        )
    }

    private fun chat(message: String, formatting: Formatting) {
        MinecraftClient.getInstance().player?.sendMessage(
            Text.literal("[POLARIS] $message").formatted(formatting),
            false,
        )
    }
}
