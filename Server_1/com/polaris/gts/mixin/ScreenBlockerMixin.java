package com.polaris.gts.mixin;

import com.polaris.gts.PolarisState;
import com.tlean07.gts.client.GtsAuctionHouseScreen;
import com.tlean07.gts.common.PokemonSummary;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import net.minecraft.class_310;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_310.class)
public class ScreenBlockerMixin {
   @Inject(method = "method_1507", at = @At("HEAD"), cancellable = true)
   private void polaris$onSetScreen(class_437 screen, CallbackInfo ci) {
      if (screen != null) {
         String className = screen.getClass().getName();
         if (screen instanceof GtsAuctionHouseScreen && System.currentTimeMillis() < PolarisState.forceGtsBackpackUntil) {
            GtsAuctionHouseScreen gtsScreen = (GtsAuctionHouseScreen)screen;
            List<PokemonSummary> allListings = this.polaris$readListings(gtsScreen, "allListings");
            List<PokemonSummary> myListings = this.polaris$readListings(gtsScreen, "myListings");
            if (myListings.isEmpty()) {
               myListings = PolarisState.latestGtsMyListings;
            }

            PolarisState.latestGtsMyListings = myListings;
            PolarisState.latestGtsSnapshotAt = System.currentTimeMillis();
            PolarisState.pendingGtsBackpackAllListings = allListings;
            PolarisState.pendingGtsBackpackMyListings = myListings;
            PolarisState.pendingGtsBackpackOpen = true;
            ci.cancel();
         } else if (PolarisState.blockGtsUi && className.startsWith("com.tlean07.gts.client.")) {
            ci.cancel();
         } else if (PolarisState.blockShopUi && className.startsWith("com.tlean07.shop.client.")) {
            ci.cancel();
         } else if (PolarisState.blockClanUi && className.startsWith("com.tlean07.clan.client.")) {
            ci.cancel();
         } else if (PolarisState.blockBfUi && className.startsWith("com.tlean07.battlefactory.client.")) {
            ci.cancel();
         }
      }
   }

   private List<PokemonSummary> polaris$readListings(Object screen, String fieldName) {
      try {
         Field field = screen.getClass().getDeclaredField(fieldName);
         field.setAccessible(true);
         Object value = field.get(screen);
         if (value instanceof List) {
            return (List<PokemonSummary>)value;
         }
      } catch (Throwable var5) {
      }

      return Collections.emptyList();
   }
}
