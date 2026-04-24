# Treasures 插件实现计划

## 项目概述
开发一个 Paper 插件，实现特殊物品系统和战利品箱探索功能。

## 核心设计原则

1. **物品平衡性** - 特殊物品强于玩家正常获取的物品，但不包括工具、武器等破坏游戏平衡的物品
2. **无商店功能** - 所有商店功能使用其他插件实现
3. **多世界支持** - 可配置哪些世界生成战利品箱

---

## 核心功能模块

### 1. 特殊物品系统
**文件:** `src/main/java/com/mc/treasures/treasures/special/`

#### 1.1 SpecialItem.java
- 特殊物品的基础类
- 属性：
  - `id`: 物品唯一标识符
  - `name`: 显示名称
  - `description`: 描述信息
  - `material`: 物品材质
  - `rarity`: 稀有度（普通/稀有/珍贵/史诗/传说）
  - `enchantments`: 附魔列表
  - `lore`: 自定义描述
- 功能：
  - `createItemStack()`: 创建物品实例
  - `getSpecialItemId()`: 从ItemStack提取物品ID
  - 稀有度枚举（带颜色和掉落几率）

#### 1.2 SpecialItemManager.java
- 管理所有特殊物品的注册和生成
- 功能：
  - `registerItems()`: 注册所有预设的特殊物品
  - `getItemById(String id)`: 根据ID获取物品
  - `getRandomItem()`: 根据稀有度权重随机获取物品
  - `getItemsByRarity(Rarity rarity)`: 获取特定稀有度的所有物品
  - `createSpecialItemStack(String id)`: 创建指定ID的物品实例

**设计理念：** 物品应该提供便利性、装饰性或功能性增益，而非破坏游戏平衡

**预设物品清单（按稀有度）：**

**普通 (COMMON):**
- 探险家指南针 - 指向最近的未开启战利品箱
- 临时篝火 - 放置后可发光并驱赶附近的怪物
- 储物箱锁 - 放置在储物箱上防止其他玩家打开

**稀有 (UNCOMMON):**
- 经验宝石 - 携带时增加少量经验获取
- 矿工幸运符 - 增加地下矿石生成概率的视觉指示
- 建筑师之书 - 记录最近访问过的坐标

**珍贵 (RARE):**
- 钻石碎片胸针 - 装饰性物品，闪烁效果
- 传送卷轴 - 单次使用，传送回主城或绑定点
- 钓鱼宝箱钥匙 - 可以开启特殊钓鱼奖励箱

**史诗 (EPIC):**
- 夜视药水背包 - 无限时长夜视效果
- 隐身斗篷碎片 - 短暂的隐身效果
- 幸运马蹄铁 - 显著提升生物掉落稀有物品概率

**传说 (LEGENDARY):**
- 生命之心碎片 - 增加一颗临时心（可叠加，最大5颗）
- 世界守护者徽章 - 显示附近稀有生物位置的雷达
- 泰拉瑞亚水晶 - 放置后发出彩色光柱，可被远程探测

---

### 2. 战利品箱系统
**文件:** `src/main/java/com/mc/treasures/treasures/loot/`

#### 2.1 LootChestManager.java
- 管理战利品箱的生成和奖励
- 功能：
  - `generateChests(World world)`: 在指定世界生成战利品箱
  - `findNearestChest(Location location)`: 查找最近的未开启战利品箱
  - `openChest(Player player, Chest chest)`: 玩家打开战利品箱
  - `respawnChest(Chest chest)`: 战利品箱重生（可配置时间）
  - `cleanup()`: 清理保存的战利品箱数据
- 配置：
  - 每个世界生成的战利品箱数量
  - 生成半径范围
  - 战利品箱重生时间
  - 最小生成间距

#### 2.2 LootTable.java
- 定义战利品表
- 功能：
  - `rollLoot()`: 根据权重掷骰获取奖励
  - `addLootEntry(SpecialItem item, int weight)`: 添加物品条目
  - `getLoot()`: 获取完整的战利品列表

---

### 3. 结构探索系统
**文件:** `src/main/java/com/mc/treasures/treasures/structure/`

#### 3.1 StructureManager.java
- 管理可探索结构
- 功能：
  - `registerStructures()`: 注册自定义结构类型
  - `placeStructure(World world, Location center, StructureType type)`: 放置结构
  - `findStructures(World world, Player player)`: 查找附近的结构
  - `exploreStructure(Player player, Structure structure)`: 玩家探索结构
