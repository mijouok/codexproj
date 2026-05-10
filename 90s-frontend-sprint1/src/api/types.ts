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
