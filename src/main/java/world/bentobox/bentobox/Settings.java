package world.bentobox.bentobox;

import java.util.HashSet;
import java.util.Set;

import world.bentobox.bentobox.api.configuration.ConfigComment;
import world.bentobox.bentobox.api.configuration.ConfigEntry;
import world.bentobox.bentobox.api.configuration.ConfigObject;
import world.bentobox.bentobox.api.configuration.StoreAt;
import world.bentobox.bentobox.database.DatabaseSetup.DatabaseType;

/**
 * All the plugin settings are here
 * @author tastybento
 */
@StoreAt(filename="config.yml") // Explicitly call out what name this should have.
@ConfigComment("BentoBox v[version] 配置文件.")
@ConfigComment("")
@ConfigComment("你可以在这里设置以下项:")
@ConfigComment(" * 数据存储方式")
@ConfigComment(" * 游戏模式 (指令, ...)")
@ConfigComment(" * 网络连接 (web-based content-enriched 功能, ...)")
@ConfigComment("")
@ConfigComment("注意这个配置文件是会实时变化的:")
@ConfigComment(" * BentoBox 读取完此文件后会用最新的设置和注释更新此文件.")
@ConfigComment(" * 当更新 BentoBox 时, 新设置会自动加到此文件中.")
@ConfigComment("    * 并会加入像这样的注释:")
@ConfigComment("       Added since X.Y.Z.")
@ConfigComment("    * 新设置都是默认值, 一般不会对你的服务器造成影响.")
@ConfigComment(" * 你可以在服务器运行时编辑此文件.")
@ConfigComment("   但是你需要执行此命令使其生效: /bentobox reload.")
@ConfigComment("")
@ConfigComment("这是一些开始之前的建议:")
@ConfigComment(" * 阅读插件文档, 里面有有用的提示和见解.")
@ConfigComment("    英文文档链接: https://github.com/BentoBoxWorld/BentoBox/wiki")
@ConfigComment("    中文文档链接: https://www.mcbbs.net/thread-1009602-1-1.html")
@ConfigComment(" * 建议在关闭服务器的状态下编辑此文件.")
@ConfigComment(" * 另外, 在更新 BentoBox 后, 建议先在测试服务器中进行测试.")
@ConfigComment("    This will allow you to configure the new settings beforehand instead of applying them inadvertently on a live production server.")
public class Settings implements ConfigObject {

    /*      GENERAL     */
    @ConfigComment("新玩家的默认语言.")
    @ConfigComment("请填写 locales 文件夹中不带 .yml 后缀的文件名.")
    @ConfigComment("如果你填写的文件不存在, 将会使用默认的美式英语.")
    @ConfigEntry(path = "general.default-language")
    private String defaultLanguage = "zh-CN";

    @ConfigComment("是否开启经济功能. 如果开启, 你必须安装一个经济插件. 如果关闭, 插件的任何功能都不会收取或奖励金钱.")
    @ConfigComment("如果服务器未安装经济插件, 此项将会自动禁用.")
    @ConfigEntry(path = "general.use-economy")
    private boolean useEconomy = true;

    // Database
    @ConfigComment("JSON, MYSQL, MARIADB, MONGODB, SQLITE, POSTGRESQL 和 YAML(不推荐).")
    @ConfigComment("数据库转换选项:")
    @ConfigComment("  YAML2JSON, YAML2MARIADB, YAML2MYSQL, YAML2MONGODB, YAML2SQLITE")
    @ConfigComment("  JSON2MARIADB, JSON2MYSQL, JSON2MONGODB, JSON2SQLITE, JSON2POSTGRESQL")
    @ConfigComment("  MYSQL2JSON, MARIADB2JSON, MONGODB2JSON, SQLITE2JSON, POSTGRESQL2JSON")
    @ConfigComment("如果你想使用其它存储方式, 请在 GitHub 上建议")
    @ConfigComment("最低要求:")
    @ConfigComment("   MySQL 5.7 或更高")
    @ConfigComment("   MariaDB 10.2.3 或更高")
    @ConfigComment("   MongoDB 3.6 或更高")
    @ConfigComment("   SQLite 3.28 或更高")
    @ConfigComment("   PostgreSQL 9.4 或更高")
    @ConfigComment("数据库转换选项用于转换数据库类型时迁移数据. 使用指令 /bbox migrate.")
    @ConfigComment("YAML 和 JSON 是基于文件的存储类型.")
    @ConfigComment("MYSQL 可能会在某些平台上出现问题: 如果是这样的话, 请使用专用数据库类型 (如 MARIADB).")
    @ConfigComment("如果你在使用 MONGODB, 你必须安装 BSBMongo 插件 (非扩展).")
    @ConfigComment("见 https://github.com/tastybento/bsbMongo/releases/.")
    @ConfigEntry(path = "general.database.type", video = "https://www.bilibili.com/video/BV1ap4y1C7bo/")
    private DatabaseType databaseType = DatabaseType.JSON;

