package com.polaris.gts

import com.mojang.authlib.GameProfile
import com.tlean07.gts.client.GtsAuctionHouseScreen
import com.tlean07.gts.client.GtsClientData
import com.tlean07.gts.client.GtsMyAuctionsScreen
import com.tlean07.gts.common.PokemonSummary
import com.tlean07.gts.common.payloads.ListPokemonPayload
import com.tlean07.gts.common.payloads.WithdrawListingPayload
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap
import java.util.HashSet
import java.util.Locale
import java.util.Random
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.enums.EnumEntries
import kotlin.internal.ProgressionUtilKt
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.IntRef
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.class_1109
import net.minecraft.class_1113
import net.minecraft.class_124
import net.minecraft.class_2561
import net.minecraft.class_2960
import net.minecraft.class_310
import net.minecraft.class_332
import net.minecraft.class_3414
import net.minecraft.class_3417
import net.minecraft.class_342
import net.minecraft.class_364
import net.minecraft.class_437
import net.minecraft.class_4587
import net.minecraft.class_634
import net.minecraft.class_746
import net.minecraft.class_8710

@Environment(EnvType.CLIENT)
@SourceDebugExtension(["SMAP\nPolarisScenarioScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PolarisScenarioScreen.kt\ncom/polaris/gts/PolarisScenarioScreen\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 5 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n+ 6 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,1039:1\n1573#2:1040\n1604#2,4:1041\n1740#2,3:1045\n1669#2,8:1056\n1869#2,2:1078\n1563#2:1080\n1634#2,3:1081\n382#3,7:1048\n1#4:1055\n1463#5,14:1064\n1400#6,2:1084\n1400#6,2:1086\n*S KotlinDebug\n*F\n+ 1 PolarisScenarioScreen.kt\ncom/polaris/gts/PolarisScenarioScreen\n*L\n250#1:1040\n250#1:1041,4\n407#1:1045,3\n701#1:1056,8\n749#1:1078,2\n805#1:1080\n805#1:1081,3\n515#1:1048,7\n710#1:1064,14\n844#1:1084,2\n853#1:1086,2\n*E\n"])
public class PolarisScenarioScreen : class_437(class_2561.method_43470("Polaris") as class_2561) {
   private final var view: com.polaris.gts.PolarisScenarioScreen.View = PolarisScenarioScreen.View.SCENARIO
   private final var selectedSlot: Int = -1

   private final val partyCache: List<com.polaris.gts.PolarisScenarioScreen.PokeInfo?> by LazyKt.lazy({ 
      `this$0`.readParty()
   })
      private final get() {
         return this.partyCache$delegate.getValue() as MutableList<PolarisScenarioScreen.PokeInfo>
      }


   private final val floatingStates: HashMap<Int, Any> = HashMap()
   private final val startedAt: Long = System.currentTimeMillis()
   private final var stars: List<com.polaris.gts.PolarisScenarioScreen.Star> = CollectionsKt.emptyList()
   private final var priceField: class_342?
   private final var countdownField: class_342?
   private final var guiLeft: Int
   private final var guiTop: Int
   private final var guiW: Int = 520
   private final var guiH: Int = 360
   private final var compact: Boolean
   private final val void: Int = color$default(this, 1, 3, 12, 0, 8, null)
   private final val deep: Int = color$default(this, 4, 7, 20, 0, 8, null)
   private final val panel: Int = this.color(5, 8, 18, 150)
   private final val panel2: Int = this.color(8, 13, 29, 158)
   private final val card: Int = this.color(4, 7, 17, 190)
   private final val cardHover: Int = this.color(13, 24, 50, 210)
   private final val cyan: Int = color$default(this, 65, 235, 255, 0, 8, null)
   private final val cyanDim: Int = color$default(this, 31, 95, 132, 0, 8, null)
   private final val gold: Int = color$default(this, 255, 226, 136, 0, 8, null)
   private final val goldSoft: Int = color$default(this, 179, 142, 67, 0, 8, null)
   private final val violet: Int = color$default(this, 113, 60, 170, 0, 8, null)
   private final val violetDim: Int = color$default(this, 64, 35, 98, 0, 8, null)
   private final val green: Int = color$default(this, 92, 255, 145, 0, 8, null)
   private final val red: Int = color$default(this, 255, 82, 112, 0, 8, null)
   private final val white: Int = color$default(this, 235, 241, 255, 0, 8, null)
   private final val muted: Int = color$default(this, 150, 159, 196, 0, 8, null)
   private final val dim: Int = color$default(this, 84, 92, 126, 0, 8, null)

   fun method_25426() {
      var fieldW: Int
      var var2: class_342
      var var10001: java.lang.String
      run label24@{
         super.method_25426()
         this.updateLayout()
         this.generateStars()
         fieldW = RangesKt.coerceAtLeast(this.configRect().w - 18, 118)
         var2 = class_342(this.field_22793, this.configRect().x + 9, this.priceFieldY(), fieldW, 20, class_2561.method_43470("Preco") as class_2561)
         var2.method_1880(20)
         if (this.priceField != null) {
            var10001 = this.priceField.method_1882()
            if (var10001 != null) {
               return@label24
            }
         }

         var10001 = "100M"
      }

      run label27@{
         var2.method_1852(var10001)
         this.priceField = var2
         this.method_37063(this.priceField as class_364)
         var2 = class_342(this.field_22793, this.configRect().x + 9, this.countdownFieldY(), fieldW, 20, class_2561.method_43470("Countdown") as class_2561)
         var2.method_1880(3)
         if (this.countdownField != null) {
            var10001 = this.countdownField.method_1882()
            if (var10001 != null) {
               return@label27
            }
         }

         var10001 = "10"
      }

      var2.method_1852(var10001)
      this.countdownField = var2
      this.method_37063(this.countdownField as class_364)
   }

   fun method_25410(client: class_310, width: Int, height: Int) {
      var var10000: java.lang.String
      run label34@{
         if (this.priceField != null) {
            var10000 = this.priceField.method_1882()
            if (var10000 != null) {
               return@label34
            }
         }

         var10000 = "100M"
      }

      run label37@{
         if (this.countdownField != null) {
            var10000 = this.countdownField.method_1882()
            if (var10000 != null) {
               return@label37
            }
         }

         var10000 = "10"
      }

      super.method_25410(client, width, height)
      if (this.priceField != null) {
         this.priceField.method_1852(var10000)
      }

      if (this.countdownField != null) {
         this.countdownField.method_1852(var10000)
      }
   }

   fun method_25421(): Boolean {
      false
   }

   fun method_25420(ctx: class_332, mouseX: Int, mouseY: Int, delta: Float) {
      ctx.method_25294(0, 0, this.field_22789, this.field_22790, this.color(0, 0, 0, 200))
   }

   fun method_25394(ctx: class_332, mouseX: Int, mouseY: Int, delta: Float) {
      this.method_25420(ctx, mouseX, mouseY, delta)
      this.drawFrame(ctx)
      this.drawHeader(ctx, mouseX, mouseY)
      if (this.view === PolarisScenarioScreen.View.SCENARIO) {
         this.setFieldsVisible(true)
         this.drawScenario(ctx, mouseX, mouseY, delta)
      } else {
         this.setFieldsVisible(false)
         this.drawHelp(ctx)
      }

      super.method_25394(ctx, mouseX, mouseY, delta)
   }

   fun method_25402(mouseX: Double, mouseY: Double, button: Int): Boolean {
      if (button == 0) {
         val mx: Int = (int)mouseX
         val my: Int = (int)mouseY
         if (this.helpButtonRect().contains(mx, (int)mouseY)) {
            this.view = if (this.view === PolarisScenarioScreen.View.SCENARIO) PolarisScenarioScreen.View.HELP else PolarisScenarioScreen.View.SCENARIO
            true
         }

         if (this.view === PolarisScenarioScreen.View.SCENARIO) {
            for (var9 in this.presetRects()) {
               val seconds: Int = (var9.component1() as java.lang.Number).intValue()
               val rect: PolarisScenarioScreen.Rect = var9.component2() as PolarisScenarioScreen.Rect
               if (rect.w > 0 && rect.contains(mx, my)) {
                  if (this.countdownField != null) {
                     this.countdownField.method_1852(java.lang.String.valueOf(seconds))
                  }

                  true
               }
            }

            repeat(5) { var12 ->
               if (this.slotRect(var12).contains(mx, my) && CollectionsKt.getOrNull(this.partyCache, var12) != null) {
                  this.selectedSlot = var12
                  true
               }
            }

            if (this.cancelRect().contains(mx, my)) {
               this.method_25419()
               true
            }

            val var13: PolarisScenarioScreen.Rect = this.repeatRect()
            if (var13.w >= 72 && var13.contains(mx, my) && this.hasLastScenario()) {
               this.selectedSlot = PolarisState.lastScenarioSlot
               if (this.priceField != null) {
                  this.priceField.method_1852(PolarisState.lastScenarioPriceText)
               }

               if (this.countdownField != null) {
                  this.countdownField.method_1852(PolarisState.lastScenarioDelayText)
               }

               this.startScenario()
               true
            }

            if (this.startRect().contains(mx, my)) {
               this.startScenario()
               true
            }
         }
      }

      super.method_25402(mouseX, mouseY, button)
   }

   private fun updateLayout() {
      val margin: Int = if (this.field_22789 >= 560 && this.field_22790 >= 340) 12 else 6
      this.guiW = Math.min(860, RangesKt.coerceAtLeast(this.field_22789 - (if (this.field_22789 >= 560 && this.field_22790 >= 340) 12 else 6) * 2, 300))
      this.guiH = Math.min(460, RangesKt.coerceAtLeast(this.field_22790 - margin * 2, 246))
      this.guiLeft = (this.field_22789 - this.guiW) / 2
      this.guiTop = (this.field_22790 - this.guiH) / 2
      this.compact = this.guiW < 660 || this.guiH < 350
   }

   private fun setFieldsVisible(visible: Boolean) {
      if (this.priceField != null) {
         this.priceField.field_22764 = visible
      }

      if (this.priceField != null) {
         this.priceField.field_22763 = visible
      }

      if (this.countdownField != null) {
         this.countdownField.field_22764 = visible
      }

      if (this.countdownField != null) {
         this.countdownField.field_22763 = visible
      }
   }

   private fun headerH(): Int {
      return if (this.compact) 46 else 60
   }

