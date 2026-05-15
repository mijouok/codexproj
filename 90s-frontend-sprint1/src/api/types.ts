export type TokenPair = {
  access_token?: string;
  refresh_token?: string;
  accessToken?: string;
  refreshToken?: string;
};

export type MeSpace = {
  id: string;
  name: string;
  membership_status: "ACTIVE" | "PENDING" | "REJECTED";
};

export type MeResponse = {
  id: string;
  nickname: string;
  email?: string;
  phone?: string;
  trust_level: 0 | 1 | 2 | 3;
  spaces: MeSpace[];
};

export type LoginReq = { identifier: string; password: string };
export type RegisterReq = { identifier: string; nickname: string; password: string };

export type HomeSpace = {
  name: string;
  membershipStatus: "ACTIVE" | "PENDING" | "REJECTED" | string;
};

export type HomeMessage = {
  fromNickname: string;
  content: string;
  actionText: string;
  timeText: string;
};

export type HomeActivity = {
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
  school: string;
  department: string;
  statusText: string;
  spaces: HomeSpace[];
  messages: HomeMessage[];
  activities: HomeActivity[];
  albums: HomeAlbumItem[];
  visitors: HomeVisitor[];
  widgets: HomeWidget[];
};
