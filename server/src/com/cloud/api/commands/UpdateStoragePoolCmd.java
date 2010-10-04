/**
 *  Copyright (C) 2010 Cloud.com, Inc.  All rights reserved.
 * 
 * This software is licensed under the GNU General Public License v3 or later.
 * 
 * It is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package com.cloud.api.commands;

import org.apache.log4j.Logger;

import com.cloud.api.ApiDBUtils;
import com.cloud.api.BaseCmd;
import com.cloud.api.BaseCmd.Manager;
import com.cloud.api.Implementation;
import com.cloud.api.Parameter;
import com.cloud.api.response.StoragePoolResponse;
import com.cloud.dc.ClusterVO;
import com.cloud.storage.StoragePoolVO;
import com.cloud.storage.StorageStats;

@Implementation(method="updateStoragePool", manager=Manager.StorageManager)
public class UpdateStoragePoolCmd extends BaseCmd {
    public static final Logger s_logger = Logger.getLogger(UpdateStoragePoolCmd.class.getName());

    private static final String s_name = "updatestoragepoolresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name="id", type=CommandType.LONG, required=true)
    private Long id;

    @Parameter(name="tags", type=CommandType.STRING)
    private String tags;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public Long getId() {
        return id;
    }

    public String getTags() {
        return tags;
    }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

    @Override
    public String getName() {
        return s_name;
    }

    @Override @SuppressWarnings("unchecked")
    public StoragePoolResponse getResponse() {
        StoragePoolVO pool = (StoragePoolVO) getResponseObject();

        StoragePoolResponse response = new StoragePoolResponse();
        response.setId(pool.getId());
        response.setZoneId(pool.getDataCenterId());
        response.setZoneName(ApiDBUtils.findZoneById(pool.getDataCenterId()).getName());
        if (pool.getPodId() != null) {
            response.setPodId(pool.getPodId());
            response.setPodName(ApiDBUtils.findPodById(pool.getPodId()).getName());
        }
        response.setName(pool.getName());
        response.setIpAddress(pool.getHostAddress());
        response.setPath(pool.getPath());
        response.setCreated(pool.getCreated());

        if (pool.getPoolType() != null) {
            response.setType(pool.getPoolType().toString());
        }

        if (pool.getClusterId() != null) {
            ClusterVO cluster = ApiDBUtils.findClusterById(pool.getClusterId());
            response.setClusterId(cluster.getId());
            response.setClusterName(cluster.getName());
        }

        StorageStats stats = ApiDBUtils.getStoragePoolStatistics(pool.getId());
        long capacity = pool.getCapacityBytes();
        long available = pool.getAvailableBytes();
        long used = capacity - available;

        if (stats != null) {
            used = stats.getByteUsed();
            available = capacity - used;
        }

        if (s_logger.isDebugEnabled()) {
            s_logger.debug("Successfully recieved the storagePool statistics. TotalDiskSize - " + capacity + " AllocatedDiskSize - " + used);
        }

        response.setDiskSizeTotal(pool.getCapacityBytes());
        response.setDiskSizeAllocated(used);
        response.setTags(ApiDBUtils.getStoragePoolTags(pool.getId()));

        response.setResponseName(getName());
        return response;
    }
}
