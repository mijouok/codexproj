package com.nineties.alumni.home.service;

import com.nineties.alumni.auth.model.User;
import com.nineties.alumni.auth.repo.UserRepository;
import com.nineties.alumni.common.ApiException;
import com.nineties.alumni.home.dto.HomeResponse;
import com.nineties.alumni.home.model.HomeStatusPost;
import com.nineties.alumni.home.model.HomeWallMessage;
import com.nineties.alumni.home.repo.HomeStatusPostRepository;
import com.nineties.alumni.home.repo.HomeWallMessageRepository;
import com.nineties.alumni.space.model.Membership;
import com.nineties.alumni.space.model.MembershipStatus;
import com.nineties.alumni.space.model.Space;
import com.nineties.alumni.space.repo.MembershipRepository;
import com.nineties.alumni.space.repo.SpaceRepository;
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
  private final MembershipRepository membershipRepository;
  private final SpaceRepository spaceRepository;
  private final HomeStatusPostRepository homeStatusPostRepository;
  private final HomeWallMessageRepository homeWallMessageRepository;

  public HomeService(UserRepository userRepository,
                     MembershipRepository membershipRepository,
                     SpaceRepository spaceRepository,
                     HomeStatusPostRepository homeStatusPostRepository,
                     HomeWallMessageRepository homeWallMessageRepository) {
    this.userRepository = userRepository;
    this.membershipRepository = membershipRepository;
    this.spaceRepository = spaceRepository;
    this.homeStatusPostRepository = homeStatusPostRepository;
    this.homeWallMessageRepository = homeWallMessageRepository;
  }

  public HomeResponse buildHome(String userId) {
    User me = userRepository.findById(userId)
        .orElseThrow(() -> new ApiException("NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));

    List<Membership> memberships = membershipRepository.findByUserIdAndMembershipStatus(userId, MembershipStatus.ACTIVE);
    Map<String, Space> spacesById = spaceRepository.findAllById(
            memberships.stream().map(Membership::getSpaceId).distinct().toList()
        )
        .stream()
        .collect(Collectors.toMap(Space::getId, s -> s));

    List<HomeResponse.HomeSpace> spaceCards = memberships.stream()
        .map(m -> {
          Space s = spacesById.get(m.getSpaceId());
          String name = s != null ? s.getName() : "Unknown Space";
          return new HomeResponse.HomeSpace(name, m.getMembershipStatus().name());
        })
        .toList();

    List<String> spaceIds = memberships.stream()
        .map(Membership::getSpaceId)
        .distinct()
        .toList();

    Set<String> peerIds = spaceIds.isEmpty()
        ? Set.of()
        : membershipRepository.findBySpaceIdInAndMembershipStatus(spaceIds, MembershipStatus.ACTIVE)
            .stream()
            .map(Membership::getUserId)
            .filter(id -> !id.equals(userId))
            .collect(Collectors.toSet());

    Set<String> visibleUserIds = new HashSet<>(peerIds);
    visibleUserIds.add(userId);
    Map<String, String> nicknameById = userRepository.findAllById(visibleUserIds).stream()
        .collect(Collectors.toMap(User::getId, u -> displayName(u.getNickname()), (left, right) -> left));

    String statusText = homeStatusPostRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
        .map(HomeStatusPost::getContent)
        .orElse("还没有发布状态。");

    List<HomeResponse.HomeMessage> messages = homeWallMessageRepository.findTop20ByToUserIdOrderByCreatedAtDesc(userId)
        .stream()
        .map(m -> new HomeResponse.HomeMessage(
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
              nicknameById.getOrDefault(s.getUserId(), "Classmate"),
              "updated status: " + s.getContent(),
              "View",
              humanize(s.getCreatedAt())
          ))
          .toList();
    }

    List<HomeResponse.HomeAlbumItem> albums = List.of();

    List<HomeResponse.HomeVisitor> visitors = List.of();

    List<HomeResponse.HomeWidget> widgets = buildWidgets(userId, spaceCards.size(), peerIds);

    return new HomeResponse(
        primarySchool(spaceCards),
        primaryDepartment(spaceCards),
        statusText,
        spaceCards,
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

  public void createWallMessage(String userId, String content) {
    userRepository.findById(userId)
        .orElseThrow(() -> new ApiException("NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));

    HomeWallMessage message = new HomeWallMessage();
    message.setFromUserId(userId);
    message.setToUserId(userId);
    message.setContent(content.trim());
    message.setCreatedAt(Instant.now());
    homeWallMessageRepository.save(message);
  }

  private List<HomeResponse.HomeWidget> buildWidgets(String userId, int spaceCount, Set<String> peerIds) {
    long messageCount = homeWallMessageRepository.countByToUserId(userId);
    long peerActivityCount = peerIds.isEmpty() ? 0 : homeStatusPostRepository.countByUserIdIn(peerIds);
    return List.of(
        new HomeResponse.HomeWidget("已加入空间", String.valueOf(spaceCount)),
        new HomeResponse.HomeWidget("收到留言", String.valueOf(messageCount)),
        new HomeResponse.HomeWidget("同空间动态", String.valueOf(peerActivityCount))
    );
  }

  private String primarySchool(List<HomeResponse.HomeSpace> spaceCards) {
    if (spaceCards.isEmpty()) {
      return "未加入空间";
    }
    return spaceCards.get(0).name();
  }

  private String primaryDepartment(List<HomeResponse.HomeSpace> spaceCards) {
    if (spaceCards.isEmpty()) {
      return "待完善资料";
    }
    return "来自 " + spaceCards.get(0).name();
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
