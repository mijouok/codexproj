package com.nineties.alumni.home.dto;

import java.util.List;

public record HomeResponse(
    String school,
    String department,
    String statusText,
    List<HomeSpace> spaces,
    List<HomeMessage> messages,
    List<HomeActivity> activities,
    List<HomeAlbumItem> albums,
    List<HomeVisitor> visitors,
    List<HomeWidget> widgets
) {
  public record HomeSpace(String name, String membershipStatus) {}

  public record HomeMessage(String fromNickname, String content, String actionText, String timeText) {}

  public record HomeActivity(String actorNickname, String content, String actionText, String timeText) {}

  public record HomeAlbumItem(String title, String marker) {}

  public record HomeVisitor(String nickname, String note, String timeText) {}

  public record HomeWidget(String title, String content) {}
}
