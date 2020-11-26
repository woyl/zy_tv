package com.zhongyou.meettvapplicaion.whiteboard;

import com.herewhite.sdk.Room;
import com.herewhite.sdk.domain.MemberState;
import com.herewhite.sdk.domain.RoomPhase;
import com.herewhite.sdk.domain.SceneState;

public interface BoardEventListener {

    void onRoomPhaseChanged(RoomPhase phase);

    void onSceneStateChanged(SceneState state);

    void onMemberStateChanged(MemberState state);

    void onRoom(Room room);

}
