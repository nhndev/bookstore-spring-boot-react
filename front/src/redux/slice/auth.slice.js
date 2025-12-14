import { createSlice } from '@reduxjs/toolkit';

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    id: '',
    email: '',
    fullName: '',
    phoneNumber: '',
    role: '',
    avatarUrl: '',
    permissions: '',
  },
  reducers: {
    setUser(_state, action) {
     return {...action.payload }
    },
  },
});

const { actions, reducer } = authSlice;

export const { setUser } = actions;

export default reducer;
