import { createSlice } from '@reduxjs/toolkit';

const appSlice = createSlice({
  name: 'app',
  initialState: {
    isLoading: false
  },
  reducers: {
    setLoading(state, action) {
     state.isLoading = action.payload
    },
  },
});

const { actions, reducer } = appSlice;

export const { setLoading } = actions;

export default reducer;
