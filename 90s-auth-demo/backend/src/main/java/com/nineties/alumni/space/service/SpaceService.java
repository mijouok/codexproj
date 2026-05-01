package com.nineties.alumni.space.service;

import com.nineties.alumni.auth.service.RoleNames;
import com.nineties.alumni.auth.service.RoleService;
import com.nineties.alumni.common.ApiException;
import com.nineties.alumni.security.SecurityUtil;
import com.nineties.alumni.space.dto.CreateInviteCodeRequest;
import com.nineties.alumni.space.dto.CreateSpaceRequest;
import com.nineties.alumni.space.dto.MembershipResponse;
import com.nineties.alumni.space.dto.SpaceResponse;
import com.nineties.alumni.space.model.*;
import com.nineties.alumni.space.repo.MembershipRepository;
import com.nineties.alumni.space.repo.SpaceInviteCodeRepository;
import com.nineties.alumni.space.repo.SpaceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class SpaceService {
  private final SpaceRepository spaceRepository;
  private final SpaceInviteCodeRepository inviteCodeRepository;
  private final MembershipRepository membershipRepository;
  private final RoleService roleService;

  public SpaceService(SpaceRepository spaceRepository,
                      SpaceInviteCodeRepository inviteCodeRepository,
                      MembershipRepository membershipRepository,
                      RoleService roleService) {
    this.spaceRepository = spaceRepository;
    this.inviteCodeRepository = inviteCodeRepository;
    this.membershipRepository = membershipRepository;
    this.roleService = roleService;
  }

  public SpaceResponse createSpace(CreateSpaceRequest req, String creatorUserId) {
    if (spaceRepository.findBySlug(req.getSlug()).isPresent()) {
      throw new ApiException("SPACE_SLUG_TAKEN", "Space slug already exists", HttpStatus.CONFLICT);
    }
    Space s = new Space();
    s.setName(req.getName());
    s.setSlug(req.getSlug());
    s.setCreatedBy(creatorUserId);
    s.setVisibility(SpaceVisibility.INVITE_ONLY);
    Space saved = spaceRepository.save(s);

    // Add creator as ACTIVE member
    Membership m = new Membership();
    m.setSpaceId(saved.getId());
    m.setUserId(creatorUserId);
    m.setMembershipStatus(MembershipStatus.ACTIVE);
    m.setJoinedAt(Instant.now());
    membershipRepository.save(m);

    // Assign roles within the space
    roleService.assignSpaceRole(creatorUserId, saved.getId(), RoleNames.SPACE_OWNER);
    roleService.assignSpaceRole(creatorUserId, saved.getId(), RoleNames.SPACE_ADMIN);
    roleService.assignSpaceRole(creatorUserId, saved.getId(), RoleNames.MEMBER);

    return new SpaceResponse(saved.getId(), saved.getName(), saved.getSlug());
  }

  public String createInviteCode(String spaceId, CreateInviteCodeRequest req, String requesterUserId) {
    requireActiveMember(spaceId, requesterUserId);
    InviteCodeType inviteCodeType;
    try {
      inviteCodeType = InviteCodeType.valueOf(req.getType());
    } catch (IllegalArgumentException ex) {
      throw new ApiException("INVALID_INVITE_TYPE", "Invite code type is invalid", HttpStatus.BAD_REQUEST);
    }

    SpaceInviteCode code = new SpaceInviteCode();
    code.setSpaceId(spaceId);
    code.setCode(generateCode());
    code.setCreatedBy(requesterUserId);
    code.setType(inviteCodeType);
    code.setMaxUses(req.getMaxUses() == null ? 50 : req.getMaxUses());
    int days = req.getExpiresInDays() == null ? 30 : req.getExpiresInDays();
    code.setExpiresAt(Instant.now().plusSeconds(days * 86400L));
    inviteCodeRepository.save(code);
    return code.getCode();
  }

  public MembershipResponse joinByCode(String codeStr, String userId) {
    SpaceInviteCode code = inviteCodeRepository.findByCode(codeStr)
        .orElseThrow(() -> new ApiException("INVITE_NOT_FOUND", "Invalid invite code", HttpStatus.NOT_FOUND));

    if (code.getExpiresAt() != null && Instant.now().isAfter(code.getExpiresAt())) {
      throw new ApiException("INVITE_EXPIRED", "Invite code expired", HttpStatus.BAD_REQUEST);
    }

    if (code.getUsedCount() != null && code.getMaxUses() != null && code.getUsedCount() >= code.getMaxUses()) {
      throw new ApiException("INVITE_EXHAUSTED", "Invite code is used up", HttpStatus.BAD_REQUEST);
    }

    String spaceId = code.getSpaceId();

    Membership existing = membershipRepository.findBySpaceIdAndUserId(spaceId, userId).orElse(null);
    if (existing != null) {
      return new MembershipResponse(spaceId, userId, existing.getMembershipStatus().name());
    }

    Membership m = new Membership();
    m.setSpaceId(spaceId);
    m.setUserId(userId);
    m.setMembershipStatus(MembershipStatus.ACTIVE);
    m.setJoinedAt(Instant.now());
    membershipRepository.save(m);

    // count usage
    code.setUsedCount(code.getUsedCount() == null ? 1 : code.getUsedCount() + 1);
    inviteCodeRepository.save(code);

    // basic member role
    roleService.assignSpaceRole(userId, spaceId, RoleNames.MEMBER);

    return new MembershipResponse(spaceId, userId, m.getMembershipStatus().name());
  }

  public SpaceResponse getSpace(String spaceId, String userId) {
    requireActiveMember(spaceId, userId);
    Space s = spaceRepository.findById(spaceId)
        .orElseThrow(() -> new ApiException("SPACE_NOT_FOUND", "Space not found", HttpStatus.NOT_FOUND));
    return new SpaceResponse(s.getId(), s.getName(), s.getSlug());
  }

  public MembershipResponse myMembership(String spaceId, String userId) {
    Membership m = requireActiveMember(spaceId, userId);
    return new MembershipResponse(spaceId, userId, m.getMembershipStatus().name());
  }

  private Membership requireActiveMember(String spaceId, String userId) {
    Membership m = membershipRepository.findBySpaceIdAndUserId(spaceId, userId)
        .orElseThrow(() -> new ApiException("NOT_A_MEMBER", "You are not a member of this space", HttpStatus.FORBIDDEN));
    if (m.getMembershipStatus() != MembershipStatus.ACTIVE) {
      throw new ApiException("MEMBERSHIP_INACTIVE", "Membership is not active", HttpStatus.FORBIDDEN);
    }
    return m;
  }

  private String generateCode() {
    byte[] bytes = new byte[9];
    new SecureRandom().nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }
}
