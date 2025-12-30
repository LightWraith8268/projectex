# ProjectEX Reforged - 1.21.1+ NeoForge Port

**A modern NeoForge port of ProjectEX, extending ProjectE with low-lag EMC generation, improved transmutation tables, and massive EMC storage solutions.**

**Porting ProjectEX from Forge 1.12.2/1.16.5 to NeoForge 1.21.1+**

## Project Overview

This repository contains the source code and resources for porting **ProjectEX** to modern Minecraft versions (1.21.1+) using the NeoForge modding platform.

ProjectEX is an unofficial extension mod for ProjectE that provides:
- Single block, low lag EMC trees (Power Flowers & Collectors)
- An improved Transmutation tablet
- EMC linking systems for external mods (AE2, Refined Storage)
- EMC Relays
- Magnum Stars & Colossal Stars
- Knowledge Sharing Book
- Craftable Tome of Knowledge
- The Final Star
- And more EMC-based utilities

## Repository Structure

```
projectex_reforged/
├── 1.12.2 source/          # Original Forge 1.18.1 source code (reference)
├── 1.16.5/                 # ProjectEX 1.16.5 JAR (reference)
│   └── projectex-1605.2.0-build.3.jar
├── integrations/           # Integration mod JARs for 1.21.1+
│   ├── ProjectE-1.21.1-PE1.1.0.jar
│   └── refinedstorage-neoforge-2.0.0.jar
└── projectex_reforged/     # 1.21.1+ NeoForge port (IN PROGRESS)
    ├── src/main/java/      # Ported source code
    ├── src/main/resources/ # Resources and metadata
    ├── build.gradle        # NeoForge build configuration
    └── LICENSE.txt         # LGPL-3.0 license
```

## Porting Goals

### Target Platform
- **Minecraft Version:** 1.21.1+
- **Mod Loader:** NeoForge (latest stable)
- **Java Version:** 21+

### Core Dependencies
- **ProjectE** 1.21.1-PE1.1.0 (EMC system integration)
- **Refined Storage** NeoForge 2.0.0 (storage integration)

## Porting Checklist

### Phase 1: Setup & Infrastructure ✅ COMPLETED
- [x] Set up NeoForge 1.21.1+ development environment
- [x] Create mod project structure
- [x] Configure build.gradle with dependencies
- [x] Set up mods.toml metadata
- [x] Configure IDE for NeoForge development

### Phase 2: Core Systems Migration 🔄 IN PROGRESS
- [x] Port Matter enum system (15 tiers)
- [x] Port Star enum system (6 variants)
- [x] Set up DeferredRegister pattern
- [ ] Port EMC calculation and storage systems
- [x] Migrate power flower mechanics (block class complete)
- [x] Port collector mechanics (block class complete)
- [x] Update relay systems (block class complete)
- [ ] Migrate link systems (Personal, Refined, Energy, Compressed)
- [ ] Implement block entities with capabilities

### Phase 3: Items & Blocks 🔄 IN PROGRESS
- [x] Port all matter items (colored matter system)
- [x] Port star items (Magnum, Colossal, Final)
- [x] Port collector blocks (all tiers)
- [x] Port power flower blocks (all tiers)
- [x] Port relay blocks (all tiers)
- [ ] Port link blocks (Energy, Personal, Refined, Compressed)
- [ ] Port transmutation tables (Stone, Alchemy)
- [ ] Port utility items (Arcane Tablet, Knowledge Sharing Book, Tome of Knowledge)

### Phase 4: Integration
- [ ] ProjectE integration (EMC hooks)
- [ ] Refined Storage integration (storage linking)
- [ ] Update to new NeoForge capabilities system
- [ ] Implement modern data-driven recipes

### Phase 5: Data Generation ✅ COMPLETED
- [x] Migrate blockstates to new format
- [x] Migrate item/block models
- [x] Update recipes to 1.21.1 format
- [x] Generate lang files
- [x] Copy textures from reference versions
- [ ] Update advancements (optional)

### Phase 6: Testing & Polish
- [ ] Test all EMC calculations
- [ ] Verify power flower/collector rates
- [ ] Test integration with ProjectE
- [ ] Test Refined Storage linking
- [ ] Balance adjustments
- [ ] Bug fixes

