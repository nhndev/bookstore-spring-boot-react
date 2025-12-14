import httpClent from './httpClient';

const USER_API = '/api/v1/users';

const userService = {
  getProfile: () => {
    const url = USER_API + '/profile';
    return httpClent.get(url);
  }
};

export default userService;
