import { http } from "./http";

export const spacesApi = {
  joinByCode: async (code: string): Promise<void> => {
    await http.post("/api/spaces/join-by-code", { code });
  }
};
