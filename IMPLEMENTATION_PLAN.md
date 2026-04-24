# Treasures 插件实现计划

## 项目概述
开发一个 Paper 插件，在原版MC结构的战利品箱中生成特殊物品，提供探索收集的乐趣。

---

## 实现顺序表（按优先级排列）

### 第一阶段：项目基础框架 ⏱️ 预计30分钟

| 序号 | 任务 | 文件 | 说明 |
|-----|------|------|------|
| 1.1 | 创建目录结构 | - | 创建commands, config, data, effects, listeners, loot, special目录 |
| 1.2 | 创建plugin.yml | plugin.yml | 定义插件基本信息、命令和权限 |
| 1.3 | 创建config.yml | config.yml | 配置文件（世界列表、结构权重、消息等） |
| 1.4 | 实现ConfigManager | ConfigManager.java | 配置加载和保存 |
| 1.5 | 更新Treasures主类 | Treasures.java | 注册管理器、监听器和命令 |

### 第二阶段：特殊物品系统 ⏱️ 预计45分钟

| 序号 | 任务 | 文件 | 说明 |
|-----|------|------|------|
| 2.1 | 实现SpecialItem类 | SpecialItem.java | 物品基础类，包含稀有度、附魔等 |
| 2.2 | 实现SpecialItemManager | SpecialItemManager.java | 注册11个特殊物品 |
| 2.3 | 创建消息文件 | messages.yml | 中英文消息配置 |

### 第三阶段：战利品表注入系统 ⏱️ 预计40分钟

| 序号 | 任务 | 文件 | 说明 |
|-----|------|------|------|
| 3.1 | 实现LootTableConfig | LootTableConfig.java | 配置每个结构的权重 |
| 3.2 | 实现LootTableInjector | LootTableInjector.java | 核心：修改原版战利品表 |
| 3.3 | 实现LootGenerateListener | LootGenerateListener.java | 监听战利品生成事件 |

### 第四阶段：物品效果系统 ⏱️ 预计50分钟

| 序号 | 任务 | 文件 | 说明 |
|-----|------|------|------|
| 4.1 | 实现ItemEffects定义 | ItemEffects.java | 定义所有物品效果类型 |
| 4.2 | 实现ItemEffectManager | ItemEffectManager.java | 管理物品效果应用和移除 |
| 4.3 | 实现ItemUseListener | ItemUseListener.java | 监听物品交互事件 |

### 第五阶段：数据存储系统 ⏱️ 预计25分钟

| 序号 | 任务 | 文件 | 说明 |
|-----|------|------|------|
| 5.1 | 实现PlayerData类 | PlayerData.java | 玩家数据结构 |
| 5.2 | 实现DataManager | DataManager.java | 数据持久化存储 |
| 5.3 | 实现PlayerListener | PlayerListener.java | 监听玩家加入/退出事件 |

### 第六阶段：命令系统 ⏱️ 预计20分钟

| 序号 | 任务 | 文件 | 说明 |
|-----|------|------|------|
| 6.1 | 实现TreasuresCommand | TreasuresCommand.java | 实现所有命令处理 |

---

## 核心设计原则

1. **物品平衡性** - 特殊物品强于玩家正常获取的物品，但不包括工具、武器等破坏游戏平衡的物品
2. **无商店功能** - 所有商店功能使用其他插件实现
3. **多世界支持** - 可配置哪些世界生成特殊物品
4. **原版结构集成** - 不生成自定义结构，修改原版结构战利品表
5. **可实现性** - 所有物品都基于Paper API实现，确保稳定运行

---

## 特殊物品清单

### 普通 (COMMON) - 基础探索辅助

| 物品ID | 显示名称 | 材质 | 描述 | 实现方式 |
|--------|---------|------|------|---------|
| explorer_compass | 探险家指南针 | COMPASS | 指向最近的宝箱位置 | 指南针绑定到最近的容器方块 |
| portable_torch | 便携火把 | TORCH | 右键放置发光方块，5分钟后消失 | 放置临时发光方块，定时删除 |
| chest_lock | 储物箱锁 | IRON_BARS | 右键点击储物箱上锁，他人无法打开 | 添加方块保护状态 |

### 稀有 (UNCOMMON) - 功能增强

| 物品ID | 显示名称 | 材质 | 描述 | 实现方式 |
|--------|---------|------|------|------|
| xp_shard | 经验碎片 | PRISMARINE_SHARD | 携带时增加10%经验获取 | 监听PlayerExpChangeEvent |
| ore_detector | 矿石探测器 | CLOCK | 显示附近地下矿石位置 | 生成粒子效果标记矿石 |
| waypoint_book | 航点标记书 | BOOK | 右键设置航点，左键传送至航点 | 记录坐标，执行传送 |

### 珍贵 (RARE) - 强力辅助

| 物品ID | 显示名称 | 材质 | 描述 | 实现方式 |
|--------|---------|------|------|---------|
| glowstone_shard | 荧石碎片 | GLOWSTONE_DUST | 佩戴时脚下发光，适合探索 | 生成跟随玩家的发光效果 |
| teleport_pearl | 传送珍珠 | ENDER_PEARL | 投掷后传送到目标位置，消耗耐久 | 一次性传送道具 |
| underwater_seal | 水下封印符 | LILY_PAD | 允许在水下呼吸额外30秒 | 给予waterBreathing效果 |

### 史诗 (EPIC) - 高级增益

| 物品ID | 显示名称 | 材质 | 描述 | 实现方式 |
|--------|---------|------|------|---------|
| night_vision_goggles | 夜视镜 | GHAST_TEAR | 永久夜视效果（需保持背包栏位） | 给予夜视药水效果 |
| invisibility_chalk | 隐身粉笔 | BONE | 30秒隐身效果，5分钟冷却 | 给予隐身效果+冷却系统 |
| ancient_compass | 远古罗盘 | COMPASS | 显示最近村庄或遗迹位置 | 指向最近结构 |

