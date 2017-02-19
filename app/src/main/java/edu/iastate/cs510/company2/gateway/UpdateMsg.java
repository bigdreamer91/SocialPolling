package edu.iastate.cs510.company2.gateway;

import com.google.gson.Gson;

import edu.iastate.cs510.company2.models.Poll;

import static edu.iastate.cs510.company2.gateway.PsGateway.Cmd.update;

/**
 * Created by David on 11/1/2016.
 */

public class UpdateMsg extends Message {

    public UpdateMsg(String addr, String topic, String key, String id, String blob){
        super();
        super.server = addr;
        super.topic = topic;
        super.cmd = new PsGateway.Command(update);
        cmd.setParam("pageSize", "1");
        cmd.setParam("key", key);
        cmd.setParam("index", id);
        super.payload.put(key, blob);
        super.callback = null; //update never needs deferred?
    }

    public UpdateMsg(String addr, String topic, String key, String id, Poll poll){
        this(addr, topic, key, id, new Gson().toJson(poll));
    }

    public PsGateway.Response send(PsGateway gw){
        return gw.send(this);
    }

}
