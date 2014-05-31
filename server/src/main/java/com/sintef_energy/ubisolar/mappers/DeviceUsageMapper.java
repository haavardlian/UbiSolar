/*
 * Licensed to UbiCollab.org under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership. UbiCollab.org licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.sintef_energy.ubisolar.mappers;

import com.sintef_energy.ubisolar.structs.DeviceUsage;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by haavard on 2/19/14.
 */
public class DeviceUsageMapper implements ResultSetMapper<DeviceUsage>
{
    public DeviceUsage map(int index, ResultSet r, StatementContext ctx) throws SQLException
    {
        return new DeviceUsage(r.getLong("id"), r.getLong("device_id"), r.getLong("timestamp"),
                r.getDouble("power_usage"), r.getBoolean("deleted"), r.getLong("last_updated"));

    }
}
