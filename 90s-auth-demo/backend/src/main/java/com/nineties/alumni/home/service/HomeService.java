package com.nineties.alumni.home.service;

import com.nineties.alumni.auth.model.User;
import com.nineties.alumni.auth.repo.UserRepository;
import com.nineties.alumni.common.ApiException;
import com.nineties.alumni.friend.service.FriendService;
import com.nineties.alumni.home.dto.HomeResponse;
import com.nineties.alumni.home.model.HomeStatusPost;
import com.nineties.alumni.home.model.HomeWallMessage;
import com.nineties.alumni.home.repo.HomeStatusPostRepository;
import com.nineties.alumni.home.repo.HomeWallMessageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HomeService {

  private final UserRepository userRepository;
  private final HomeStatusPostRepository homeStatusPostRepository;
  private final HomeWallMessageRepository homeWallMessageRepository;
  private final FriendService friendService;

  public HomeService(UserRepository userRepository,
                     HomeStatusPostRepository homeStatusPostRepository,
                     HomeWallMessageRepository homeWallMessageRepository,
                     FriendService friendService) {
    this.userRepository = userRepository;
    this.homeStatusPostRepository = homeStatusPostRepository;
    this.homeWallMessageRepository = homeWallMessageRepository;
    this.friendService = friendService;
  }

  public HomeResponse buildHome(String userId) {
    return buildHome(userId, userId);
  }

  public HomeResponse buildHome(String viewerUserId, String profileUserId) {
    User profileUser = userRepository.findById(profileUserId)
        .orElseThrow(() -> new ApiException("NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));

    boolean owner = viewerUserId.equals(profileUserId);
    if (!owner) {
      requireProfileVisible(viewerUserId, profileUserId);
    }

    Set<String> peerIds = friendService.friendIds(profileUserId);

    Set<String> visibleUserIds = new HashSet<>(peerIds);
    visibleUserIds.add(profileUserId);
    Map<String, String> nicknameById = userRepository.findAllById(visibleUserIds).stream()
        .collect(Collectors.toMap(User::getId, u -> displayName(u.getNickname()), (left, right) -> left));

    String statusText = homeStatusPostRepository.findTopByUserIdOrderByCreatedAtDesc(profileUserId)
        .map(HomeStatusPost::getContent)
        .orElse("No status yet.");

    List<HomeResponse.HomeMessage> messages = homeWallMessageRepository.findTop20ByToUserIdOrderByCreatedAtDesc(profileUserId)
        .stream()
        .map(m -> new HomeResponse.HomeMessage(
            m.getFromUserId(),
            nicknameById.getOrDefault(m.getFromUserId(), "Classmate"),
            m.getContent(),
            "Reply",
            humanize(m.getCreatedAt())
        ))
        .toList();

    List<HomeResponse.HomeActivity> activities = new ArrayList<>();
    if (!peerIds.isEmpty()) {
      activities = homeStatusPostRepository.findTop20ByUserIdInOrderByCreatedAtDesc(peerIds)
          .stream()
          .map(s -> new HomeResponse.HomeActivity(
              s.getUserId(),
              nicknameById.getOrDefault(s.getUserId(), "Classmate"),
              "updated status: " + s.getContent(),
              "View",
              humanize(s.getCreatedAt())
          ))
          .toList();
    }

    List<HomeResponse.HomeAlbumItem> albums = List.of();

    List<HomeResponse.HomeVisitor> visitors = List.of();

    List<HomeResponse.HomeWidget> widgets = buildWidgets(profileUserId, peerIds);

    return new HomeResponse(
        profileUser.getId(),
        displayName(profileUser.getNickname()),
        owner ? profileUser.getEmail() : null,
        owner ? profileUser.getPhone() : null,
        profileUser.getTrustLevel(),
        owner,
        friendService.relationship(viewerUserId, profileUserId),
        owner ? null : friendService.pendingRequestId(viewerUserId, profileUserId),
        "Campus Network",
        owner ? "My profile" : "Classmate profile",
        statusText,
        messages,
        activities,
        albums,
        visitors,
        widgets
    );
  }

  public void createStatus(String userId, String content) {
    userRepository.findById(userId)
        .orElseThrow(() -> new ApiException("NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));

    HomeStatusPost status = new HomeStatusPost();
    status.setUserId(userId);
    status.setContent(content.trim());
    status.setCreatedAt(Instant.now());
    homeStatusPostRepository.save(status);
  }

  public void createWallMessage(String fromUserId, String toUserId, String content) {
    userRepository.findById(fromUserId)
        .orElseThrow(() -> new ApiException("NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));
    userRepository.findById(toUserId)
        .orElseThrow(() -> new ApiException("NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));
    if (!fromUserId.equals(toUserId)) {
      requireProfileVisible(fromUserId, toUserId);
    }

    HomeWallMessage message = new HomeWallMessage();
    message.setFromUserId(fromUserId);
    message.setToUserId(toUserId);
    message.setContent(content.trim());
    message.setCreatedAt(Instant.now());
    homeWallMessageRepository.save(message);
  }

  private void requireProfileVisible(String viewerUserId, String profileUserId) {
    if (!friendService.canViewProfile(viewerUserId, profileUserId)) {
      throw new ApiException("PROFILE_NOT_VISIBLE", "Profile is not visible", HttpStatus.FORBIDDEN);
    }
  }

  private List<HomeResponse.HomeWidget> buildWidgets(String userId, Set<String> peerIds) {
    long messageCount = homeWallMessageRepository.countByToUserId(userId);
    long peerActivityCount = peerIds.isEmpty() ? 0 : homeStatusPostRepository.countByUserIdIn(peerIds);
    return List.of(
        new HomeResponse.HomeWidget("Friends", String.valueOf(peerIds.size())),
        new HomeResponse.HomeWidget("Wall messages", String.valueOf(messageCount)),
        new HomeResponse.HomeWidget("Friend activities", String.valueOf(peerActivityCount))
    );
  }

  private String displayName(String nickname) {
    if (nickname == null || nickname.isBlank()) {
      return "Classmate";
    }
    return nickname;
  }

  private String humanize(Instant ts) {
    if (ts == null) {
      return "Recently";
    }

    Duration delta = Duration.between(ts, Instant.now());
    if (delta.isNegative()) {
      return "Just now";
    }

    long minutes = delta.toMinutes();
    if (minutes < 1) {
      return "Just now";
    }
    if (minutes < 60) {
      return minutes + " min ago";
    }

    long hours = delta.toHours();
    if (hours < 24) {
      return hours + " h ago";
    }

    long days = delta.toDays();
    return days + " d ago";
  }
}
