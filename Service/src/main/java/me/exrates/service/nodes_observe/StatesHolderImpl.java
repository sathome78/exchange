package me.exrates.service.nodes_observe;


import com.sun.org.apache.xalan.internal.lib.NodeInfo;
import me.exrates.model.dto.NodesInfoDto;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatesHolderImpl implements StatesHolder {

    Map<String, NodesInfoDto> map = new HashMap<>();

    @PostConstruct
    private void init() {
        /*init map here*/
        /*mock implementation*/
        for (int i = 0; i < 120; i++) {
            NodesInfoDto dto = makeRandomDto();
            map.put(dto.getNodeName(), dto);
        }

    }

    public Map<String, NodesInfoDto> getMap() {
        return map;
    }


    @Override
    public List<NodesInfoDto> getNodesCurrentStates() {
        return new ArrayList<>(map.values());
    }

    private NodesInfoDto makeRandomDto() {
        NodesInfoDto nodesInfoDto = new NodesInfoDto();
        nodesInfoDto.setNodeName(RandomStringUtils.random(10));
        nodesInfoDto.setNodeWork(Math.random() < 0.5);
        nodesInfoDto.setNodeWorkCorrect(Math.random() < 0.5);
        nodesInfoDto.setLastPollingTime(LocalDateTime.now());
        return nodesInfoDto;
    }
}