- 结构类型：
  - `LOOT_CHEST`: 战利品箱结构（埋藏的箱子）
  - `HIDDEN_CACHE`: 隐藏的储物空间
  - `ANCIENT_TOMB`: 古老坟墓

#### 3.2 StructureType.java
- 结构类型枚举
- 属性：
  - `displayName`: 显示名称
  - `description`: 描述
  - `lootTable`: 使用的战利品表
  - `explorationTime`: 探索所需时间

---

### 4. 配置文件系统
**文件:** `src/main/java/com/mc/treasures/treasures/config/`

#### 4.1 ConfigManager.java
- 管理插件配置
- 功能：
  - `loadConfig()`: 加载配置
  - `saveConfig()`: 保存配置
  - `reloadConfig()`: 重新加载配置
  - `getConfig()`: 获取配置对象
  - `getMessage(String path)`: 获取消息

#### 4.2 config.yml
```yaml
# 资源世界设置（支持多个世界）
resource-worlds:
  enabled-worlds:
    - "resource"      # 启用战利品箱的世界
    - "zy"           # 可以添加更多世界
  disabled-worlds:
    - "world"        # 禁用战利品箱的世界
    - "world_nether"
    - "world_the_end"
  
# 战利品箱设置
loot-chests:
  count-per-world: 50
  min-distance: 50
  respawn-time: 3600  # 秒（1小时）
  generation-radius: 5000
  generation-y-range: 0-64  # 只在地表和地下生成
  
# 特殊物品掉落率配置
loot-tables:
  default:
    common: 40
    uncommon: 25
    rare: 20
    epic: 10
    legendary: 5
    
# 战利品箱外观设置
chest-appearance:
  type: "mossy_chest"  # mossy_chest 或 chest
  particles: true       # 是否显示粒子效果
  glow-effect: true     # 是否发光
    
# 消息配置
messages:
  prefix: "&6[Treasures] "
  chest-located: "&a已定位最近的宝箱，距离: &f{distance}格"
  chest-found: "&a你发现了一个宝箱！"
  chest-opened: "&e你开启了宝箱，获得了: &f{item}"
  chest-respawned: "&7宝箱已重新生成"
  no-chest-found: "&c附近没有找到宝箱"
  world-disabled: "&c该世界禁止生成宝箱"
  no-permission: "&c你没有权限执行此操作！"
```

---

### 5. 命令系统
**文件:** `src/main/java/com/mc/treasures/treasures/commands/`

#### 5.1 TreasuresCommand.java
主命令处理

**命令列表：**
- `/treasures help` - 显示帮助信息
- `/treasures locate` - 定位最近的宝箱（显示距离和方向）
- `/treasures give <item_id>` - 给予指定特殊物品（管理员）
- `/treasures reload` - 重新加载配置（管理员）
- `/treasures stats` - 显示统计数据
- `/treasures list` - 列出所有特殊物品
- `/treasures info <item_id>` - 查看物品详细信息

**权限：**
- `treasures.use` - 基础使用权限
- `treasures.locate` - 定位宝箱权限
- `treasures.admin` - 管理员权限
- `treasures.give` - 给予物品权限

---

### 6. 事件监听器
**文件:** `src/main/java/com/mc/treasures/treasures/listeners/`

#### 6.1 ChestListener.java
- 监听战利品箱相关事件
- 事件：
  - `BlockBreakEvent` - 阻止破坏战利品箱
  - `PlayerInteractEvent` - 处理与战利品箱交互
  - `InventoryClickEvent` - 处理仓库点击

#### 6.2 PlayerListener.java
- 监听玩家相关事件
- 事件：
  - `PlayerJoinEvent` - 玩家加入时发送提示
  - `PlayerQuitEvent` - 保存玩家数据

---

### 7. 数据存储
**文件:** `src/main/java/com/mc/treasures/treasures/data/`

#### 7.1 DataManager.java
- 管理插件数据持久化
- 功能：
  - `saveChestData()` - 保存战利品箱数据
  - `loadChestData()` - 加载战利品箱数据
  - `savePlayerData(Player player)` - 保存玩家数据
  - `loadPlayerData(Player player)` - 加载玩家数据
- 存储方式：YAML文件

#### 7.2 PlayerData.java
- 玩家数据类
- 属性：
  - `UUID uuid` - 玩家唯一标识
  - `int chestsOpened` - 开启的宝箱数量
  - `List<String> discoveredItems` - 发现的物品列表
  - `Map<String, Long> cooldowns` - 冷却时间

---

