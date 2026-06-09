package com.nineties.alumni.home.dto;

import java.util.List;

public record HomeResponse(
    String userId,
    String nickname,
    String email,
    String phone,
    int trustLevel,
    boolean owner,
    String friendStatus,
    String friendRequestId,
    String school,
    String department,
    String statusText,
    List<HomeMessage> messages,
    List<HomeActivity> activities,
    List<HomeAlbumItem> albums,
    List<HomeVisitor> visitors,
    List<HomeWidget> widgets
) {
  public record HomeMessage(String fromUserId, String fromNickname, String content, String actionText, String timeText) {}

  public record HomeActivity(String actorUserId, String actorNickname, String content, String actionText, String timeText) {}

  public record HomeAlbumItem(String title, String marker) {}

  public record HomeVisitor(String nickname, String note, String timeText) {}

  public record HomeWidget(String title, String content) {}
}
