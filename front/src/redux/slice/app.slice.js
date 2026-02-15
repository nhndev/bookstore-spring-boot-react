import { createSlice } from '@reduxjs/toolkit';

const appSlice = createSlice({
  name: 'app',
  initialState: {
    isLoading: false,
    errorPage: {
      show: false,
      status: null,
      message: ''
    }
  },
  reducers: {
    setLoading(state, action) {
      state.isLoading = action.payload;
    },
    setErrorPage(state, action) {
      state.errorPage = {
        show: true,
        status: action.payload?.status ?? null,
        message: action.payload?.message ?? ''
      };
    },
    clearErrorPage(state) {
      state.errorPage = { show: false, status: null, message: '' };
    }
  }
});

const { actions, reducer } = appSlice;

export const { setLoading, setErrorPage, clearErrorPage } = actions;

export default reducer;