    @ConfigEntry(path = "general.database.host")
    private String databaseHost = "localhost";

    @ConfigComment("3306 是 MySQL 的默认端口. 27017 是 MongoDB 的默认端口.")
    @ConfigEntry(path = "general.database.port")
    private int databasePort = 3306;

    @ConfigEntry(path = "general.database.name")
    private String databaseName = "bentobox";

    @ConfigEntry(path = "general.database.username")
    private String databaseUsername = "username";

    @ConfigEntry(path = "general.database.password")
    private String databasePassword = "password";

    @ConfigComment("多久保存一次数据到文件中. 默认是 5 分钟.")
    @ConfigComment("这可以相对减少服务器崩溃造成的数据丢失.")
    @ConfigComment("数据还会在某些重要操作执行时保存.")
    @ConfigEntry(path = "general.database.backup-period")
    private int databaseBackupPeriod = 5;

    @ConfigComment("开启对 MongoDB, MariaDB, MySQL 和 PostgreSQL 的加密连接支持.")
    @ConfigEntry(path = "general.database.use-ssl", since = "1.12.0")
    private boolean useSSL = false;

    @ConfigComment("数据表前缀. 若使用文件存储方式请无视此项.")
    @ConfigComment("仅允许 A-Z, a-z, 0-9. 其它字符将被替换为下划线(_).")
    @ConfigComment("如果你的多个 BentoBox 服务器使用同一个数据库，请务必设置此项.")
    @ConfigEntry(path = "general.database.prefix-character", since = "1.13.0")
    private String databasePrefix = "";

    @ConfigComment("允许 FTB(https://www.feed-the-beast.com/ 一个 MOD) 模组的自激活仪器(Autonomous Activator)工作 (将会允许虚拟玩家 [CoFH] 放置和破坏方块并拾取物品)")
    @ConfigComment("如果需要的话，在这里添加更多虚拟玩家的名字")
    @ConfigEntry(path = "general.fakeplayers", experimental = true)
    private Set<String> fakePlayers = new HashSet<>();

    @ConfigComment("当玩家点击菜单之外的区域时关闭菜单")
    @ConfigEntry(path = "panel.close-on-click-outside")
    private boolean closePanelOnClickOutside = true;

    /*
     * Logs
     */
    @ConfigComment("是否在服务器日志中记录超平坦区块的重新生成.")
    @ConfigComment("如果超平坦区块很多的话，它会霸占你的控制台.")
    @ConfigComment("但是, 超平坦区块的重新生成非常占用服务器性能, 所以推荐将")
    @ConfigComment("此功能打开, 这样会方便你查找服务器卡顿问题的原因.")
    @ConfigEntry(path = "logs.clean-super-flat-chunks", since = "1.2.0")
    private boolean logCleanSuperFlatChunks = true;

    @ConfigComment("从 GitHub 下载数据的行为是否要记录在日志中.")
    @ConfigEntry(path = "logs.github-download-data", since = "1.5.0")
    private boolean logGithubDownloadData = true;

    /*
     * Island
     */
    // Cooldowns
    @ConfigComment("玩家被踢出一个队伍多少分钟后可以再次加入一个队伍.")
    @ConfigComment("这可以降低玩家重复进入队伍重复做任务的效率")
    @ConfigEntry(path = "island.cooldown.time.invite")
    private int inviteCooldown = 60;

