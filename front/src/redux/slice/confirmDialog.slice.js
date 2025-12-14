import { createSlice } from '@reduxjs/toolkit';

const confirmDialogSlice = createSlice({
  name: 'confirmDialog',
  initialState: {
    isShow: false,
    message: '',
    okFunc: null,
    cancelFunc: null
  },
  reducers: {
    setShow(state, action) {
     state.isShow = action.payload
    },
    setMessage(state, action) {
     state.message = action.payload
    },
    setOkFunc(state, action) {
     state.okFunc = action.payload
    },
    setCancelFunc(state, action) {
     state.cancelFunc = action.payload
    },
    showConfirmDialog(state, { payload: { message, okFunc } }) {
      state.isShow = true
      state.message = message
      state.okFunc = okFunc
    }
  },
});

const { actions, reducer } = confirmDialogSlice;

export const { setShow, setMessage, setOkFunc, setCancelFunc, showConfirmDialog } = actions;

export default reducer;
