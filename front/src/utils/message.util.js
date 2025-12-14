import { toast } from 'react-toastify';

const messageUtil = {
  showErrorMessage: (msg) => {
    if (!msg) {
      return;
    }
    toast.error(msg, { autoClose: 2000 });
  },
  showSuccessMessage: (msg = 'Thao tác thành công!') => {
    toast.success(msg, { autoClose: 2000 });
  },
  getDeleteConfirmationMsg: (param) => {
    return `Thao tác ${param} không thể hoàn tác. Bạn vẫn muốn tiếp tục?`
  },
  getSearchConditionMissing:() => {
    return 'Vui lòng nhập ít nhất 1 điều kiện tìm kiếm!'
  }
};

export default messageUtil;
