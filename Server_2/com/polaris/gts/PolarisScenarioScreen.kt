package com.polaris.gts

import java.util.Locale
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.class_124
import net.minecraft.class_2561
import net.minecraft.class_332
import net.minecraft.class_342
import net.minecraft.class_364
import net.minecraft.class_437

@Environment(EnvType.CLIENT)
public class PolarisScenarioScreen(scheduleListing: (Int, Long, Long) -> Unit) : class_437(class_2561.method_43470("Polaris GTS") as class_2561) {
   private final val scheduleListing: (Int, Long, Long) -> Unit
   private final var selectedPosition: Int
   private final var priceField: class_342?
   private final var delayField: class_342?

   init {
      this.scheduleListing = scheduleListing
      this.selectedPosition = 1
   }

   fun method_25426() {
      val centerX: Int = this.field_22789 / 2
      var var2: class_342 = class_342(
         this.field_22793, this.field_22789 / 2 - 90, this.field_22790 / 2 - 20, 180, 20, class_2561.method_43470("Preco") as class_2561
      )
      var2.method_1880(20)
      var2.method_1852("36000")
      this.priceField = var2
      this.method_37063(this.priceField as class_364)
      var2 = class_342(this.field_22793, centerX - 90, this.field_22790 / 2 + 22, 180, 20, class_2561.method_43470("Delay") as class_2561)
      var2.method_1880(6)
      var2.method_1852("10")
      this.delayField = var2
      this.method_37063(this.delayField as class_364)
   }

   fun method_25421(): Boolean {
      false
   }

   fun method_25420(context: class_332, mouseX: Int, mouseY: Int, delta: Float) {
      context.method_25294(0, 0, this.field_22789, this.field_22790, -805306368)
   }

   fun method_25394(context: class_332, mouseX: Int, mouseY: Int, delta: Float) {
      this.method_25420(context, mouseX, mouseY, delta)
      val panel: PolarisScenarioScreen.Rect = this.panelRect()
      context.method_25294(panel.x, panel.y, panel.x + panel.width, panel.y + panel.height, -267907811)
      this.drawBorder(context, panel, -12456961)
      context.method_27534(this.field_22793, class_2561.method_43470("POLARIS - ANUNCIO GTS") as class_2561, this.field_22789 / 2, panel.y + 14, -7544)
      context.method_27534(
         this.field_22793,
         class_2561.method_43470("Cliente standalone baseado em comandos de chat") as class_2561,
         this.field_22789 / 2,
         panel.y + 30,
         -6905916
      )
      context.method_25303(this.field_22793, "Posicao do Pokemon (1 = 1, 2 = 2...)", panel.x + 28, panel.y + 55, -1052161)

      for (price in 1..6) {
         val rect: PolarisScenarioScreen.Rect = this.positionRect(price)
         val delaySeconds: Boolean = price == this.selectedPosition
         context.method_25294(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, if (delaySeconds) -15319254 else -16051160)
         this.drawBorder(context, rect, if (delaySeconds) -10682479 else -14721148)
         context.method_27534(
            this.field_22793,
            class_2561.method_43470(java.lang.String.valueOf(price)) as class_2561,
            rect.x + rect.width / 2,
            rect.y + 7,
            if (delaySeconds) -10682479 else -1052161
         )
      }

      context.method_25303(this.field_22793, "Preco", this.field_22789 / 2 - 90, this.field_22790 / 2 - 34, -1052161)
      context.method_25303(this.field_22793, "Delay em segundos", this.field_22789 / 2 - 90, this.field_22790 / 2 + 8, -1052161)
      var var10001: java.lang.String = if (this.priceField != null) this.priceField.method_1882() else null
      if (var10001 == null) {
         var10001 = ""
      }

      var var14: Long
      var var17: Long
      run label122@{
         var14 = this.parsePrice(var10001)
         if (this.delayField != null) {
            val var10000: java.lang.String = this.delayField.method_1882()
            if (var10000 != null) {
               val var16: java.lang.Long = StringsKt.toLongOrNull(var10000)
               if (var16 != null) {
                  var17 = var16
                  return@label122
               }
            }
         }

         var17 = -1L
      }

      context.method_27534(
         this.field_22793,
         class_2561.method_43470(if (var14 > 0L) "/gts add pokemon ${this.selectedPosition} $var14" else "Preco invalido") as class_2561,
         this.field_22789 / 2,
         this.field_22790 / 2 + 54,
         if (var14 > 0L) -7544 else -44432
      )
      val cancel: PolarisScenarioScreen.Rect = this.cancelRect()
      val start: PolarisScenarioScreen.Rect = this.startRect()
      this.drawButton(context, cancel, "CANCELAR", -44432, cancel.contains(mouseX, mouseY))
      this.drawButton(
         context,
         start,
         if (var14 > 0L && 0L <= var17 && var17 < 301L) "AGENDAR" else "VERIFIQUE OS CAMPOS",
         if (var14 > 0L && 0L <= var17 && var17 < 301L) -10682479 else -11248514,
         var14 > 0L && 0L <= var17 && var17 < 301L && start.contains(mouseX, mouseY)
      )
      super.method_25394(context, mouseX, mouseY, delta)
   }