    @ConfigComment("玩家需要等多久才能再次与一个玩家合作.")
    @ConfigEntry(path = "island.cooldown.time.coop")
    private int coopCooldown = 5;

    @ConfigComment("玩家需要等几分钟才能再次信任一个玩家.")
    @ConfigEntry(path = "island.cooldown.time.trust")
    private int trustCooldown = 5;

    @ConfigComment("玩家在解封一个玩家多久后可以再封禁他. 以分钟为单位.")
    @ConfigEntry(path = "island.cooldown.time.ban")
    private int banCooldown = 10;

    @ConfigComment("玩家两次重置岛屿需最少间隔多少秒.")
    @ConfigEntry(path = "island.cooldown.time.reset")
    private int resetCooldown = 300;

    @ConfigComment("岛屿重置限制是否应在玩家第一次创建岛屿时就开始执行.")
    @ConfigEntry(path = "island.cooldown.options.set-reset-cooldown-on-create", since = "1.2.0")
    private boolean resetCooldownOnCreate = true;

    // Timeout for team kick and leave commands
    @ConfigComment("玩家需要在多少秒内确认某些重要操作, 如岛屿重置.")
    @ConfigEntry(path = "island.confirmation.time")
    private int confirmationTime = 10;

    // Timeout for team kick and leave commands
    @ConfigComment("玩家需要站着不动多少秒后才能触发传送操作, 如 /island go.")
    @ConfigEntry(path = "island.delay.time")
    private int delayTime = 0;

    @ConfigComment("哪些操作需要再次输入指令确认.")
    @ConfigEntry(path = "island.confirmation.commands.kick")
    private boolean kickConfirmation = true;

    @ConfigEntry(path = "island.confirmation.commands.leave")
    private boolean leaveConfirmation = true;

    @ConfigEntry(path = "island.confirmation.commands.reset")
    private boolean resetConfirmation = true;

    @ConfigComment("玩家是否需要确认信任/合作请求.")
    @ConfigComment("由于安全原因, 团队邀请始终需要确认.")
    @ConfigEntry(path = "island.confirmation.invites", since = "1.8.0")
    private boolean inviteConfirmation = false;

    @ConfigComment("岛屿名最短需要多少个字符.")
    @ConfigEntry(path = "island.name.min-length")
    private int nameMinLength = 4;
    @ConfigComment("岛屿名最多可以包含的字符数.")
    @ConfigEntry(path = "island.name.max-length")
    private int nameMaxLength = 20;
    @ConfigComment("是否禁止岛屿重名.")
    @ConfigComment("如果开启，每个游戏模式的每一个岛屿的名字都必须是不同的.")
    @ConfigComment("注意岛屿名并不会被插件用来识别岛屿.")
    @ConfigEntry(path = "island.name.uniqueness", since = "1.7.0")
    private boolean nameUniqueness = false;

    @ConfigComment("移除在传送目的地多少范围内的敌对生物")
    @ConfigComment("如果敌对生物已清除，玩家周围的火柴盒就会被移除")
    @ConfigComment("如设为 5 将会在玩家周围搭起 10 x 10 x 10 的火柴盒")
    @ConfigComment("尽量不要设太大. 此项不影响末地和下界的传送.")
    @ConfigEntry(path = "island.clear-radius", since = "1.6.0")
    private int clearRadius = 5;

    @ConfigComment("粘贴蓝图的速度(多少方块/tick).")
    @ConfigComment("值越小，对服务器的影响越小，但是粘贴时间会加长.")
    @ConfigComment("过大的值可能导致服务器崩溃")
    @ConfigEntry(path = "island.paste-speed")
    private int pasteSpeed = 64;

    @ConfigComment("删除岛屿的速度(多少区块/tick/世界).")
    @ConfigComment("末地和下界的速度是这个的 3 倍")
    @ConfigComment("值越小，对服务器的影响越小，但是粘贴时间会加长.")
    @ConfigComment("设为 0 将保留岛屿方块 (不推荐).")
    @ConfigEntry(path = "island.delete-speed", since = "1.7.0")
    private int deleteSpeed = 1;