   private fun footerH(): Int {
      return if (this.compact) 72 else 78
   }

   private fun pad(): Int {
      return if (this.compact) 8 else 14
   }

   private fun gap(): Int {
      return if (this.compact) 5 else 7
   }

   private fun contentTop(): Int {
      return this.guiTop + this.headerH()
   }

   private fun footerTop(): Int {
      return this.guiTop + this.guiH - this.footerH()
   }

   private fun configW(): Int {
      return Math.min(if (this.compact) 188 else 260, Math.max(150, this.guiW * 32 / 100))
   }

   private fun configTight(): Boolean {
      return this.configRect().h < 178
   }

   private fun partyRect(): com.polaris.gts.PolarisScenarioScreen.Rect {
      val p: Int = this.pad()
      return PolarisScenarioScreen.Rect(
         this.guiLeft + p, this.contentTop(), this.configRect().x - this.guiLeft - p * 2, this.footerTop() - this.contentTop() - 10
      )
   }

   private fun configRect(): com.polaris.gts.PolarisScenarioScreen.Rect {
      val w: Int = this.configW()
      return PolarisScenarioScreen.Rect(this.guiLeft + this.guiW - this.pad() - w, this.contentTop(), w, this.footerTop() - this.contentTop() - 10)
   }

   private fun gridTop(): Int {
      return this.partyRect().y + (if (this.compact) 19 else 28)
   }

   private fun slotGap(): Int {
      return if (this.compact) 4 else 6
   }

   private fun slotW(): Int {
      return Math.min(RangesKt.coerceAtLeast((this.partyRect().w - this.slotGap() * 2) / 3, 46), if (this.compact) 88 else 128)
   }

   private fun slotH(): Int {
      return Math.min(
         RangesKt.coerceAtLeast((this.partyRect().h - (this.gridTop() - this.partyRect().y) - this.slotGap()) / 2, 56), if (this.compact) 82 else 118
      )
   }

   private fun gridLeft(): Int {
      return this.partyRect().x + RangesKt.coerceAtLeast((this.partyRect().w - (this.slotW() * 3 + this.slotGap() * 2)) / 2, 0)
   }

   private fun slotRect(slot: Int): com.polaris.gts.PolarisScenarioScreen.Rect {
      val rect: PolarisScenarioScreen.Rect = this.partyRect()
      val w: Int = this.slotW()
      val h: Int = this.slotH()
      return PolarisScenarioScreen.Rect(this.gridLeft() + slot % 3 * (w + this.slotGap()), this.gridTop() + slot / 3 * (h + this.slotGap()), w, h)
   }

   private fun helpButtonRect(): com.polaris.gts.PolarisScenarioScreen.Rect {
      return PolarisScenarioScreen.Rect(this.guiLeft + this.guiW - this.pad() - 58, this.guiTop + (if (this.compact) 10 else 13), 58, 18)
   }

   private fun cancelRect(): com.polaris.gts.PolarisScenarioScreen.Rect {
      return PolarisScenarioScreen.Rect(this.guiLeft + this.pad(), this.guiTop + this.guiH - this.pad() - 22, if (this.compact) 112 else 136, 22)
   }

   private fun startRect(): com.polaris.gts.PolarisScenarioScreen.Rect {
      return PolarisScenarioScreen.Rect(
         this.guiLeft + this.guiW - this.pad() - (if (this.compact) 140 else 166),
         this.guiTop + this.guiH - this.pad() - 22,
         if (this.compact) 140 else 166,
         22
      )
   }

   private fun repeatRect(): com.polaris.gts.PolarisScenarioScreen.Rect {
      val cancel: PolarisScenarioScreen.Rect = this.cancelRect()
      val start: PolarisScenarioScreen.Rect = this.startRect()
      val x: Int = cancel.x + cancel.w + 7
      return PolarisScenarioScreen.Rect(x, start.y, RangesKt.coerceAtLeast(Math.min(if (this.compact) 94 else 126, start.x - x - 7), 0), 22)
   }

   private fun presetRects(): List<Pair<Int, com.polaris.gts.PolarisScenarioScreen.Rect>> {
      val rect: PolarisScenarioScreen.Rect = this.configRect()
      val y: Int = this.countdownFieldY() + 23
      val gap: Int = 3
      val w: Int = RangesKt.coerceAtLeast((rect.w - 18 - 3 * 3) / 4, 0)
      val `$this$mapIndexedTo$iv$iv`: java.lang.Iterable = TIME_PRESETS
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(TIME_PRESETS, 10))
      var `index$iv$iv`: Int = 0

      for (`item$iv$iv` in `$this$mapIndexedTo$iv$iv`) {
         val var13: Int = `index$iv$iv`++
         if (var13 < 0) {
            CollectionsKt.throwIndexOverflow()
         }

         `destination$iv$iv`.add(
            TuplesKt.to((`item$iv$iv` as java.lang.Number).intValue(), PolarisScenarioScreen.Rect(rect.x + 9 + var13 * (w + gap), y, w, 14))
         )
      }

