package me.exrates.service.nodes_observe;

import me.exrates.model.dto.NodesInfoDto;

import java.util.List;

public interface StatesHolder {
    List<NodesInfoDto> getNodesCurrentStates();
}
