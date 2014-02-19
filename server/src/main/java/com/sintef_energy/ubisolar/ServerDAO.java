package com.sintef_energy.ubisolar;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.Date;
import java.util.List;

/**
 * Created by haavard on 2/12/14.
 */
@RegisterMapper(TotalUsageMapper.class)
public interface ServerDAO {
    @SqlUpdate("INSERT INTO device (device_id, user_id, name, description) VALUES (:device_id, :user_id, :name, :description)")
    int createDevice(@Bind("user_id") int user_id, @Bind("name") String name, @Bind("description") String description, @Bind("device_id") int device_id);

    @SqlQuery("SELECT * FROM device WHERE user_id = :user_id AND device_id = :device_id LIMIT 1")
    @Mapper(DeviceMapper.class)
    Device getDeviceForUserById(@Bind("user_id") int user_id, @Bind("device_id") int device_id);

    @SqlQuery("SELECT * FROM device WHERE user_id = :user_id")
    @Mapper(DeviceMapper.class)
    List<Device> getDevicesForUser(@Bind("user_id") int user_id);

    @SqlQuery("SELECT * FROM power_usage WHERE device_id = :device_id")
    @Mapper(UsageMapper.class)
    List<DeviceUsage> getUsageForDevice(@Bind("device_id") int device_id);

    @SqlUpdate("INSERT INTO power_usage (device_id, name, power_usage, user_id) VALUES (:device_id, :datetime, :power_usage")
    int addUsageForDevice(@Bind("device_id") int device_id, @Bind("name") Date datetime, @Bind("power_usage") double power_usage);

    @SqlQuery("SELECT * FROM total_power_usage WHERE user_id = :user_id")
    @Mapper(TotalUsageMapper.class)
    List<TotalUsage> getTotalUsageForUser(@Bind("user_id") int user_id);

    @SqlUpdate("INSERT INTO total_power_usage (user_id, datetime, power_used) VALUES(:user_id, :datetime, :power_used)")
    int addTotalUsageForUser(@Bind("user_id") int user_id, @Bind("datetime") Date datetime, @Bind("power_used") double power_used);
}
