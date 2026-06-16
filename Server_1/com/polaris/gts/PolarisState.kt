package com.polaris.gts

import com.tlean07.gts.common.PokemonSummary
import org.jetbrains.annotations.NotNull

public object PolarisState {
   @JvmField
   public final var blockGtsUi: Boolean
      private set

   @JvmField
   public final var blockShopUi: Boolean
      private set

   @JvmField
   public final var blockClanUi: Boolean
      private set

   @JvmField
   public final var blockBfUi: Boolean
      private set

   @JvmField
   @NotNull
   public final var latestGtsMyListings: List<PokemonSummary> = CollectionsKt.emptyList()
      private set

   @JvmField
   public final var latestGtsSnapshotAt: Long
      private set

   @JvmField
   public final var scenarioActiveUntil: Long
      private set

   @JvmField
   public final var scenarioCriticalUntil: Long
      private set

   @JvmField
   public final var scenarioFlashUntil: Long
      private set

   @JvmField
   public final var scenarioCountdownEndsAt: Long
      private set

   @JvmField
   public final var forceGtsBackpackUntil: Long
      private set

   @JvmField
   public final var pendingGtsBackpackOpen: Boolean
      private set

   @JvmField
   @NotNull
   public final var pendingGtsBackpackAllListings: List<PokemonSummary> = CollectionsKt.emptyList()
      private set

   @JvmField
   @NotNull
   public final var pendingGtsBackpackMyListings: List<PokemonSummary> = CollectionsKt.emptyList()
      private set

   @JvmField
   public final var lastScenarioSlot: Int = -1
      private set

   @JvmField
   @NotNull
   public final var lastScenarioPriceText: String = "100M"
      private set

   @JvmField
   @NotNull
   public final var lastScenarioDelayText: String = "10"
      private set
}