## Key Migration Challenges

### API Changes (1.12.2 → 1.21.1)
1. **Registry System:** Complete overhaul to DeferredRegister
2. **Capabilities:** Migrated to NeoForge's AttachCapabilitiesEvent
3. **NBT Handling:** CompoundTag API changes
4. **Networking:** New packet system using SimpleChannel
5. **Rendering:** Client-side rendering completely rewritten
6. **Data Generation:** Now required for most JSON files
7. **Tile Entities → Block Entities:** Naming and API changes
8. **Containers → Menus:** Inventory system refactor

### ProjectE Integration Updates
- EMC API access patterns changed
- Knowledge system integration updates
- Transmutation interface updates

### Refined Storage Integration
- Storage API completely new in 2.0.0
- Network linking mechanics updated
- Capability system changes

## Development Setup

### Prerequisites
- Java Development Kit (JDK) 21+
- IntelliJ IDEA or Eclipse
- Git

### Initial Setup
```bash
# Clone the repository
git clone https://github.com/LightWraith8268/projectex_reforged.git
cd projectex_reforged/projectex_reforged

# Setup workspace
./gradlew genIntellijRuns  # For IntelliJ
# or
./gradlew genEclipseRuns   # For Eclipse
```

## Building

**IMPORTANT:** Requires Java 21 (will not build with Java 25+)

```bash
# Build the mod
cd projectex_reforged
./gradlew build

# Output will be in build/libs/
```

## Reference Materials

### Original Mod Info
- **1.12.2 Source:** Based on FTB ProjectEX (Forge)
- **Original Repository:** https://github.com/FTBTeam/ProjectEX
- **CurseForge:** https://www.curseforge.com/minecraft/mc-mods/projectex