### 传说 (LEGENDARY) - 顶级收藏

| 物品ID | 显示名称 | 材质 | 描述 | 实现方式 |
|--------|---------|------|------|---------|
| heart_fragment | 生命碎片 | NETHER_STAR | 收集5个增加一颗临时心 | 临时心系统（独立机制） |
| music_box | 音乐盒 | JUKEBOX | 放置后播放音乐，营造探索氛围 | 播放音乐唱片效果 |
| soul_shard | 灵魂碎片 | SOUL_LANTERN | 收集3个可召唤友善幽灵跟随 | 召唤跟随实体 |

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
├── effects/
│   ├── ItemEffectManager.java        # 物品效果管理
│   └── ItemEffects.java              # 物品效果定义
├── listeners/
│   ├── LootGenerateListener.java     # 战利品生成监听
│   ├── ItemUseListener.java          # 物品使用监听
│   └── PlayerListener.java           # 玩家监听
├── loot/
│   ├── LootTableInjector.java        # 战利品表注入
│   └── LootTableConfig.java          # 战利品表配置
└── special/
    ├── SpecialItem.java              # 特殊物品类
    └── SpecialItemManager.java       # 特殊物品管理

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
- **Multiverse-Core** - 多世界管理

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
description: 原版结构战利品特殊物品插件

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
  treasures.admin:
    description: 管理员权限
    default: op
  treasures.give:
    description: 给予物品权限
    default: op
```

---

### config.yml
```yaml
# 资源世界设置（支持多个世界）
resource-worlds:
  enabled-worlds:
    - "resource"      # 启用特殊物品的世界
    - "zy"           # 可以添加更多世界
  disabled-worlds:
    - "world"        # 禁用特殊物品的世界
    - "world_nether"
    - "world_the_end"
  
# 原版结构战利品表注入设置
loot-injection:
  enabled: true
  structures:
    village:
      enabled: true
      weight: 30
      min-pieces: 1
      max-pieces: 2
    desert_temple:
      enabled: true
      weight: 50
      min-pieces: 2
      max-pieces: 3
    jungle_temple:
      enabled: true
      weight: 50
      min-pieces: 2
      max-pieces: 3
    shipwreck:
      enabled: true
      weight: 40
      min-pieces: 1
      max-pieces: 2
    abandoned_mineshaft:
      enabled: true
      weight: 25
      min-pieces: 1
      max-pieces: 2
    stronghold:
      enabled: true
      weight: 60
      min-pieces: 3
      max-pieces: 5
    witch_hut:
      enabled: true
      weight: 35
      min-pieces: 1
      max-pieces: 2
    igloo:
      enabled: true
      weight: 30
      min-pieces: 1
      max-pieces: 2
    ocean_monument:
      enabled: true
      weight: 55
      min-pieces: 2
      max-pieces: 4
    bastion_remnant:
      enabled: true
      weight: 45
      min-pieces: 2
      max-pieces: 3
    ancient_city:
      enabled: true
      weight: 70
      min-pieces: 3
      max-pieces: 5
  
# 特殊物品掉落率配置
loot-tables:
  default:
    common: 40
    uncommon: 25
    rare: 20
    epic: 10
    legendary: 5
    
# 物品效果设置
item-effects:
  heart-shard:
    max-stacks: 5
    extra-hearts: 1
  soul-shard:
    max-stacks: 3
  compass:
    update-interval: 20  # ticks
    range: 100
  invisibility-chalk:
    duration: 600  # ticks (30秒)
    cooldown: 6000  # ticks (5分钟)
    
# 消息配置
messages:
  prefix: "&6[Treasures] "
  item-found: "&a你在战利品箱中发现了: &f{item}"
  effect-applied: "&e你使用了 &f{item}&e，效果已激活"
  effect-expired: "&7物品效果已结束"
  no-permission: "&c你没有权限执行此操作！"
  world-disabled: "&c该世界禁止生成特殊物品"
```

---

## 玩家使用流程

1. 玩家进入启用的资源世界（如 "resource" 或 "zy"）
2. 玩家探索各种原版结构（村庄、沙漠神殿、沉船等）
3. 开启战利品箱时，有几率获得特殊物品
4. 获得特殊物品后，物品自动添加到背包
5. 右键使用物品激活效果
6. 物品效果提供各种便利功能

---

## 管理员命令流程

1. `/treasures give <item_id> [玩家]` - 给予特定物品给玩家
2. `/treasures reload` - 重新加载配置
3. `/treasures stats` - 查看统计数据
4. 修改 `config.yml` 调整世界和结构设置

---

## 物品设计指南

**✅ 推荐的特殊物品类型：**
- 导航/定位工具（指南针、坐标记录）
- 状态增益物品（经验、夜视、水下呼吸）
- 功能性物品（传送道具、临时光源）
- 视觉指示器（矿石探测粒子）
- 保护性物品（储物箱锁）
- 装饰/氛围物品（音乐盒、发光效果）
- 社交/趣味物品（幽灵跟随）

**❌ 不应包含的物品类型：**
- 武器（剑、弓等）
- 工具（镐、斧、铲等）
- 护甲（头盔、胸甲等）
- 任何增加直接战斗力的物品
- 过度便利的传送物品

---

**计划版本：** 6.0（最终版）
**预计文件数：** 13个Java文件
**预计特殊物品数：** 11个（3个普通 + 3个稀有 + 3个史诗 + 2个传说）
**预计总开发时间：** 3-4小时
