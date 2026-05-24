import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  id: '',
  email: '',
  fullName: '',
  phoneNumber: '',
  role: '',
  avatarUrl: '',
  permissions: '',
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setUser(_state, action) {
      return { ...action.payload };
    },
    clearUser() {
      return { ...initialState };
    },
  },
});

const { actions, reducer } = authSlice;

export const { setUser, clearUser } = actions;

export default reducer;
