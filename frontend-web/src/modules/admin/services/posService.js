import api from "../../../api/axios";

const root = "/admin/pos-terminals";

export const listTerminals = () => api.get(root).then(({ data }) => data);
export const registerTerminal = (payload) => api.post(root, payload).then(({ data }) => data);
export const assignVoyage = (code, voyageId) =>
  api.put(`${root}/${encodeURIComponent(code)}/voyage`, { voyageId });
export const listVoyagePassengers = (voyageId) =>
  api.get(`${root}/voyages/${voyageId}/passengers`).then(({ data }) => data);
export const listCredentials = (voyageId) =>
  api.get(`${root}/credentials`, { params: { voyageId } }).then(({ data }) => data);
export const issueCredential = (payload) =>
  api.post(`${root}/credentials`, payload).then(({ data }) => data);
export const revokeCredential = (id) => api.patch(`${root}/credentials/${id}/revoke`);
