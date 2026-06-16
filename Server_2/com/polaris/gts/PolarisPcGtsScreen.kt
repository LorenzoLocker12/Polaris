package com.polaris.gts

import com.cobblemon.mod.common.api.storage.pc.PCPosition
import com.cobblemon.mod.common.client.storage.ClientBox
import com.cobblemon.mod.common.client.storage.ClientPC
import com.cobblemon.mod.common.client.storage.ClientParty
import com.cobblemon.mod.common.pokemon.Pokemon
import java.util.Locale
import kotlin.jvm.internal.SourceDebugExtension
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.class_2561
import net.minecraft.class_332
import net.minecraft.class_342
import net.minecraft.class_364
import net.minecraft.class_437
import net.minecraft.class_5250

@Environment(EnvType.CLIENT)
@SourceDebugExtension(["SMAP\nPolarisPcGtsScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PolarisPcGtsScreen.kt\ncom/polaris/gts/PolarisPcGtsScreen\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,412:1\n1#2:413\n295#3,2:414\n295#3,2:416\n*S KotlinDebug\n*F\n+ 1 PolarisPcGtsScreen.kt\ncom/polaris/gts/PolarisPcGtsScreen\n*L\n337#1:414,2\n341#1:416,2\n*E\n"])
public class PolarisPcGtsScreen(pc: ClientPC,
   party: ClientParty,
   initialBox: Int,
   submit: (ClientPC, PCPosition, Pokemon, Int, Long, Long) -> Unit,
   cancelSession: () -> Unit
) : class_437(class_2561.method_43470("Polaris PC GTS") as class_2561) {
   private final val pc: ClientPC
   private final val party: ClientParty
   private final val submit: (ClientPC, PCPosition, Pokemon, Int, Long, Long) -> Unit
   private final val cancelSession: () -> Unit
   private final var selectedBox: Int
   private final var selectedPcSlot: Int?
   private final var selectedPartySlot: Int
   private final var priceField: class_342?
   private final var delayField: class_342?
   private final var submitted: Boolean

   init {
      this.pc = pc
      this.party = party
      this.submit = submit
      this.cancelSession = cancelSession
      this.selectedBox = RangesKt.coerceIn(initialBox, 0, RangesKt.coerceAtLeast(this.pc.getBoxes().size() - 1, 0))
      this.selectedPcSlot = this.firstPokemonSlot(this.selectedBox)
      this.selectedPartySlot = this.firstEmptyPartySlot()
   }

   fun method_25426() {
      val panel: PolarisPcGtsScreen.Rect = this.panelRect()
      var var2: class_342 = class_342(this.field_22793, panel.x + 22, panel.y + 232, 170, 20, class_2561.method_43470("Preco") as class_2561)
      var2.method_1880(20)
      var2.method_1852("36000")
      this.priceField = var2
      this.method_37063(this.priceField as class_364)
      var2 = class_342(this.field_22793, panel.x + 210, panel.y + 232, 105, 20, class_2561.method_43470("Delay") as class_2561)
      var2.method_1880(6)
      var2.method_1852("10")
      this.delayField = var2
      this.method_37063(this.delayField as class_364)
   }

   fun method_25421(): Boolean {
      false
   }

   fun method_25419() {
      if (!this.submitted) {
         this.cancelSession()
      }

      super.method_25419()
   }

   fun method_25420(context: class_332, mouseX: Int, mouseY: Int, delta: Float) {
      context.method_25294(0, 0, this.field_22789, this.field_22790, -805306368)
   }

   fun method_25394(context: class_332, mouseX: Int, mouseY: Int, delta: Float) {
      this.method_25420(context, mouseX, mouseY, delta)
      val panel: PolarisPcGtsScreen.Rect = this.panelRect()
      context.method_25294(panel.x, panel.y, panel.x + panel.width, panel.y + panel.height, -267907811)
      this.drawBorder(context, panel, -12456961)
      context.method_27534(this.field_22793, class_2561.method_43470("POLARIS - PC PARA GTS") as class_2561, this.field_22789 / 2, panel.y + 10, -7544)
      context.method_27534(
         this.field_22793,
         class_2561.method_43470("Selecione o Pokemon, um slot livre da party, preco e delay") as class_2561,
         this.field_22789 / 2,
         panel.y + 24,
         -6905916
      )
      this.drawBoxHeader(context, panel, mouseX, mouseY)
      this.drawPcSlots(context, panel, mouseX, mouseY)
      this.drawPartySlots(context, panel, mouseX, mouseY)
      context.method_25303(this.field_22793, "Preco", panel.x + 22, panel.y + 220, -1052161)
      context.method_25303(this.field_22793, "Delay (segundos)", panel.x + 210, panel.y + 220, -1052161)
      val selectedPokemon: Pokemon = this.selectedPokemon()
      var var10001: java.lang.String = if (this.priceField != null) this.priceField.method_1882() else null
      if (var10001 == null) {
         var10001 = ""
      }

      var price: Long
      var var19: Long
      run label137@{
         price = this.parsePrice(var10001)
         if (this.delayField != null) {
            val var10000: java.lang.String = this.delayField.method_1882()
            if (var10000 != null) {
               val var18: java.lang.Long = StringsKt.toLongOrNull(var10000)
               if (var18 != null) {
                  var19 = var18
                  return@label137
               }
            }
         }

         var19 = -1L
      }

      val partyAvailable: Boolean = 0 <= this.selectedPartySlot && this.selectedPartySlot < 6 && this.party.get(this.selectedPartySlot) == null
      val var16: Boolean = selectedPokemon != null && selectedPokemon.getTradeable() && partyAvailable && price > 0L && 0L <= var19 && var19 < 301L
      context.method_27534(
         this.field_22793,
         class_2561.method_43470(
            if (selectedPokemon == null)
               "Selecione um Pokemon do PC"
               else
               (
                  if (!selectedPokemon.getTradeable())
                     "${this.pokemonName(selectedPokemon)} nao pode ser trocado"
                     else
                     (
                        if (0 > this.selectedPartySlot || this.selectedPartySlot >= 6)
                           "A party esta cheia"
                           else
                           "PC ${this.selectedBox + 1}/${(if (this.selectedPcSlot != null) this.selectedPcSlot else 0) + 1} -> Party ${this.selectedPartySlot
                              + 1} -> /gts add pokemon ${this.selectedPartySlot + 1} $price"
                     )
               )
         ) as class_2561,
         this.field_22789 / 2,
         panel.y + 259,
         if (var16) -7544 else -36728
      )
      val var17: PolarisPcGtsScreen.Rect = this.cancelRect(panel)
      val start: PolarisPcGtsScreen.Rect = this.startRect(panel)
      this.drawButton(context, var17, "CANCELAR", -44432, var17.contains(mouseX, mouseY))
      this.drawButton(
         context, start, if (var16) "AGENDAR FLUXO" else "VERIFIQUE OS CAMPOS", if (var16) -10682479 else -11248514, var16 && start.contains(mouseX, mouseY)
      )
      context.method_27534(
         this.field_22793, class_2561.method_43470("Durante o delay, o Pokemon permanece no PC.") as class_2561, this.field_22789 / 2, panel.y + 296, -6905916
      )
      super.method_25394(context, mouseX, mouseY, delta)
   }

   fun method_25402(mouseX: Double, mouseY: Double, button: Int): Boolean {
      if (button != 0) {
         super.method_25402(mouseX, mouseY, button)
      } else {
         val x: Int = (int)mouseX
         val y: Int = (int)mouseY
         val panel: PolarisPcGtsScreen.Rect = this.panelRect()
         if (this.previousBoxRect(panel).contains(x, y)) {
            this.changeBox(-1)
            true
         } else if (this.nextBoxRect(panel).contains(x, y)) {
            this.changeBox(1)
            true
         } else {
            repeat(29) { slot ->
               if (this.pcSlotRect(panel, slot).contains(x, y) && this.pokemonAt(this.selectedBox, slot) != null) {
                  this.selectedPcSlot = slot
                  true
               }
            }

            repeat(5) { var16 ->
               if (this.partySlotRect(panel, var16).contains(x, y) && this.party.get(var16) == null) {
                  this.selectedPartySlot = var16
                  true
               }
            }

            if (this.cancelRect(panel).contains(x, y)) {
               this.method_25419()
               true
            } else if (this.startRect(panel).contains(x, y)) {
               val var17: Int = this.selectedPcSlot
               val pokemon: Pokemon = this.selectedPokemon()
               var var10001: java.lang.String = if (this.priceField != null) this.priceField.method_1882() else null
               if (var10001 == null) {
                  var10001 = ""
               }

               var price: Long
               var var19: Long
               run label128@{
                  price = this.parsePrice(var10001)
                  if (this.delayField != null) {
                     val var10000: java.lang.String = this.delayField.method_1882()
                     if (var10000 != null) {
                        val var18: java.lang.Long = StringsKt.toLongOrNull(var10000)
                        if (var18 != null) {
                           var19 = var18
                           return@label128
                        }
                     }
                  }

                  var19 = -1L
               }

               if (var17 != null
                  && pokemon != null
                  && pokemon.getTradeable()
                  && 0 <= this.selectedPartySlot
                  && this.selectedPartySlot < 6
                  && this.party.get(this.selectedPartySlot) == null
                  && price > 0L
                  && 0L <= var19
                  && var19 < 301L) {
                  this.submitted = true
                  this.submit(this.pc, PCPosition(this.selectedBox, var17), pokemon, this.selectedPartySlot, price, var19 * 1000L)
                  super.method_25419()
               }

               true
            } else {
               super.method_25402(mouseX, mouseY, button)
            }
         }
      }
   }

   private fun drawBoxHeader(context: class_332, panel: com.polaris.gts.PolarisPcGtsScreen.Rect, mouseX: Int, mouseY: Int) {
      var var13: java.lang.String
      run label34@{
         val previous: PolarisPcGtsScreen.Rect = this.previousBoxRect(panel)
         val next: PolarisPcGtsScreen.Rect = this.nextBoxRect(panel)
         this.drawButton(context, previous, "<", -12456961, previous.contains(mouseX, mouseY))
         this.drawButton(context, next, ">", -12456961, next.contains(mouseX, mouseY))
         val var10000: ClientBox = CollectionsKt.getOrNull(this.pc.getBoxes(), this.selectedBox) as ClientBox
         if (var10000 != null) {
            val var11: class_5250 = var10000.getName()
            if (var11 != null) {
               var13 = var11.getString()
               if (var13 != null) {
                  var13 = if (!StringsKt.isBlank(var13)) var13 else null
                  if (var13 != null) {
                     return@label34
                  }
               }
            }
         }

         var13 = "Caixa ${this.selectedBox + 1}"
      }

      context.method_27534(
         this.field_22793,
         class_2561.method_43470("$var13 (${this.selectedBox + 1}/${this.pc.getBoxes().size()})") as class_2561,
         this.field_22789 / 2,
         panel.y + 45,
         -1052161
      )
   }

   private fun drawPcSlots(context: class_332, panel: com.polaris.gts.PolarisPcGtsScreen.Rect, mouseX: Int, mouseY: Int) {
      repeat(29) { slot ->
         val rect: PolarisPcGtsScreen.Rect = this.pcSlotRect(panel, slot)
         val pokemon: Pokemon = this.pokemonAt(this.selectedBox, slot)
         val selected: Boolean = this.selectedPcSlot != null && this.selectedPcSlot == slot
         val fill: Int = if (selected) -15319254 else (if (rect.contains(mouseX, mouseY) && pokemon != null) -15195072 else -16051160)
         val border: Int = if (selected) -10682479 else (if (pokemon != null) -14721148 else -14143414)
         context.method_25294(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, fill)
         this.drawBorder(context, rect, border)
         if (pokemon == null) {
            context.method_27534(this.field_22793, class_2561.method_43470("${slot + 1} -") as class_2561, rect.x + rect.width / 2, rect.y + 7, -10919557)
         } else {
            context.method_25303(
               this.field_22793,
               "${slot + 1}. ${this.shorten(this.pokemonName(pokemon), 10)}${if (pokemon.getShiny()) "*" else ""}",
               rect.x + 3,
               rect.y + 3,
               if (pokemon.getTradeable()) -1052161 else -36728
            )
            context.method_25303(this.field_22793, "Lv.${pokemon.getLevel()}", rect.x + 3, rect.y + 13, -6905916)
         }
      }
   }

   private fun drawPartySlots(context: class_332, panel: com.polaris.gts.PolarisPcGtsScreen.Rect, mouseX: Int, mouseY: Int) {
      context.method_25303(this.field_22793, "Destino na party", panel.x + 22, panel.y + 183, -1052161)

      repeat(5) { slot ->
         val rect: PolarisPcGtsScreen.Rect = this.partySlotRect(panel, slot)
         val pokemon: Pokemon = this.party.get(slot)
         val fill: Int = if (this.selectedPartySlot == slot) -15319254 else (if (rect.contains(mouseX, mouseY) && pokemon == null) -15195072 else -16051160)
         val border: Int = if (this.selectedPartySlot == slot) -10682479 else (if (pokemon == null) -14721148 else -11248514)
         context.method_25294(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, fill)
         this.drawBorder(context, rect, border)
         context.method_27534(
            this.field_22793,
            class_2561.method_43470(if (pokemon == null) "${slot + 1}: Livre" else "${slot + 1}: ${this.shorten(this.pokemonName(pokemon), 7)}") as class_2561,
            rect.x + rect.width / 2,
            rect.y + 7,
            if (pokemon == null) -1052161 else -8945763
         )
      }
   }

   private fun changeBox(delta: Int) {
      if (!this.pc.getBoxes().isEmpty()) {
         this.selectedBox = (this.selectedBox + delta + this.pc.getBoxes().size()) % this.pc.getBoxes().size()
         this.selectedPcSlot = this.firstPokemonSlot(this.selectedBox)
      }
   }

   private fun selectedPokemon(): Pokemon? {
      return if (this.selectedPcSlot != null) this.pokemonAt(this.selectedBox, this.selectedPcSlot) else null
   }

   private fun firstPokemonSlot(box: Int): Int? {
      val var4: java.util.Iterator = (RangesKt.until(0, 30) as java.lang.Iterable).iterator()

      var var10000: Any
      while (true) {
         if (var4.hasNext()) {
            val `element$iv`: Any = var4.next()
            if (this.pokemonAt(box, (`element$iv` as java.lang.Number).intValue()) == null) {
               continue
            }

            var10000 = `element$iv`
            break
         }

         var10000 = null
         break
      }

      return var10000 as Int
   }

   private fun firstEmptyPartySlot(): Int {
      val var3: java.util.Iterator = (RangesKt.until(0, 6) as java.lang.Iterable).iterator()

      var var10000: Any
      while (true) {
         if (var3.hasNext()) {
            val `element$iv`: Any = var3.next()
            if (this.party.get((`element$iv` as java.lang.Number).intValue()) != null) {
               continue
            }

            var10000 = `element$iv`
            break
         }

         var10000 = null
         break
      }

      return if (var10000 as Int != null) var10000 as Int else -1
   }

   private fun pokemonAt(box: Int, slot: Int): Pokemon? {
      val var10000: ClientBox = CollectionsKt.getOrNull(this.pc.getBoxes(), box) as ClientBox
      if (var10000 != null) {
         val var3: java.util.List = var10000.getSlots()
         if (var3 != null) {
            return CollectionsKt.getOrNull(var3, slot) as Pokemon
         }
      }

      return null
   }

   private fun pokemonName(pokemon: Pokemon): String {
      val var10000: java.lang.String = pokemon.getDisplayName(false).getString()
      return var10000
   }

   private fun shorten(value: String, maxLength: Int): String {
      return if (value.length() <= maxLength) value else "${StringsKt.take(value, maxLength - 1)}."
   }

   private fun panelRect(): com.polaris.gts.PolarisPcGtsScreen.Rect {
      return PolarisPcGtsScreen.Rect(this.field_22789 / 2 - 225, this.field_22790 / 2 - 165, 450, 330)
   }

   private fun previousBoxRect(panel: com.polaris.gts.PolarisPcGtsScreen.Rect): com.polaris.gts.PolarisPcGtsScreen.Rect {
      return PolarisPcGtsScreen.Rect(panel.x + 22, panel.y + 39, 24, 20)
   }

   private fun nextBoxRect(panel: com.polaris.gts.PolarisPcGtsScreen.Rect): com.polaris.gts.PolarisPcGtsScreen.Rect {
      return PolarisPcGtsScreen.Rect(panel.x + panel.width - 46, panel.y + 39, 24, 20)
   }

   private fun pcSlotRect(panel: com.polaris.gts.PolarisPcGtsScreen.Rect, slot: Int): com.polaris.gts.PolarisPcGtsScreen.Rect {
      return PolarisPcGtsScreen.Rect(panel.x + 22 + slot % 6 * 68, panel.y + 66 + slot / 6 * 22, 64, 20)
   }

   private fun partySlotRect(panel: com.polaris.gts.PolarisPcGtsScreen.Rect, slot: Int): com.polaris.gts.PolarisPcGtsScreen.Rect {
      return PolarisPcGtsScreen.Rect(panel.x + 22 + slot * 68, panel.y + 196, 64, 20)
   }

   private fun cancelRect(panel: com.polaris.gts.PolarisPcGtsScreen.Rect): com.polaris.gts.PolarisPcGtsScreen.Rect {
      return PolarisPcGtsScreen.Rect(panel.x + 72, panel.y + 276, 128, 24)
   }

   private fun startRect(panel: com.polaris.gts.PolarisPcGtsScreen.Rect): com.polaris.gts.PolarisPcGtsScreen.Rect {
      return PolarisPcGtsScreen.Rect(panel.x + 218, panel.y + 276, 160, 24)
   }

   private fun drawButton(context: class_332, rect: com.polaris.gts.PolarisPcGtsScreen.Rect, label: String, color: Int, hover: Boolean) {
      context.method_25294(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, if (hover) -15195072 else -16052192)
      this.drawBorder(context, rect, color)
      context.method_27534(this.field_22793, class_2561.method_43470(label) as class_2561, rect.x + rect.width / 2, rect.y + 7, color)
   }

   private fun drawBorder(context: class_332, rect: com.polaris.gts.PolarisPcGtsScreen.Rect, color: Int) {
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
         return mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height
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

      public fun copy(x: Int = this.x, y: Int = this.y, width: Int = this.width, height: Int = this.height): com.polaris.gts.PolarisPcGtsScreen.Rect {
         return PolarisPcGtsScreen.Rect(x, y, width, height)
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
            return other is PolarisPcGtsScreen.Rect
               && this.x == (other as PolarisPcGtsScreen.Rect).x
               && this.y == (other as PolarisPcGtsScreen.Rect).y
               && this.width == (other as PolarisPcGtsScreen.Rect).width
               && this.height == (other as PolarisPcGtsScreen.Rect).height
            }
      }
   }
}
