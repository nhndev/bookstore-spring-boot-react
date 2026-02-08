import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Modal } from 'react-bootstrap';
import { setShow } from '../../../redux/slice/confirmDialog.slice';
import './ConfirmDialog.scss';

// Question/Confirm icon
const QuestionIcon = () => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 24 24"
    fill="currentColor"
  >
    <path
      fillRule="evenodd"
      d="M2.25 12c0-5.385 4.365-9.75 9.75-9.75s9.75 4.365 9.75 9.75-4.365 9.75-9.75 9.75S2.25 17.385 2.25 12zm11.378-3.917c-.89-.777-2.366-.777-3.255 0a.75.75 0 01-.988-1.129c1.454-1.272 3.776-1.272 5.23 0 1.513 1.324 1.513 3.518 0 4.842a3.75 3.75 0 01-.837.552c-.676.328-1.028.774-1.028 1.152v.75a.75.75 0 01-1.5 0v-.75c0-1.279 1.06-2.107 1.875-2.502.182-.088.351-.199.503-.331.83-.727.83-1.857 0-2.584zM12 18a.75.75 0 100-1.5.75.75 0 000 1.5z"
      clipRule="evenodd"
    />
  </svg>
);

function ConfirmDialog() {
  const dispatch = useDispatch();
  const { isShow, message, okFunc, cancelFunc } = useSelector(
    (state) => state.confirmDialog,
  );

  const onClickCancel = () => {
    if (cancelFunc) {
      cancelFunc();
    }
    dispatch(setShow(false));
  };

  const onClickOk = async () => {
    if (okFunc) {
      await okFunc();
    }
    dispatch(setShow(false));
  };

  return (
    <Modal
      centered
      show={isShow}
      onHide={onClickCancel}
      className="confirm-dialog"
      backdropClassName="confirm-dialog-backdrop"
    >
      <Modal.Header closeButton />
      <Modal.Body>
        <div className="confirm-icon">
          <QuestionIcon />
        </div>
        <h4 className="confirm-title">Xác nhận thao tác</h4>
        <div
          className="confirm-message"
          dangerouslySetInnerHTML={{ __html: message }}
        />
      </Modal.Body>
      <Modal.Footer>
        <button className="btn-cancel" onClick={onClickCancel}>
          Huỷ bỏ
        </button>
        <button className="btn-confirm" onClick={onClickOk}>
          Xác nhận
        </button>
      </Modal.Footer>
    </Modal>
  );
}

export default ConfirmDialog;