## 文件结构

```
src/main/java/com/mc/treasures/treasures/
├── Treasures.java                    # 主插件类
├── commands/
│   └── TreasuresCommand.java         # 命令处理
├── config/
│   └── ConfigManager.java            # 配置管理
├── data/
│   ├── DataManager.java              # 数据管理
│   └── PlayerData.java               # 玩家数据
├── listeners/
│   ├── ChestListener.java            # 宝箱监听
│   └── PlayerListener.java           # 玩家监听
├── loot/
│   ├── LootChestManager.java        # 战利品箱管理
│   └── LootTable.java               # 战利品表
├── special/
│   ├── SpecialItem.java              # 特殊物品类
│   └── SpecialItemManager.java       # 特殊物品管理
└── structure/
    ├── StructureManager.java        # 结构管理
    └── StructureType.java            # 结构类型

src/main/resources/
├── config.yml                        # 配置文件
├── messages.yml                      # 消息文件
└── plugin.yml                        # 插件描述文件
```

---

## 依赖关系

### 必需依赖：
- **Paper API 1.21.8** - 核心API

### 软依赖（可选）：
- **Multiverse-Core** - 多世界管理（用于获取世界列表）

### 可选功能：
- **PlaceholderAPI** - 变量支持
- **WorldGuard** - 区域保护

---

## 实现步骤

### 第一阶段：基础框架
1. 创建项目结构
2. 实现 ConfigManager（支持多世界配置）
3. 实现 SpecialItem 和 SpecialItemManager（设计平衡物品）
4. 创建基础命令系统

### 第二阶段：核心功能
5. 实现战利品箱系统（基于世界配置）
6. 实现结构探索系统
7. 创建数据存储系统

### 第三阶段：完善
8. 实现事件监听器
9. 创建配置和消息文件
10. 测试所有功能
11. 优化性能

---

## 配置示例

### plugin.yml
```yaml
name: Treasures
version: 1.0
main: com.mc.treasures.treasures.Treasures
api-version: '1.21'
authors:
  - Developer
description: 特殊物品和战利品箱探索插件

softdepend:
  - Multiverse-Core
  
commands:
  treasures:
    description: Treasures 插件主命令
    usage: /<command> [args]
    aliases: [tr, treasure]
    
permissions:
  treasures.use:
    description: 基础使用权限
    default: true
  treasures.locate:
    description: 定位宝箱权限
    default: true
  treasures.admin:
    description: 管理员权限
    default: op
  treasures.give:
    description: 给予物品权限
    default: op
```

---

## 玩家使用流程

1. 玩家进入启用的资源世界（如 "resource" 或 "zy"）
2. 使用 `/treasures locate` 定位最近的宝箱
3. 根据距离和方向指引找到宝箱位置
4. 破坏方块打开宝箱
5. 获得随机特殊物品
6. 物品自动添加到背包或掉落在地上

---

## 管理员命令流程

1. `/treasures give <item_id>` - 给予特定物品给玩家
2. `/treasures reload` - 重新加载配置
3. `/treasures stats` - 查看统计数据
4. 修改 `config.yml` 调整世界设置

---

## 技术特点

1. **高性能** - 使用异步任务处理大量战利品箱
2. **可扩展** - 模块化设计，易于添加新物品和结构
3. **数据持久化** - 所有数据自动保存
4. **多世界支持** - 灵活配置哪些世界启用功能
5. **权限系统** - 细粒度权限控制
6. **配置灵活** - 高度可配置的选项
7. **物品平衡** - 特殊物品提供便利但不破坏游戏平衡

---

## 物品设计指南

**✅ 推荐的特殊物品类型：**
- 导航/定位工具（指南针、雷达等）
- 状态增益饰品（经验、生命、运气）
- 功能性物品（传送卷轴、钥匙等）
- 装饰性物品（闪烁胸针、光柱等）
- 便捷工具（记录坐标、保护储物箱）

**❌ 不应包含的物品类型：**
- 武器（剑、弓等）
- 工具（镐、斧、铲等）
- 护甲（头盔、胸甲等）
- 任何增加战斗力的物品

---

## 潜在问题与解决方案

1. **战利品箱重叠** - 确保最小距离配置生效
2. **性能问题** - 使用分块加载和异步处理
3. **数据丢失** - 定期自动保存
4. **世界不存在** - 启动时检测并提示

---

**计划版本：** 2.0
**预计文件数：** 12+ 个Java文件
**预计开发时间：** 3-4小时
**测试时间：** 1小时
