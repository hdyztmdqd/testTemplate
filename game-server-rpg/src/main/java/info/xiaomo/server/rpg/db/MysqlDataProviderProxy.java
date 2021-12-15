package info.xiaomo.server.rpg.db;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import info.xiaomo.gengine.persist.mysql.MysqlDataProvider;
import info.xiaomo.gengine.persist.mysql.jdbc.ConnectionPool;
import info.xiaomo.gengine.persist.mysql.jdbc.DruidConnectionPool;
import info.xiaomo.gengine.persist.mysql.jdbc.JdbcTemplate;
import info.xiaomo.gengine.persist.mysql.persist.CacheAble;
import info.xiaomo.gengine.persist.mysql.persist.PersistAble;
import info.xiaomo.gengine.utils.JdbcUtil;
import info.xiaomo.server.rpg.db.mapper.UserMapper;
import info.xiaomo.server.rpg.db.msyql.UserPersistFactory;
import info.xiaomo.server.rpg.entify.User;
import info.xiaomo.server.rpg.server.game.GameContext;
import info.xiaomo.server.rpg.system.user.field.UserField;

/**
 * mysql的数据库代理,所有的数据存储相关的请求,都代理到了CacheManager
 *
 * @author Administrator
 */
public class MysqlDataProviderProxy implements IDataProvider {

    private static final Object NULL = new Object();
    private static final String SELECT_USER = "select  * from p_user";
    private static final String SELECT_BY_LOGIN_NAME = "select * from p_user where login_name = ?";

    private final Map<String, Long> nameSidPid2Uid = new ConcurrentHashMap<>();

    private final Map<Long, Object> uidMap = new ConcurrentHashMap<>();

    private final JdbcTemplate template;

    private final MysqlDataProvider provider;

    MysqlDataProviderProxy() throws Exception {
        // 创建数据库模板
        ConnectionPool connectionPool = new DruidConnectionPool(GameContext.getGameDBConfigPath());
        JdbcTemplate template = new JdbcTemplate(connectionPool);
        this.template = template;
        provider = new MysqlDataProvider();
        provider.init(this.template);
        JdbcUtil.init(this.template);

        // 注册factory
        provider.registerPersistTask(new UserPersistFactory());

        // 加载所有的uid
        List<Map<String, Object>> users = template.queryList(SELECT_USER, JdbcTemplate.MAP_MAPPER);
        for (Map<String, Object> user : users) {
            long id = (long) user.get(UserField.ID);
            String loginName = (String) user.get(UserField.LOGIN_NAME);
            int sid = (int) user.get(UserField.SERVER_ID);
            int pid = (int) user.get(UserField.PLATFORM_ID);
            nameSidPid2Uid.put(loginName + "_" + sid + "_" + pid, id);
            uidMap.put(id, NULL);
        }
    }

    /**
     * 是否有这个玩家
     *
     * @param id id
     * @return bool
     */
    private boolean hasUser(long id) {

        return uidMap.containsKey(id);
    }

    @Override
    public void updateData(CacheAble cache, boolean immediately) {
        provider.update(cache.getId(), cache.dataType(), immediately);
    }

    @Override
    public void updateData(long dataId, int dataType, boolean immediately) {
        provider.update(dataId, dataType, immediately);
    }

    @Override
    public void deleteData(CacheAble cache, boolean immediately) {
        provider.removeFromDisk(cache.getId(), cache.dataType(), immediately);
    }

    @Override
    public void insertData(CacheAble cache, boolean immediately) {
        provider.insert((PersistAble) cache, immediately);
    }

    @Override
    public void addData(CacheAble cache) {
        provider.put((PersistAble) cache);
    }

    @Override
    public void removeData(CacheAble cache) {
        provider.removeFromCache(cache.getId(), cache.dataType());
    }

    @Override
    public void registerUser(User user) {
        nameSidPid2Uid.put(
                user.getLoginName() + "_" + user.getServerId() + "_" + user.getPlatformId(),
                user.getId());
        uidMap.put(user.getId(), NULL);
    }

    @Override
    public User getUser(String loginName, int sid, int pid) {
        String key = loginName + "_" + sid + "_" + pid;
        Long id = nameSidPid2Uid.get(key);
        if (id == null) {
            return null;
        }
        User user = provider.get(id, DataType.USER);
        if (user == null) {
            // 从数据库中查询
            user = this.template.query(SELECT_USER, new UserMapper(), id);
            return user;
        }
        return user;
    }

    /**
     * 获取用户数据
     *
     * @param id id
     * @return User
     */
    @Override
    public User getUser(long id) {

        if (hasUser(id)) {
            User user = provider.get(id, DataType.USER);
            if (user == null) {
                // 从数据库中查询
                user = this.template.query(SELECT_USER, new UserMapper(), id);
                if (user != null) {
                    provider.put(user);
                }
                return user;
            }
        }
        return null;
    }

    @Override
    public User getUser(String loginName) {
        return template.query(SELECT_BY_LOGIN_NAME, new UserMapper(), loginName);
    }

    @Override
    public void store() {
        provider.store();
    }
}
