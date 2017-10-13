/*-------------------------------------------------------------------------------------------------
 _______ __   _ _______ _______ ______  ______
 |_____| | \  |    |    |______ |     \ |_____]
 |     | |  \_|    |    ______| |_____/ |_____]

 Copyright (c) 2016, antsdb.com and/or its affiliates. All rights reserved. *-xguo0<@

 This program is free software: you can redistribute it and/or modify it under the terms of the
 GNU Affero General Public License, version 3, as published by the Free Software Foundation.

 You should have received a copy of the GNU Affero General Public License along with this program.
 If not, see <https://www.gnu.org/licenses/agpl-3.0.txt>
-------------------------------------------------------------------------------------------------*/
package com.antsdb.saltedfish.sql;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.antsdb.saltedfish.cpp.MemoryManager;
import com.antsdb.saltedfish.sql.vdm.Cursor;
import com.antsdb.saltedfish.sql.vdm.CursorMaker;
import com.antsdb.saltedfish.sql.vdm.CursorMeta;
import com.antsdb.saltedfish.sql.vdm.Parameters;
import com.antsdb.saltedfish.sql.vdm.VdmContext;
import com.antsdb.saltedfish.util.CursorUtil;
import com.antsdb.saltedfish.util.LongLong;
import com.antsdb.saltedfish.util.UberFormatter;
import com.antsdb.saltedfish.util.UberUtil;

/**
 * 
 * @author wgu0
 */
public class SystemInfoView extends CursorMaker {
	Orca orca;
	CursorMeta meta;
	
	public SystemInfoView(Orca orca) {
		this.orca = orca;
		meta = CursorUtil.toMeta(Properties.class);
	}

	@Override
	public CursorMeta getCursorMeta() {
		return meta;
	}

	@Override
	public Object run(VdmContext ctx, Parameters params, long pMaster) {
	    Map<String, Object> props = new HashMap<>();
	    props.put("antsdb.last_sp", this.orca.getHumpback().getLatestSP());
	    props.put("antsdb.memory_allocated", MemoryManager.getAllocated());
	    props.put("antsdb.memory_pooled", MemoryManager.getPooled());
        props.put("antsdb.trx_service_size", this.orca.getTrxMan().size());
        props.put("antsdb.trx_service_oldest", this.orca.getTrxMan().getOldest());
        props.put("antsdb.trx_service_last", this.orca.getTrxMan().getLastTrxId());
        props.put("antsdb.statistician_log_pointer", getStatisticianLogPointer());
        props.put("antsdb.storage_log_pointer", getStorageLogPointer());
        props.put("runtime.runtime_total_memory", Runtime.getRuntime().totalMemory());
        props.put("runtime.runtime_free_memory", Runtime.getRuntime().freeMemory());
        props.put("runtime.runtime_max_memory", Runtime.getRuntime().maxMemory());
        props.put("runtime.runtime_available_processors", Runtime.getRuntime().availableProcessors());
        props.put("vm.java_vm_info", System.getProperty("java.vm.info"));
        props.put("vm.java_vm_name", System.getProperty("java.vm.name"));
        props.put("vm.java_vm_vendor", System.getProperty("java.vm.vendor"));
        props.put("vm.java_vm_specification", System.getProperty("java.vm.specification.version"));
        props.put("vm.java_vm_version", System.getProperty("java.vm.version"));
        props.put("system.system cpu load", UberUtil.getSystemCpuLoad());
        props.put("system.process cpu load", UberUtil.getProcessCpuLoad());
		
		// done
        Cursor c = CursorUtil.toCursor(this.meta, props);
        return c;
	}

	String getStatisticianLogPointer() {
	    long result = orca.getHumpback().getStatistician().getReplicateLogPointer();
	    return UberFormatter.hex(result);
	}
	
	String getStorageLogPointer() {
	    LongLong result = orca.getHumpback().getStorageEngine().getLogSpan();
	    return (result != null) ? UberFormatter.hex(result.y) : "";
	}
}