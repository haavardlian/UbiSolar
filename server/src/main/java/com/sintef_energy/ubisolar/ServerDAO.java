package com.sintef_energy.ubisolar;

import com.sintef_energy.ubisolar.mappers.TotalUsageMapper;
import com.sintef_energy.ubisolar.mappers.UsageMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
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
    @SqlUpdate("INSERT INTO device (device_id, user_id, name, description) VALUES (:device.deviceId, :device.userId, :device.name, :device.description)")
    int createDevice(@BindBean("device") Device device);

    @SqlQuery("SELECT * FROM device WHERE user_id = :user_id AND device_id = :device_id LIMIT 1")
    @Mapper(DeviceMapper.class)
    Device getDeviceForUserById(@Bind("user_id") int user_id, @Bind("device_id") int device_id);

    @SqlQuery("SELECT * FROM device WHERE user_id = :user_id")
    @Mapper(DeviceMapper.class)
    List<Device> getDevicesForUser(@Bind("user_id") int user_id);

    @SqlQuery("SELECT * FROM power_usage WHERE device_id = :device_id")
    @Mapper(UsageMapper.class)
    List<DeviceUsage> getUsageForDevice(@Bind("device_id") int device_id);

    @SqlUpdate("INSERT INTO power_usage (device_id, datetime, power_usage) VALUES (:usage.deviceId, :usage.datetime, :usage.powerUsage)")
    int addUsageForDevice(@BindBean("usage") DeviceUsage usage);

    @SqlQuery("SELECT * FROM total_power_usage WHERE user_id = :user_id")
    @Mapper(TotalUsageMapper.class)
    List<TotalUsage> getTotalUsageForUser(@Bind("user_id") int user_id);

    @SqlUpdate("INSERT INTO total_power_usage (user_id, datetime, power_used) VALUES(:usage.userId, :usage.datetime, :usage.powerUsed)")
    int addTotalUsageForUser(@BindBean("usage") TotalUsage usage);
}