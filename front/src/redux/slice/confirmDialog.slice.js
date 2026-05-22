import { createSlice } from '@reduxjs/toolkit';

const confirmDialogSlice = createSlice({
  name: 'confirmDialog',
  initialState: {
    isShow: false,
    message: '',
    okFunc: null,
    cancelFunc: null,
    variant: 'confirm', // 'confirm' | 'error'
  },
  reducers: {
    setShow(state, action) {
      state.isShow = action.payload;
    },
    setMessage(state, action) {
      state.message = action.payload;
    },
    setOkFunc(state, action) {
      state.okFunc = action.payload;
    },
    setCancelFunc(state, action) {
      state.cancelFunc = action.payload;
    },
    showConfirmDialog(state, { payload: { message, okFunc } }) {
      state.isShow = true;
      state.message = message;
      state.okFunc = okFunc;
      state.variant = 'confirm';
    },
    showErrorDialog(state, { payload: { message } }) {
      state.isShow = true;
      state.message = message || 'Đã xảy ra lỗi.';
      state.okFunc = null;
      state.cancelFunc = null;
      state.variant = 'error';
    },
  },
});

const { actions, reducer } = confirmDialogSlice;

export const { setShow, setMessage, setOkFunc, setCancelFunc, showConfirmDialog, showErrorDialog } = actions;

export default reducer;
