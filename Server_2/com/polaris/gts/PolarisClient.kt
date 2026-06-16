package com.polaris.gts

import com.cobblemon.mod.common.api.storage.party.PartyPosition
import com.cobblemon.mod.common.api.storage.pc.PCPosition
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.gui.pc.PCGUI
import com.cobblemon.mod.common.client.storage.ClientPC
import com.cobblemon.mod.common.client.storage.ClientParty
import com.cobblemon.mod.common.net.messages.server.storage.pc.MovePCPokemonToPartyPacket
import com.cobblemon.mod.common.net.messages.server.storage.pc.UnlinkPlayerFromPCPacket
import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import java.util.ArrayList
import java.util.Locale
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.jvm.internal.SourceDebugExtension
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.class_124
import net.minecraft.class_1799
import net.minecraft.class_2561
import net.minecraft.class_2596
import net.minecraft.class_2868
import net.minecraft.class_310
import net.minecraft.class_634
import net.minecraft.class_7157
import net.minecraft.class_746

@Environment(EnvType.CLIENT)
@SourceDebugExtension(["SMAP\nPolarisClient.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PolarisClient.kt\ncom/polaris/gts/PolarisClient\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,752:1\n295#2,2:753\n1878#2,3:756\n1869#2,2:759\n1#3:755\n*S KotlinDebug\n*F\n+ 1 PolarisClient.kt\ncom/polaris/gts/PolarisClient\n*L\n354#1:753,2\n699#1:756,3\n693#1:759,2\n*E\n"])
public class PolarisClient : ClientModInitializer {
   private final val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor({ runnable: Runnable ->
      val var1: Thread = Thread(runnable, "PolarisScheduler")
      var1.setDaemon(true)
      var1
   })

   private final var pendingPcScreenTicks: Int = -1
   private final var pcSessionActive: Boolean
   private final var pendingPcMove: com.polaris.gts.PolarisClient.PendingPcMove?

