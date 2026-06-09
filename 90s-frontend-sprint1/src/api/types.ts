export type TokenPair = {
  access_token?: string;
  refresh_token?: string;
  accessToken?: string;
  refreshToken?: string;
};

export type MeResponse = {
  userId: string;
  nickname: string;
  email?: string;
  phone?: string;
  trustLevel?: 0 | 1 | 2 | 3;
  trust_level: 0 | 1 | 2 | 3;
  roles: string[];
};

export type LoginReq = { identifier: string; password: string };
export type RegisterReq = { identifier: string; nickname: string; password: string };

export type HomeMessage = {
  fromUserId: string;
  fromNickname: string;
  content: string;
  actionText: string;
  timeText: string;
};

export type HomeActivity = {
  actorUserId: string;
  actorNickname: string;
  content: string;
  actionText: string;
  timeText: string;
};

export type HomeAlbumItem = {
  title: string;
  marker: string;
};

export type HomeVisitor = {
  nickname: string;
  note: string;
  timeText: string;
};

export type HomeWidget = {
  title: string;
  content: string;
};

export type HomeResponse = {
  userId: string;
  nickname: string;
  email?: string;
  phone?: string;
  trustLevel: 0 | 1 | 2 | 3;
  owner: boolean;
  friendStatus: "SELF" | "NONE" | "OUTGOING" | "INCOMING" | "FRIEND" | string;
  friendRequestId?: string;
  school: string;
  department: string;
  statusText: string;
  messages: HomeMessage[];
  activities: HomeActivity[];
  albums: HomeAlbumItem[];
  visitors: HomeVisitor[];
  widgets: HomeWidget[];
};

export type FriendRequest = {
  id: string;
  requesterId: string;
  requesterNickname: string;
  recipientId: string;
  recipientNickname: string;
  status: "PENDING" | "ACCEPTED" | "REJECTED" | string;
  message?: string;
  createdAt: string;
  respondedAt?: string;
};

export type FriendSummary = {
  userId: string;
  nickname: string;
  trustLevel: number;
  createdAt: string;
};

export type FriendOverview = {
  friends: FriendSummary[];
  incoming: FriendRequest[];
  outgoing: FriendRequest[];
  suggestions: FriendSummary[];
};
