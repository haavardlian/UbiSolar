package com.sintef_energy.ubisolar;

import com.sintef_energy.ubisolar.mappers.*;
import com.sintef_energy.ubisolar.structs.*;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by haavard on 2/12/14.
 */
@RegisterMapper(TotalUsageMapper.class)
public interface ServerDAO {
    @SqlUpdate("INSERT INTO device (device_id, user_id, name, description) VALUES (:device.deviceId, :device.userId, :device.name, :device.description)")
    int createDevice(@BindBean("device") Device device);

    @SqlQuery("SELECT device_power_usage.id, device.user_id, timestamp, SUM(device_power_usage.power_usage) AS power_usage, YEAR(timestamp) " +
              "AS year, MONTH(timestamp) AS month, WEEK(timestamp) AS week, DAY(timestamp) AS day, HOUR(timestamp) AS " +
              "hour FROM device_power_usage, device WHERE device_power_usage.device_id = device.id AND " +
              "device.user_id = :userId GROUP BY year")
    @Mapper(TotalUsageMapper.class)
    List<TotalUsage> getTotalDevicesUsageYearly(@Bind("userId") int userId);

    @SqlQuery("SELECT device_power_usage.id, device.user_id, timestamp, SUM(device_power_usage.power_usage) AS power_usage, YEAR(timestamp) " +
            "AS year, MONTH(timestamp) AS month, WEEK(timestamp) AS week, DAY(timestamp) AS day, HOUR(timestamp) AS " +
            "hour FROM device_power_usage, device WHERE device_power_usage.device_id = device.id AND " +
            "device.user_id = :userId GROUP BY year, month")
    @Mapper(TotalUsageMapper.class)
    List<TotalUsage> getTotalDevicesUsageMonthly(@Bind("userId") int userId);

    @SqlQuery("SELECT device_power_usage.id, device.user_id, timestamp, SUM(device_power_usage.power_usage) AS power_usage, YEAR(timestamp) " +
            "AS year, MONTH(timestamp) AS month, WEEK(timestamp) AS week, DAY(timestamp) AS day, HOUR(timestamp) AS " +
            "hour FROM device_power_usage, device WHERE device_power_usage.device_id = device.id AND " +
            "device.user_id = :userId GROUP BY year, month, week, day")
    @Mapper(TotalUsageMapper.class)
    List<TotalUsage> getTotalDevicesUsageDaily(@Bind("userId") int userId);

    @SqlQuery("SELECT * FROM device WHERE user_id = :user_id AND id = :device_id LIMIT 1")
    @Mapper(DeviceMapper.class)
    Device getDeviceForUserById(@Bind("user_id") int user_id, @Bind("device_id") int device_id);

    @SqlQuery("DELETE FROM device WHERE user_id = :user_id AND id = :device_id LIMIT 1")
    int deleteDeviceForUserById(@Bind("user_id") int user_id, @Bind("device_id") int device_id);

    @SqlQuery("SELECT * FROM device WHERE user_id = :user_id")
    @Mapper(DeviceMapper.class)
    List<Device> getDevicesForUser(@Bind("user_id") int user_id);

    @SqlQuery("SELECT * FROM device_power_usage WHERE device_id = :device_id")
    @Mapper(DeviceUsageMapper.class)
    List<DeviceUsage> getUsageForDevice(@Bind("device_id") int device_id);

    @SqlUpdate("INSERT INTO device_power_usage (device_id, datetime, power_usage) VALUES (:usage.deviceId, :usage.datetime, :usage.powerUsage)")
    int addUsageForDevice(@BindBean("usage") DeviceUsage usage);

    @SqlQuery("SELECT * FROM total_power_usage WHERE user_id = :user_id")
    @Mapper(TotalUsageMapper.class)
    List<TotalUsage> getTotalUsageForUser(@Bind("user_id") int user_id);

    @SqlUpdate("INSERT INTO total_power_usage (user_id, timestamp, power_usage) VALUES(:usage.userId, :usage.datetime, :usage.powerUsage)")
    int addTotalUsageForUser(@BindBean("usage") TotalUsage usage);

    @SqlQuery("SELECT tip.*, (SELECT AVG(rating) FROM tip_rating WHERE tip_id=tip.id) AS average_rating, (SELECT COUNT(rating) FROM tip_rating WHERE tip_id=tip.id) AS n_ratings FROM tip")
    @Mapper(TipMapper.class)
    List<Tip> getAllTips();

    @SqlQuery("SELECT * FROM tip WHERE id = :id")
    @Mapper(TipMapper.class)
    Tip getTipById(@Bind("id") int id);

    @SqlUpdate("INSERT INTO tip (name, description) VALUES (:tip.name, :tip.description)")
    int createTip(@BindBean("tip") Tip tip);

    @SqlQuery("SELECT * FROM tip_rating WHERE tip_id = :id ORDER BY rating DESC")
    @Mapper(TipRatingMapper.class)
    List<TipRating> getRatingsForTip(@Bind("id") int id);

    @SqlUpdate("INSERT INTO tip_rating (tip_id, rating, user_id) VALUES (:rating.tipId, :rating.rating, :rating.userId) ON DUPLICATE KEY UPDATE rating = :rating.rating")
    int createRating(@BindBean("rating") TipRating rating);

    @SqlUpdate("INSERT INTO user (access_token) VALUES (:access_token)")
    @GetGeneratedKeys
    int createUser(@Bind("access_token") String access_token);

    @SqlQuery("SELECT access_token FROM user WHERE id = :id")
    @Mapper(SimpleTokenMapper.class)
    SimpleToken getAccessToken(@Bind("id") int id);

    @SqlQuery("SELECT id FROM user WHERE access_token = :access_token")
    int getUserId(@Bind("access_token") String access_token);

 }