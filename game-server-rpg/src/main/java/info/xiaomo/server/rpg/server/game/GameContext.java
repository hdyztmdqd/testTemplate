package info.xiaomo.server.rpg.server.game;

import java.util.Date;
import info.xiaomo.gengine.utils.PathUtil;
import info.xiaomo.gengine.utils.StringUtil;
import info.xiaomo.gengine.utils.TimeUtil;
import info.xiaomo.server.rpg.server.BackServer;
import info.xiaomo.server.rpg.server.GameServer;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author xiaomo */
@Data
public class GameContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameContext.class);
    /** 服务器关闭逻辑已经是否已经执行 */
    public static boolean serverCloseLogicExecuted;
    private static int serverId;
    private static int serverType;
    private static String gameDBConfigPath;
    private static String logDBConfigPath;
    private static int gameServerPort;
    private static int backServerPort;
    /** 开服日期 */
    private static Date openTime;
    /** 开服当天凌晨0点时间戳 */
    private static long openDayZeroTime;
    /** 合服日期 */
    private static Date combineTime;
    /** 合服日期当天凌晨0点时间戳 */
    private static long combineDayZeroTime;
    /** 是否已经合服 */
    private static boolean combined = false;
    /** 是否开启全服双倍经验 */
    private static int expDouble = 1;
    private static ServerOption option;
    private static GameServer gameServer;
    private static BackServer backServer;
    /** 游戏服务器关闭 */
    private static boolean closed;

    public static void init(ServerOption option) {
        GameContext.option = option;
        serverId = option.getServerId();
        serverType = option.getServerType();
        openTime = option.getOpenTime();

        if (StringUtil.isBlank(option.getGameDbConfigPath())) {
            gameDBConfigPath = PathUtil.getConfigPath() + "gameDb.yml";
        } else {
            gameDBConfigPath = option.getGameDbConfigPath();
        }

        if (StringUtil.isBlank(option.getGameDbConfigPath())) {
            logDBConfigPath = PathUtil.getConfigPath() + "logDbConfig.yml";
        } else {
            logDBConfigPath = option.getGameDbConfigPath();
        }

        gameServerPort = option.getGameServerPort();
        backServerPort = option.getBackServerPort();
        openDayZeroTime = TimeUtil.getZeroClockTime(openTime.getTime());

        LOGGER.info("开服时间：{}", openTime);
        LOGGER.info("开服当天凌晨0点时间戳：{}", openDayZeroTime);
        LOGGER.info("开服距离开服当天凌晨：{}", (openTime.getTime() - openDayZeroTime));

        combineTime = option.getCombineTime();
        if (combineTime != null) {
            combineDayZeroTime = TimeUtil.getZeroClockTime(option.getCombineTime().getTime());
            if (combineTime.getTime() <= openTime.getTime()) {
                throw new RuntimeException("开服与合服时间配置错误，合服时间早于或等于开服时间....");
            }
            combined = true;

            LOGGER.info("合服时间：{}", combineTime);
            LOGGER.info("合服当天凌晨0点时间戳：{}", combineDayZeroTime);
            LOGGER.info("合服距离开服当天凌晨：{}", (combineTime.getTime() - combineDayZeroTime));
        }
    }

    public static GameServer createGameServer() {
        try {
            gameServer = new GameServer();
            return gameServer;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static BackServer createBackServer() {
        try {
            return new BackServer();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isStarted() {
        return gameServer != null && gameServer.isOpen();
    }

    public static int getExpDouble() {
        return expDouble;
    }

    public static void setExpDouble(int expDouble) {
        GameContext.expDouble = expDouble;
    }

    public static Date getOpenTime() {
        return openTime;
    }

    public static void setOpenTime(Date openTime) {
        GameContext.openTime = openTime;
    }

    public static long getOpenDayZeroTime() {
        return openDayZeroTime;
    }

    public static void setOpenDayZeroTime(long openDayZeroTime) {
        GameContext.openDayZeroTime = openDayZeroTime;
    }

    public static Date getCombineTime() {
        return combineTime;
    }

    public static void setCombineTime(Date combineTime) {
        GameContext.combineTime = combineTime;
    }

    public static long getCombineDayZeroTime() {
        return combineDayZeroTime;
    }

    public static void setCombineDayZeroTime(long combineDayZeroTime) {
        GameContext.combineDayZeroTime = combineDayZeroTime;
    }

    public static boolean isCombined() {
        return combined;
    }

    public static ServerOption getOption() {
        return option;
    }

    public static GameServer getGameServer() {
        return gameServer;
    }

    public static BackServer getBackServer() {
        return backServer;
    }

    public static int getServerId() {
        return serverId;
    }

    public static int getServerType() {
        return serverType;
    }

    public static boolean isServerCloseLogicExecuted() {
        return serverCloseLogicExecuted;
    }

    public static void setServerCloseLogicExecuted(boolean serverCloseLogicExecuted) {
        GameContext.serverCloseLogicExecuted = serverCloseLogicExecuted;
    }

    public static boolean isClosed() {
        return closed;
    }

    public static void setClosed(boolean closed) {
        GameContext.closed = closed;
    }

    public static String getGameDBConfigPath() {
        return gameDBConfigPath;
    }

    public static String getLogDBConfigPath() {
        return logDBConfigPath;
    }

    public static int getGameServerPort() {
        return gameServerPort;
    }

    public static int getBackServerPort() {
        return backServerPort;
    }
}