      return `destination$iv$iv` as MutableList<Pair<Integer, PolarisScenarioScreen.Rect>>
   }

   private fun priceFieldY(): Int {
      return this.configRect().y + (if (this.configTight()) 32 else (if (this.compact) 38 else 48))
   }

   private fun countdownFieldY(): Int {
      return this.priceFieldY() + (if (this.configTight()) 58 else (if (this.compact) 72 else 88))
   }

   private fun drawFrame(ctx: class_332) {
      ctx.method_25294(this.guiLeft, this.guiTop, this.guiLeft + this.guiW, this.guiTop + this.guiH, this.void)
      ctx.method_25294(this.guiLeft + 2, this.guiTop + 2, this.guiLeft + this.guiW - 2, this.guiTop + this.guiH - 2, this.deep)
      ctx.method_25293(SKY_TEXTURE, this.guiLeft + 5, this.guiTop + 5, this.guiW - 10, this.guiH - 10, 0.0F, 0.0F, 1600, 900, 1600, 900)
      ctx.method_25294(this.guiLeft + 5, this.guiTop + 5, this.guiLeft + this.guiW - 5, this.guiTop + this.guiH - 5, this.color(0, 0, 0, 28))
      this.drawCenterPolaris(ctx)
      this.drawStars(ctx)
      this.drawConstellation(ctx)
      this.border(ctx, this.guiLeft, this.guiTop, this.guiW, this.guiH, this.cyanDim)
      this.border(ctx, this.guiLeft + 2, this.guiTop + 2, this.guiW - 4, this.guiH - 4, this.violet)
      ctx.method_25294(this.guiLeft + 5, this.guiTop + 5, this.guiLeft + this.guiW - 5, this.guiTop + 6, this.cyan)
      ctx.method_25294(this.guiLeft + 5, this.guiTop + this.guiH - 6, this.guiLeft + this.guiW - 5, this.guiTop + this.guiH - 5, this.violet)
   }

   private fun drawHeader(ctx: class_332, mouseX: Int, mouseY: Int) {
      val starX: Int = this.guiLeft + this.pad() + 13
      this.drawPolaris(ctx, starX, this.guiTop + (if (this.compact) 21 else 25), if (this.compact) 8 else 11)
      ctx.method_51448().method_22903()
      ctx.method_51448().method_22904((double)(starX + 20), (double)(this.guiTop + 10), 0.0)
      ctx.method_51448().method_22905(if (this.compact) 1.05F else 1.25F, if (this.compact) 1.05F else 1.25F, 1.0F)
      this.text(ctx, "POLARIS", 0, 0, this.gold)
      ctx.method_51448().method_22909()
      val subtitleMaxW: Int = RangesKt.coerceAtLeast(this.helpButtonRect().x - (starX + 27), 48)
      this.text(ctx, this.fitName("North Star trade timing", subtitleMaxW), starX + 21, this.guiTop + (if (this.compact) 29 else 34), this.muted)
      val creditY: Int = this.guiTop + (if (this.compact) 38 else 46)
      ctx.method_51448().method_22903()
      ctx.method_51448().method_22904((double)(starX + 21), (double)creditY, 0.0)
      ctx.method_51448().method_22905(0.72F, 0.72F, 1.0F)
      this.text(ctx, this.fitName("Developed by: Ich.", (int)((float)subtitleMaxW / 0.72F)), 0, 0, this.goldSoft)
      ctx.method_51448().method_22909()
      val bx: PolarisScenarioScreen.Rect = this.helpButtonRect()
      val hover: Boolean = bx.contains(mouseX, mouseY)
      this.drawCosmogGuide(ctx, bx)
      ctx.method_25294(bx.x, bx.y, bx.x + bx.w, bx.y + bx.h, if (hover) this.cardHover else this.card)
      this.border(ctx, bx.x, bx.y, bx.w, bx.h, if (hover) this.gold else this.cyanDim)
      this.textCenter(
         ctx, if (this.view === PolarisScenarioScreen.View.SCENARIO) "GUIA" else "VOLTAR", bx.x + bx.w / 2, bx.y + 5, if (hover) this.gold else this.muted
      )
   }

   private fun drawCosmogGuide(ctx: class_332, guide: com.polaris.gts.PolarisScenarioScreen.Rect) {
      val critical: Boolean = this.scenarioCritical()
      val active: Boolean = this.scenarioActive()
      val frameMs: Long = if (critical) 35L else (if (active) 70L else 125L)
      val now: Long = System.currentTimeMillis()
      val frame: Int = (int)((now - this.startedAt) / frameMs % COSMOG_FRAMES.size())
      val drawW: Int = (int)(87 * (if (this.compact) 0.55F else (if (active) 0.78F else 0.68F)))
      val drawH: Int = (int)(57 * (if (this.compact) 0.55F else (if (active) 0.78F else 0.68F)))
      val t: Double = (now - this.startedAt) / (if (critical) 190.0 else (if (active) 330.0 else 880.0))
      ctx.method_25293(
         COSMOG_FRAMES.get(frame),
         RangesKt.coerceAtLeast(
            guide.x
               - drawW
               - 8
               + (int)(
                  Math.sin((double)(now - this.startedAt) / (if (critical) 190.0 else (if (active) 330.0 else 880.0)))
                     * (double)(if (critical) 9 else (if (active) 6 else 3))
               ),
            this.guiLeft + this.pad() + 86
         ),
         RangesKt.coerceIn(
            guide.y + guide.h / 2 - drawH / 2 + (int)(Math.sin(t * 1.35) * (double)(if (critical) 9 else (if (active) 6 else 3))),
            this.guiTop + 5,
            this.guiTop + this.headerH() - drawH - 1
         ),
         drawW,
         drawH,
         0.0F,
         0.0F,
         87,
         57,
         87,
         57
      )
   }

   private fun scenarioActive(): Boolean {
      return System.currentTimeMillis() < PolarisState.scenarioActiveUntil
   }

   private fun scenarioCritical(): Boolean {
      return System.currentTimeMillis() < PolarisState.scenarioCriticalUntil
   }

   private fun drawScenario(ctx: class_332, mouseX: Int, mouseY: Int, delta: Float) {
      this.drawPartyPanel(ctx, mouseX, mouseY, delta)
      this.drawConfigPanel(ctx)
      this.drawStatus(ctx)
      this.drawButtons(ctx, mouseX, mouseY)
   }

   private fun drawPartyPanel(ctx: class_332, mouseX: Int, mouseY: Int, delta: Float) {
      val rect: PolarisScenarioScreen.Rect = this.partyRect()
      ctx.method_25294(rect.x - 1, rect.y - 1, rect.x + rect.w + 1, rect.y + rect.h + 1, this.panel2)
      this.border(ctx, rect.x - 1, rect.y - 1, rect.w + 2, rect.h + 2, this.cyanDim)
      this.text(ctx, "Sua Party", rect.x + 6, rect.y + 5, this.cyan)
      val partyHintW: Int = rect.w - 86
      if (!this.compact && partyHintW > 80) {
         this.text(ctx, this.fitName("selecione o Pokemon que esta no TRADE", partyHintW), rect.x + 76, rect.y + 5, this.muted)
      }

      repeat(5) { slot ->
         val r: PolarisScenarioScreen.Rect = this.slotRect(slot)
         val pkm: PolarisScenarioScreen.PokeInfo = CollectionsKt.getOrNull(this.partyCache, slot) as PolarisScenarioScreen.PokeInfo
         val selected: Boolean = this.selectedSlot == slot
         val hover: Boolean = r.contains(mouseX, mouseY) && pkm != null
         ctx.method_25294(r.x, r.y, r.x + r.w, r.y + r.h, if (!hover && !selected) this.card else this.cardHover)
         this.drawSlotAccent(ctx, r, selected, hover)
         this.border(ctx, r.x, r.y, r.w, r.h, if (selected) this.green else (if (hover) this.gold else this.cyanDim))
         this.text(ctx, "#${slot + 1}", r.x + 4, r.y + 4, if (selected) this.green else this.dim)
         if (pkm == null) {
            this.textCenter(ctx, "vazio", r.x + r.w / 2, r.y + r.h / 2 - 4, this.dim)
         } else {
            this.renderPokemonModel(ctx, pkm, r.x, r.y, r.w, r.h, slot)
            val nameY: Int = r.y + r.h - (if (this.compact) 24 else 26)
            val levelY: Int = r.y + r.h - (if (this.compact) 12 else 13)
            this.textCenter(
               ctx,
               this.fitName(if (pkm.shiny) "* ${pkm.speciesName}" else pkm.speciesName, r.w - 8),
               r.x + r.w / 2,
               nameY,
               if (pkm.shiny) this.gold else this.cyan
            )
            this.textCenter(ctx, "Lv. ${pkm.level}", r.x + r.w / 2, levelY, this.white)
            if (!pkm.tradeable) {
               this.text(ctx, "LOCK", r.x + r.w - 26, r.y + 5, this.red)
            }
         }
      }
   }

   private fun drawConfigPanel(ctx: class_332) {
      var rect: PolarisScenarioScreen.Rect
      var tight: Boolean
      var showHints: Boolean
      var var10001: java.lang.String
      run label155@{
         rect = this.configRect()
         tight = this.configTight()
         showHints = !tight && rect.h >= 198
         ctx.method_25294(rect.x - 1, rect.y - 1, rect.x + rect.w + 1, rect.y + rect.h + 1, this.panel2)
         this.border(ctx, rect.x - 1, rect.y - 1, rect.w + 2, rect.h + 2, this.violetDim)
         this.text(ctx, "Configuracao", rect.x + 7, rect.y + 5, this.gold)
         this.drawPolaris(ctx, rect.x + rect.w - 17, rect.y + 11, 5)
         this.text(ctx, "Preco:", rect.x + 9, this.priceFieldY() - 14, this.white)
         if (this.priceField != null) {
            var10001 = this.priceField.method_1882()
            if (var10001 != null) {
               return@label155
            }
         }

         var10001 = ""
      }

      val price: Long = this.parsePrice(var10001)
      if (showHints) {
         this.text(
            ctx,
            this.fitName(if (this.compact) "K/M/B: 100K, 5M, 1B" else "Use K/M/B - ex: 100K, 5M, 1B", rect.w - 18),
            rect.x + 9,
            this.priceFieldY() + 24,
            this.dim
         )
      }

      this.text(
         ctx,
         "= ${this.formatPriceDisplay(price)} money",
         rect.x + 9,
         this.priceFieldY() + (if (showHints) 37 else 24),
         if (price > 0L) this.green else this.red
      )
      if (!tight) {
         val delay: Int = this.countdownFieldY() - 18
         ctx.method_25294(rect.x + 9, delay, rect.x + rect.w - 9, delay + 1, this.cyanDim)
         this.drawPolaris(ctx, rect.x + rect.w / 2, delay, 4)
      }

      var var19: Int
      run label161@{
         this.text(ctx, if (!this.compact && !tight) "Tempo (segundos):" else "Tempo:", rect.x + 9, this.countdownFieldY() - 14, this.white)
         if (this.countdownField != null) {
            val var10000: java.lang.String = this.countdownField.method_1882()
            if (var10000 != null) {
               val var18: Int = StringsKt.toIntOrNull(var10000)
               if (var18 != null) {
                  var19 = var18
                  return@label161
               }
            }
         }

         var19 = 0
      }

      val presets: java.util.List = this.presetRects()
      val delayValueY: java.lang.Iterable = presets
      var var20: Boolean
      if (presets is java.util.Collection && (presets as java.util.Collection).isEmpty()) {
         var20 = true
      } else {
         run label164@{
            for (`element$iv` in delayValueY) {
               if (((`element$iv` as Pair).getSecond() as PolarisScenarioScreen.Rect).w < 24) {
                  var20 = false
                  return@label164
               }
            }

            var20 = true
         }
      }

      val showPresets: Boolean = var20 && ((CollectionsKt.first(presets) as Pair).getSecond() as PolarisScenarioScreen.Rect).y + 14 <= rect.y + rect.h - 18
      if (showPresets) {
         this.drawTimePresets(ctx, presets, var19)
      } else if (showHints) {
         this.text(ctx, if (this.compact) "No 2: confirme TRADE" else "No 2: confirme o TRADE", rect.x + 9, this.countdownFieldY() + 24, this.gold)
      }

      this.text(
         ctx,
         "= $var19s",
         rect.x + 9,
         Math.min(this.countdownFieldY() + (if (showPresets) 40 else (if (showHints) 37 else 24)), rect.y + rect.h - 12),
         if (1 <= var19 && var19 < 61) this.green else this.red
      )
   }

   private fun drawTimePresets(ctx: class_332, presets: List<Pair<Int, com.polaris.gts.PolarisScenarioScreen.Rect>>, currentDelay: Int) {
      for (var5 in presets) {
         val seconds: Int = (var5.component1() as java.lang.Number).intValue()
         val rect: PolarisScenarioScreen.Rect = var5.component2() as PolarisScenarioScreen.Rect
         val selected: Boolean = currentDelay == seconds
         ctx.method_25294(
            rect.x, rect.y, rect.x + rect.w, rect.y + rect.h, if (currentDelay == seconds) this.color(46, 61, 31, 210) else this.color(10, 14, 26, 210)
         )
         this.border(ctx, rect.x, rect.y, rect.w, rect.h, if (selected) this.gold else this.cyanDim)
         this.textCenter(ctx, "$secondss", rect.x + rect.w / 2, rect.y + 3, if (selected) this.gold else this.muted)
      }
   }

   private fun drawStatus(ctx: class_332) {
      val y: Int = this.footerTop() + (if (this.compact) 29 else 34)
      this.text(ctx, "Selecionado:", this.guiLeft + this.pad(), y, this.white)
      val pkm: PolarisScenarioScreen.PokeInfo = CollectionsKt.getOrNull(this.partyCache, this.selectedSlot) as PolarisScenarioScreen.PokeInfo
      val valueX: Int = this.guiLeft + this.pad() + 82
      val repeat: PolarisScenarioScreen.Rect = this.repeatRect()
      this.text(
         ctx,
         if (pkm == null)
            "(nenhum)"
            else
            this.fitName(
               "slot ${this.selectedSlot + 1} - ${pkm.speciesName}",
               RangesKt.coerceAtLeast((if (repeat.w > 0) repeat.x else this.startRect().x) - valueX - 8, if (this.compact) 76 else 120)
            ),
         this.guiLeft + this.pad() + 82,
         y,
         if (pkm == null) this.red else this.green
      )
      val timingX: Int = this.guiLeft + this.guiW / 2 - 48
      val timingMaxW: Int = RangesKt.coerceAtLeast(this.startRect().x - timingX - 8, 0)
      if (!this.compact && timingMaxW > 120) {
         this.text(ctx, this.fitName("Timing: confirme o TRADE no segundo 2", timingMaxW), timingX, y, this.goldSoft)
      }
   }

   private fun drawButtons(ctx: class_332, mouseX: Int, mouseY: Int) {
      var cancel: PolarisScenarioScreen.Rect
      var start: PolarisScenarioScreen.Rect
      var var15: Boolean
      run label158@{
         cancel = this.cancelRect()
         start = this.startRect()
         if (this.selectedSlot >= 0) {
            var var10001: java.lang.String
            run label149@{
               if (this.priceField != null) {
                  var10001 = this.priceField.method_1882()
                  if (var10001 != null) {
                     return@label149
                  }
               }

               var10001 = ""
            }

            if (this.parsePrice(var10001) > 0L) {
               run label152@{
                  if (this.countdownField != null) {
                     var10000 = this.countdownField.method_1882()
                     if (var10000 != null) {
                        return@label152
                     }
                  }

                  var10000 = ""
               }

               val var14: Int = StringsKt.toIntOrNull(var10000)
               val cancelHover: Int = var14 ?: 0
               if (1 <= cancelHover && cancelHover < 61) {
                  var15 = true
                  return@label158
               }
            }
         }

         var15 = false
      }

      ctx.method_25294(
         cancel.x,
         cancel.y,
         cancel.x + cancel.w,
         cancel.y + cancel.h,
         if (cancel.contains(mouseX, mouseY)) color$default(this, 70, 12, 28, 0, 8, null) else color$default(this, 32, 6, 14, 0, 8, null)
      )
      this.border(ctx, cancel.x, cancel.y, cancel.w, cancel.h, this.red)
      this.textCenter(ctx, "CANCELAR", cancel.x + cancel.w / 2, cancel.y + 7, this.red)
      val repeat: PolarisScenarioScreen.Rect = this.repeatRect()
      if (repeat.w >= 72) {
         val startHover: Boolean = this.hasLastScenario()
         ctx.method_25294(
            repeat.x,
            repeat.y,
            repeat.x + repeat.w,
            repeat.y + repeat.h,
            if (!startHover)
               color$default(this, 9, 10, 16, 0, 8, null)
               else
               (if (startHover && repeat.contains(mouseX, mouseY)) color$default(this, 44, 37, 71, 0, 8, null) else color$default(this, 24, 20, 44, 0, 8, null))
         )
         this.border(ctx, repeat.x, repeat.y, repeat.w, repeat.h, if (startHover) this.violet else this.dim)
         this.textCenter(
            ctx, if (repeat.w < 100) "REPETIR" else "REPETIR ULTIMO", repeat.x + repeat.w / 2, repeat.y + 7, if (startHover) this.gold else this.dim
         )
      }

      ctx.method_25294(
         start.x,
         start.y,
         start.x + start.w,
         start.y + start.h,
         if (!var15)
            color$default(this, 9, 10, 16, 0, 8, null)
            else
            (if (var15 && start.contains(mouseX, mouseY)) color$default(this, 20, 78, 43, 0, 8, null) else color$default(this, 9, 48, 28, 0, 8, null))
      )
      this.border(ctx, start.x, start.y, start.w, start.h, if (var15) this.green else this.dim)
      this.textCenter(
         ctx,
         if (!var15) (if (start.w < 150) "CONFIGURE" else "CONFIGURE TUDO") else (if (start.w < 132) "INICIAR" else "INICIAR TESTE"),
         start.x + start.w / 2,
         start.y + 7,
         if (var15) this.green else this.dim
      )
   }

   private fun drawHelp(ctx: class_332) {
      val rect: PolarisScenarioScreen.Rect = PolarisScenarioScreen.Rect(
         this.guiLeft + this.pad(), this.contentTop(), this.guiW - this.pad() * 2, this.footerTop() - this.contentTop() - 8
      )
      ctx.method_25294(rect.x, rect.y, rect.x + rect.w, rect.y + rect.h, this.panel2)
      this.border(ctx, rect.x, rect.y, rect.w, rect.h, this.goldSoft)
      val y: IntRef = IntRef()
      y.element = rect.y + 12
      drawHelp$title(y, this, ctx, rect, "Como usar")
      drawHelp$line$default(this, ctx, rect, y, "1. Inicie TRADE com outro player.", 0, 32, null)
      drawHelp$line$default(this, ctx, rect, y, "2. Coloque no TRADE o Pokemon do slot selecionado.", 0, 32, null)
      drawHelp$line$default(this, ctx, rect, y, "3. Escolha o mesmo slot e preco no Polaris.", 0, 32, null)
      drawHelp$line$default(this, ctx, rect, y, "4. Clique INICIAR TESTE e volte para o TRADE.", 0, 32, null)
      drawHelp$line(this, ctx, rect, y, "5. Quando o chat mostrar 2, confirme o TRADE.", this.gold)
      drawHelp$line$default(this, ctx, rect, y, "6. O Polaris lista e retira do GTS automaticamente.", 0, 32, null)
      drawHelp$line$default(this, ctx, rect, y, "7. Ao terminar, abre /gts mochila.", 0, 32, null)
      if (!this.compact) {
         drawHelp$title(y, this, ctx, rect, "Tema Polaris")
         drawHelp$line$default(this, ctx, rect, y, "A estrela dourada marca o timing: ela e o guia visual do teste.", 0, 32, null)
      }
   }

   private fun renderPokemonModel(ctx: class_332, pkm: com.polaris.gts.PolarisScenarioScreen.PokeInfo, x: Int, y: Int, w: Int, h: Int, slot: Int) {
      if (pkm.resourceId == null) {
         this.drawInitialFallback(ctx, pkm, x, y, w, h)
      } else {
         try {
            val modelBottom: java.util.Map = this.floatingStates
            val modelH: Any = slot
            val `value$iv`: Any = modelBottom.get(modelH)
            var var27: Any
            if (`value$iv` == null) {
               var27 = this.createFloatingState()
               if (var27 == null) {
                  this.drawInitialFallback(ctx, pkm, x, y, w, h)
                  return
               }

               modelBottom.put(modelH, var27)
               var27 = var27
            } else {
               var27 = `value$iv`
            }

            val var8: Any = var27
            this.setAspects(var27, pkm.aspects)
            val var24: Int = y + h - (if (this.compact) 27 else 31)
            ctx.method_44379(x + 1, y + 1, x + w - 1, y + h - (if (this.compact) 27 else 31))

            try {
               ctx.method_51448().method_22903()

               try {
                  ctx.method_51448().method_22904((double)(x + w / 2), (double)(y + (if (this.compact) 10 else 14)), 100.0)
                  val var26: Float = RangesKt.coerceIn(
                     (float)Math.min(w, RangesKt.coerceAtLeast(var24 - y, 36)) * (if (this.compact) 0.27F else 0.31F),
                     11.0F,
                     if (this.compact) 22.0F else 30.0F
                  )
                  val var10001: class_4587 = ctx.method_51448()
                  this.drawProfileReflect(var10001, pkm.resourceId, var8, var26)
               } finally {
                  ctx.method_51448().method_22909()
               }
            } finally {
               ctx.method_44380()
            }
         } catch (var23: java.lang.Throwable) {
            this.drawInitialFallback(ctx, pkm, x, y, w, h)
         }
      }
   }

   private fun drawInitialFallback(ctx: class_332, pkm: com.polaris.gts.PolarisScenarioScreen.PokeInfo, x: Int, y: Int, w: Int, h: Int) {
      val var7: java.lang.String = StringsKt.take(pkm.speciesName, 1)
      val var10002: Locale = Locale.ROOT
      val var8: java.lang.String = var7.toUpperCase(var10002)
      this.textCenter(ctx, var8, x + w / 2, y + h / 2 - 8, this.white)
   }

   private fun startScenario() {
      var slot: Int
      var var10000: java.lang.String
      run label68@{
         slot = this.selectedSlot
         if (this.priceField != null) {
            var10000 = this.priceField.method_1882()
            if (var10000 != null) {
               return@label68
            }
         }

         var10000 = ""
      }

      run label71@{
         if (this.countdownField != null) {
            var10000 = this.countdownField.method_1882()
            if (var10000 != null) {
               return@label71
            }
         }

         var10000 = ""
      }

      val price: Long = this.parsePrice(var10000)
      val var15: Int = StringsKt.toIntOrNull(var10000)
      val delay: Int = var15 ?: 0
      val player: class_746 = class_310.method_1551().field_1724
      if (slot >= 0 && CollectionsKt.getOrNull(this.partyCache, slot) != null && price > 0L && 1 <= delay && delay < 61 && player != null) {
         val now: Long = System.currentTimeMillis()
         PolarisState.blockGtsUi = true
         PolarisState.latestGtsMyListings = CollectionsKt.emptyList()
         PolarisState.latestGtsSnapshotAt = 0L
         PolarisState.lastScenarioSlot = slot
         PolarisState.lastScenarioPriceText = (if (StringsKt.isBlank(var10000)) java.lang.String.valueOf(price) else var10000) as java.lang.String
         PolarisState.lastScenarioDelayText = java.lang.String.valueOf(delay)
         PolarisState.scenarioActiveUntil = now + delay * 1000L + 7000L
         PolarisState.scenarioCountdownEndsAt = now + delay * 1000L
         PolarisState.scenarioCriticalUntil = 0L
         PolarisState.scenarioFlashUntil = 0L
         player.method_7353(
            class_2561.method_43470("[POLARIS] Teste iniciado. Confirme o TRADE quando chegar em 2.").method_27692(class_124.field_1054) as class_2561, false
         )

         for (var12 in delay downTo 1) {
            scheduler.schedule({ 
               class_310.method_1551().execute({ 
                  if (`$seconds` == 1) {
                     `this$0`.triggerOneSecondCue()
                  }

                  var var10000: java.lang.String
                  when (`$seconds`) {
                     1 -> var10000 = "[POLARIS] 1... brilho da Polaris."
                     2 -> var10000 = "[POLARIS] 2... CONFIRME O TRADE AGORA."
                     else -> var10000 = "[POLARIS] $`$seconds`..."
                  }

                  var var4: class_124
                  when (`$seconds`) {
                     1 -> var4 = class_124.field_1054
                     2 -> var4 = class_124.field_1065
                     else -> var4 = class_124.field_1080
                  }

                  val var5: class_746 = class_310.method_1551().field_1724
                  if (var5 != null) {
                     var5.method_7353(class_2561.method_43470(var10000).method_27692(var4) as class_2561, false)
                  }
               })
            }, (long)(delay - var12) * 1000L, TimeUnit.MILLISECONDS)
         }

         scheduler.schedule(
            { 
               class_310.method_1551()
                  .execute(
                     { 
                        try {
                           ClientPlayNetworking.send(ListPokemonPayload(`$slot`, `$price`) as class_8710)
                           val var5: class_746 = class_310.method_1551().field_1724
                           if (var5 != null) {
                              var5.method_7353(
                                 class_2561.method_43470("[POLARIS] ListPokemonPayload(slot=$`$slot`, price=$`$price`) enviado.")
                                    .method_27692(class_124.field_1061) as class_2561,
                                 false
                              )
                           }
                        } catch (var4: java.lang.Throwable) {
                           val var10000: class_746 = class_310.method_1551().field_1724
                           if (var10000 != null) {
                              var10000.method_7353(
                                 class_2561.method_43470("[POLARIS] Erro: ${var4.getMessage()}").method_27692(class_124.field_1061) as class_2561, false
                              )
                           }
                        }
                     }
                  )
               },
            (long)delay * 1000L,
            TimeUnit.MILLISECONDS
         )
         scheduler.schedule({ 
            class_310.method_1551().execute({ 
               `this$0`.runAutoWithdrawAttempt(`$slot`, `$price`, 8)
            })
         }, (long)delay * 1000L + 2000L, TimeUnit.MILLISECONDS)
         this.method_25419()
      }
   }

   private fun triggerOneSecondCue() {
      val now: Long = System.currentTimeMillis()
      PolarisState.scenarioCriticalUntil = now + 1800L
      PolarisState.scenarioFlashUntil = now + 1400L
      PolarisState.scenarioCountdownEndsAt = 0L
      this.playAlertSound(1.55F)
      scheduler.schedule({ 
         class_310.method_1551().execute({ 
            `this$0`.playAlertSound(1.9F)
         })
      }, 160L, TimeUnit.MILLISECONDS)
   }

   private fun playAlertSound(pitch: Float) {
      val var2: PolarisScenarioScreen = this

      try {
         var var6: PolarisScenarioScreen = var2
         class_310.method_1551().method_1483().method_4873(class_1109.method_4757(class_3417.field_14622.comp_349() as class_3414, 1.0F, pitch) as class_1113)
         var6 = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
      } catch (var5: java.lang.Throwable) {
         val `$this$playAlertSound_u24lambda_u240`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var5))
      }
   }

   private fun runAutoWithdrawAttempt(slot: Int, price: Long, attemptsLeft: Int) {
      if (attemptsLeft >= 8) {
         this.triggerGtsSyncForCache()
      }

      if (!this.autoWithdrawListedPokemon(slot, price, attemptsLeft <= 1) && attemptsLeft > 1) {
         scheduler.schedule({ 
            class_310.method_1551().execute({ 
               `this$0`.runAutoWithdrawAttempt(`$slot`, `$price`, `$attemptsLeft` - 1)
            })
         }, 500L, TimeUnit.MILLISECONDS)
      } else {
         PolarisState.blockGtsUi = false
         PolarisState.scenarioActiveUntil = System.currentTimeMillis() + 1500L
         PolarisState.scenarioCountdownEndsAt = 0L
         this.sendPolarisMessage("GTS liberado novamente.", class_124.field_1060)
         scheduler.schedule({ 
            class_310.method_1551().execute({ 
               `this$0`.openGtsBackpack()
            })
         }, 400L, TimeUnit.MILLISECONDS)
      }
   }

   private fun triggerGtsSyncForCache() {
      val var10000: class_634 = class_310.method_1551().method_1562()
      if (var10000 != null) {
         val handler: class_634 = var10000
         val var2: PolarisScenarioScreen = this

         try {
            var var6: PolarisScenarioScreen = var2
            handler.method_45730("gts")
            var6 = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
         } catch (var5: java.lang.Throwable) {
            val `$this$triggerGtsSyncForCache_u24lambda_u240`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var5))
         }
      }
   }

   private fun autoWithdrawListedPokemon(slot: Int, price: Long, finalAttempt: Boolean): Boolean {
      val listing: PokemonSummary = this.findListingForAutoWithdraw(slot, price)
      if (listing == null) {
         if (finalAttempt) {
            this.sendPolarisMessage("Nao achei o UUID do listing para withdraw automatico.", class_124.field_1054)
         }

         return false
      } else {
         try {
            ClientPlayNetworking.send(WithdrawListingPayload(listing.getListingId()) as class_8710)
            this.sendPolarisMessage("Withdraw automatico enviado: ${listing.getSpecies()} Lv.${listing.getLevel()}.", class_124.field_1060)
            return true
         } catch (var7: java.lang.Throwable) {
            this.sendPolarisMessage("Erro no withdraw automatico: ${var7.getMessage()}", class_124.field_1061)
            return true
         }
      }
   }

   private fun findListingForAutoWithdraw(slot: Int, price: Long): PokemonSummary? {
      val var10000: PolarisScenarioScreen.PokeInfo = CollectionsKt.getOrNull(this.partyCache, slot) as PolarisScenarioScreen.PokeInfo
      if (var10000 == null) {
         return null
      } else {
         val cached: java.util.List = PolarisState.latestGtsMyListings
         val screenListings: java.util.List = this.currentScreenMyListings()
         val playerName: PolarisScenarioScreen = this

         var `$this$maxByOrNull$iv`: PolarisScenarioScreen
         try {
            `$this$maxByOrNull$iv` = playerName
            `$this$maxByOrNull$iv` = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(GtsClientData.INSTANCE.getSelectedListing())
         } catch (var19: java.lang.Throwable) {
            `$this$maxByOrNull$iv` = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var19))
         }

         var selected: PokemonSummary
         run label72@{
            selected = (if (Result.isFailure_impl/* $VF was: isFailure-impl */(`$this$maxByOrNull$iv`)) null else `$this$maxByOrNull$iv`) as PokemonSummary
            val var37: class_746 = class_310.method_1551().field_1724
            if (var37 != null) {
               val var38: GameProfile = var37.method_7334()
               if (var38 != null) {
                  var39 = var38.getName()
                  return@label72
               }
            }

            var39 = null
         }

         `$this$maxByOrNull$iv` = CollectionsKt.plus(CollectionsKt.plus(cached, screenListings), CollectionsKt.listOfNotNull(selected))
         val `iterator$iv`: HashSet = HashSet()
         val `maxElem$iv`: ArrayList = ArrayList()

         for (`e$iv` in `$this$maxByOrNull$iv`) {
            if (`iterator$iv`.add((`e$iv` as PokemonSummary).getListingId())) {
               `maxElem$iv`.add(`e$iv`)
            }
         }

         val var27: java.util.Iterator = SequencesKt.filter(
               SequencesKt.filter(SequencesKt.filter(SequencesKt.filter(SequencesKt.filter(CollectionsKt.asSequence(`maxElem$iv`), { it: PokemonSummary ->
                  it.getPrice() == `$price`
               }), { it: PokemonSummary ->
                  it.getLevel() == `$target`.level
               }), { it: PokemonSummary ->
                  it.getShiny() == `$target`.shiny
               }), { it: PokemonSummary ->
                  StringsKt.equals(it.getSpecies(), `$target`.speciesName, true)
               }),
               { listing: PokemonSummary ->
                  `$cached`.contains(listing)
                     || `$screenListings`.contains(listing)
                     || `$playerName` == null
                     || StringsKt.equals(listing.getSellerName(), `$playerName`, true)
                  }
            )
            .iterator()
            val var40: Any
         if (!var27.hasNext()) {
            var40 = null
         } else {
            var var28: Any = var27.next()
            if (!var27.hasNext()) {
               var40 = var28
            } else {
               var var30: Long = (var28 as PokemonSummary).getTimestamp()

               do {
                  val var32: Any = var27.next()
                  val var35: Long = (var32 as PokemonSummary).getTimestamp()
                  if (var30 < var35) {
                     var28 = var32
                     var30 = var35
                  }
               } while (var27.hasNext())

               var40 = var28
            }
         }

         return var40 as PokemonSummary
      }
   }

   private fun currentScreenMyListings(): List<PokemonSummary> {
      val var10000: class_437 = class_310.method_1551().field_1755
      if (var10000 == null) {
         return CollectionsKt.emptyList()
      } else {
         val screen: class_437 = var10000
         val var2: PolarisScenarioScreen = this

         var `$this$currentScreenMyListings_u24lambda_u240`: PolarisScenarioScreen
         try {
            `$this$currentScreenMyListings_u24lambda_u240` = var2
            val field: Field = screen.getClass().getDeclaredField("myListings")
            field.setAccessible(true)
            val var6: Any = field.get(screen)
            `$this$currentScreenMyListings_u24lambda_u240` = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(
               var6 as? java.util.List
            )
         } catch (var7: java.lang.Throwable) {
            `$this$currentScreenMyListings_u24lambda_u240` = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(
               ResultKt.createFailure(var7)
            )
         }

         var var10: java.util.List = (
            if (Result.isFailure_impl/* $VF was: isFailure-impl */(`$this$currentScreenMyListings_u24lambda_u240`))
               null
               else
               `$this$currentScreenMyListings_u24lambda_u240`
         ) as java.util.List
         if (var10 == null) {
            var10 = CollectionsKt.emptyList()
         }

         return var10
      }
   }

   private fun sendPolarisMessage(message: String, color: class_124) {
      val var10000: class_746 = class_310.method_1551().field_1724
      if (var10000 != null) {
         var10000.method_7353(class_2561.method_43470("[POLARIS] $message").method_27692(color) as class_2561, false)
      }
   }

   private fun hasLastScenario(): Boolean {
      if (0 <= PolarisState.lastScenarioSlot && PolarisState.lastScenarioSlot < 6 && !StringsKt.isBlank(PolarisState.lastScenarioPriceText)) {
         val var3: IntRange = IntRange(1, 60)
         val var2: Int = StringsKt.toIntOrNull(PolarisState.lastScenarioDelayText)
         if (var2 != null && var3.contains(var2)) {
            return true
         }
      }

      return false
   }

   private fun openGtsBackpack() {
      val handler: class_634 = class_310.method_1551().method_1562()
      if (handler == null) {
         this.sendPolarisMessage("Nao consegui executar /gts mochila: networkHandler null.", class_124.field_1061)
      } else {
         PolarisState.forceGtsBackpackUntil = System.currentTimeMillis() + 10000L
         handler.method_45730("gts mochila")
         this.sendPolarisMessage("Comando /gts mochila executado.", class_124.field_1060)
         this.scheduleBackpackScreenFix()
      }
   }

   private fun scheduleBackpackScreenFix() {
      for (`element$iv` in CollectionsKt.listOf(arrayOf(100L, 250L, 500L, 900L, 1500L, 2600L, 4200L, 6500L, 9000L))) {
         scheduler.schedule({ 
            class_310.method_1551().execute({ 
               `this$0`.forceBackpackScreenIfGtsOpened()
            })
         }, (`element$iv` as java.lang.Number).longValue(), TimeUnit.MILLISECONDS)
      }

      scheduler.schedule({ 
         class_310.method_1551().execute({ 
            PolarisState.forceGtsBackpackUntil = 0L
         })
      }, 10000L, TimeUnit.MILLISECONDS)
   }

   private fun forceBackpackScreenIfGtsOpened() {
      val client: class_310 = class_310.method_1551()
      if (PolarisState.pendingGtsBackpackOpen) {
         val var7: java.util.List = PolarisState.pendingGtsBackpackAllListings
         val var9: java.util.Collection = PolarisState.pendingGtsBackpackMyListings
         val var8: java.util.List = (if (PolarisState.pendingGtsBackpackMyListings.isEmpty()) PolarisState.latestGtsMyListings else var9) as java.util.List
         PolarisState.pendingGtsBackpackOpen = false
         PolarisState.forceGtsBackpackUntil = 0L
         client.method_1507(GtsMyAuctionsScreen(var7, var8) as class_437)
      } else if (client.field_1755 != null) {
         val screen: class_437 = client.field_1755
         if (client.field_1755 !is GtsMyAuctionsScreen) {
            if (client.field_1755 is GtsAuctionHouseScreen) {
               val allListings: java.util.List = this.readListingField(client.field_1755, "allListings")
               val var5: java.util.Collection = this.readListingField(screen, "myListings")
               val myListings: java.util.List = (if (var5.isEmpty()) PolarisState.latestGtsMyListings else var5) as java.util.List
               PolarisState.forceGtsBackpackUntil = 0L
               client.method_1507(GtsMyAuctionsScreen(allListings, myListings) as class_437)
            }
         }
      }
   }

   private fun readListingField(screen: Any, fieldName: String): List<PokemonSummary> {
      val var3: PolarisScenarioScreen = this

      var `$this$readListingField_u24lambda_u240`: PolarisScenarioScreen
      try {
         `$this$readListingField_u24lambda_u240` = var3
         val field: Field = screen.getClass().getDeclaredField(fieldName)
         field.setAccessible(true)
         val var7: Any = field.get(screen)
         `$this$readListingField_u24lambda_u240` = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(var7 as? java.util.List)
      } catch (var8: java.lang.Throwable) {
         `$this$readListingField_u24lambda_u240` = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var8))
      }

      var var10000: java.util.List = (
         if (Result.isFailure_impl/* $VF was: isFailure-impl */(`$this$readListingField_u24lambda_u240`)) null else `$this$readListingField_u24lambda_u240`
      ) as java.util.List
      if (var10000 == null) {
         var10000 = CollectionsKt.emptyList()
      }

      return var10000
   }

   private fun readParty(): List<com.polaris.gts.PolarisScenarioScreen.PokeInfo?> {
      var clientClass: java.util.List
      try {
         val var19: Class = Class.forName("com.cobblemon.mod.common.client.CobblemonClient")
         val var20: Any = var19.getMethod("getStorage").invoke(var19.getField("INSTANCE").get(null))
         val var10000: Any = var20.getClass().getMethod("getParty").invoke(var20)
         val var23: java.lang.Iterable = var10000 as java.lang.Iterable
         val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var10000 as java.lang.Iterable, 10))

         for (`item$iv$iv` in var23) {
            `destination$iv$iv`.add(if (`item$iv$iv` != null) this.extractInfo(`item$iv$iv`) else null)
         }

         val var22: java.util.List = CollectionsKt.toMutableList(`destination$iv$iv` as java.util.List)

         while (var22.size() < 6) {
            var22.add(null)
         }

         clientClass = CollectionsKt.take(var22, 6)
      } catch (var18: java.lang.Throwable) {
         val storage: Byte = 6
         val party: ArrayList = ArrayList(6)

         repeat(storage) { result ->
            party.add(null)
         }

         clientClass = party
      }

      return clientClass
   }

   private fun extractInfo(pokemon: Any): com.polaris.gts.PolarisScenarioScreen.PokeInfo {
      var species: PolarisScenarioScreen.PokeInfo
      try {
         val var20: Any = pokemon.getClass().getMethod("getSpecies").invoke(pokemon)
         var var10000: Any = var20.getClass().getMethod("getName").invoke(var20)
         val var3: java.lang.String = var10000 as java.lang.String
         var10000 = pokemon.getClass().getMethod("getLevel").invoke(pokemon)
         val level: Int = var10000 as Int
         val tradeable: PolarisScenarioScreen = this

         var resourceId: PolarisScenarioScreen
         try {
            resourceId = tradeable
            var10000 = pokemon.getClass().getMethod("getShiny").invoke(pokemon)
            resourceId = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(var10000 as java.lang.Boolean)
         } catch (var18: java.lang.Throwable) {
            resourceId = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var18))
         }

         val shiny: Boolean = (if (Result.isFailure_impl/* $VF was: isFailure-impl */(resourceId)) false else resourceId) as java.lang.Boolean
         resourceId = this

         var var28: PolarisScenarioScreen
         try {
            var28 = resourceId
            var10000 = pokemon.getClass().getMethod("getTradeable").invoke(pokemon)
            var28 = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(var10000 as java.lang.Boolean)
         } catch (var17: java.lang.Throwable) {
            var28 = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var17))
         }

         val var22: Boolean = (if (Result.isFailure_impl/* $VF was: isFailure-impl */(var28)) true else var28) as java.lang.Boolean
         var28 = this

         var var34: PolarisScenarioScreen
         try {
            var34 = var28
            val `$this$extractInfo_u24lambda_u244`: Any = var20.getClass().getMethod("getResourceIdentifier").invoke(var20)
            var34 = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(`$this$extractInfo_u24lambda_u244` as? class_2960)
         } catch (var16: java.lang.Throwable) {
            var34 = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var16))
         }

         val var27: class_2960 = (if (Result.isFailure_impl/* $VF was: isFailure-impl */(var34)) null else var34) as class_2960
         var var37: PolarisScenarioScreen = this

         var var41: PolarisScenarioScreen
         try {
            var41 = var37
            val var13: Any = pokemon.getClass().getMethod("getAspects").invoke(pokemon)
            var41 = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(var13 as? java.util.Set)
         } catch (var15: java.lang.Throwable) {
            var41 = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var15))
         }

         var var50: java.util.Set = (if (Result.isFailure_impl/* $VF was: isFailure-impl */(var41)) null else var41) as java.util.Set
         if (var50 == null) {
            var50 = SetsKt.emptySet()
         }

         var37 = this

         try {
            var41 = var37
            var41 = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(
               pokemon.getClass().getMethod("getUuid").invoke(pokemon).toString()
            )
         } catch (var14: java.lang.Throwable) {
            var41 = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var14))
         }

         species = PolarisScenarioScreen.PokeInfo(
            pokemon,
            var3,
            level,
            shiny,
            var22,
            var27,
            var50,
            (if (Result.isFailure_impl/* $VF was: isFailure-impl */(var41)) "?" else var41) as java.lang.String
         )
      } catch (var19: java.lang.Throwable) {
         species = PolarisScenarioScreen.PokeInfo(pokemon, "Pokemon", 0, false, true, null, SetsKt.emptySet(), "?")
      }

      return species
   }

   private fun createFloatingState(): Any? {
      val var1: PolarisScenarioScreen = this

      var `$this$createFloatingState_u24lambda_u240`: PolarisScenarioScreen
      try {
         `$this$createFloatingState_u24lambda_u240` = var1
         `$this$createFloatingState_u24lambda_u240` = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(
            Class.forName("com.cobblemon.mod.common.client.render.models.blockbench.FloatingState").getDeclaredConstructor().newInstance()
         )
      } catch (var4: java.lang.Throwable) {
         `$this$createFloatingState_u24lambda_u240` = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(
            ResultKt.createFailure(var4)
         )
      }

      return if (Result.isFailure_impl/* $VF was: isFailure-impl */(`$this$createFloatingState_u24lambda_u240`))
         null
         else
         `$this$createFloatingState_u24lambda_u240`
      }

   private fun setAspects(state: Any, aspects: Set<String>) {
      val var3: PolarisScenarioScreen = this

      try {
         var var16: PolarisScenarioScreen = var3
         var var10000: Method = state.getClass().getMethods()
         val `$this$firstOrNull$iv`: Array<Any> = var10000 as Array<Any>
         var var8: Int = 0
         val var9: Int = `$this$firstOrNull$iv`.length

         while (true) {
            if (var8 >= var9) {
               var10000 = null
               break
            }

            val `element$iv`: Any = `$this$firstOrNull$iv`[var8]
            if ((`$this$firstOrNull$iv`[var8] as Method).getParameterCount() == 1
               && (
                  (`$this$firstOrNull$iv`[var8] as Method).getName() == "setCurrentAspects"
                     || (`$this$firstOrNull$iv`[var8] as Method).getName() == "currentAspects"
                     || (`$this$firstOrNull$iv`[var8] as Method).getName() == "setAspects"
               )) {
               var10000 = (Method)`element$iv`
               break
            }

            var8++
         }

         var10000 = var10000
         if (var10000 == null) {
            return
         }

         var16 = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(var10000.invoke(state, aspects))
      } catch (var15: java.lang.Throwable) {
         val `$this$setAspects_u24lambda_u240`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var15))
      }
   }

   private fun drawProfileReflect(matrices: class_4587, resourceId: class_2960, state: Any, scale: Float) {
      val var5: PolarisScenarioScreen = this

      try {
         var var19: PolarisScenarioScreen = var5
         var var10000: Method = Class.forName("com.cobblemon.mod.common.api.gui.GuiUtilsKt").getMethods()
         val `$this$firstOrNull$iv`: Array<Any> = var10000 as Array<Any>
         var var10: Int = 0
         val var11: Int = `$this$firstOrNull$iv`.length

         while (true) {
            if (var10 >= var11) {
               var10000 = null
               break
            }

            var `element$iv`: Any
            run label64@{
               `element$iv` = `$this$firstOrNull$iv`[var10]
               val it: Method = `$this$firstOrNull$iv`[var10] as Method
               if ((`$this$firstOrNull$iv`[var10] as Method).getName() == "drawProfile") {
                  val var15: Int = it.getParameterCount()
                  if (5 <= var15 && var15 < 7) {
                     var23 = true
                     return@label64
                  }
               }

               var23 = false
            }

            if (var23) {
               var10000 = (Method)`element$iv`
               break
            }

            var10++
         }

         var10000 = var10000
         if (var10000 == null) {
            return
         }

         val var26: Array<Any> = if (var10000.getParameterCount() == 5)
            arrayOf(resourceId, matrices, state, 0.0F, scale)
            else
            arrayOf(resourceId, matrices, state, 0.0F, scale, 0.0F)
            var19 = (PolarisScenarioScreen)Result.constructor_impl/* $VF was: constructor-impl */(var10000.invoke(null, Arrays.copyOf(var26, var26.length)))
      } catch (var18: java.lang.Throwable) {
         val `$this$drawProfileReflect_u24lambda_u240`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var18))
      }
   }

   private fun generateStars() {
      val rng: Random = Random(this.field_22789 * 31L + this.field_22790 * 17L + 22605187068873043L)
      val count: Int = RangesKt.coerceIn(this.guiW * this.guiH / 1900, 55, 150)
      val var3: ArrayList = ArrayList(count)

      repeat(count) { var4 ->
         var3.add(
            PolarisScenarioScreen.Star(
               this.guiLeft + 8 + rng.nextInt(RangesKt.coerceAtLeast(this.guiW - 16, 1)),
               this.guiTop + 8 + rng.nextInt(RangesKt.coerceAtLeast(this.guiH - 16, 1)),
               if (rng.nextInt(18) == 0) 2 else 1,
               38 + rng.nextInt(95),
               rng.nextFloat() * (float) Math.PI * 2.0F
            )
         )
      }

      this.stars = var3
   }

   private fun drawNebula(ctx: class_332) {
      val top: Int = this.guiTop + 6
      val bottom: Int = this.guiTop + this.guiH - 6

      repeat(5) { i ->
         ctx.method_25294(
            this.guiLeft + 6,
            top + i * (bottom - top) / 6,
            this.guiLeft + this.guiW - 6,
            top + (i + 1) * (bottom - top) / 6,
            color$default(this, 3 + i, 7 + i, 12 + i * 4, 0, 8, null)
         )
      }

      ctx.method_25294(this.guiLeft + this.guiW * 2 / 3, this.guiTop + 6, this.guiLeft + this.guiW - 6, this.guiTop + this.guiH - 6, this.color(10, 7, 27, 190))
   }

   private fun drawStars(ctx: class_332) {
      val t: Double = (System.currentTimeMillis() - this.startedAt) / 1400.0

      for (star in this.stars) {
         ctx.method_25294(
            star.x,
            star.y,
            star.x + star.size,
            star.y + star.size,
            this.color(230, 240, 255, RangesKt.coerceIn((int)((double)star.alpha * (0.75 + Math.sin(t + (double)star.phase) * 0.25)), 20, 150))
         )
      }
   }

   private fun drawCenterPolaris(ctx: class_332) {
      val cx: Int = this.guiLeft + this.guiW / 2
      val cy: Int = this.guiTop + this.guiH / 2
      val active: Boolean = this.scenarioActive()
      val critical: Boolean = this.scenarioCritical()
      val pulse: Int = (int)(
         Math.sin((double)(System.currentTimeMillis() - this.startedAt) / (if (critical) 145.0 else (if (active) 310.0 else 620.0)))
            * (if (critical) 34 else (if (active) 24 else 16))
      )
      val haloMax: Int = if (critical) 124 else (if (active) 98 else 82)
      var coreMax: Int = if (critical) 124 else (if (active) 98 else 82)
      var rx: Int = ProgressionUtilKt.getProgressionLastElement(if (critical) 124 else (if (active) 98 else 82), 18, -8)
      if (rx <= coreMax) {
         while (true) {
            val alpha: Int = RangesKt.coerceIn(8 + pulse / 3 + (haloMax - coreMax) / 3 + (if (active) 8 else 0), 6, if (critical) 90 else 52)
            ctx.method_25294(cx - coreMax, cy - 1, cx + coreMax + 1, cy + 2, this.color(255, 226, 136, alpha))
            ctx.method_25294(cx - 1, cy - coreMax, cx + 2, cy + coreMax + 1, this.color(255, 226, 136, alpha))
            if (coreMax == rx) {
               break
            }

            coreMax -= 8
         }
      }

      coreMax = if (critical) 48 else (if (active) 40 else 34)
      rx = if (critical) 48 else (if (active) 40 else 34)
      val var16: Int = ProgressionUtilKt.getProgressionLastElement(if (critical) 48 else (if (active) 40 else 34), 10, -6)
      if (var16 <= rx) {
         while (true) {
            val alphax: Int = RangesKt.coerceIn(32 + pulse + (coreMax - rx) * 2 + (if (active) 12 else 0), 18, if (critical) 135 else 92)
            ctx.method_25294(cx - rx, cy - 2, cx + rx + 1, cy + 3, this.color(255, 226, 136, alphax))
            ctx.method_25294(cx - 2, cy - rx, cx + 3, cy + rx + 1, this.color(255, 226, 136, alphax))
            if (rx == var16) {
               break
            }

            rx -= 6
         }
      }

      this.drawPolaris(ctx, cx, cy, if (critical) 28 else (if (active) 24 else (if (this.compact) 15 else 22)))
   }

   private fun drawConstellation(ctx: class_332) {
      val baseX: Int = this.guiLeft + this.guiW - this.pad() - (if (this.compact) 66 else 92)
      val baseY: Int = this.guiTop + (if (this.compact) 43 else 58)
      val points: java.util.List = CollectionsKt.listOf(
         arrayOf(TuplesKt.to(baseX, baseY), TuplesKt.to(baseX + 24, baseY + 12), TuplesKt.to(baseX + 49, baseY - 4), TuplesKt.to(baseX + 78, baseY + 20))
      )
      val var12: Boolean = this.scenarioActive()
      val critical: Boolean = this.scenarioCritical()
      val lineColor: Int = this.color(55, 174, 230, if (critical) 230 else (if (var12) 170 else 105))
      var i: Int = 0

      for (var9 in CollectionsKt.getLastIndex(points)..i) {
         this.drawLine(
            ctx,
            ((points.get(i) as Pair).getFirst() as java.lang.Number).intValue(),
            ((points.get(i) as Pair).getSecond() as java.lang.Number).intValue(),
            ((points.get(i + 1) as Pair).getFirst() as java.lang.Number).intValue(),
            ((points.get(i + 1) as Pair).getSecond() as java.lang.Number).intValue(),
            lineColor
         )
      }

      for (var14 in points) {
         this.drawPolaris(ctx, (var14.component1() as java.lang.Number).intValue(), (var14.component2() as java.lang.Number).intValue(), if (var12) 5 else 4)
      }

      this.drawPolaris(ctx, baseX + (if (this.compact) 92 else 110), baseY - 19, if (critical) 14 else (if (var12) 12 else (if (this.compact) 8 else 11)))
   }

   private fun drawSlotAccent(ctx: class_332, r: com.polaris.gts.PolarisScenarioScreen.Rect, selected: Boolean, hover: Boolean) {
      val color: Int = if (selected) this.green else (if (hover) this.gold else this.violetDim)
      ctx.method_25294(r.x + 1, r.y + 1, r.x + r.w - 1, r.y + 2, color)
      ctx.method_25294(r.x + 1, r.y + r.h - 2, r.x + r.w - 1, r.y + r.h - 1, color)
      if (selected) {
         this.drawPolaris(ctx, r.x + r.w - 10, r.y + 10, 4)
      }
   }

   private fun drawPolaris(ctx: class_332, x: Int, y: Int, size: Int) {
      val halo: Int = this.color(255, 226, 136, 58)
      ctx.method_25294(x - size - 3, y, x + size + 4, y + 1, halo)
      ctx.method_25294(x, y - size - 3, x + 1, y + size + 4, halo)
      ctx.method_25294(x - size, y, x + size + 1, y + 1, this.gold)
      ctx.method_25294(x, y - size, x + 1, y + size + 1, this.gold)
      var i: Int = 1
      val var7: Int = RangesKt.coerceAtLeast(size / 2, 1)
      if (1 <= var7) {
         while (true) {
            ctx.method_25294(x - i, y - i, x - i + 1, y - i + 1, this.goldSoft)
            ctx.method_25294(x + i, y - i, x + i + 1, y - i + 1, this.goldSoft)
            ctx.method_25294(x - i, y + i, x - i + 1, y + i + 1, this.goldSoft)
            ctx.method_25294(x + i, y + i, x + i + 1, y + i + 1, this.goldSoft)
            if (i == var7) {
               break
            }

            i++
         }
      }

      ctx.method_25294(x - 1, y - 1, x + 2, y + 2, this.white)
   }

   private fun drawLine(ctx: class_332, x1: Int, y1: Int, x2: Int, y2: Int, c: Int) {
      val steps: Int = RangesKt.coerceAtLeast(Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1)), 1)
      var i: Int = 0
      if (0 <= steps) {
         while (true) {
            ctx.method_25294(x1 + (x2 - x1) * i / steps, y1 + (y2 - y1) * i / steps, x1 + (x2 - x1) * i / steps + 1, y1 + (y2 - y1) * i / steps + 1, c)
            if (i == steps) {
               break
            }

            i++
         }
      }
   }

   private fun parsePrice(raw: String): Long {
      val factor: java.lang.String = StringsKt.replace$default(StringsKt.trim(raw).toString(), "_", "", false, 4, null)
      val var10000: Locale = Locale.ROOT
      val var7: java.lang.String = factor.toUpperCase(var10000)
      if (var7.length() == 0) {
         return 0L
      } else {
         val var6: Double = if (StringsKt.endsWith$default(var7, "K", false, 2, null))
            1000.0
            else
            (
               if (StringsKt.endsWith$default(var7, "M", false, 2, null))
                  1000000.0
                  else
                  (if (StringsKt.endsWith$default(var7, "B", false, 2, null)) 1.0E9 else 1.0)
            )
            val var8: java.lang.Double = StringsKt.toDoubleOrNull(if (var6 == 1.0) var7 else StringsKt.dropLast(var7, 1))
         return RangesKt.coerceAtLeast((long)((var8 ?: 0.0) * var6), 0L)
      }
   }

   private fun formatPriceDisplay(value: Long): String {
      return if (value >= 1000000000L && value % 1000000000 == 0L)
         "${value / 1000000000}B"
         else
         (
            if (value >= 1000000L && value % 1000000 == 0L)
               "${value / 1000000}M"
               else
               (if (value >= 1000L && value % 1000 == 0L) "${value / 1000}K" else java.lang.String.valueOf(value))
         )
      }

   private fun fitName(value: String, width: Int): String {
      if (this.field_22793.method_1727(value) <= width) {
         return value
      } else {
         var end: Int = value.length()

         while (end > 3 && this.field_22793.method_1727(StringsKt.take(value, end) + "...") > width) {
            end--
         }

         return "${StringsKt.take(value, end)}..."
      }
   }

   private fun text(ctx: class_332, value: String, x: Int, y: Int, c: Int) {
      ctx.method_25303(this.field_22793, value, x, y, c)
   }

   private fun textCenter(ctx: class_332, value: String, x: Int, y: Int, c: Int) {
      ctx.method_27534(this.field_22793, class_2561.method_43470(value) as class_2561, x, y, c)
   }

   private fun border(ctx: class_332, x: Int, y: Int, w: Int, h: Int, c: Int) {
      ctx.method_25294(x, y, x + w, y + 1, c)
      ctx.method_25294(x, y + h - 1, x + w, y + h, c)
      ctx.method_25294(x, y, x + 1, y + h, c)
      ctx.method_25294(x + w - 1, y, x + w, y + h, c)
   }

   private fun color(r: Int, g: Int, b: Int, a: Int = 255): Int {
      return RangesKt.coerceIn(a, 0, 255) shl 24 or RangesKt.coerceIn(r, 0, 255) shl 16 or RangesKt.coerceIn(g, 0, 255) shl 8 or RangesKt.coerceIn(b, 0, 255)
   }

   @JvmStatic
   fun `drawHelp$line`(`this$0`: PolarisScenarioScreen, `$ctx`: class_332, rect: PolarisScenarioScreen.Rect, y: IntRef, value: java.lang.String, c: Int) {
      `this$0`.text(`$ctx`, value, rect.x + 12, y.element, c)
      y.element = y.element + (if (`this$0`.compact) 11 else 13)
   }

   @JvmStatic
   fun `drawHelp$title`(y: IntRef, `this$0`: PolarisScenarioScreen, `$ctx`: class_332, rect: PolarisScenarioScreen.Rect, value: java.lang.String) {
      y.element += 4
      `this$0`.text(`$ctx`, value, rect.x + 12, y.element, `this$0`.gold)
      y.element = y.element + (if (`this$0`.compact) 12 else 15)
   }

   @JvmStatic
   fun {
      val var10000: class_2960 = class_2960.method_60655("polaris", "textures/gui/sky.png")
      SKY_TEXTURE = var10000
      val var10: Byte = 24
      val var1: ArrayList = ArrayList(24)

      repeat(var10) { var2 ->
         val var6: Locale = Locale.ROOT
         val var11: Array<Any> = arrayOf(var2)
         val var10001: java.lang.String = java.lang.String.format(var6, "textures/gui/cosmog/cosmog_%02d.png", Arrays.copyOf(var11, var11.length))
         var1.add(class_2960.method_60655("polaris", var10001))
      }

      COSMOG_FRAMES = var1
      scheduler = Executors.newSingleThreadScheduledExecutor({ runnable: Runnable ->
         val var1: Thread = Thread(runnable, "PolarisScenarioCountdown")
         var1.setDaemon(true)
         var1
      })
   }

   public companion object {
      private final val SKY_TEXTURE: class_2960
      private final val TIME_PRESETS: List<Int>
      private const val COSMOG_W: Int = 87
      private const val COSMOG_H: Int = 57
      private final val COSMOG_FRAMES: List<class_2960>
      private final val scheduler: ScheduledExecutorService
   }

   private data class PokeInfo(raw: Any,
      speciesName: String,
      level: Int,
      shiny: Boolean,
      tradeable: Boolean,
      resourceId: class_2960?,
      aspects: Set<String>,
      uuid: String
   ) {
      public final val raw: Any
      public final val speciesName: String
      public final val level: Int
      public final val shiny: Boolean
      public final val tradeable: Boolean
      public final val resourceId: class_2960?
      public final val aspects: Set<String>
      public final val uuid: String

      init {
         this.raw = raw
         this.speciesName = speciesName
         this.level = level
         this.shiny = shiny
         this.tradeable = tradeable
         this.resourceId = resourceId
         this.aspects = aspects
         this.uuid = uuid
      }

      public operator fun component1(): Any {
         return this.raw
      }

      public operator fun component2(): String {
         return this.speciesName
      }

      public operator fun component3(): Int {
         return this.level
      }

      public operator fun component4(): Boolean {
         return this.shiny
      }

      public operator fun component5(): Boolean {
         return this.tradeable
      }

      public operator fun component6(): class_2960? {
         return this.resourceId
      }

      public operator fun component7(): Set<String> {
         return this.aspects
      }

      public operator fun component8(): String {
         return this.uuid
      }

      public fun copy(
         raw: Any = this.raw,
         speciesName: String = this.speciesName,
         level: Int = this.level,
         shiny: Boolean = this.shiny,
         tradeable: Boolean = this.tradeable,
         resourceId: class_2960? = this.resourceId,
         aspects: Set<String> = this.aspects,
         uuid: String = this.uuid
      ): com.polaris.gts.PolarisScenarioScreen.PokeInfo {
         return PolarisScenarioScreen.PokeInfo(raw, speciesName, level, shiny, tradeable, resourceId, aspects, uuid)
      }

      public override fun toString(): String {
         return "PokeInfo(raw=${this.raw}, speciesName=${this.speciesName}, level=${this.level}, shiny=${this.shiny}, tradeable=${this.tradeable}, resourceId=${this.resourceId}, aspects=${this.aspects}, uuid=${this.uuid})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (
                                    (
                                             ((this.raw.hashCode() * 31 + this.speciesName.hashCode()) * 31 + Integer.hashCode(this.level)) * 31
                                                + java.lang.Boolean.hashCode(this.shiny)
                                          )
                                          * 31
                                       + java.lang.Boolean.hashCode(this.tradeable)
                                 )
                                 * 31
                              + (if (this.resourceId == null) 0 else this.resourceId.hashCode())
                        )
                        * 31
                     + this.aspects.hashCode()
               )
               * 31
            + this.uuid.hashCode()
         }

      public override operator fun equals(other: Any?): Boolean {
         label64@
         if (this === other) {
            return true
         } else {
            return other is PolarisScenarioScreen.PokeInfo
               && this.raw == (other as PolarisScenarioScreen.PokeInfo).raw
               && this.speciesName == (other as PolarisScenarioScreen.PokeInfo).speciesName
               && this.level == (other as PolarisScenarioScreen.PokeInfo).level
               && this.shiny == (other as PolarisScenarioScreen.PokeInfo).shiny
               && this.tradeable == (other as PolarisScenarioScreen.PokeInfo).tradeable
               && this.resourceId == (other as PolarisScenarioScreen.PokeInfo).resourceId
               && this.aspects == (other as PolarisScenarioScreen.PokeInfo).aspects
               && this.uuid == (other as PolarisScenarioScreen.PokeInfo).uuid
            }
      }
   }

   private data class Rect(x: Int, y: Int, w: Int, h: Int) {
      public final val x: Int
      public final val y: Int
      public final val w: Int
      public final val h: Int

      init {
         this.x = x
         this.y = y
         this.w = w
         this.h = h
      }

      public fun contains(mx: Int, my: Int): Boolean {
         return mx >= this.x && mx <= this.x + this.w && my >= this.y && my <= this.y + this.h
      }

      public operator fun component1(): Int {
         return this.x
      }

      public operator fun component2(): Int {
         return this.y
      }

      public operator fun component3(): Int {
         return this.w
      }

      public operator fun component4(): Int {
         return this.h
      }

      public fun copy(x: Int = this.x, y: Int = this.y, w: Int = this.w, h: Int = this.h): com.polaris.gts.PolarisScenarioScreen.Rect {
         return PolarisScenarioScreen.Rect(x, y, w, h)
      }

      public override fun toString(): String {
         return "Rect(x=${this.x}, y=${this.y}, w=${this.w}, h=${this.h})"
      }

      public override fun hashCode(): Int {
         return ((Integer.hashCode(this.x) * 31 + Integer.hashCode(this.y)) * 31 + Integer.hashCode(this.w)) * 31 + Integer.hashCode(this.h)
      }

      public override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is PolarisScenarioScreen.Rect
               && this.x == (other as PolarisScenarioScreen.Rect).x
               && this.y == (other as PolarisScenarioScreen.Rect).y
               && this.w == (other as PolarisScenarioScreen.Rect).w
               && this.h == (other as PolarisScenarioScreen.Rect).h
            }
      }
   }

   private data class Star(x: Int, y: Int, size: Int, alpha: Int, phase: Float) {
      public final val x: Int
      public final val y: Int
      public final val size: Int
      public final val alpha: Int
      public final val phase: Float

      init {
         this.x = x
         this.y = y
         this.size = size
         this.alpha = alpha
         this.phase = phase
      }

      public operator fun component1(): Int {
         return this.x
      }

      public operator fun component2(): Int {
         return this.y
      }

      public operator fun component3(): Int {
         return this.size
      }

      public operator fun component4(): Int {
         return this.alpha
      }

      public operator fun component5(): Float {
         return this.phase
      }

      public fun copy(x: Int = this.x, y: Int = this.y, size: Int = this.size, alpha: Int = this.alpha, phase: Float = this.phase): com.polaris.gts.PolarisScenarioScreen.Star {
         return PolarisScenarioScreen.Star(x, y, size, alpha, phase)
      }

      public override fun toString(): String {
         return "Star(x=${this.x}, y=${this.y}, size=${this.size}, alpha=${this.alpha}, phase=${this.phase})"
      }

      public override fun hashCode(): Int {
         return (((Integer.hashCode(this.x) * 31 + Integer.hashCode(this.y)) * 31 + Integer.hashCode(this.size)) * 31 + Integer.hashCode(this.alpha)) * 31
            + java.lang.Float.hashCode(this.phase)
         }

      public override operator fun equals(other: Any?): Boolean {
         label46@
         if (this === other) {
            return true
         } else {
            return other is PolarisScenarioScreen.Star
               && this.x == (other as PolarisScenarioScreen.Star).x
               && this.y == (other as PolarisScenarioScreen.Star).y
               && this.size == (other as PolarisScenarioScreen.Star).size
               && this.alpha == (other as PolarisScenarioScreen.Star).alpha
               && java.lang.Float.compare(this.phase, (other as PolarisScenarioScreen.Star).phase) == 0
            }
      }
   }

   private enum class View {
      SCENARIO,
      HELP;

      @JvmStatic
      fun getEntries(): EnumEntries<PolarisScenarioScreen.View> {
         $ENTRIES
      }
   }
}
