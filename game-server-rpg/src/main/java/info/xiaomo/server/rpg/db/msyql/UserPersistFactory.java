package info.xiaomo.server.rpg.db.msyql;

import info.xiaomo.gengine.persist.mysql.persist.PersistAble;
import info.xiaomo.gengine.persist.mysql.persist.PersistFactory;
import info.xiaomo.gengine.utils.JdbcUtil;
import info.xiaomo.server.rpg.db.DataType;
import info.xiaomo.server.rpg.entify.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 把今天最好的表现当作明天最新的起点．．～ いま 最高の表現 として 明日最新の始発．．～ Today the best performance as tomorrow newest
 * starter! Created by IntelliJ IDEA.
 *
 * <p>
 *
 * @author : xiaomo github: https://github.com/xiaomoinfo email : xiaomo@xiaomo.info QQ : 83387856
 *     Date : 2017/7/13 17:25 desc : Copyright(©) 2017 by xiaomo.
 */
public class UserPersistFactory implements PersistFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserPersistFactory.class);

    private static final String CREATE =
            "CREATE TABLE `p_user` ("
                    + "  `id` bigint(20) NOT NULL,"
                    + "  `login_name` varchar(64) DEFAULT NULL,"
                    + "  `server_id` int(11) DEFAULT NULL,"
                    + "  `platform_id` tinyint(4) DEFAULT NULL,"
                    + "  `gm_level` int(4) DEFAULT NULL,"
                    + "  `id_number` varchar(18) DEFAULT NULL,"
                    + "  `register_time` bigint(20) DEFAULT NULL,"
                    + "  PRIMARY KEY (`id`)"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";

    private static final String INSERT =
            "insert into p_user ("
                    + "id,"
                    + " login_name,"
                    + " server_id,"
                    + " platform_id,"
                    + " gm_level,"
                    + " id_number,"
                    + " register_time"
                    + ") values (?, ?, ?, ?, ?,?, ?)";

    private static final String UPDATE =
            "update p_user set gm_level = ?, id_number = ? where id = ?";

    private static final String DELETE = "delete from p_user where id = ?";

    public UserPersistFactory() {
        if (!JdbcUtil.hasTable(tableName())) {
            JdbcUtil.createTable(createCreateSql());
            LOGGER.warn("{}表不存在, 开始创建: {}", tableName(), tableName());
        }
    }

    @Override
    public String tableName() {
        return "p_user";
    }

    @Override
    public int dataType() {
        return DataType.USER;
    }

    @Override
    public String createCreateSql() {
        return CREATE;
    }

    @Override
    public String createInsertSql() {
        return INSERT;
    }

    @Override
    public String createUpdateSql() {
        return UPDATE;
    }

    @Override
    public String createDeleteSql() {
        return DELETE;
    }

    @Override
    public Object[] createInsertParameters(PersistAble obj) {
        User user = (User) obj;
        return createParameters(user);
    }

    @Override
    public Object[] createUpdateParameters(PersistAble obj) {
        User user = (User) obj;
        return createParameters(user);
    }

    @Override
    public Object[] createDeleteParameters(PersistAble obj) {
        return new Object[] {obj.getId()};
    }

    @Override
    public long taskPeriod() {
        return 60 * 1000;
    }

    private Object[] createParameters(User user) {
        return new Object[] {
            user.getId(),
            user.getLoginName(),
            user.getServerId(),
            user.getPlatformId(),
            user.getGmLevel(),
            user.getIdNumber(),
            user.getRegisterTime()
        };
    }
}
