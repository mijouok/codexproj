import { http } from "./http";
import type { HomeResponse } from "./types";

export const homeApi = {
  getHome: async (): Promise<HomeResponse> => {
    const { data } = await http.get("/api/home");
    return data;
  },

  getUserHome: async (userId: string): Promise<HomeResponse> => {
    const { data } = await http.get(`/api/home/users/${userId}`);
    return data;
  },

  postStatus: async (content: string): Promise<void> => {
    await http.post("/api/home/status", { content });
  },

  postMessage: async (content: string): Promise<void> => {
    await http.post("/api/home/messages", { content });
  },

  postUserMessage: async (userId: string, content: string): Promise<void> => {
    await http.post(`/api/home/users/${userId}/messages`, { content });
  }
};
