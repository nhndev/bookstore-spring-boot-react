import { configureStore } from '@reduxjs/toolkit';
import authReducer from './slice/auth.slice';
import appReducer from './slice/app.slice';
import confirmDialogReducer from './slice/confirmDialog.slice';

const rootReducer = {
  auth: authReducer,
  app: appReducer,
  confirmDialog: confirmDialogReducer
};

const store = configureStore({
  reducer: rootReducer,
  middleware: (getDefaultMiddleware) => getDefaultMiddleware({serializableCheck: false})
});

export default store;
