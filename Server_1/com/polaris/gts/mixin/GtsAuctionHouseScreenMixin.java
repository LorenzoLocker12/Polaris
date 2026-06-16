package com.polaris.gts.mixin;

import com.polaris.gts.PolarisState;
import com.tlean07.gts.client.GtsAuctionHouseScreen;
import com.tlean07.gts.common.PokemonSummary;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GtsAuctionHouseScreen.class, remap = false)
public class GtsAuctionHouseScreenMixin {
   @Inject(method = "<init>(Ljava/util/List;Ljava/util/List;)V", at = @At("RETURN"))
   private void polaris$captureListings(List<PokemonSummary> allListings, List<PokemonSummary> myListings, CallbackInfo ci) {
      PolarisState.latestGtsMyListings = myListings;
      PolarisState.latestGtsSnapshotAt = System.currentTimeMillis();
   }
}
