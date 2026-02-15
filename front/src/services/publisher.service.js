import httpClent from './httpClient';

const PUBLISHER_API = '/api/v1/book-publishers';

const publisherService = {
  search: (params) => {
    return httpClent.get(PUBLISHER_API, { params });
  },
  create: (data) => {
    return httpClent.post(PUBLISHER_API, data);
  },
  update: (id, data) => {
    return httpClent.put(`${PUBLISHER_API}/${id}`, data);
  },
  delete: (id) => {
    return httpClent.delete(`${PUBLISHER_API}/${id}`);
  },
};

export default publisherService;