### Documentation
- [NeoForge Documentation](https://docs.neoforged.net/)
- [ProjectE Wiki](https://github.com/sinkillerj/ProjectE/wiki)
- [Refined Storage Docs](https://refinedmods.com/refined-storage/)

## Contributing

This is a port project. Major features should remain consistent with the original ProjectEX design philosophy while adapting to modern Minecraft/NeoForge standards.

## Current Port Status

**Version:** 1.7.3
**Status:** Feature Complete - In-Game Testing Phase
**Last Updated:** 2025-12-30

### ✅ Completed Implementation (All Phases)

**Phase 1: Core Infrastructure**
- NeoForge 21.1.181 build system
- Mod metadata with ProjectE 1.21.1-PE1.1.0 dependency
- DeferredRegister pattern for all registries
- Creative tab with all items
- LGPL-3.0 licensing with proper attribution

**Phase 2-3: Block & Item Systems**
- 54 blocks implemented (16 Collector/Relay/Power Flower tiers + 4 Links + 2 Tables)
- 30 items implemented (14 Matter items, 12 Stars, 4 special items)
- All block entities with tick-based logic
- ProjectE IEmcStorage capability integration

**Phase 4: Alchemy Table - Smart Crafting**
- Inventory-first ingredient consumption
- EMC transmutation for missing items
- Bulk crafting with EMC budget calculation
- Auto-learn system with knowledge sync
- Klein Star auto-charging (1-second tick rate)

**Phase 5: Custom GUI & Rendering**
- Purple/gold shimmer on transmutable items
- EMC cost overlays (formatted: K/M/B)
- Klein Star charge display
- AlchemyTableScreen with custom rendering

**Phase 6: JEI Integration**
- JEI plugin registration (@JeiPlugin)
- Recipe transfer handler foundation
- Validation for recipe grid size
- Error message system via IRecipeTransferError

**Phase 7: Complete Recipe Port**
- 135+ recipes ported from 1.12.2 source
- Matter item recipes (horizontal/vertical patterns)
- All tier upgrades (Collectors, Relays, Power Flowers)
- Compressed Collector recipes (all 16 tiers)
- Star tier upgrades (Magnum, Colossal, base recipes)
- Link block recipes (Energy, Personal, Refined, Compressed)
- Table recipes (Stone Table, Alchemy Table, Arcane Tablet)
- Final tier items (Final Star, Final Star Shard, Knowledge Book)

**Phase 8-9: Tables & Integration**
- Arcane Tablet: Portable Alchemy Table functionality
- Stone Table: Opens ProjectE TransmutationContainer
- Directional placement (6 faces)
- Fixed hitbox alignment

**Phase 10-11: Link Blocks & EMC System**
- EMC capability registration for all Link types
- Personal Link: EMC → Player transfer
- Energy Link: Placeholder (requires FE capability)
- Refined Link: Placeholder (requires RS API)
- Compressed Refined Link: Placeholder (requires RS API)
- LinkBaseBlockEntity with tick-based transfer
- Matter-tier scaling for all 16 tiers

**Data Generation:**
- 424 files generated (models, blockstates, recipes, lang)
- All 76 texture files from reference versions
- Compressed collector 3D models with enchanted glint

**Build System:**
- Builds successfully with Java 21
- Output JAR: `projectex_reforged-1.21.1-1.7.3.jar`
- Deployed to test environment

### 🐛 Recent Fixes (v1.7.3)

**Compressed Collector Textures**
- Fixed flat texture rendering → now uses full 3D block models
- File: `ProjectEXItemModelProvider.java:78-83`

**Stone Table Hitbox Alignment**
- Fixed misaligned hitbox (visual vs collision)
- File: `StoneTableBlock.java:55-62`

**Complete Recipe Port**
- Ported ALL 135+ recipes from 1.12.2 source
- File: `ProjectEXRecipeProvider.java`
- Data generation: 424 files (up from 321)

### ⚠️ Active Issues

**CRITICAL: EMC Generation Not Working**
- Collectors, relays, and power flowers not generating EMC
- Server logs show "Items with existing EMC: 0"
- Investigating EMC capability registration and tick logic

**Reported Crashes (Unverified)**
- User reported Arcane Tablet and Stone Table crashes
- No crash logs found from 2025-12-30
- Awaiting user confirmation or new crash reports

### 📝 Known Limitations

**Placeholder Implementations:**
1. Energy Link: Requires FE capability implementation
2. Refined Storage Links: Require RS 2.0.0 API integration
3. JEI Recipe Transfer: Validation works, actual transfer pending
4. Klein Star Charge Bar: Shows fixed 50% charge (needs NBT reading)

**Deferred Features:**
- Knowledge Sharing Book: Registered but not functional
- Multiplayer Sync: Needs dedicated server testing
- Advanced JEI Features: Recipe highlighting, ingredient alternatives

### 📊 Implementation Statistics

- **54 Blocks**: 16 tiers × 3 types + 4 Links + 2 Tables
- **30 Items**: 14 Matter + 12 Stars + 4 special items
- **135+ Recipes**: Complete port from 1.12.2 source
- **20+ Block Entity Files**: All with tick-based logic
- **424 Generated Files**: Models, blockstates, recipes, lang

### 🚀 Next Steps

1. **Debug EMC generation** (CRITICAL)
   - Verify capability registration
   - Check tick logic in CollectorBlockEntity
   - Test EMC transfer between blocks

2. **Verify crash reports**
   - Get new crash logs if still occurring
   - Test Arcane Tablet and Stone Table in-game

3. **Integration work** (when core systems stable)
   - Implement FE capability for Energy Link
   - Integrate Refined Storage 2.0.0 API
   - Complete JEI recipe transfer

4. **Multiplayer testing**
   - Test on dedicated server
   - Verify knowledge sync
   - Check EMC transfer across players

---

## License

This project is licensed under **LGPL-3.0** (GNU Lesser General Public License v3.0).

**Original Author:** LatvianModder (ProjectEX 1.12.2/1.16.5)
**Port Author:** LightWraith8268 (1.21.1+ NeoForge port)

See [LICENSE.txt](projectex_reforged/LICENSE.txt) and [CREDITS.md](projectex_reforged/CREDITS.md) for full licensing information.

## Disclaimer

This mod is an **unofficial** extension of ProjectE. This port is not affiliated with the original FTB ProjectEX team or ProjectE developers.

For issues specific to this 1.21.1+ port, please use this repository's issue tracker.
For general ProjectEX features/bugs, refer to the original repository.

---

**Port Status:** Feature Complete - EMC Generation Debug Phase
**Version:** 1.7.3
**Last Build:** 2025-12-30