    // Automated ownership transfer
    @ConfigComment("是否自动切换岛主.")
    @ConfigComment("若原岛主长时间处于不活跃状态，将自动授予一个成员岛主身份")
    @ConfigComment("该成员为该岛上等级最高且最活跃的玩家")
    @ConfigComment("并且在该岛的时间最长")
    @ConfigComment("设为 'false' 将禁用此功能.")
    @ConfigEntry(path = "island.automated-ownership-transfer.enable", hidden = true)
    private boolean enableAutoOwnershipTransfer = false;

    @ConfigComment("岛主连续不上线多少天后将被视为不活跃")
    @ConfigEntry(path = "island.automated-ownership-transfer.inactivity-threshold", hidden = true)
    private int autoOwnershipTransferInactivityThreshold = 30;

    @ConfigComment("修改岛主时是否考虑成员等级")
    @ConfigComment("如果忽略，插件将仅考虑玩家的活跃度")
    @ConfigComment("和成为该岛成员的时长")
    @ConfigEntry(path = "island.automated-ownership-transfer.ignore-ranks", hidden = true)
    private boolean autoOwnershipTransferIgnoreRanks = false;

    // Island deletion related settings
	@ConfigComment("玩家重置岛屿时是否清除旧岛.")
	@ConfigComment("* 如果设为 'true', 当玩家重置岛屿时, 旧岛将会保留且变为未领取状态.")
	@ConfigComment("  但是你仍然可以以后清除它们.")
	@ConfigComment("  如果你的服务器规模较大, 这会导致世界体积变大.")
	@ConfigComment("  然而这将允许管理员恢复一个玩家的岛屿.")
	@ConfigComment("  管理员也可以为旧岛设置所有者.")
	@ConfigComment("* 如果设为 'false', 当玩家重置岛屿时, 旧岛就会被删除.")
	@ConfigComment("  这是默认的值.")
	@ConfigEntry(path = "island.deletion.keep-previous-island-on-reset", since = "1.13.0")
	private boolean keepPreviousIslandOnReset = false;

    /* WEB */
    @ConfigComment("是否允许 BentoBox 连接 GitHub 获取更新和扩展.")
    @ConfigComment("禁用此功能将导致更新检测不可用并将禁用依赖 GitHub 数据的功能.")
    @ConfigComment("插件不会发送任何信息.")
    @ConfigEntry(path = "web.github.download-data", since = "1.5.0")
    private boolean githubDownloadData = true;

    @ConfigComment("连接到 GitHub 服务器的间隔, 以分钟为单位.")
    @ConfigComment("这样有助于收集最新信息.")
    @ConfigComment("但是由于 GitHub API 不会实时更新数据, 所以此值不得少于 60 分钟.")
    @ConfigComment("设为 0 禁用(仅在启动时连接).")
    @ConfigEntry(path = "web.github.connection-interval", since = "1.5.0")
    private int githubConnectionInterval = 120;

    @ConfigEntry(path = "web.updater.check-updates.bentobox", since = "1.3.0", hidden = true)
    private boolean checkBentoBoxUpdates = true;

    @ConfigEntry(path = "web.updater.check-updates.addons", since = "1.3.0", hidden = true)
    private boolean checkAddonsUpdates = true;

    // ---------------------------------------------
    // Getters and setters

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public boolean isUseEconomy() {
        return useEconomy;
    }

