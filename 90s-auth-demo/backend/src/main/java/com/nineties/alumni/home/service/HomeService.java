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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

    List<User> peers = userRepository.findAll().stream()
        .filter(u -> !u.getId().equals(userId))
        .sorted(Comparator.comparing(User::getLastLoginAt, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(User::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
        .toList();

    Map<String, String> nicknameById = userRepository.findAll().stream()
        .collect(Collectors.toMap(User::getId, u -> displayName(u.getNickname()), (left, right) -> left));

    String statusText = homeStatusPostRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
        .map(HomeStatusPost::getContent)
        .orElse("Busy with finals these days.");

    List<HomeResponse.HomeMessage> messages = homeWallMessageRepository.findTop20ByToUserIdOrderByCreatedAtDesc(userId)
        .stream()
        .map(m -> new HomeResponse.HomeMessage(
            nicknameById.getOrDefault(m.getFromUserId(), "Classmate"),
            m.getContent(),
            "Reply",
            humanize(m.getCreatedAt())
        ))
        .toList();

    if (messages.isEmpty()) {
      messages = defaultMessages(peers);
    }

    List<String> peerIds = peers.stream().map(User::getId).toList();
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

    if (activities.isEmpty()) {
      activities = defaultActivities(peers);
    }

    List<HomeResponse.HomeAlbumItem> albums = List.of(
        new HomeResponse.HomeAlbumItem("Field Sunset", "1"),
        new HomeResponse.HomeAlbumItem("Library Corner", "2"),
        new HomeResponse.HomeAlbumItem("Dorm Chat", "3"),
        new HomeResponse.HomeAlbumItem("Campus Path", "4"),
        new HomeResponse.HomeAlbumItem("Club Poster", "5"),
        new HomeResponse.HomeAlbumItem("Final Week", "6")
    );

    List<HomeResponse.HomeVisitor> visitors = peers.stream()
        .limit(4)
        .map(u -> new HomeResponse.HomeVisitor(
            displayName(u.getNickname()),
            "Dropped by",
            humanize(u.getLastLoginAt() != null ? u.getLastLoginAt() : u.getUpdatedAt())
        ))
        .toList();

    if (visitors.isEmpty()) {
      visitors = List.of(
          new HomeResponse.HomeVisitor("Classmate A", "Dropped by", "Today"),
          new HomeResponse.HomeVisitor("Classmate B", "Checked your page", "Yesterday")
      );
    }

    List<HomeResponse.HomeWidget> widgets = List.of(
        new HomeResponse.HomeWidget("Mood", "Cloudy then sunny, good for a night walk."),
        new HomeResponse.HomeWidget("Now Playing", "Rice Fragrance on repeat."),
        new HomeResponse.HomeWidget("Dorm Notice", "Remember to pay utilities on Saturday night.")
    );

    return new HomeResponse(
        "Jiangcheng University",
        "School of Computer and Information Engineering",
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

  private List<HomeResponse.HomeMessage> defaultMessages(List<User> peers) {
    String n1 = pick(peers, 0, "Xiao Nan");
    String n2 = pick(peers, 1, "A Ze");
    String n3 = pick(peers, 2, "Tuan Tuan");
    return List.of(
        new HomeResponse.HomeMessage(n1, "See you in the library tomorrow morning.", "Reply", "Today 12:02"),
        new HomeResponse.HomeMessage(n2, "Your sunset photos from the field are amazing.", "Reply", "Today 09:47"),
        new HomeResponse.HomeMessage(n3, "Dropping by your page. Good luck this week!", "Reply", "Yesterday 22:16")
    );
  }

  private List<HomeResponse.HomeActivity> defaultActivities(List<User> peers) {
    String n1 = pick(peers, 0, "Lin");
    String n2 = pick(peers, 1, "A Miao");
    String n3 = pick(peers, 2, "Xiao Yu");
    return List.of(
        new HomeResponse.HomeActivity(n1, "uploaded a new album: Spring Field", "Open", "5 min ago"),
        new HomeResponse.HomeActivity(n2, "updated status: Cafeteria lunch was great today.", "Reply", "38 min ago"),
        new HomeResponse.HomeActivity(n3, "left a note on your wall.", "View", "Today 08:21")
    );
  }

  private String pick(List<User> peers, int index, String fallback) {
    if (index >= peers.size()) {
      return fallback;
    }
    return displayName(peers.get(index).getNickname());
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