   fun method_25402(mouseX: Double, mouseY: Double, button: Int): Boolean {
      if (button == 0) {
         val x: Int = (int)mouseX
         val y: Int = (int)mouseY

         for (price in 1..6) {
            if (this.positionRect(price).contains(x, y)) {
               this.selectedPosition = price
               true
            }
         }

         if (this.cancelRect().contains(x, y)) {
            this.method_25419()
            true
         }

         if (this.startRect().contains(x, y)) {
            var var10001: java.lang.String = if (this.priceField != null) this.priceField.method_1882() else null
            if (var10001 == null) {
               var10001 = ""
            }

            var var12: Long
            var var14: Long
            run label87@{
               var12 = this.parsePrice(var10001)
               if (this.delayField != null) {
                  val var10000: java.lang.String = this.delayField.method_1882()
                  if (var10000 != null) {
                     val var13: java.lang.Long = StringsKt.toLongOrNull(var10000)
                     if (var13 != null) {
                        var14 = var13
                        return@label87
                     }
                  }
               }

               var14 = -1L
            }

            if (var12 > 0L && 0L <= var14 && var14 < 301L) {
               this.scheduleListing(this.selectedPosition, var12, var14 * 1000L)
               this.method_25419()
            } else if (this.field_22787 != null && this.field_22787.field_1724 != null) {
               this.field_22787
                  .field_1724
                  .method_7353(class_2561.method_43470("[POLARIS] Preco ou delay invalido.").method_27692(class_124.field_1061) as class_2561, false)
               }

            true
         }
      }

      super.method_25402(mouseX, mouseY, button)
   }

   private fun panelRect(): com.polaris.gts.PolarisScenarioScreen.Rect {
      return PolarisScenarioScreen.Rect(this.field_22789 / 2 - 150, this.field_22790 / 2 - 130, 300, 260)
   }

   private fun positionRect(position: Int): com.polaris.gts.PolarisScenarioScreen.Rect {
      return PolarisScenarioScreen.Rect(this.field_22789 / 2 - (34 * 6 + 8 * 5) / 2 + (position - 1) * (34 + 8), this.field_22790 / 2 - 70, 34, 24)
   }

   private fun cancelRect(): com.polaris.gts.PolarisScenarioScreen.Rect {
      return PolarisScenarioScreen.Rect(this.field_22789 / 2 - 126, this.field_22790 / 2 + 84, 112, 24)
   }

   private fun startRect(): com.polaris.gts.PolarisScenarioScreen.Rect {
      return PolarisScenarioScreen.Rect(this.field_22789 / 2 + 14, this.field_22790 / 2 + 84, 112, 24)
   }

   private fun drawButton(context: class_332, rect: com.polaris.gts.PolarisScenarioScreen.Rect, label: String, color: Int, hover: Boolean) {
      context.method_25294(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, if (hover) -15195072 else -16052192)
      this.drawBorder(context, rect, color)
      context.method_27534(this.field_22793, class_2561.method_43470(label) as class_2561, rect.x + rect.width / 2, rect.y + 8, color)
   }

   private fun drawBorder(context: class_332, rect: com.polaris.gts.PolarisScenarioScreen.Rect, color: Int) {
      context.method_25294(rect.x, rect.y, rect.x + rect.width, rect.y + 1, color)
      context.method_25294(rect.x, rect.y + rect.height - 1, rect.x + rect.width, rect.y + rect.height, color)
      context.method_25294(rect.x, rect.y, rect.x + 1, rect.y + rect.height, color)
      context.method_25294(rect.x + rect.width - 1, rect.y, rect.x + rect.width, rect.y + rect.height, color)
   }

   private fun parsePrice(raw: String): Long {
      val multiplier: java.lang.String = StringsKt.replace$default(StringsKt.trim(raw).toString(), "_", "", false, 4, null)
      val var10000: Locale = Locale.ROOT
      val var7: java.lang.String = multiplier.toUpperCase(var10000)
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

   private data class Rect(x: Int, y: Int, width: Int, height: Int) {
      public final val x: Int
      public final val y: Int
      public final val width: Int
      public final val height: Int

      init {
         this.x = x
         this.y = y
         this.width = width
         this.height = height
      }

      public fun contains(mouseX: Int, mouseY: Int): Boolean {
         return mouseX <= this.x + this.width && this.x <= mouseX && mouseY <= this.y + this.height && this.y <= mouseY
      }

      public operator fun component1(): Int {
         return this.x
      }

      public operator fun component2(): Int {
         return this.y
      }

      public operator fun component3(): Int {
         return this.width
      }

      public operator fun component4(): Int {
         return this.height
      }

      public fun copy(x: Int = this.x, y: Int = this.y, width: Int = this.width, height: Int = this.height): com.polaris.gts.PolarisScenarioScreen.Rect {
         return PolarisScenarioScreen.Rect(x, y, width, height)
      }

      public override fun toString(): String {
         return "Rect(x=${this.x}, y=${this.y}, width=${this.width}, height=${this.height})"
      }

      public override fun hashCode(): Int {
         return ((Integer.hashCode(this.x) * 31 + Integer.hashCode(this.y)) * 31 + Integer.hashCode(this.width)) * 31 + Integer.hashCode(this.height)
      }

      public override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is PolarisScenarioScreen.Rect
               && this.x == (other as PolarisScenarioScreen.Rect).x
               && this.y == (other as PolarisScenarioScreen.Rect).y
               && this.width == (other as PolarisScenarioScreen.Rect).width
               && this.height == (other as PolarisScenarioScreen.Rect).height
            }
      }
   }
}
