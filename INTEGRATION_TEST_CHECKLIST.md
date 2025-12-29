# ProjectEX Integration Test Checklist

## Test Environment Setup

1. **Minecraft Version**: 1.21.1
2. **NeoForge Version**: 21.1.73+
3. **Required Mods**:
   - ProjectE 1.21.1-PE1.1.0 (in `integrations/` folder)
   - ProjectEX Reforged (build JAR from `projectex_reforged/build/libs/`)

## Phase 1: Mod Loading ✅ (Completed in previous test)

- [x] Mod appears in mods list as "ProjectEX Reforged"
- [x] No crashes on startup
- [x] Creative tab appears with Arcane Tablet icon
- [x] All items/blocks registered correctly

## Phase 2: ProjectE Integration Tests

### EMC Capability Registration
**Purpose**: Verify all blocks properly expose EMC storage capability

**Test Steps**:
1. Place a Basic Collector in creative mode
2. Place a Basic Relay next to it
3. Give collector EMC (via commands or ProjectE transmutation table)
4. Verify EMC transfers from Collector → Relay
5. Repeat for higher tier collectors (Dark, Red, Magenta, etc.)

**Expected Results**:
- Collectors generate EMC every 20 ticks (1 second)
- EMC transfers to adjacent relays/acceptors
- Higher tiers generate more EMC (see Matter.java for rates)

### Klein Star Auto-Charging
**Purpose**: Verify Alchemy Table charges Klein Stars

**Test Steps**:
1. Obtain a Klein Star (any tier) from ProjectE
2. Open Alchemy Table GUI
3. Place Klein Star in charging slot (bottom-left)
4. Have EMC in player knowledge (via transmutation)
5. Wait for auto-charging

**Expected Results**:
- Klein Star charges with player EMC every second
- Charge bar displays under Klein Star slot
- Player EMC decreases as star charges

### Transmutation Integration
**Purpose**: Verify Alchemy Table can transmute items using player EMC

**Test Steps**:
1. Learn several items in ProjectE transmutation table
2. Open Alchemy Table
3. Place known craftable recipe in crafting grid
4. Verify items you don't have in inventory show purple/gold shimmer
5. Craft item using shift-click (bulk craft)

**Expected Results**:
- Missing items get transmuted using player EMC
- Inventory items used first, EMC only for shortfall
- Bulk crafting works within EMC budget
- Auto-learning triggers with sound/message

### Auto-Learn System
**Purpose**: Verify crafting unknown items adds them to knowledge

**Test Steps**:
1. Clear ProjectE knowledge (or use fresh player)
2. Craft an item in Alchemy Table that has EMC value
3. Check if item appears in ProjectE transmutation table

**Expected Results**:
- Level-up sound plays
- Action bar message: "✦ Learned: [Item Name] (XXX EMC)"
- Item appears in transmutation table
- Only triggers once per bulk craft session

### Stone Table Integration
**Purpose**: Verify Stone Table opens ProjectE transmutation GUI

**Test Steps**:
1. Place Stone Table
2. Right-click to open
3. Verify ProjectE transmutation interface appears

**Expected Results**:
- Opens TransmutationContainer from ProjectE
- All ProjectE transmutation features work
- Can be placed on any face (directional block)

## Phase 3: Link Block Tests

### Personal Link
**Purpose**: Verify Personal Link transfers EMC to player

**Test Steps**:
1. Place Personal Link (any tier)
2. Right-click to bind to player
3. Give link EMC (via collector or commands)
4. Wait 20 ticks (1 second)
5. Check player EMC in ProjectE

**Expected Results**:
- Link stores EMC in internal buffer
- Every 20 ticks, transfers stored EMC to player
- Player EMC increases by buffered amount
- Buffer resets to zero after transfer

### Energy Link (Placeholder)
**Purpose**: Verify Energy Link exists but doesn't crash

**Test Steps**:
1. Place Energy Link
2. Verify it can be placed/removed without crashes

**Expected Results**:
- Block places successfully
- No crashes when adjacent to energy sources
- Full functionality requires FE capability implementation

### Refined Link (Placeholder)
**Purpose**: Verify Refined Link exists but doesn't crash

**Test Steps**:
1. Place Refined Link
2. Verify it can be placed/removed without crashes

**Expected Results**:
- Block places successfully
- No crashes when adjacent to RS networks
- Full functionality requires Refined Storage integration

## Phase 4: JEI Integration Tests

### Recipe Transfer
**Purpose**: Verify JEI recipe transfer to Alchemy Table

**Test Steps**:
1. Install JEI (Just Enough Items) mod
2. Open Alchemy Table GUI
3. View crafting recipe in JEI
4. Click "+" button to transfer recipe

**Expected Results**:
- Recipe ingredients populate crafting grid
- Ghost items appear for transmutable ingredients
- Shift-click transfer shows bulk craft preview

## Phase 5: EMC Generation Rate Validation

### Collector Output Verification
**Purpose**: Verify collectors generate correct EMC amounts

