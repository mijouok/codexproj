package com.nineties.alumni.home;

import com.nineties.alumni.home.dto.CreateStatusRequest;
import com.nineties.alumni.home.dto.CreateWallMessageRequest;
import com.nineties.alumni.home.dto.HomeResponse;
import com.nineties.alumni.home.service.HomeService;
import com.nineties.alumni.security.CurrentUser;
import com.nineties.alumni.security.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class HomeController {

  private final HomeService homeService;

  public HomeController(HomeService homeService) {
    this.homeService = homeService;
  }

  @GetMapping
  public HomeResponse getHome() {
    CurrentUser cu = SecurityUtil.requireCurrentUser();
    return homeService.buildHome(cu.userId());
  }

  @PostMapping("/status")
  public void createStatus(@Valid @RequestBody CreateStatusRequest request) {
    CurrentUser cu = SecurityUtil.requireCurrentUser();
    homeService.createStatus(cu.userId(), request.getContent());
  }

  @PostMapping("/messages")
  public void createMessage(@Valid @RequestBody CreateWallMessageRequest request) {
    CurrentUser cu = SecurityUtil.requireCurrentUser();
    homeService.createWallMessage(cu.userId(), request.getContent());
  }
}
