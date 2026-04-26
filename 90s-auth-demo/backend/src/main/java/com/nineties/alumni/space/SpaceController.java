package com.nineties.alumni.space;

import com.nineties.alumni.security.SecurityUtil;
import com.nineties.alumni.space.dto.*;
import com.nineties.alumni.space.service.SpaceService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/spaces")
public class SpaceController {

  private final SpaceService spaceService;

  public SpaceController(SpaceService spaceService) {
    this.spaceService = spaceService;
  }

  @PostMapping
  @PreAuthorize("hasRole('platform_admin')")
  public SpaceResponse createSpace(@Valid @RequestBody CreateSpaceRequest req) {
    String uid = SecurityUtil.requireCurrentUser().userId();
    return spaceService.createSpace(req, uid);
  }

  @GetMapping("/{id}")
  public SpaceResponse getSpace(@PathVariable String id) {
    String uid = SecurityUtil.requireCurrentUser().userId();
    return spaceService.getSpace(id, uid);
  }

  @PostMapping("/{id}/invite-codes")
  public InviteCodeResponse createInvite(@PathVariable String id, @Valid @RequestBody CreateInviteCodeRequest req) {
    String uid = SecurityUtil.requireCurrentUser().userId();
    String code = spaceService.createInviteCode(id, req, uid);
    return new InviteCodeResponse(code);
  }

  @PostMapping("/join-by-code")
  public MembershipResponse joinByCode(@Valid @RequestBody JoinByCodeRequest req) {
    String uid = SecurityUtil.requireCurrentUser().userId();
    return spaceService.joinByCode(req.getCode(), uid);
  }

  @GetMapping("/{id}/members/me")
  public MembershipResponse myMembership(@PathVariable String id) {
    String uid = SecurityUtil.requireCurrentUser().userId();
    return spaceService.myMembership(id, uid);
  }
}