**Test Rates** (from Matter.java):
```
BASIC: 4 EMC/sec (collectorOutput = 4)
DARK: 40 EMC/sec (collectorOutput = 40)
RED: 104 EMC/sec (collectorOutput = 104)
MAGENTA: 272 EMC/sec (collectorOutput = 272)
PINK: 656 EMC/sec (collectorOutput = 656)
PURPLE: 1552 EMC/sec (collectorOutput = 1552)
VIOLET: 3600 EMC/sec (collectorOutput = 3600)
BLUE: 8272 EMC/sec (collectorOutput = 8272)
CYAN: 18832 EMC/sec (collectorOutput = 18832)
GREEN: 42656 EMC/sec (collectorOutput = 42656)
LIME: 96272 EMC/sec (collectorOutput = 96272)
YELLOW: 216656 EMC/sec (collectorOutput = 216656)
ORANGE: 486416 EMC/sec (collectorOutput = 486416)
WHITE: 1090832 EMC/sec (collectorOutput = 1090832)
FADING: 2444272 EMC/sec (collectorOutput = 2444272)
FINAL: 5471312 EMC/sec (collectorOutput = 5471312)
```

**Test Steps**:
1. Place each tier collector
2. Let run for 10 seconds
3. Measure EMC output
4. Verify matches expected rate × 10

### Power Flower Output Verification
**Purpose**: Verify power flowers use formula: `collectorOutput × 18 + relayBonus × 30`

**Sample Calculations**:
```
BASIC: 4×18 + 1×30 = 102 EMC/sec
DARK: 40×18 + 13×30 = 1110 EMC/sec
RED: 104×18 + 37×30 = 2982 EMC/sec
...
FINAL: 5471312×18 + 1365328×30 = 139444046 EMC/sec
```

**Test Steps**:
1. Place each tier power flower
2. Let run for 10 seconds
3. Measure EMC output
4. Verify formula matches

## Phase 6: GUI Rendering Tests

### Custom Overlays
**Purpose**: Verify Alchemy Table GUI rendering

**Test Steps**:
1. Open Alchemy Table
2. Place items with EMC values in crafting grid
3. Place Klein Star in charging slot

**Expected Results**:
- Purple/gold shimmer on transmutable items
- EMC cost displayed (formatted: K/M/B)
- Klein Star shows charge bar
- No rendering glitches or z-fighting

### Tooltip Display
**Purpose**: Verify tooltips show correctly

**Expected Results**:
- Alchemy Table: "Advanced transmutation crafting table"
- Arcane Tablet: "Portable transmutation and crafting"
- Stone Table: "Compact transmutation table"
- Link blocks: Show ownership and EMC info

## Phase 7: Recipe Validation

### Alchemy Table Recipe
**Pattern**:
```
[Obsidian] [Ender Chest] [Obsidian]
[Ender Chest] [Stone Table] [Ender Chest]
[Obsidian] [Ender Chest] [Obsidian]
```

**Note**: Using placeholders until ProjectE items available

**Test**: Craft Alchemy Table using recipe above

### Arcane Tablet Recipe
**Pattern**:
```
[Nether Star] [Alchemy Table] [Nether Star]
[Nether Star] [Ender Chest] [Nether Star]
[Nether Star] [Nether Star] [Nether Star]
```

**Note**: Using placeholders until ProjectE items available

**Test**: Craft Arcane Tablet using recipe above

## Known Issues / Future Work

1. **Energy Link**: Requires FE capability implementation for energy conversion
2. **Refined Storage Links**: Require RS API integration for network item extraction
3. **Compressed Collectors**: Not yet implemented (deferred to future update)
4. **Recipe Placeholders**: Using vanilla items instead of Dark/Red Matter (ProjectE integration needed)
5. **JEI Recipe Transfer**: Currently shows validation only, actual transfer logic TODO

## Success Criteria

### Critical (Must Pass)
- [x] Mod loads without crashes
- [ ] Collectors generate EMC at correct rates
- [ ] EMC transfers between blocks via capability
- [ ] Alchemy Table transmutation works
- [ ] Auto-learn system functions
- [ ] Klein Star charging works
- [ ] Personal Link transfers EMC to player

### Important (Should Pass)
- [ ] Power Flower rates match formula
- [ ] Stone Table opens ProjectE GUI
- [ ] GUI overlays render correctly
- [ ] Recipes work with placeholders

### Nice-to-Have (Can Defer)
- [ ] JEI recipe transfer fully functional
- [ ] Energy Link FE conversion (requires implementation)
- [ ] RS integration (requires implementation)

## Test Execution Notes

**Last Run**: [DATE]
**Tester**: [NAME]
**Environment**: Minecraft 1.21.1 + NeoForge 21.1.73 + ProjectE 1.1.0
**Build**: projectex_reforged-1.21.1-1.0.0.jar

**Results**: [SUMMARY]

**Issues Found**: [LIST]

**Recommendations**: [NOTES]