   public open fun onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK.register({ client: class_310 ->
         while (`$pcGtsKey`.method_1436()) {
            `this$0`.requestPcGtsScreen(client)
         }

         while (`$gtsKey`.method_1436()) {
            if (client.field_1755 == null) {
               client.method_1507(PolarisScenarioScreen({ position: Int, price: Long, delayMs: Long ->
                  schedulePokemonListing$default(`this$0`, position, price, delayMs, null, 8, null)
                  Unit.INSTANCE
               }))
            }
         }

         `this$0`.tickPcIntegration(client)
      })
      ClientCommandRegistrationCallback.EVENT
         .register(
            { dispatcher: CommandDispatcher, var2: class_7157 ->
               dispatcher.register(
                  (ClientCommandManager.literal("polaris").then(ClientCommandManager.literal("help").executes({ it: CommandContext ->
                        `this$0`.printHelp()
                        1
                     })) as LiteralArgumentBuilder)
                     .then(
                        ((((ClientCommandManager.literal("gts")
                                       .then(
                                          ClientCommandManager.literal("list")
                                             .then(
                                                ClientCommandManager.argument("position", IntegerArgumentType.integer(1, 6) as ArgumentType)
                                                   .then(
                                                      (ClientCommandManager.argument("price", LongArgumentType.longArg(1L) as ArgumentType)
                                                            .executes(
                                                               { ctx: CommandContext ->
                                                                  schedulePokemonListing$default(
                                                                     `this$0`,
                                                                     IntegerArgumentType.getInteger(ctx, "position"),
                                                                     LongArgumentType.getLong(ctx, "price"),
                                                                     0L,
                                                                     null,
                                                                     8,
                                                                     null
                                                                  )
                                                                  1
                                                               }
                                                            ) as RequiredArgumentBuilder)
                                                         .then(
                                                            ClientCommandManager.argument("delay_ms", IntegerArgumentType.integer(0, 300000) as ArgumentType)
                                                               .executes(
                                                                  { ctx: CommandContext ->
                                                                     schedulePokemonListing$default(
                                                                        `this$0`,
                                                                        IntegerArgumentType.getInteger(ctx, "position"),
                                                                        LongArgumentType.getLong(ctx, "price"),
                                                                        (long)IntegerArgumentType.getInteger(ctx, "delay_ms"),
                                                                        null,
                                                                        8,
                                                                        null
                                                                     )
                                                                     1
                                                                  }
                                                               )
                                                         )
                                                   )
                                             )
                                       ) as LiteralArgumentBuilder)
                                    .then(
                                       ClientCommandManager.literal("item_drop_race")
                                          .then(
                                             ClientCommandManager.argument("position", IntegerArgumentType.integer(1, 9) as ArgumentType)
                                                .then(
                                                   ClientCommandManager.argument("price", LongArgumentType.longArg(1L) as ArgumentType)
                                                      .then(
                                                         (ClientCommandManager.argument("quantity", IntegerArgumentType.integer(1, 64) as ArgumentType)
                                                               .executes(
                                                                  { ctx: CommandContext ->
                                                                     `this$0`.runItemDropRace(
                                                                        IntegerArgumentType.getInteger(ctx, "position"),
                                                                        LongArgumentType.getLong(ctx, "price"),
                                                                        IntegerArgumentType.getInteger(ctx, "quantity"),
                                                                        0L,
                                                                        "add_first"
                                                                     )
                                                                     1
                                                                  }
                                                               ) as RequiredArgumentBuilder)
                                                            .then(
                                                               (ClientCommandManager.argument("gap_ms", IntegerArgumentType.integer(0, 1000) as ArgumentType)
                                                                     .executes(
                                                                        { ctx: CommandContext ->
                                                                           `this$0`.runItemDropRace(
                                                                              IntegerArgumentType.getInteger(ctx, "position"),
                                                                              LongArgumentType.getLong(ctx, "price"),
                                                                              IntegerArgumentType.getInteger(ctx, "quantity"),
                                                                              (long)IntegerArgumentType.getInteger(ctx, "gap_ms"),
                                                                              "add_first"
                                                                           )
                                                                           1
                                                                        }
                                                                     ) as RequiredArgumentBuilder)
                                                                  .then(
                                                                     ClientCommandManager.argument("order", StringArgumentType.word() as ArgumentType)
                                                                        .executes({ ctx: CommandContext ->
                                                                           val var10001: Int = IntegerArgumentType.getInteger(ctx, "position")
                                                                           val var10002: Long = LongArgumentType.getLong(ctx, "price")
                                                                           val var10003: Int = IntegerArgumentType.getInteger(ctx, "quantity")
                                                                           val var10004: Long = IntegerArgumentType.getInteger(ctx, "gap_ms")
                                                                           val var10005: java.lang.String = StringArgumentType.getString(ctx, "order")
                                                                           `this$0`.runItemDropRace(var10001, var10002, var10003, var10004, var10005)
                                                                           1
                                                                        })
                                                                  )
                                                            )
                                                      )
                                                )
                                          )
                                    ) as LiteralArgumentBuilder)
                                 .then(
                                    ClientCommandManager.literal("item_add_burst")
                                       .then(
                                          ClientCommandManager.argument("position", IntegerArgumentType.integer(1, 9) as ArgumentType)
                                             .then(
                                                ClientCommandManager.argument("price", LongArgumentType.longArg(1L) as ArgumentType)
                                                   .then(
                                                      ClientCommandManager.argument("quantity", IntegerArgumentType.integer(1, 64) as ArgumentType)
                                                         .then(
                                                            (ClientCommandManager.argument("attempts", IntegerArgumentType.integer(2, 200) as ArgumentType)
                                                                  .executes(
                                                                     { ctx: CommandContext ->
                                                                        `this$0`.runItemAddBurst(
                                                                           IntegerArgumentType.getInteger(ctx, "position"),
                                                                           LongArgumentType.getLong(ctx, "price"),
                                                                           IntegerArgumentType.getInteger(ctx, "quantity"),
                                                                           IntegerArgumentType.getInteger(ctx, "attempts"),
                                                                           0L
                                                                        )
                                                                        1
                                                                     }
                                                                  ) as RequiredArgumentBuilder)
                                                               .then(
                                                                  ClientCommandManager.argument("gap_ms", IntegerArgumentType.integer(0, 1000) as ArgumentType)
                                                                     .executes(
                                                                        { ctx: CommandContext ->
                                                                           `this$0`.runItemAddBurst(
                                                                              IntegerArgumentType.getInteger(ctx, "position"),
                                                                              LongArgumentType.getLong(ctx, "price"),
                                                                              IntegerArgumentType.getInteger(ctx, "quantity"),
                                                                              IntegerArgumentType.getInteger(ctx, "attempts"),
                                                                              (long)IntegerArgumentType.getInteger(ctx, "gap_ms")
                                                                           )
                                                                           1
                                                                        }
                                                                     )
                                                               )
                                                         )
                                                   )
                                             )
                                       )
                                 ) as LiteralArgumentBuilder)
                              .then(
                                 ClientCommandManager.literal("item_price_race")
                                    .then(
                                       ClientCommandManager.argument("position", IntegerArgumentType.integer(1, 9) as ArgumentType)
                                          .then(
                                             ClientCommandManager.argument("price_a", LongArgumentType.longArg(1L) as ArgumentType)
                                                .then(
                                                   ClientCommandManager.argument("price_b", LongArgumentType.longArg(1L) as ArgumentType)
                                                      .then(
                                                         ClientCommandManager.argument("quantity", IntegerArgumentType.integer(1, 64) as ArgumentType)
                                                            .then(
                                                               (ClientCommandManager.argument("cycles", IntegerArgumentType.integer(1, 100) as ArgumentType)
                                                                     .executes(
                                                                        { ctx: CommandContext ->
                                                                           `this$0`.runItemPriceRace(
                                                                              IntegerArgumentType.getInteger(ctx, "position"),
                                                                              LongArgumentType.getLong(ctx, "price_a"),
                                                                              LongArgumentType.getLong(ctx, "price_b"),
                                                                              IntegerArgumentType.getInteger(ctx, "quantity"),
                                                                              IntegerArgumentType.getInteger(ctx, "cycles"),
                                                                              0L
                                                                           )
                                                                           1
                                                                        }
                                                                     ) as RequiredArgumentBuilder)
                                                                  .then(
                                                                     ClientCommandManager.argument(
                                                                           "gap_ms", IntegerArgumentType.integer(0, 1000) as ArgumentType
                                                                        )
                                                                        .executes(
                                                                           { ctx: CommandContext ->
                                                                              `this$0`.runItemPriceRace(
                                                                                 IntegerArgumentType.getInteger(ctx, "position"),
                                                                                 LongArgumentType.getLong(ctx, "price_a"),
                                                                                 LongArgumentType.getLong(ctx, "price_b"),
                                                                                 IntegerArgumentType.getInteger(ctx, "quantity"),
                                                                                 IntegerArgumentType.getInteger(ctx, "cycles"),
                                                                                 (long)IntegerArgumentType.getInteger(ctx, "gap_ms")
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
                              ) as LiteralArgumentBuilder)
                           .then(
                              ClientCommandManager.literal("item_quantity_race")
                                 .then(
                                    ClientCommandManager.argument("position", IntegerArgumentType.integer(1, 9) as ArgumentType)
                                       .then(
                                          ClientCommandManager.argument("price", LongArgumentType.longArg(1L) as ArgumentType)
                                             .then(
                                                ClientCommandManager.argument("quantity_a", IntegerArgumentType.integer(1, 64) as ArgumentType)
                                                   .then(
                                                      ClientCommandManager.argument("quantity_b", IntegerArgumentType.integer(1, 64) as ArgumentType)
                                                         .then(
                                                            (ClientCommandManager.argument("cycles", IntegerArgumentType.integer(1, 100) as ArgumentType)
                                                                  .executes(
                                                                     { ctx: CommandContext ->
                                                                        `this$0`.runItemQuantityRace(
                                                                           IntegerArgumentType.getInteger(ctx, "position"),
                                                                           LongArgumentType.getLong(ctx, "price"),
                                                                           IntegerArgumentType.getInteger(ctx, "quantity_a"),
                                                                           IntegerArgumentType.getInteger(ctx, "quantity_b"),
                                                                           IntegerArgumentType.getInteger(ctx, "cycles"),
                                                                           0L
                                                                        )
                                                                        1
                                                                     }
                                                                  ) as RequiredArgumentBuilder)
                                                               .then(
                                                                  ClientCommandManager.argument("gap_ms", IntegerArgumentType.integer(0, 1000) as ArgumentType)
                                                                     .executes(
                                                                        { ctx: CommandContext ->
                                                                           `this$0`.runItemQuantityRace(
                                                                              IntegerArgumentType.getInteger(ctx, "position"),
                                                                              LongArgumentType.getLong(ctx, "price"),
                                                                              IntegerArgumentType.getInteger(ctx, "quantity_a"),
                                                                              IntegerArgumentType.getInteger(ctx, "quantity_b"),
                                                                              IntegerArgumentType.getInteger(ctx, "cycles"),
                                                                              (long)IntegerArgumentType.getInteger(ctx, "gap_ms")
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
                     ) as LiteralArgumentBuilder
               )
            }
         )
      }

   private fun requestPcGtsScreen(client: class_310) {
      if (this.pendingPcMove != null) {
         this.chat("Ja existe um fluxo PC -> party -> GTS agendado.", class_124.field_1061)
      } else if (client.field_1755 is PCGUI) {
         this.openPcGtsScreen(client, client.field_1755 as PCGUI)
      } else if (client.field_1755 !is PolarisPcGtsScreen) {
         if (client.field_1755 != null) {
            this.chat("Feche a tela atual ou abra /pc e pressione F8.", class_124.field_1061)
         } else if (client.method_1562() == null) {
            this.chat("Entre em um servidor antes de acessar o PC.", class_124.field_1061)
         } else {
            this.pendingPcScreenTicks = 100
            val var10000: class_634 = client.method_1562()
            if (var10000 != null) {
               var10000.method_45730("pc")
            }

            this.chat("Abrindo /pc e aguardando a sincronizacao do Cobblemon...", class_124.field_1075)
         }
      }
   }

   private fun tickPcIntegration(client: class_310) {
      if (client.field_1724 != null && client.method_1562() != null) {
         if (this.pendingPcScreenTicks >= 0) {
            val pending: PCGUI = client.field_1755 as? PCGUI
            if ((client.field_1755 as? PCGUI) != null) {
               this.pendingPcScreenTicks = -1
               this.openPcGtsScreen(client, pending)
            } else {
               this.pendingPcScreenTicks += -1
               if (this.pendingPcScreenTicks == 0) {
                  this.pendingPcScreenTicks = -1
                  this.chat("O /pc nao abriu a tempo. Confirme se o comando existe no servidor.", class_124.field_1061)
               }
            }
         }

         if (this.pendingPcMove != null) {
            val var11: PolarisClient.PendingPcMove = this.pendingPcMove
            if (this.pendingPcMove.moveSent) {
               val var13: ClientParty = CobblemonClient.INSTANCE.getStorage().getParty()
               val var7: java.util.Iterator = (RangesKt.until(0, 6) as java.lang.Iterable).iterator()

               var var10000: Any
               while (true) {
                  if (var7.hasNext()) {
                     val `element$iv`: Any = var7.next()
                     val var16: Pokemon = var13.get((`element$iv` as java.lang.Number).intValue())
                     if (!((if (var16 != null) var16.getUuid() else null) == var11.pokemonId)) {
                        continue
                     }

                     var10000 = `element$iv`
                     break
                  }

                  var10000 = null
                  break
               }

               val actualSlot: Int = var10000 as Int
               if (var10000 as Int != null) {
                  this.pendingPcMove = null
                  if (actualSlot != var11.requestedPartySlot) {
                     this.chat("O servidor moveu o Pokemon para a party ${actualSlot + 1}; o anuncio foi ajustado.", class_124.field_1054)
                  } else {
                     this.chat("Pokemon sincronizado na party ${actualSlot + 1}.", class_124.field_1060)
                  }

                  this.sendCommand("gts add pokemon ${actualSlot + 1} ${var11.price}")
                  this.unlinkPcSession()
               } else {
                  var11.ticksRemaining = var11.ticksRemaining + -1
                  if (var11.ticksRemaining <= 0) {
                     this.pendingPcMove = null
                     this.chat("O movimento PC -> party nao sincronizou; o anuncio foi cancelado.", class_124.field_1061)
                     this.unlinkPcSession()
                  }
               }
            }
         }
      } else {
         this.pendingPcScreenTicks = -1
         this.pendingPcMove = null
         this.pcSessionActive = false
      }
   }

   private fun openPcGtsScreen(client: class_310, pcScreen: PCGUI) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: Anonymous class does not have Class Kotlin metadata
      //   at org.vineflower.kotlin.KotlinWriter.writeClassDefinition(KotlinWriter.java:742)
      //   at org.vineflower.kotlin.KotlinWriter.writeClass(KotlinWriter.java:309)
      //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:178)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:770)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:736)
      //
      // Bytecode:
      // 00: aload 0
      // 01: bipush 1
      // 02: putfield com/polaris/gts/PolarisClient.pcSessionActive Z
      // 05: aload 1
      // 06: new com/polaris/gts/PolarisPcGtsScreen
      // 09: dup
      // 0a: aload 2
      // 0b: invokevirtual com/cobblemon/mod/common/client/gui/pc/PCGUI.getPc ()Lcom/cobblemon/mod/common/client/storage/ClientPC;
      // 0e: aload 2
      // 0f: invokevirtual com/cobblemon/mod/common/client/gui/pc/PCGUI.getParty ()Lcom/cobblemon/mod/common/client/storage/ClientParty;
      // 12: getstatic com/cobblemon/mod/common/client/CobblemonClient.INSTANCE Lcom/cobblemon/mod/common/client/CobblemonClient;
      // 15: invokevirtual com/cobblemon/mod/common/client/CobblemonClient.getLastPcBoxViewed ()I
      // 18: new com/polaris/gts/PolarisClient$openPcGtsScreen$1
      // 1b: dup
      // 1c: aload 0
      // 1d: invokespecial com/polaris/gts/PolarisClient$openPcGtsScreen$1.<init> (Ljava/lang/Object;)V
      // 20: checkcast kotlin/jvm/functions/Function6
      // 23: new com/polaris/gts/PolarisClient$openPcGtsScreen$2
      // 26: dup
      // 27: aload 0
      // 28: invokespecial com/polaris/gts/PolarisClient$openPcGtsScreen$2.<init> (Ljava/lang/Object;)V
      // 2b: checkcast kotlin/jvm/functions/Function0
      // 2e: invokespecial com/polaris/gts/PolarisPcGtsScreen.<init> (Lcom/cobblemon/mod/common/client/storage/ClientPC;Lcom/cobblemon/mod/common/client/storage/ClientParty;ILkotlin/jvm/functions/Function6;Lkotlin/jvm/functions/Function0;)V
      // 31: checkcast net/minecraft/class_437
      // 34: invokevirtual net/minecraft/class_310.method_1507 (Lnet/minecraft/class_437;)V
      // 37: return
   }

   private fun movePcPokemonAndSchedule(pc: ClientPC, position: PCPosition, pokemon: Pokemon, partySlot: Int, price: Long, delayMs: Long) {
      if (!this.pcSessionActive) {
         this.chat("A sessao do PC nao esta ativa. Abra novamente com F8.", class_124.field_1061)
      } else if (this.pendingPcMove != null) {
         this.chat("Ja existe um movimento PC -> party em andamento.", class_124.field_1061)
      } else {
         val var10000: Pokemon = pc.get(position)
         if (!((if (var10000 != null) var10000.getUuid() else null) == pokemon.getUuid())) {
            this.chat("O slot do PC mudou antes do envio. Abra o F8 novamente.", class_124.field_1061)
            this.unlinkPcSession()
         } else if (0 <= partySlot && partySlot < 6 && CobblemonClient.INSTANCE.getStorage().getParty().get(partySlot) == null) {
            val var10005: UUID = pokemon.getUuid()
            val var10006: java.lang.String = pokemon.getDisplayName(false).getString()
            this.pendingPcMove = PolarisClient.PendingPcMove(pc, position, var10005, var10006, partySlot, price, false, 0, 192, null)
            val delay: Long = RangesKt.coerceIn(delayMs, 0L, 300000L)
            this.chat("Agendado em $delayms: ${pokemon.getDisplayName(false).getString()} permanecera no PC ate o delay terminar.", class_124.field_1075)
            this.scheduler.schedule({ 
               class_310.method_1551().execute({ 
                  val var10001: UUID = `$pokemon`.getUuid()
                  `this$0`.executePendingPcMove(var10001)
               })
            }, delay, TimeUnit.MILLISECONDS)
         } else {
            this.chat("O slot ${partySlot + 1} da party nao esta mais livre.", class_124.field_1061)
            this.unlinkPcSession()
         }
      }
   }

   private fun executePendingPcMove(pokemonId: UUID) {
      val pending: PolarisClient.PendingPcMove = this.pendingPcMove
      if (this.pendingPcMove != null && this.pendingPcMove.pokemonId == pokemonId && !pending.moveSent) {
         if (!this.pcSessionActive) {
            this.pendingPcMove = null
            this.chat("A sessao do PC foi encerrada durante o delay; fluxo cancelado.", class_124.field_1061)
         } else {
            val var10000: Pokemon = pending.pc.get(pending.pcPosition)
            if (!((if (var10000 != null) var10000.getUuid() else null) == pending.pokemonId)) {
               this.pendingPcMove = null
               this.chat("O Pokemon nao esta mais no slot selecionado; fluxo cancelado.", class_124.field_1061)
               this.unlinkPcSession()
            } else {
               val party: ClientParty = CobblemonClient.INSTANCE.getStorage().getParty()
               val var4: Int = pending.requestedPartySlot
               if (0 <= var4 && var4 < 6 && party.get(pending.requestedPartySlot) == null) {
                  pending.moveSent = true
                  pending.ticksRemaining = 100
                  MovePCPokemonToPartyPacket(pending.pokemonId, pending.pcPosition, PartyPosition(pending.requestedPartySlot)).sendToServer()
                  this.chat("Delay concluido: movendo ${pending.pokemonName} para a party ${pending.requestedPartySlot + 1}.", class_124.field_1075)
               } else {
                  this.pendingPcMove = null
                  this.chat("O slot ${pending.requestedPartySlot + 1} da party foi ocupado durante o delay.", class_124.field_1061)
                  this.unlinkPcSession()
               }
            }
         }
      }
   }

   private fun unlinkPcSession() {
      if (this.pcSessionActive) {
         this.pcSessionActive = false
         val var1: PolarisClient = this

         try {
            var var5: PolarisClient = var1
            UnlinkPlayerFromPCPacket().sendToServer()
            var5 = (PolarisClient)Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
         } catch (var4: java.lang.Throwable) {
            val `$this$unlinkPcSession_u24lambda_u240`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var4))
         }
      }
   }

   private fun schedulePokemonListing(position: Int, price: Long, delayMs: Long, afterSend: (() -> Unit)? = null) {
      val command: java.lang.String = "gts add pokemon $position $price"
      val delay: Long = RangesKt.coerceIn(delayMs, 0L, 300000L)
      this.chat("Agendado: /$command em $delayms.", class_124.field_1075)
      this.scheduler.schedule({ 
         class_310.method_1551().execute({ 
            `this$0`.sendCommand(`$command`)
            if (`$afterSend` != null) {
               `$afterSend`()
            }
         })
      }, delay, TimeUnit.MILLISECONDS)
   }

   private fun runItemDropRace(position: Int, price: Long, quantity: Int, gapMs: Long, order: String) {
      val var10000: Locale = Locale.ROOT
      val var18: java.lang.String = order.toLowerCase(var10000)
      if (!(var18 == "add_first") && !(var18 == "drop_first")) {
         this.chat("Ordem invalida. Use add_first ou drop_first.", class_124.field_1061)
      } else {
         val var17: class_310 = class_310.method_1551()
         val player: class_746 = var17.field_1724
         val handler: class_634 = var17.method_1562()
         if (player != null && handler != null) {
            val hotbarSlot: Int = position - 1
            val stack: class_1799 = player.method_31548().method_5438(position - 1)
            if (!stack.method_7960() && stack.method_7947() >= quantity) {
               val command: java.lang.String = "gts add item $position $price $quantity"
               val gap: Long = RangesKt.coerceIn(gapMs, 0L, 1000L)
               player.method_31548().field_7545 = hotbarSlot
               handler.method_52787(class_2868(hotbarSlot) as class_2596)
               this.chat("Race: /$command + drop de $quantity item(ns), gap=$gapms, order=$var18.", class_124.field_1075)
               var17.execute(
                  { 
                     // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
                     // java.lang.IllegalStateException: Anonymous class does not have Class Kotlin metadata
                     //   at org.vineflower.kotlin.KotlinWriter.writeClassDefinition(KotlinWriter.java:742)
                     //   at org.vineflower.kotlin.KotlinWriter.writeClass(KotlinWriter.java:309)
                     //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:178)
                     //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:770)
                     //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:736)
                  }
               )
            } else {
               this.chat("A posicao $position nao possui $quantity item(ns).", class_124.field_1061)
            }
         } else {
            this.chat("Entre em um servidor antes de iniciar o teste.", class_124.field_1061)
         }
      }
   }

   private fun runItemAddBurst(position: Int, price: Long, quantity: Int, attempts: Int, gapMs: Long) {
      val var10000: PolarisClient.HotbarItem = this.inspectHotbarItem(position, quantity)
      if (var10000 != null) {
         val requested: Long = (long)quantity * attempts
         val command: java.lang.String = "gts add item $position $price $quantity"
         this.chat("Mesmo slot: $attempts x /gts add item $position $price $quantity; stack=${var10000.count}; solicitado=$requested.", class_124.field_1075)
         if (requested > var10000.count) {
            this.chat("A soma solicitada excede o stack. O servidor deve aceitar no maximo o estoque real.", class_124.field_1065)
         }

         val var12: ArrayList = ArrayList(attempts)

         repeat(attempts) { var13 ->
            var12.add(command)
         }

         this.dispatchItemCommands("item_add_burst", var12, gapMs)
      }
   }

   private fun runItemPriceRace(position: Int, priceA: Long, priceB: Long, quantity: Int, cycles: Int, gapMs: Long) {
      if (priceA == priceB) {
         this.chat("Use dois precos diferentes para item_price_race.", class_124.field_1061)
      } else {
         val var10000: PolarisClient.HotbarItem = this.inspectHotbarItem(position, quantity)
         if (var10000 != null) {
            val var13: java.util.List = CollectionsKt.createListBuilder(cycles * 2)
            val `$this$runItemPriceRace_u24lambda_u240`: java.util.List = var13

            repeat(cycles) { var16 ->
               `$this$runItemPriceRace_u24lambda_u240`.add("gts add item $position $priceA $quantity")
               `$this$runItemPriceRace_u24lambda_u240`.add("gts add item $position $priceB $quantity")
            }

            val commands: java.util.List = CollectionsKt.build(var13)
            this.chat(
               "Preco concorrente: ${commands.size()} anuncios, $priceA vs $priceB; stack=${var10000.count}; solicitado=${(long)quantity
                  * (long)commands.size()}.",
               class_124.field_1075
            )
            this.chat("Seguro: o mesmo item nao pode gerar anuncios ativos com precos conflitantes.", class_124.field_1065)
            this.dispatchItemCommands("item_price_race", commands, gapMs)
         }
      }
   }

   private fun runItemQuantityRace(position: Int, price: Long, quantityA: Int, quantityB: Int, cycles: Int, gapMs: Long) {
      if (quantityA == quantityB) {
         this.chat("Use duas quantidades diferentes para item_quantity_race.", class_124.field_1061)
      } else {
         val var10000: PolarisClient.HotbarItem = this.inspectHotbarItem(position, Math.min(quantityA, quantityB))
         if (var10000 != null) {
            val var12: java.util.List = CollectionsKt.createListBuilder(cycles * 2)
            val `$this$runItemQuantityRace_u24lambda_u240`: java.util.List = var12

            repeat(cycles) { var15 ->
               `$this$runItemQuantityRace_u24lambda_u240`.add("gts add item $position $price $quantityA")
               `$this$runItemQuantityRace_u24lambda_u240`.add("gts add item $position $price $quantityB")
            }

            val commands: java.util.List = CollectionsKt.build(var12)
            this.chat(
               "Quantidade concorrente: ${commands.size()} anuncios, qty=$quantityA vs $quantityB; stack=${var10000.count}; solicitado=${(long)cycles
                  * (long)(quantityA + quantityB)}.",
               class_124.field_1075
            )
            this.chat("Seguro: reservas concorrentes nunca podem superar a quantidade existente.", class_124.field_1065)
            this.dispatchItemCommands("item_quantity_race", commands, gapMs)
         }
      }
   }

   private fun inspectHotbarItem(position: Int, minimumQuantity: Int): com.polaris.gts.PolarisClient.HotbarItem? {
      val player: class_746 = class_310.method_1551().field_1724
      if (player == null) {
         this.chat("Entre em um servidor antes de iniciar o teste.", class_124.field_1061)
         return null
      } else {
         val stack: class_1799 = player.method_31548().method_5438(position - 1)
         if (!stack.method_7960() && stack.method_7947() >= minimumQuantity) {
            val var10002: java.lang.String = stack.method_7964().getString()
            return PolarisClient.HotbarItem(var10002, stack.method_7947())
         } else {
            this.chat("A posicao $position nao possui $minimumQuantity item(ns).", class_124.field_1061)
            return null
         }
      }
   }

   private fun dispatchItemCommands(label: String, commands: List<String>, gapMs: Long) {
      val client: class_310 = class_310.method_1551()
      if (client.method_1562() == null) {
         this.chat("Sem conexao com o servidor.", class_124.field_1061)
      } else {
         val gap: Long = RangesKt.coerceIn(gapMs, 0L, 1000L)
         this.chat("$label: ${commands.size()} comandos, gap=$gapms.", class_124.field_1080)
         if (gap == 0L) {
            client.execute({ 
               val handler: class_634 = `$client`.method_1562()
               if (handler == null) {
                  `this$0`.chat("$`$label` cancelado: conexao encerrada.", class_124.field_1061)
               } else {
                  val `$this$forEach$iv`: java.lang.Iterable = `$commands`
                  val var6: class_634 = handler

                  for (`element$iv` in `$this$forEach$iv`) {
                     var6.method_45730(`element$iv` as java.lang.String)
                  }

                  `this$0`.chat("$`$label` disparado no mesmo tick. Confira anuncios, inventario e logs.", class_124.field_1061)
               }
            })
         } else {
            val `$this$forEachIndexed$iv`: java.lang.Iterable = commands
            var `index$iv`: Int = 0

            for (`item$iv` in `$this$forEachIndexed$iv`) {
               val var13: Int = `index$iv`++
               if (var13 < 0) {
                  CollectionsKt.throwIndexOverflow()
               }

               this.scheduler.schedule({ 
                  `$client`.execute({ 
                     val var10000: class_634 = `$client`.method_1562()
                     if (var10000 != null) {
                        var10000.method_45730(`$command`)
                     }
                  })
               }, (long)var13 * gap, TimeUnit.MILLISECONDS)
            }

            this.scheduler.schedule({ 
               `$client`.execute({ 
                  `this$0`.chat("$`$label` concluido. Confira anuncios, inventario e logs.", class_124.field_1061)
               })
            }, (long)commands.size() * gap + 100L, TimeUnit.MILLISECONDS)
         }
      }
   }

   private fun sendCommand(command: String) {
      val handler: class_634 = class_310.method_1551().method_1562()
      if (handler == null) {
         this.chat("Nao foi possivel enviar /$command: sem conexao.", class_124.field_1061)
      } else {
         handler.method_45730(command)
         this.chat("/$command enviado.", class_124.field_1060)
      }
   }

   private fun printHelp() {
      this.chat("F8: abre /pc, move um Pokemon para a party e agenda o anuncio.", class_124.field_1065)
      this.chat("F9: abre a tela de anuncio de Pokemon com delay.", class_124.field_1065)
      this.chat("/polaris gts list <posicao 1-6> <preco> [delay_ms]", class_124.field_1075)
      this.chat("/polaris gts item_drop_race <posicao 1-9> <preco> <quantidade> [gap_ms] [add_first|drop_first]", class_124.field_1075)
      this.chat("/polaris gts item_add_burst <posicao> <preco> <quantidade> <tentativas> [gap_ms]", class_124.field_1075)
      this.chat("/polaris gts item_price_race <posicao> <preco_a> <preco_b> <quantidade> <ciclos> [gap_ms]", class_124.field_1075)
      this.chat("/polaris gts item_quantity_race <posicao> <preco> <qty_a> <qty_b> <ciclos> [gap_ms]", class_124.field_1075)
   }

   private fun chat(message: String, formatting: class_124) {
      val var10000: class_746 = class_310.method_1551().field_1724
      if (var10000 != null) {
         var10000.method_7353(class_2561.method_43470("[POLARIS] $message").method_27692(formatting) as class_2561, false)
      }
   }

   @JvmStatic
   fun `runItemDropRace$announce`(`this$0`: PolarisClient, command: java.lang.String) {
      `this$0`.sendCommand(command)
   }

   @JvmStatic
   fun `runItemDropRace$drop`(`$quantity`: Int, player: class_746, `this$0`: PolarisClient) {
      var dropped: Int = 0

      while (dropped < `$quantity` && player.method_7290(false)) {
         dropped++
      }

      `this$0`.chat("Drop enviado: $dropped/$`$quantity`.", if (dropped == `$quantity`) class_124.field_1054 else class_124.field_1061)
   }

   @JvmStatic
   fun `runItemDropRace$second`(gap: Long, `this$0`: PolarisClient, client: class_310, action: () -> Unit) {
      if (gap == 0L) {
         action()
      } else {
         `this$0`.scheduler.schedule({ 
            `$client`.execute({ 
               var var2: Any
               try {
                  var2 = Result.constructor_impl/* $VF was: constructor-impl */(`$action`())
               } catch (var6: java.lang.Throwable) {
                  var2 = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var6))
               }

               val var10000: java.lang.Throwable = Result.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(var2)
               if (var10000 != null) {
                  `this$0`.chat("Segunda acao falhou: ${var10000.getMessage()}", class_124.field_1061)
               }
            })
         }, gap, TimeUnit.MILLISECONDS)
      }
   }

   private data class HotbarItem(name: String, count: Int) {
      public final val name: String
      public final val count: Int

      init {
         this.name = name
         this.count = count
      }

      public operator fun component1(): String {
         return this.name
      }

      public operator fun component2(): Int {
         return this.count
      }

      public fun copy(name: String = this.name, count: Int = this.count): com.polaris.gts.PolarisClient.HotbarItem {
         return PolarisClient.HotbarItem(name, count)
      }

      public override fun toString(): String {
         return "HotbarItem(name=${this.name}, count=${this.count})"
      }

      public override fun hashCode(): Int {
         return this.name.hashCode() * 31 + Integer.hashCode(this.count)
      }

      public override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is PolarisClient.HotbarItem
               && this.name == (other as PolarisClient.HotbarItem).name
               && this.count == (other as PolarisClient.HotbarItem).count
            }
      }
   }

   private data class PendingPcMove(pc: ClientPC,
      pcPosition: PCPosition,
      pokemonId: UUID,
      pokemonName: String,
      requestedPartySlot: Int,
      price: Long,
      moveSent: Boolean = false,
      ticksRemaining: Int = 100
   ) {
      public final val pc: ClientPC
      public final val pcPosition: PCPosition
      public final val pokemonId: UUID
      public final val pokemonName: String
      public final val requestedPartySlot: Int
      public final val price: Long
      public final var moveSent: Boolean
      public final var ticksRemaining: Int

      init {
         this.pc = pc
         this.pcPosition = pcPosition
         this.pokemonId = pokemonId
         this.pokemonName = pokemonName
         this.requestedPartySlot = requestedPartySlot
         this.price = price
         this.moveSent = moveSent
         this.ticksRemaining = ticksRemaining
      }

      public operator fun component1(): ClientPC {
         return this.pc
      }

      public operator fun component2(): PCPosition {
         return this.pcPosition
      }

      public operator fun component3(): UUID {
         return this.pokemonId
      }

      public operator fun component4(): String {
         return this.pokemonName
      }

      public operator fun component5(): Int {
         return this.requestedPartySlot
      }

      public operator fun component6(): Long {
         return this.price
      }

      public operator fun component7(): Boolean {
         return this.moveSent
      }

      public operator fun component8(): Int {
         return this.ticksRemaining
      }

      public fun copy(
         pc: ClientPC = this.pc,
         pcPosition: PCPosition = this.pcPosition,
         pokemonId: UUID = this.pokemonId,
         pokemonName: String = this.pokemonName,
         requestedPartySlot: Int = this.requestedPartySlot,
         price: Long = this.price,
         moveSent: Boolean = this.moveSent,
         ticksRemaining: Int = this.ticksRemaining
      ): com.polaris.gts.PolarisClient.PendingPcMove {
         return PolarisClient.PendingPcMove(pc, pcPosition, pokemonId, pokemonName, requestedPartySlot, price, moveSent, ticksRemaining)
      }

      public override fun toString(): String {
         return "PendingPcMove(pc=${this.pc}, pcPosition=${this.pcPosition}, pokemonId=${this.pokemonId}, pokemonName=${this.pokemonName}, requestedPartySlot=${this.requestedPartySlot}, price=${this.price}, moveSent=${this.moveSent}, ticksRemaining=${this.ticksRemaining})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (
                                    (
                                             ((this.pc.hashCode() * 31 + this.pcPosition.hashCode()) * 31 + this.pokemonId.hashCode()) * 31
                                                + this.pokemonName.hashCode()
                                          )
                                          * 31
                                       + Integer.hashCode(this.requestedPartySlot)
                                 )
                                 * 31
                              + java.lang.Long.hashCode(this.price)
                        )
                        * 31
                     + java.lang.Boolean.hashCode(this.moveSent)
               )
               * 31
            + Integer.hashCode(this.ticksRemaining)
         }

      public override operator fun equals(other: Any?): Boolean {
         label64@
         if (this === other) {
            return true
         } else {
            return other is PolarisClient.PendingPcMove
               && this.pc == (other as PolarisClient.PendingPcMove).pc
               && this.pcPosition == (other as PolarisClient.PendingPcMove).pcPosition
               && this.pokemonId == (other as PolarisClient.PendingPcMove).pokemonId
               && this.pokemonName == (other as PolarisClient.PendingPcMove).pokemonName
               && this.requestedPartySlot == (other as PolarisClient.PendingPcMove).requestedPartySlot
               && this.price == (other as PolarisClient.PendingPcMove).price
               && this.moveSent == (other as PolarisClient.PendingPcMove).moveSent
               && this.ticksRemaining == (other as PolarisClient.PendingPcMove).ticksRemaining
            }
      }
   }
}
