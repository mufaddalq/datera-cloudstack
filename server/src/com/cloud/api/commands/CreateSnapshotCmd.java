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
import com.cloud.api.BaseAsyncCreateCmd;
import com.cloud.api.BaseCmd.Manager;
import com.cloud.api.Implementation;
import com.cloud.api.Parameter;
import com.cloud.api.response.SnapshotResponse;
import com.cloud.storage.Snapshot.SnapshotType;
import com.cloud.storage.SnapshotVO;
import com.cloud.storage.VolumeVO;
import com.cloud.user.Account;

@Implementation(createMethod="createSnapshotDB", method="createSnapshot", manager=Manager.SnapshotManager)
public class CreateSnapshotCmd extends BaseAsyncCreateCmd {
	public static final Logger s_logger = Logger.getLogger(CreateSnapshotCmd.class.getName());
	private static final String s_name = "createsnapshotresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name="account", type=CommandType.STRING)
    private String accountName;

    @Parameter(name="domainid", type=CommandType.LONG)
    private Long domainId;

    @Parameter(name="volumeid", type=CommandType.LONG, required=true)
    private Long volumeId;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public String getAccountName() {
        return accountName;
    }

    public Long getDomainId() {
        return domainId;
    }

    public Long getVolumeId() {
        return volumeId;
    }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

    @Override
    public String getName() {
        return s_name;
    }
    
    public static String getResultObjectName() {
    	return "snapshot";
    }

    @Override @SuppressWarnings("unchecked")
    public SnapshotResponse getResponse() {
        SnapshotVO snapshot = (SnapshotVO)getResponseObject();

        SnapshotResponse response = new SnapshotResponse();
        response.setId(snapshot.getId());

        Account account = ApiDBUtils.findAccountById(snapshot.getAccountId());
        if (account != null) {
            response.setAccountName(account.getAccountName());
            response.setDomainId(account.getDomainId());
            response.setDomainName(ApiDBUtils.findDomainById(account.getDomainId()).getName());
        }

        VolumeVO volume = ApiDBUtils.findVolumeById(snapshot.getVolumeId());
        String snapshotTypeStr = SnapshotType.values()[snapshot.getSnapshotType()].name();
        response.setSnapshotType(snapshotTypeStr);
        response.setVolumeId(snapshot.getVolumeId());
        response.setVolumeName(volume.getName());
        response.setVolumeType(volume.getVolumeType().toString());
        response.setCreated(snapshot.getCreated());
        response.setName(snapshot.getName());

        response.setResponseName(getName());
        return response;
    }
}