    public void setUseEconomy(boolean useEconomy) {
        this.useEconomy = useEconomy;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    public String getDatabaseHost() {
        return databaseHost;
    }

    public void setDatabaseHost(String databaseHost) {
        this.databaseHost = databaseHost;
    }

    public int getDatabasePort() {
        return databasePort;
    }

    /**
     * This method returns the useSSL value.
     * @return the value of useSSL.
     * @since 1.12.0
     */
    public boolean isUseSSL() {
        return useSSL;
    }

    /**
     * This method sets the useSSL value.
     * @param useSSL the useSSL new value.
     * @since 1.12.0
     */
    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public void setDatabasePort(int databasePort) {
        this.databasePort = databasePort;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseUsername() {
        return databaseUsername;
    }

    public void setDatabaseUsername(String databaseUsername) {
        this.databaseUsername = databaseUsername;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }

    public int getDatabaseBackupPeriod() {
        return databaseBackupPeriod;
    }

    public void setDatabaseBackupPeriod(int databaseBackupPeriod) {
        this.databaseBackupPeriod = databaseBackupPeriod;
    }

    public Set<String> getFakePlayers() {
        return fakePlayers;
    }

    public void setFakePlayers(Set<String> fakePlayers) {
        this.fakePlayers = fakePlayers;
    }

    public boolean isClosePanelOnClickOutside() {
        return closePanelOnClickOutside;
    }

    public void setClosePanelOnClickOutside(boolean closePanelOnClickOutside) {
        this.closePanelOnClickOutside = closePanelOnClickOutside;
    }

    public int getInviteCooldown() {
        return inviteCooldown;
    }

    public void setInviteCooldown(int inviteCooldown) {
        this.inviteCooldown = inviteCooldown;
    }

    public int getCoopCooldown() {
        return coopCooldown;
    }

    public void setCoopCooldown(int coopCooldown) {
        this.coopCooldown = coopCooldown;
    }

    public int getTrustCooldown() {
        return trustCooldown;
    }

    public void setTrustCooldown(int trustCooldown) {
        this.trustCooldown = trustCooldown;
    }

    public int getBanCooldown() {
        return banCooldown;
    }

    public void setBanCooldown(int banCooldown) {
        this.banCooldown = banCooldown;
    }

    public int getResetCooldown() {
        return resetCooldown;
    }

    public void setResetCooldown(int resetCooldown) {
        this.resetCooldown = resetCooldown;
    }

    public int getConfirmationTime() {
        return confirmationTime;
    }

    public void setConfirmationTime(int confirmationTime) {
        this.confirmationTime = confirmationTime;
    }

    public boolean isKickConfirmation() {
        return kickConfirmation;
    }

    public void setKickConfirmation(boolean kickConfirmation) {
        this.kickConfirmation = kickConfirmation;
    }

    public boolean isLeaveConfirmation() {
        return leaveConfirmation;
    }

    public void setLeaveConfirmation(boolean leaveConfirmation) {
        this.leaveConfirmation = leaveConfirmation;
    }

    public boolean isResetConfirmation() {
        return resetConfirmation;
    }

    public void setResetConfirmation(boolean resetConfirmation) {
        this.resetConfirmation = resetConfirmation;
    }

    public int getNameMinLength() {
        return nameMinLength;
    }

    public void setNameMinLength(int nameMinLength) {
        this.nameMinLength = nameMinLength;
    }

    public int getNameMaxLength() {
        return nameMaxLength;
    }

    public void setNameMaxLength(int nameMaxLength) {
        this.nameMaxLength = nameMaxLength;
    }

    /**
     * @since 1.7.0
     */
    public boolean isNameUniqueness() {
        return nameUniqueness;
    }

    /**
     * @since 1.7.0
     */
    public void setNameUniqueness(boolean nameUniqueness) {
        this.nameUniqueness = nameUniqueness;
    }

    /**
     * @param pasteSpeed the pasteSpeed to set
     */
    public void setPasteSpeed(int pasteSpeed) {
        this.pasteSpeed = pasteSpeed;
    }

    /**
     * @return paste speed in blocks per tick
     */
    public int getPasteSpeed() {
        return this.pasteSpeed;
    }

    /**
     * @return the deleteSpeed
     * @since 1.7.0
     */
    public int getDeleteSpeed() {
        return deleteSpeed;
    }

    /**
     * @param deleteSpeed the deleteSpeed to set
     * @since 1.7.0
     */
    public void setDeleteSpeed(int deleteSpeed) {
        this.deleteSpeed = deleteSpeed;
    }

    public boolean isEnableAutoOwnershipTransfer() {
        return enableAutoOwnershipTransfer;
    }

    public void setEnableAutoOwnershipTransfer(boolean enableAutoOwnershipTransfer) {
        this.enableAutoOwnershipTransfer = enableAutoOwnershipTransfer;
    }

    public int getAutoOwnershipTransferInactivityThreshold() {
        return autoOwnershipTransferInactivityThreshold;
    }

    public void setAutoOwnershipTransferInactivityThreshold(int autoOwnershipTransferInactivityThreshold) {
        this.autoOwnershipTransferInactivityThreshold = autoOwnershipTransferInactivityThreshold;
    }

    public boolean isAutoOwnershipTransferIgnoreRanks() {
        return autoOwnershipTransferIgnoreRanks;
    }

    public void setAutoOwnershipTransferIgnoreRanks(boolean autoOwnershipTransferIgnoreRanks) {
        this.autoOwnershipTransferIgnoreRanks = autoOwnershipTransferIgnoreRanks;
    }

    public boolean isLogCleanSuperFlatChunks() {
        return logCleanSuperFlatChunks;
    }

    public void setLogCleanSuperFlatChunks(boolean logCleanSuperFlatChunks) {
        this.logCleanSuperFlatChunks = logCleanSuperFlatChunks;
    }

    public boolean isResetCooldownOnCreate() {
        return resetCooldownOnCreate;
    }

    public void setResetCooldownOnCreate(boolean resetCooldownOnCreate) {
        this.resetCooldownOnCreate = resetCooldownOnCreate;
    }

    public boolean isGithubDownloadData() {
        return githubDownloadData;
    }

    public void setGithubDownloadData(boolean githubDownloadData) {
        this.githubDownloadData = githubDownloadData;
    }

    public int getGithubConnectionInterval() {
        return githubConnectionInterval;
    }

    public void setGithubConnectionInterval(int githubConnectionInterval) {
        this.githubConnectionInterval = githubConnectionInterval;
    }

    public boolean isCheckBentoBoxUpdates() {
        return checkBentoBoxUpdates;
    }

    public void setCheckBentoBoxUpdates(boolean checkBentoBoxUpdates) {
        this.checkBentoBoxUpdates = checkBentoBoxUpdates;
    }

    public boolean isCheckAddonsUpdates() {
        return checkAddonsUpdates;
    }

    public void setCheckAddonsUpdates(boolean checkAddonsUpdates) {
        this.checkAddonsUpdates = checkAddonsUpdates;
    }

    public boolean isLogGithubDownloadData() {
        return logGithubDownloadData;
    }

    public void setLogGithubDownloadData(boolean logGithubDownloadData) {
        this.logGithubDownloadData = logGithubDownloadData;
    }

    public int getDelayTime() {
        return delayTime;
    }

    /**
     * @param delayTime the delayTime to set
     */
    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    /**
     * @return the clearRadius
     */
    public int getClearRadius() {
        if (clearRadius < 0) clearRadius = 0;
        return clearRadius;
    }

    /**
     * @param clearRadius the clearRadius to set. Cannot be negative.
     */
    public void setClearRadius(int clearRadius) {
        if (clearRadius < 0) clearRadius = 0;
        this.clearRadius = clearRadius;
    }

    /**
     * @return the inviteConfirmation
     * @since 1.8.0
     */
    public boolean isInviteConfirmation() {
        return inviteConfirmation;
    }

    /**
     * @param inviteConfirmation the inviteConfirmation to set
     * @since 1.8.0
     */
    public void setInviteConfirmation(boolean inviteConfirmation) {
        this.inviteConfirmation = inviteConfirmation;
    }

    /**
     * @return the databasePrefix
     */
    public String getDatabasePrefix() {
        if (databasePrefix == null) databasePrefix = "";
        return databasePrefix.isEmpty() ? "" : databasePrefix.replaceAll("[^a-zA-Z0-9]", "_").substring(0,1);
    }

    /**
     * @param databasePrefix the databasePrefix to set
     */
    public void setDatabasePrefix(String databasePrefix) {
        this.databasePrefix = databasePrefix;
    }

	/**
	 * Returns whether islands, when reset, should be kept or deleted.
	 * @return {@code true} if islands, when reset, should be kept; {@code false} otherwise.
	 * @since 1.13.0
	 */
	public boolean isKeepPreviousIslandOnReset() {
		return keepPreviousIslandOnReset;
	}

	/**
	 * Sets whether islands, when reset, should be kept or deleted.
	 * @param keepPreviousIslandOnReset {@code true} if islands, when reset, should be kept; {@code false} otherwise.
	 * @since 1.13.0
	 */
	public void setKeepPreviousIslandOnReset(boolean keepPreviousIslandOnReset) {
		this.keepPreviousIslandOnReset = keepPreviousIslandOnReset;
	}
}
